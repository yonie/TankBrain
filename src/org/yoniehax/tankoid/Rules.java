package org.yoniehax.tankoid;

public class Rules {

	private double movementSpeed;
	private double rotationSpeed;
	private double turretRotationSpeed;
	private double fireInterval;
	private double ballisticsTravelSpeed;
	private int fieldOfView;
	private int turretFieldOfView;
	private int hitPoints;
	private int ballisticDamage;
	private int enemyHitScore;
	private int enemyKillScore;
	private int tankStatusUpdateRate;

	/**
	 * The <b>Rules</b> object is used to store game rules.
	 * 
	 * @param movementSpeed
	 *            maximum movement speed of the Tank
	 * @param rotationSpeed
	 *            maximum rotation speed of the Tank
	 * @param turretRotationSpeed
	 *            rotation speed of the Tank turret
	 * @param fireInterval
	 *            fire interval of the Tank
	 * @param ballisticsTravelSpeed
	 *            travel speed for ballistics
	 * @param fieldOfView
	 *            FOV for the Tank
	 * @param turretFieldOfView
	 *            FOV for the Tank turret
	 * @param hitPoints
	 *            amount of hit points for the Tank
	 * @param ballisticDamage
	 *            damage caused by ballistics
	 * @param enemyHitScore
	 *            point reward for hitting enemies
	 * @param enemyKillScore
	 *            point reward for killing enemies
	 */
	public Rules(double movementSpeed, double rotationSpeed, double turretRotationSpeed, double fireInterval,
			double ballisticsTravelSpeed, int fieldOfView, int turretFieldOfView, int hitPoints, int ballisticDamage,
			int enemyHitScore, int enemyKillScore, int tankStatusUpdateRate) {

		this.movementSpeed = movementSpeed;
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
		this.tankStatusUpdateRate = tankStatusUpdateRate;

	}

	/**
	 * Gets the Tank maximum rotation speed.
	 * 
	 * @return the Tank maximum rotation speed
	 */
	public double getRotationSpeed() {
		return rotationSpeed;
	}

	/**
	 * Gets the maximum Tank movement speed.
	 * 
	 * @return the maximum Tank movement speed
	 */
	public double getMovementSpeed() {
		return movementSpeed;
	}
}
