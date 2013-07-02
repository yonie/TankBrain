package org.yoniehax.tankoid;

import java.util.Collections;
import java.util.Vector;

import org.yoniehax.helper.QuickLog;

public class HeatMap {

	private int heatMapSize;
	private int resolution;
	private int[][] heatMap;

	/**
	 * The <b>HeatMap</b> can be used to store data about the game world and
	 * objects in it. This can then be used to find busy or silent spots. The
	 * HeatMap also features a resolution setting to concatenate values.
	 * 
	 * @param mapSize
	 *            map size to be used. Should be set to maximum positive or
	 *            negative value of the game grid.
	 * @param resolution
	 *            the amount of grid points concatenated (meaning a resolution
	 *            of 1 is full grid resolution, 2 is half resolution, and so
	 *            on).
	 */
	public HeatMap(int mapSize, int resolution) {
		QuickLog.debug("Creating new HeatMap for grid with size " + mapSize + " with resolution " + resolution + "...");
		this.heatMapSize = (int) Math.floor(mapSize / resolution) + 1;
		this.resolution = resolution;
		heatMap = new int[heatMapSize][heatMapSize];
	}

	/**
	 * Increments the HeatMap on a specific Place, to make it 'more warm'.
	 * 
	 * @param place
	 *            the Place that should be incremented in the HeatMap.
	 */
	public void increment(Place place) {
		int x = (int) Math.floor(place.getX() / resolution);
		int y = (int) Math.floor(place.getY() / resolution);

		heatMap[x][y] += 1;
	}

	/**
	 * Finds the most crowded Place (with most hits) in the HeatMap.
	 * 
	 * @return the most crowded Place. If more than one place has the same
	 *         amount of hits, the first found place will be returned.
	 */
	public Place findMostCrowdedPlace() {
		int mostCrowdedPlaceX = 0;
		int mostCrowdedPlaceY = 0;

		for (int x = 0; x < heatMapSize; x++) {
			for (int y = 0; y < heatMapSize; y++) {
				if (heatMap[x][y] > heatMap[mostCrowdedPlaceX][mostCrowdedPlaceY]) {
					mostCrowdedPlaceX = x;
					mostCrowdedPlaceY = y;
				}
			}
		}

		return new Place(mostCrowdedPlaceX * resolution, mostCrowdedPlaceY * resolution);
	}

	/**
	 * Finds the most crowded places currently available in the HeatMap.
	 * 
	 * @param num
	 *            the amount of crowded places to find.
	 * @return the most crowded places currently available in the HeatMap.
	 */
	public Vector<HeatMapPlace> findCrowdedPlaces(int num) {
		Vector<HeatMapPlace> results = new Vector<HeatMapPlace>(num);
		results.add(new HeatMapPlace(0, 0, 0));

		for (int x = 0; x < heatMapSize; x++) {
			for (int y = 0; y < heatMapSize; y++) {
				if (heatMap[x][y] > results.get(0).getHeat()) {
					QuickLog.debug("Adding new HeatMapPlace " + x + " " + y + " " + heatMap[x][y]);
					if (results.size() == num)
						results.remove(0);
					results.add(new HeatMapPlace(x * resolution, y * resolution, heatMap[x][y]));
					Collections.sort(results);
				}
			}
		}

		return results;
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

		for (int x = 0; x < heatMapSize; x++) {
			for (int y = 0; y < heatMapSize; y++) {
				if (heatMap[x][y] < heatMap[quietPlaceX][quietPlaceY]) {
					quietPlaceX = x;
					quietPlaceY = y;
				}
			}
		}

		return new Place(quietPlaceX * resolution, quietPlaceY * resolution);
	}

	/**
	 * Finds a random Place on the HeatMap.
	 * 
	 * @return a random Place.
	 */
	public Place findRandomPlace() {
		int x = (int) (Math.random() * heatMapSize);
		int y = (int) (Math.random() * heatMapSize);

		return new Place(x * resolution, y * resolution);
	}

}
