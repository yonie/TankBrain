package org.yoniehax.tankoid;
public class Tank {
	Place place;
	double tankAngle;
	double turretAngle;
	boolean isMoving;
	boolean isRotating;
	boolean isTurrentRotating;

	/**
	 * The Tank is the main object in game.
	 * 
	 * @param x
	 *            x-coordinate of this tank
	 * @param y
	 *            y-coordinate of this tank
	 * @param tankAngle
	 *            Angle at which the Tank is pointing.
	 * @param turretAngle
	 *            Angle at which the turret of the Tank is pointing.
	 */
	public Tank(double x, double y, double tankAngle, double turretAngle, boolean isMoving, boolean isRotating, boolean isTurrentRotating) {
		this.place = new Place(x, y);
		this.tankAngle = tankAngle;
		this.turretAngle = turretAngle;
		this.isMoving = isMoving;
		this.isRotating = isRotating;
		this.isTurrentRotating = isTurrentRotating;
	}

	/**
	 * 
	 * @return
	 */
	public Place getPlace() {
		return place;
	}

	/**
	 * 
	 * @return
	 */
	public double getAngle() {
		return tankAngle;
	}
}
