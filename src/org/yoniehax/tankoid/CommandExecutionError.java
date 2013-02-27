package org.yoniehax.tankoid;

public class CommandExecutionError {
	private String executingCommand;
	private String reason;

	/**
	 * Creates a new <b>CommandExecutionError</b>, which contains an error that
	 * occurred during execution of a command by the server.
	 * 
	 * @param executingCommand
	 *            the command that was executing when the error occurred
	 * @param reason
	 *            the actual error reason
	 */
	public CommandExecutionError(String executingCommand, String reason) {
		this.executingCommand = executingCommand;
		this.reason = reason;
	}

	/**
	 * Gets the command that was executing when the error occurred.
	 * 
	 * @return the command that was executing when the error occurred
	 */
	public String getExecutingCommand() {
		return executingCommand;
	}

	/**
	 * Gets the actual error reason.
	 * 
	 * @return the actual error reason
	 */
	public String getReason() {
		return reason;
	}
}
