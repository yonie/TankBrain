package org.yoniehax.tankoid;

public class Place {

	private double x;
	private double y;

	/**
	 * A Place is a specific location in the game grid.
	 * 
	 * @param x
	 *            x-position of the Place.
	 * @param y
	 *            y-position of the Place.
	 */
	public Place(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the x-position of the Place.
	 * 
	 * @return x-position of the Place.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the x-position of the Place.
	 * 
	 * @return x-position of the Place.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Determines if this Place is nearby given other place.
	 * 
	 * @param otherPlace
	 *            the other Place to compare to.
	 * @return true if this Place is within 5 units of the other place,
	 *         otherwise false.
	 */
	public boolean isNearby(Place otherPlace) {
		if (otherPlace == null)
			return false;
		double distanceX = this.getX() - otherPlace.getX();
		double distanceY = this.getY() - otherPlace.getY();
		double totalDistance = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY));
		return (totalDistance < 5 ? true : false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "x=" + x + ", y=" + y;
	}

}
