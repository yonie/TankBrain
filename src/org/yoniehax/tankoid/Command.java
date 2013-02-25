package org.yoniehax.tankoid;
public class Command {
	private String command;
	private String param;

	/**
	 * A Command is used to store any command that is sent to the server.
	 * 
	 * @param command
	 *            The first hand of the command.
	 * @param param
	 *            The second hand of the command.
	 */
	public Command(String command, String param) {
		this.command = command;
		this.param = param;
	}
	
	/**
	 * 
	 * @param command
	 * @param param
	 */
	public Command(String command, Double param) {
		this.command = command;
		this.param = "" + param;
	}

	/**
	 * 
	 * @param command
	 * @param param
	 */
	public Command(String command, int param) {
		this.command = command;
		this.param = "" + param;
	}

	/**
	 * Returns the first hand of the command.
	 * 
	 * @return The first hand of the command.
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Returns the second hand of the command
	 * 
	 * @return The second hand of the command.
	 */
	public String getParam() {
		return param;
	}

}
