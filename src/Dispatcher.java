import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class Dispatcher {

	Processor processor;

	String remoteHost;
	int downstreamPort;
	int upstreamPort;
	String versionString;
	String userName;
	int tankColorRed;
	int tankColorGreen;
	int tankColorBlue;

	/**
	 * The Dispatcher sets up connectivity and runs the main thread keeping connection to the server.
	 * 
	 * @param remoteHost The remote host address to connect to.
	 * @param downstreamPort The port of the downstream connection to use.
	 * @param upstreamPort The port of the upstream connection to use.
	 * @param versionString The version string to use during connection. Note that this must match the server side <i>major</i> version. 
	 * @param userName The user name to use whilst identifying on the server. 
	 * @param tankColorRed The amount of "red" for the tank color, should be between 0-255. 
	 * @param tankColorGreen The amount of "green" for the tank color, should be between 0-255.
	 * @param tankColorBlue The amount of "blue" for the tank color, should be between 0-255.
	 */
	public Dispatcher(String remoteHost, int downstreamPort, int upstreamPort,
			String versionString, String userName, int tankColorRed,
			int tankColorGreen, int tankColorBlue) {
		this.remoteHost = remoteHost;
		this.downstreamPort = downstreamPort;
		assert (downstreamPort < 65536);
		this.upstreamPort = upstreamPort;
		assert (upstreamPort < 65536);
		this.versionString = versionString;
		this.userName = userName;
		this.tankColorRed = tankColorRed;
		this.tankColorGreen = tankColorGreen;
		this.tankColorBlue = tankColorBlue;

		processor = new Processor();

		try {

			// set up upstream connection
			Socket upstreamSocket = new Socket(remoteHost, upstreamPort);
			BufferedReader upstreamInput = new BufferedReader(
					new InputStreamReader(upstreamSocket.getInputStream()));
			BufferedWriter upstreamOutput = new BufferedWriter(
					new OutputStreamWriter(upstreamSocket.getOutputStream()));

			// build connect upstream channel message
			JSONObject connectUpstreamChannelMessage = buildConnectUpstreamChannelMessage(
					userName, tankColorRed, tankColorGreen, tankColorBlue,
					versionString);

			// send connect upstream message
			System.out.println("DEBUG: Connecting upstream: "
					+ connectUpstreamChannelMessage.toString());
			upstreamOutput.write(connectUpstreamChannelMessage.toString());
			upstreamOutput.newLine();
			upstreamOutput.flush();

			// get response
			System.out.println("DEBUG: Awaiting response...");
			String connectUpstreamResponse = upstreamInput.readLine();
			System.out.println("DEBUG: Got response: "
					+ connectUpstreamResponse);

			// parse response
			JSONObject connectUpstreamResponseJSON = new JSONObject(
					connectUpstreamResponse);
			int UID = connectUpstreamResponseJSON.getJSONObject(
					"connectionAccepted").getInt("withUID");
			String returnedUserName = connectUpstreamResponseJSON
					.getJSONObject("connectionAccepted").getString("forUser");
			assert (userName == returnedUserName);

			// set up downstream connection
			Socket downstreamSocket = new Socket(remoteHost, downstreamPort);
			BufferedReader downstreamInput = new BufferedReader(
					new InputStreamReader(downstreamSocket.getInputStream()));
			BufferedWriter downstreamOutput = new BufferedWriter(
					new OutputStreamWriter(downstreamSocket.getOutputStream()));

			// build connect downstream channel message
			JSONObject connectDownstreamChannelMessage = buildConnectDownstreamChannelMessage(
					UID, versionString);

			// send connect downstream message
			System.out.println("DEBUG: Connecting downstream: "
					+ connectDownstreamChannelMessage.toString());
			downstreamOutput.write(connectDownstreamChannelMessage.toString());
			downstreamOutput.newLine();
			downstreamOutput.flush();

			// get response
			System.out.println("DEBUG: Awaiting response...");
			String connectDownstreamResponse = downstreamInput.readLine();
			System.out.println("DEBUG: Got response: "
					+ connectDownstreamResponse);

			// parse response
			JSONObject initialGameState = new JSONObject(
					connectDownstreamResponse).getJSONObject("welcomeToGame");
			System.out.println("DEBUG: Entered game: "
					+ initialGameState.toString());

			while (true) {

				// listen for input (blocking)
				String rawContext = downstreamInput.readLine();
				System.out.println("DEBUG: Recieved context: " + rawContext);

				// parse object out of JSON context
				Context context = new Context(new JSONObject(rawContext));

				// process context and get command
				Command command = processor.processContext(context);

				// make command into JSONObject
				JSONObject JSONcommand = new JSONObject().put(
						command.getCommand(), command.getParam());

				// send command
				upstreamOutput.write(JSONcommand.toString());
				System.out.println("DEBUG: Sent command: " + JSONcommand.toString());

			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Builds the JSONObject which is sent up the initial connection upstream
	 */
	private JSONObject buildConnectUpstreamChannelMessage(String userName,
			int tankColorRed, int tankColorGreen, int tankColorBlue,
			String versionString) throws JSONException {

		JSONObject connectUpstreamChannelMessageContents = new JSONObject();
		connectUpstreamChannelMessageContents.put("asUser", userName);
		connectUpstreamChannelMessageContents.put("withTankColor", ""
				+ tankColorRed + "," + tankColorGreen + "," + tankColorBlue
				+ "");
		connectUpstreamChannelMessageContents.put("usingProtocolVersion",
				versionString);
		return new JSONObject().put("connect",
				connectUpstreamChannelMessageContents);
	}

	/*
	 * Builds the JSONObject which is sent up the initial connection downstream
	 */
	private JSONObject buildConnectDownstreamChannelMessage(int UID,
			String versionString2) throws JSONException {
		JSONObject connectDownstreamChannelMessageContents = new JSONObject();
		connectDownstreamChannelMessageContents.put("asUserWithUID", UID);
		connectDownstreamChannelMessageContents.put("usingProtocolVersion",
				versionString);
		return new JSONObject().put("connect",
				connectDownstreamChannelMessageContents);
	}

}
