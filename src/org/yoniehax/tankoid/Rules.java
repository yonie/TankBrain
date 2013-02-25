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
	 * 
	 * @param movementSpeed
	 * @param rotationSpeed
	 * @param turretRotationSpeed
	 * @param fireInterval
	 * @param ballisticsTravelSpeed
	 * @param fieldOfView
	 * @param turretFieldOfView
	 * @param hitPoints
	 * @param ballisticDamage
	 * @param enemyHitScore
	 * @param enemyKillScore
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
	 * 
	 * @return
	 */
	public double getRotationSpeed() {
		return rotationSpeed;
	}

	/**
	 * 
	 * @return
	 */
	public double getMovementSpeed() {
		return movementSpeed;
	}
}
