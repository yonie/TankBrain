public class Tank {
	Place place;
	double tankAngle;
	double turretAngle;

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
	public Tank(double x, double y, double tankAngle, double turretAngle) {
		this.place = new Place(x, y);
		this.tankAngle = tankAngle;
		this.turretAngle = turretAngle;
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
