public class Processor {

	private int moveSpeed;
	private int rotationSpeed;
	private int turretRotationSpeed;
	private int fireInterval;
	private int ballisticsTravelSpeed;
	private int fieldOfView;
	private int turretFieldOfView;
	private int hitPoints;
	private int ballisticDamage;
	private int enemyHitScore;
	private int enemyKillScore;
	private int[] heatMap;

	/**
	 * @param args
	 */
	public Processor(int moveSpeed, int rotationSpeed, int turretRotationSpeed,
			int fireInterval, int ballisticsTravelSpeed, int fieldOfView,
			int turretFieldOfView, int hitPoints, int ballisticDamage,
			int enemyHitScore, int enemyKillScore) {
		this.moveSpeed = moveSpeed;
		this.rotationSpeed = rotationSpeed;
		this.turretRotationSpeed = turretRotationSpeed;
		this.fireInterval = fireInterval;
		this.ballisticsTravelSpeed = ballisticsTravelSpeed;
		this.fieldOfView = fieldOfView;
		this.turretFieldOfView = turretFieldOfView;
		this.hitPoints = hitPoints;
		this.ballisticDamage = ballisticDamage;
		this.enemyHitScore = enemyHitScore;
		this.enemyKillScore = enemyKillScore;
	}

	public Command processContext(Context context) {

		// update private stats based on context

		// update heatmap

		// TODO: insert fancy tank ops here
		if (this.moveSpeed < 1) {
			return new Command("moveForwardWithSpeed", "1.0");
		} else
			return new Command("rotateTank", "45");
	}

	public Command processError(CommandExecutionError error) {

		// TODO: do some fancy error handling here

		if (this.moveSpeed > -1)
			return new Command("moveBackwardWithSpeed", "1.0");
		else return new Command("rotateTank", "-90");
		
	}

}
