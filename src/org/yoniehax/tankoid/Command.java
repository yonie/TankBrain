package org.yoniehax.tankoid;

public class Command {
	private String command;
	private String param;

	/**
	 * A <b>Command</b> is used to store any command that can be sent to the
	 * server. Every command consists of two parts: the actual command to send
	 * and a parameter.
	 * 
	 * @param command
	 *            The actual command to send
	 * @param param
	 *            The parameter to send along with the command
	 */
	public Command(String command, String param) {
		this.command = command;
		this.param = param;
	}

	/**
	 * A <b>Command</b> is used to store any command that can be sent to the
	 * server. Every command consists of two parts: the actual command to send
	 * and a parameter.
	 * 
	 * @param command
	 *            The actual command to send
	 * @param param
	 *            The parameter to send along with the command
	 */
	public Command(String command, Double param) {
		this.command = command;
		this.param = "" + param;
	}

	/**
	 * A <b>Command</b> is used to store any command that can be sent to the
	 * server. Every command consists of two parts: the actual command to send
	 * and a parameter associated with the command.
	 * 
	 * @param command
	 *            The actual command to send
	 * @param param
	 *            The parameter associated with the command
	 */
	public Command(String command, int param) {
		this.command = command;
		this.param = "" + param;
	}

	/**
	 * Returns the actual command to send.
	 * 
	 * @return The actual command to send
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Returns the parameter associated with the command.
	 * 
	 * @return The parameter associated with the command
	 */
	public String getParam() {
		return param;
	}

}
