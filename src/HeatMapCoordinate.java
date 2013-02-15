public class HeatMapCoordinate {

	private int x;
	private int y;

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public HeatMapCoordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public HeatMapCoordinate(double gameXpos, double gameYpos, int gridOffset, int gridSize, int heatMapSize) {
		this.x = (int) Math.round(((gameXpos + gridOffset) / gridSize) * heatMapSize);
		this.y = (int) Math.round(((gameYpos + gridOffset) / gridSize) * heatMapSize);
	}

	/**
	 * 
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * 
	 * @return
	 */
	public int getY() {
		return y;
	}
}
