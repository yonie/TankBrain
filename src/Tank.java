public class Tank {
	int xpos;
	int ypos;
	int tankAngle;
	int turretAngle;

	/**
	 * The Tank is the main object in game. 
	 * 
	 * @param xpos Horizontal grid position of the Tank. 
	 * @param ypos Vertical grid position of the Tank. 
	 * @param tankAngle Angle at which the Tank is pointing. 
	 * @param turretAngle Angle at which the turret of the Tank is pointing. 
	 */
	public Tank(int xpos,int ypos, int tankAngle, int turretAngle) {
		this.xpos=xpos;
		this.ypos=ypos;
		this.tankAngle=tankAngle;
		this.turretAngle=turretAngle;
	}
}
