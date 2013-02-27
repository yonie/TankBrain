package org.yoniehax.tankoid;

public class HeatMap {

	private int heatMapSize;
	private int[][][] heatMap;

	/**
	 * The <b>HeatMap</b> can be used to store data about the game world and
	 * objects in it. This can then be used to find busy or silent spots. The
	 * HeatMap stores both positive and negative coordinates.
	 * 
	 * @param heatMapSize
	 *            size of the HeatMap to be used. Should be set to maximum
	 *            positive or negative value of the game grid.
	 */
	public HeatMap(int heatMapSize) {
		this.heatMapSize = heatMapSize;

		// 4 arrays to store both positive and negative coordinates
		heatMap = new int[4][heatMapSize][heatMapSize];
	}

	/**
	 * Increments the HeatMap on a specific Place, to make it 'more warm'.
	 * 
	 * @param place
	 *            the Place that should be incremented in the HeatMap.
	 */
	public void increment(Place place) {
		int x = (int) Math.round(place.getX());
		int y = (int) Math.round(place.getY());

		if (x >= 0 && y >= 0)
			heatMap[1][x][y] += 1;
		else if (x >= 0 && y < 0)
			heatMap[3][x][-y] += 1;
		else if (x < 0 && y >= 0)
			heatMap[0][-x][y] += 1;
		else
			heatMap[2][-x][-y] += 1;
	}

	/**
	 * Finds the most crowded Place (with most hits) in the HeatMap.
	 * 
	 * @return the most crowded Place. If more than one place has the same
	 *         amount of hits, the first found place will be returned.
	 */
	public Place findMostCrowdedPlace() {
		int mostCrowdedPlaceMap = 0;
		int mostCrowdedPlaceX = 0;
		int mostCrowdedPlaceY = 0;

		// FIXME: we only work with positive coordinates (array 1) for now
		for (int map = 1; map < 2; map++) {
			for (int x = 0; x < heatMapSize; x++) {
				for (int y = 0; y < heatMapSize; y++) {
					if (heatMap[map][x][y] > heatMap[map][mostCrowdedPlaceX][mostCrowdedPlaceY]) {
						mostCrowdedPlaceMap = map;
						mostCrowdedPlaceX = x;
						mostCrowdedPlaceY = y;
					}
				}
			}
		}

		if (mostCrowdedPlaceMap == 0)
			return new Place(-mostCrowdedPlaceX, mostCrowdedPlaceY);
		else if (mostCrowdedPlaceMap == 1)
			return new Place(mostCrowdedPlaceX, mostCrowdedPlaceY);
		else if (mostCrowdedPlaceMap == 2)
			return new Place(-mostCrowdedPlaceX, -mostCrowdedPlaceY);
		else
			return new Place(mostCrowdedPlaceX, -mostCrowdedPlaceY);
	}

	/**
	 * Finds a quiet Place (with least hits) in the HeatMap.
	 * 
	 * @return the most quiet Place. If more than one Place has the same amount
	 *         of hits, the first found Place will be returned.
	 */
	public Place findQuietPlace() {
		int quietPlaceX = 0;
		int quietPlaceY = 0;
		int quietPlaceMap = 0;

		// FIXME: we only work with positive coordinates (array 1) for now
		for (int map = 1; map < 2; map++) {
			for (int x = 0; x < heatMapSize; x++) {
				for (int y = 0; y < heatMapSize; y++) {
					if (heatMap[map][x][y] < heatMap[quietPlaceMap][quietPlaceX][quietPlaceY]) {
						quietPlaceX = x;
						quietPlaceY = y;
						quietPlaceMap = map;
					}
				}
			}
		}

		if (quietPlaceMap == 0)
			return new Place(-quietPlaceX, quietPlaceY);
		else if (quietPlaceMap == 1)
			return new Place(quietPlaceX, quietPlaceY);
		else if (quietPlaceMap == 2)
			return new Place(-quietPlaceX, -quietPlaceY);
		else
			return new Place(quietPlaceX, -quietPlaceY);
	}

	/**
	 * Finds a random Place on the HeatMap.
	 * 
	 * @return a random Place.
	 */
	public Place findRandomPlace() {
		// FIXME: we only work with positive coordinates (array 1) for now
		int map = 1; // (int) (Math.random() * 3);
		int x = (int) (Math.random() * heatMapSize);
		int y = (int) (Math.random() * heatMapSize);

		if (map == 0)
			return new Place(-x, y);
		else if (map == 1)
			return new Place(x, y);
		else if (map == 2)
			return new Place(-x, -y);
		else
			return new Place(x, -y);
	}

}
