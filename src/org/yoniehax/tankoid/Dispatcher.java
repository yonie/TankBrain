package org.yoniehax.tankoid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.yoniehax.helper.QuickLog;
import org.yoniehax.tankoid.gui.DebugGUI;

public class Dispatcher {

	private Socket upstreamSocket;
	private BufferedReader upstreamInput;
	private BufferedWriter upstreamOutput;

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
	 * The <b>Dispatcher</b> sets up connectivity and runs the main thread
	 * keeping connection to the server.
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
			QuickLog.debug("Connecting upstream: " + connectUpstreamChannelMessage.toString());
			upstreamOutput.write(connectUpstreamChannelMessage.toString());
			upstreamOutput.newLine();
			upstreamOutput.flush();

			// get response
			QuickLog.debug("DEBUG: Awaiting response...");
			String connectUpstreamResponse = upstreamInput.readLine();
			QuickLog.debug("DEBUG: Got response: " + connectUpstreamResponse);

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
			QuickLog.debug("Connecting downstream: " + connectDownstreamChannelMessage.toString());
			downstreamOutput.write(connectDownstreamChannelMessage.toString());
			downstreamOutput.newLine();
			downstreamOutput.flush();

			// get response
			QuickLog.debug("Awaiting response...");
			String connectDownstreamResponse = downstreamInput.readLine();
			QuickLog.debug("Got response: " + connectDownstreamResponse);

			// parse connect response and fetch initial game state
			JSONObject initialGameState = new JSONObject(connectDownstreamResponse).getJSONObject("welcomeToGame");
			QuickLog.info("Welcome to game: " + initialGameState.toString());

			// fetch rules object from initial game state
			JSONObject jsonRules = initialGameState.getJSONObject("rules");
			assert (jsonRules != null);

			// set up processor using rules from initial game state
			Rules gameRules = new Rules(jsonRules.getDouble("moveSpeed"), jsonRules.getDouble("rotationSpeed"),
					jsonRules.getDouble("turretRotationSpeed"), jsonRules.getDouble("fireInterval"),
					jsonRules.getDouble("ballisticsTravelSpeed"), jsonRules.getInt("fieldOfView"),
					jsonRules.getInt("turretFieldOfView"), jsonRules.getInt("hp"), jsonRules.getInt("ballisticDamage"),
					jsonRules.getInt("enemyHitScore"), jsonRules.getInt("enemyKillScore"),
					jsonRules.getInt("tankStatusUpdateRate"));
			processor = new Processor(this, gameRules);

			processor.start();

			// wait for the one-time 'game will start' message
			JSONObject gameWillStart = new JSONObject(downstreamInput.readLine()).getJSONObject("gameWillStart");
			QuickLog.info("Got gameWillStart: " + gameWillStart);

			// get the map dimensions
			// FIXME: we don't deal with non-square game maps
			int mapSize = gameWillStart.getJSONObject("onMap").getJSONObject("withSize").getInt("width");
			processor.setMapSize(mapSize);
			DebugGUI gui = new DebugGUI(processor);

			String serverResponse;

			// listen for input (blocking)
			while ((serverResponse = downstreamInput.readLine()) != null) {

				JSONObject serverResponseJSON = new JSONObject(serverResponse);

				if (serverResponseJSON.has("tankStatusUpdate")) {

					// parse context object out of raw JSON context
					JSONObject statusUpdate = serverResponseJSON.getJSONObject("tankStatusUpdate");

					Tank ownTank = new Tank(statusUpdate.getJSONObject("position").getDouble("x"), statusUpdate
							.getJSONObject("position").getDouble("y"), statusUpdate.getDouble("direction"),
							statusUpdate.getDouble("turretDirection"), statusUpdate.getBoolean("isMoving"),
							statusUpdate.getBoolean("isRotating"), statusUpdate.getBoolean("isTurretRotating"));

					// process tank status update
					processor.processTankStatusUpdate(ownTank);
					gui.repaint();

				} else {

					// TODO: handle downstream server errors

					QuickLog.error("Could not process status update: " + serverResponseJSON);

				}

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
	 * processor will process the error which might trigger sending a new
	 * Command.
	 * 
	 * @param command
	 *            Command to send.
	 */
	public void sendCommand(Command command) {

		try {

			// build JSONObject
			JSONObject jsonCommand = new JSONObject().put(command.getCommand(), command.getParam());

			// send command upstream
			upstreamOutput.write(jsonCommand.toString());
			upstreamOutput.newLine();
			upstreamOutput.flush();

			// get command ack
			JSONObject jsonCommandAck = new JSONObject(upstreamInput.readLine());

			if (jsonCommandAck.has("errorOccurred")) {

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
