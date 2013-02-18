public class Path {

	private Place startingPlace;
	private Place destinationPlace;
	private double startingAngle;
	private double movementSpeed;
	private double rotationSpeed;

	/**
	 * A <b>Path</b> is used to store a route from a starting place to a
	 * destination place. The route has a starting angle, a movement and tank
	 * rotation speed.
	 * 
	 * @param startingPlace
	 *            the starting Place of the Path.
	 * @param destinationPlace
	 *            the destination Place of the Path.
	 * @param startingAngle
	 *            the starting angle of the tank.
	 * @param movementSpeed
	 *            the movement speed used to traverse this path.
	 * @param rotationSpeed
	 *            the rotation speed of the tank.
	 */
	public Path(Place startingPlace, Place destinationPlace, double startingAngle, double movementSpeed,
			double rotationSpeed) {
		this.startingPlace = startingPlace;
		this.destinationPlace = destinationPlace;
		this.startingAngle = startingAngle;
		this.movementSpeed = movementSpeed;
		this.rotationSpeed = rotationSpeed;
	}

	/**
	 * Gets the movement duration based on the distance to travel and movement
	 * speed.
	 * 
	 * @return movement duration for this Path.
	 */
	public double getMovementDuration() {

		// determine distance to travel
		double distanceX = startingPlace.getX() - destinationPlace.getX();
		double distanceY = startingPlace.getY() - destinationPlace.getY();
		double distanceToTravel = (int) Math.round(Math.sqrt((distanceX * distanceX) + (distanceY * distanceY)));

		return (distanceToTravel / movementSpeed);
	}

	/**
	 * Gets the rotation duration based on the current and required angle.
	 * 
	 * @return the rotation duration for this Path.
	 */
	public double getRotationDuration() {
		return Math.abs(getRotationAngle() / rotationSpeed);
	}

	/**
	 * Gets the rotation angle to use for this Path. This angle is relative to
	 * the given starting angle.
	 * 
	 * @return the rotation angle.
	 */
	public double getRotationAngle() {

		// first we need to get our current angle
		double distanceX = startingPlace.getX() - destinationPlace.getX();
		double distanceY = startingPlace.getY() - destinationPlace.getY();

		// do magic to get new angle
		double newAngle = Math.toDegrees(Math.atan2(distanceX, distanceY));

		double rotationAngle = newAngle - startingAngle;

		// fix angles lower than -180 degrees
		if (rotationAngle < -180)
			rotationAngle += 360;

		assert (rotationAngle <= 180 && rotationAngle >= -180);

		return rotationAngle;
	}

	/**
	 * Gets the movement speed for this Path.
	 * 
	 * @return the movement speed.
	 */
	public double getMovementSpeed() {
		return movementSpeed;
	}

	/**
	 * Gets the starting Place for this path.
	 * 
	 * @return the starting Place.
	 */
	public Place getStartingPlace() {
		return startingPlace;
	}

	/**
	 * Gets the destination Place for this Path.
	 * 
	 * @return the destination Place. 
	 */
	public Place getDestinationPlace() {
		return destinationPlace;
	}

}
