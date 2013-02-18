public class Processor extends Thread {

	private Dispatcher dispatcher;
	private Rules rules;
	private boolean running;
	private HeatMap heatMap;
	private Context lastRecievedContext;
	private int heatMapSize;
	private double ownTankSpeed = 0.1;

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

		// TODO: dynamic array dimensions based on server input
		this.heatMapSize = 30;

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
			if (lastRecievedContext != null) {

				try {

					// TODO: insert fancy tank ops here

					Place mostCrowdedPlace = heatMap.findMostCrowdedPlace();
					Place quietPlace = heatMap.findQuietPlace();
					Place randomPlace = heatMap.findRandomPlace();

					Path pathToTraverse = new Path(lastRecievedContext.getOwnTank().getPlace(), randomPlace,
							lastRecievedContext.getOwnTank().getAngle(), rules.getMovementSpeed(),
							rules.getRotationSpeed());

					dispatcher.sendCommand(new Command("stop", "moving"));
					dispatcher.sendCommand(new Command("rotateTank", pathToTraverse.getRotationAngle()));
					System.out.println("DEBUG: Path stats: From/to is " + pathToTraverse.getStartingPlace() + ", "
							+ pathToTraverse.getDestinationPlace());
					System.out.println("DEBUG: Path stats: Rotation angle " + pathToTraverse.getRotationAngle()
							+ ", rotation duration " + pathToTraverse.getRotationDuration());

					Thread.sleep((long) Math.ceil(pathToTraverse.getRotationDuration() * 1000));

					dispatcher.sendCommand(new Command("moveForwardWithSpeed", pathToTraverse.getMovementSpeed()));
					System.out.println("DEBUG: Path stats: Movement speed " + pathToTraverse.getMovementSpeed()
							+ ", movement duration " + pathToTraverse.getMovementDuration());
					Thread.sleep((long) Math.ceil(pathToTraverse.getMovementDuration() * 1000));

					// heatMap.dumpHeatMapToSysOut();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else
				System.out.println("DEBUG: Threads not yet in sync.");
		}
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
		dispatcher.sendCommand(new Command("moveBackwardWithSpeed", ownTankSpeed));

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
