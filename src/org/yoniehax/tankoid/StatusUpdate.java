package org.yoniehax.tankoid;
import org.json.JSONException;
import org.json.JSONObject;

public class StatusUpdate {
	private Tank ownTank;
	private boolean isMoving;
	private boolean isRotating;
	private boolean isTurretRotating;
	private boolean isPerformingScan;

	// TODO: private Tank[] tanksNearby;

	/**
	 * The Context stores all the tank contextual information, meaning location,
	 * direction, any objects or other Tanks nearby.
	 * 
	 * @param JSONcontext
	 */
	public StatusUpdate(JSONObject JSONcontext) {
		try {
			ownTank = new Tank(JSONcontext.getJSONObject("position").getDouble("x"), JSONcontext.getJSONObject(
					"position").getDouble("y"), JSONcontext.getDouble("direction"),
					JSONcontext.getDouble("turretDirection"));
			isMoving = JSONcontext.getBoolean("isMoving");
			isRotating = JSONcontext.getBoolean("isRotating");
			isTurretRotating = JSONcontext.getBoolean("isTurretRotating");

			// TODO: this doesn't work yet in 0.4.x
			// isPerformingScan = JSONcontext.getString("isPerformingScan");

			// TODO: objectsInFieldOfView

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	public Tank getOwnTank() {
		return ownTank;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isMoving() {
		return isMoving;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPerformingScan() {
		return isPerformingScan;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isRotating() {
		return isRotating;
	}

}
