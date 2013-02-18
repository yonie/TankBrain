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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Place, x=" + x + ", y=" + y;
	}
}
