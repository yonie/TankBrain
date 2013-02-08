public class Command {
	private String command;
	private String param;
	
	/*
	 * { "moveForwardWithSpeed": 1.0 // [0.0 - 1.0] }
	 * 
	 * { "moveBackwardWithSpeed": 0.5 // [0.0 - 1.0] }
	 * 
	 * { "moveWithSpeed": -0.75 // [-1.0 - 1.0] }
	 * 
	 * { "rotateTank": 60.0 // [-359.0 - 359.0] }
	 * 
	 * { "rotateTurret": -60.0 // [-359.0 - 359.0] }
	 * 
	 * { "rotateTurretToRelativeAngle": 30.0 // [0.0 - 359.0] }
	 * 
	 * { "stop": "tankRotation" // ["tankRotation", "turretRotation", "moving",
	 * "scanning"] }
	 */
	
	public Command(String command, String param) {
		this.command = command;
		this.param = param;
	}
	
	public String getCommand() {
		return command;
	}

	public String getParam() {
		return param;
	}

}
