import org.json.JSONException;
import org.json.JSONObject;

public class Context {
	private Tank ownTank;
	private boolean isMoving;
	private boolean isPerformingScan;

	// TODO: private Tank[] tanksNearby;

	public Context(JSONObject JSONcontext) {
		try {
			ownTank = new Tank(JSONcontext.getJSONArray("position").getInt(0),
					JSONcontext.getJSONArray("position").getInt(1),
					JSONcontext.getInt("direction"),
					JSONcontext.getInt("turretDirection"));
			isMoving = JSONcontext.getBoolean("isMoving");
			isPerformingScan = JSONcontext.getBoolean("isPerformingScan");
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

}
