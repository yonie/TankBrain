package org.yoniehax.tankoid;

public class HeatMapPlace extends Place implements Comparable<HeatMapPlace> {
	private int heat;

	/**
	 * The HeatMapPlace can be used to store heat for a certain Place. 
	 * 
	 * @param x x-coordinate of the Place. 
	 * @param y y-coordinate of the Place. 
	 * @param heat heat for given Place. 
	 */
	public HeatMapPlace(int x, int y, int heat) {
		super(x, y);
		this.heat = heat;
	}

	@Override
	public int compareTo(HeatMapPlace otherPlace) {
		int compareHeat = otherPlace.getHeat();
		return heat - compareHeat;
	}

	/**
	 * Gets the heat for this HeatMapPlace. 
	 * 
	 * @return the heat for this HeatMapPlace.
	 */
	public int getHeat() {
		return heat;
	}
	
	public String toString() {
		return super.toString() + "heat="+heat;
	}
}
