package org.yoniehax.tankoid;
public class CommandExecutionError {
	private String executingCommand;
	private String reason;

	public CommandExecutionError(String executingCommand, String reason) {
		this.executingCommand = executingCommand;
		this.reason = reason;
	}

	public String getExecutingCommand() {
		return executingCommand;
	}

	public String getReason() {
		return reason;
	}
}
