import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Dispatcher {

	private Socket upstreamSocket;
	private BufferedReader upstreamInput;
	private BufferedWriter upstreamOutput;

	private Socket downstreamSocket;
	private BufferedReader downstreamInput;
	private BufferedWriter downstreamOutput;

	private Processor processor;
	private String remoteHost;
	private int downstreamPort;
	private int upstreamPort;
	private int majorVersion;
	private int minorVersion;
	private int revisionVersion;
	private String userName;
	private int tankColorRed;
	private int tankColorGreen;
	private int tankColorBlue;

	/**
	 * The Dispatcher sets up connectivity and runs the main thread keeping
	 * connection to the server.
	 * 
	 * @param remoteHost
	 *            The remote host address to connect to.
	 * @param downstreamPort
	 *            The port of the downstream connection to use.
	 * @param upstreamPort
	 *            The port of the upstream connection to use.
	 * @param majorVersion
	 *            The major version string to use during connection. Note that
	 *            this must match the server side <i>major</i> version.
	 * @param minorVersion
	 *            The minor version string to use during connection. Note that
	 *            this must match the server side <i>major</i> version.
	 * @param revisionVersion
	 *            The revision version string to use during connection. Note
	 *            that this must match the server side <i>major</i> version.
	 * @param userName
	 *            The user name to use whilst identifying on the server.
	 * @param tankColorRed
	 *            The amount of "red" for the tank color, should be between
	 *            0-255.
	 * @param tankColorGreen
	 *            The amount of "green" for the tank color, should be between
	 *            0-255.
	 * @param tankColorBlue
	 *            The amount of "blue" for the tank color, should be between
	 *            0-255.
	 */
	public Dispatcher(String remoteHost, int downstreamPort, int upstreamPort, int majorVersion, int minorVersion,
			int revisionVersion, String userName, int tankColorRed, int tankColorGreen, int tankColorBlue) {
		this.remoteHost = remoteHost;
		this.downstreamPort = downstreamPort;
		assert (downstreamPort < 65536);
		this.upstreamPort = upstreamPort;
		assert (upstreamPort < 65536);
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.revisionVersion = revisionVersion;
		this.userName = userName;
		this.tankColorRed = tankColorRed;
		this.tankColorGreen = tankColorGreen;
		this.tankColorBlue = tankColorBlue;

		try {

			// set up upstream connection
			upstreamSocket = new Socket(remoteHost, upstreamPort);
			upstreamInput = new BufferedReader(new InputStreamReader(upstreamSocket.getInputStream()));
			upstreamOutput = new BufferedWriter(new OutputStreamWriter(upstreamSocket.getOutputStream()));

			// build connect upstream channel message
			JSONObject connectUpstreamChannelMessage = buildConnectUpstreamChannelMessage(userName, tankColorRed,
					tankColorGreen, tankColorBlue, majorVersion, minorVersion, revisionVersion);

			// send connect upstream message
			System.out.println("DEBUG: Connecting upstream: " + connectUpstreamChannelMessage.toString());
			upstreamOutput.write(connectUpstreamChannelMessage.toString());
			upstreamOutput.newLine();
			upstreamOutput.flush();

			// get response
			System.out.println("DEBUG: Awaiting response...");
			String connectUpstreamResponse = upstreamInput.readLine();
			System.out.println("DEBUG: Got response: " + connectUpstreamResponse);

			// parse response
			JSONObject connectionAcceptedJSON = new JSONObject(connectUpstreamResponse)
					.getJSONObject("connectionAccepted");
			int UID = connectionAcceptedJSON.getInt("withUID");
			String returnedUserName = connectionAcceptedJSON.getString("forUser");
			assert (returnedUserName == userName && UID > 0);

			// set up downstream connection
			Socket downstreamSocket = new Socket(remoteHost, downstreamPort);
			BufferedReader downstreamInput = new BufferedReader(
					new InputStreamReader(downstreamSocket.getInputStream()));
			BufferedWriter downstreamOutput = new BufferedWriter(new OutputStreamWriter(
					downstreamSocket.getOutputStream()));

			// build connect downstream channel message
			JSONObject connectDownstreamChannelMessage = buildConnectDownstreamChannelMessage(UID, majorVersion,
					minorVersion, revisionVersion);

			// send connect downstream message
			System.out.println("DEBUG: Connecting downstream: " + connectDownstreamChannelMessage.toString());
			downstreamOutput.write(connectDownstreamChannelMessage.toString());
			downstreamOutput.newLine();
			downstreamOutput.flush();

			// get response
			System.out.println("DEBUG: Awaiting response...");
			String connectDownstreamResponse = downstreamInput.readLine();
			System.out.println("DEBUG: Got response: " + connectDownstreamResponse);

			// parse connect response and fetch initial game state
			JSONObject initialGameState = new JSONObject(connectDownstreamResponse).getJSONObject("welcomeToGame");
			System.out.println("DEBUG: Welcome to game: " + initialGameState.toString());

			// fetch rules object from initial game state
			JSONObject jsonRules = initialGameState.getJSONObject("rules");
			assert (jsonRules != null);

			// set up processor using initial game state rules
			Rules gameRules = new Rules(jsonRules.getDouble("moveSpeed"), jsonRules.getDouble("rotationSpeed"),
					jsonRules.getDouble("turretRotationSpeed"), jsonRules.getDouble("fireInterval"),
					jsonRules.getDouble("ballisticsTravelSpeed"), jsonRules.getInt("fieldOfView"),
					jsonRules.getInt("turretFieldOfView"), jsonRules.getInt("hp"), jsonRules.getInt("ballisticDamage"),
					jsonRules.getInt("enemyHitScore"), jsonRules.getInt("enemyKillScore"));
			processor = new Processor(this, gameRules);

			processor.start();

			while (true) {

				// listen for input (blocking)
				String rawContext = downstreamInput.readLine();

				// parse context object out of raw JSON context
				Context context = new Context(new JSONObject(rawContext).getJSONObject("tankStatusUpdate"));

				// process context
				processor.processContext(context);

			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		// make sure the threaded processor will stop
		processor.setRunning(false);
	}

	/**
	 * Sends a Command to the server. If a CommandExecutionError occurs, the
	 * processor will be asked to process the error which might trigger sending
	 * a new Command.
	 * 
	 * @param command
	 *            Command to send.
	 */
	public void sendCommand(Command command) {

		try {

			// put command into JSONObject
			JSONObject jsonCommand = new JSONObject().put(command.getCommand(), command.getParam());

			// send command upstream
			upstreamOutput.write(jsonCommand.toString());
			upstreamOutput.newLine();
			upstreamOutput.flush();
			System.out.println("DEBUG: Sent command: " + jsonCommand.toString());

			// get command ack
			JSONObject jsonCommandAck = new JSONObject(upstreamInput.readLine());
			if (jsonCommandAck.has("errorOccurred")) {

				System.out.println("DEBUG: Recieved an error: " + jsonCommandAck.toString());

				// build error
				CommandExecutionError error = new CommandExecutionError(jsonCommandAck.getJSONObject("errorOccurred")
						.getString("whileExecutingCommand"), jsonCommandAck.getJSONObject("errorOccurred").getString(
						"withReason"));

				// process error
				processor.processError(error);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Build the JSONObject which is sent up the initial connection upstream
	 */
	private JSONObject buildConnectUpstreamChannelMessage(String userName, int tankColorRed, int tankColorGreen,
			int tankColorBlue, int majorVersion, int minorVersion, int revisionVersion) throws JSONException {

		JSONObject connectUpstreamChannelMessageContents = new JSONObject();
		connectUpstreamChannelMessageContents.put("asUser", userName);
		// TODO: implement new 0.2.0 color structure
		connectUpstreamChannelMessageContents.put("withTankColor", "" + tankColorRed + "," + tankColorGreen + ","
				+ tankColorBlue + "");
		JSONObject protocolVersion = new JSONObject().put("major", majorVersion).put("minor", minorVersion)
				.put("revision", revisionVersion);
		connectUpstreamChannelMessageContents.put("usingProtocolVersion", protocolVersion);
		return new JSONObject().put("connect", connectUpstreamChannelMessageContents);
	}

	/*
	 * Build the JSONObject which is sent up the initial connection downstream
	 */
	private JSONObject buildConnectDownstreamChannelMessage(int UID, int majorVersion, int minorVersion,
			int revisionVersion) throws JSONException {
		JSONObject connectDownstreamChannelMessageContents = new JSONObject();
		connectDownstreamChannelMessageContents.put("asUserWithUID", UID);
		JSONObject protocolVersion = new JSONObject().put("major", majorVersion).put("minor", minorVersion)
				.put("revision", revisionVersion);
		connectDownstreamChannelMessageContents.put("usingProtocolVersion", protocolVersion);
		return new JSONObject().put("connect", connectDownstreamChannelMessageContents);
	}

}
