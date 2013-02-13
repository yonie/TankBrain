import org.json.JSONException;
import org.json.JSONObject;

public class Context {
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
	public Context(JSONObject JSONcontext) {
		try {
			ownTank = new Tank(JSONcontext.getJSONObject("position").getDouble("x"), JSONcontext.getJSONObject(
					"position").getDouble("y"), JSONcontext.getDouble("direction"),
					JSONcontext.getDouble("turretDirection"));
			isMoving = (JSONcontext.getString("isMoving").equals("YES") ? true : false);
			isRotating = (JSONcontext.getString("isRotating").equals("YES") ? true : false);
			isTurretRotating = (JSONcontext.getString("isTurretRotating").equals("YES") ? true : false);

			// TODO: this doesn't work yet in 0.3.0
			// isPerformingScan =
			// (JSONcontext.getString("isPerformingScan").equals("YES") ? true :
			// false);

			// TODO: objectsInFieldOfView
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Tank getOwnTank() {
		return ownTank;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public boolean isPerformingScan() {
		return isPerformingScan;
	}

	public boolean isRotating() {
		return isRotating;
	}

}
