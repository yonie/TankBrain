public class Processor {

	/**
	 * @param args
	 */
	public Processor() {

	}

	public Command processContext(Context context) {
		// TODO: insert fancy tank ops here
		if (!context.isMoving()) {
			return new Command("moveForwardWithSpeed","1.0");
		}
		else return new Command("rotateTank","45");
	}
	
}
