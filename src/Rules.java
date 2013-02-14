public class Rules {

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
	
	public Rules(int moveSpeed, int rotationSpeed, int turretRotationSpeed,
			int fireInterval, int ballisticsTravelSpeed, int fieldOfView, int turretFieldOfView, int hitPoints,
			int ballisticDamage, int enemyHitScore, int enemyKillScore) {
	
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
}
