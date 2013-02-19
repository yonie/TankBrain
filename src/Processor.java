public class Processor extends Thread {

	private Dispatcher dispatcher;
	private Rules rules;
	private boolean running;
	private HeatMap heatMap;
	private Context lastRecievedContext;
	private int heatMapSize;
	private int mode;
	private Place targetPlace;

	final int TEST = 0;
	final int RANDOM = 1;
	final int QUIET = 2;
	final int BUSY = 3;

	/**
	 * The Processor is the brains of the tank. It determines where to move,
	 * whether to shoot, and so on. To operate the Processor needs a Dispatcher
	 * that is the communication link to the server, and game Rules to live by.
	 * 
	 * @param dispatcher
	 *            The Dispatcher to send Commands to.
	 * @param rules
	 *            Game rules the Processor should live by.
	 */
	public Processor(Dispatcher dispatcher, Rules rules) {
		this.dispatcher = dispatcher;
		this.rules = rules;
		this.running = true;
		this.mode = RANDOM;

		// TODO: dynamic array dimensions based on server grid
		this.heatMapSize = 50;

		heatMap = new HeatMap(heatMapSize);
	}

	/**
	 * Updates the processor with the latest game Context.
	 * 
	 * @param context
	 *            the latest Context.
	 */
	public void processContext(Context context) {

		lastRecievedContext = context;

		// TODO: save other tanks' coordinates instead of my own

		// save coordinates in heatMap
		heatMap.increment(lastRecievedContext.getOwnTank().getPlace());
	}

	/**
	 * Starts the main Processor thread. This thread is responsible for doing
	 * all ongoing calculations, Path finding and determining steps to take.
	 */
	public void run() {

		System.out.println("DEBUG: Running the thread...");

		while (running) {

			try {
				if (lastRecievedContext != null) {

					Place currentPlace = lastRecievedContext.getOwnTank().getPlace();
					double currentAngle = lastRecievedContext.getOwnTank().getAngle();

					if (targetPlace == null || targetPlace.isNearby(currentPlace)) {
						System.out.println("DEBUG: Setting new target!");
						setNewTarget();
					}

					Path pathToTraverse = new Path(currentPlace, targetPlace, currentAngle, rules.getMovementSpeed(),
							rules.getRotationSpeed());

					System.out.println("DEBUG: Path to traverse: " + pathToTraverse);

					dispatcher.sendCommand(new Command("rotateTank", pathToTraverse.getRotationAngle()));
					Thread.sleep(Math.min(Math.round(pathToTraverse.getRotationDuration()), 1000));

					dispatcher.sendCommand(new Command("moveForwardWithSpeed", 0.345));
					Thread.sleep(Math.min(Math.round(pathToTraverse.getMovementDuration()), 1000));

					dispatcher.sendCommand(new Command("stop", "moving"));

				} else {
					System.out.println("DEBUG: Threads not yet in sync. Waiting 1 second...");
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();

			}
		}
	}

	/**
	 * 
	 * @param mode
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * 
	 * @return
	 */
	private void setNewTarget() {
		if (mode == TEST)
			targetPlace = new Place(10, 0);
		else if (mode == RANDOM)
			targetPlace = heatMap.findRandomPlace();
		else if (mode == QUIET)
			targetPlace = heatMap.findQuietPlace();
		else
			targetPlace = heatMap.findMostCrowdedPlace();
	}

	/**
	 * Processes an CommandExecutionError that occurred during execution of a
	 * previous Command.
	 * 
	 * @param error
	 *            The CommandExecutionError to process.
	 */
	public void processError(CommandExecutionError error) {

		System.out.println("DEBUG: Processing error with reason: " + error.getReason());

		// TODO: do some fancy error handling here

	}

	/**
	 * Sets whether the Processor thread should be running.
	 * 
	 * @param running
	 *            true if the processor thread should be running.
	 */
	public void setRunning(Boolean running) {
		this.running = running;
	}

}
