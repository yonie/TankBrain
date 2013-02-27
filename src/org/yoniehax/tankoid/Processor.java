package org.yoniehax.tankoid;

import org.yoniehax.helper.QuickLog;

public class Processor extends Thread {

	private Dispatcher dispatcher;
	private Rules rules;
	private boolean running;
	private HeatMap heatMap;
	private StatusUpdate lastRecievedContext;
	private int heatMapSize;
	private int mode;
	private Place targetPlace;

	private double rotationSpeedCorrection;
	private double movementSpeedCorrection;

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
		this.heatMapSize = 1000;

		heatMap = new HeatMap(heatMapSize);
	}

	/**
	 * Updates the processor with the latest game Context.
	 * 
	 * @param context
	 *            the latest Context.
	 */
	public void processStatusUpdate(StatusUpdate context) {

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

		QuickLog.debug("Running the thread...");

		while (running) {

			try {

				if (lastRecievedContext != null) {

					Place startingPlace = lastRecievedContext.getOwnTank().getPlace();
					double startingAngle = lastRecievedContext.getOwnTank().getAngle();

					while (targetPlace == null || targetPlace.isNearby(startingPlace)) {
						QuickLog.info("Setting new target!");
						setNewTarget();
					}

					// make patch, include speed correction
					Path pathToTraverse = new Path(startingPlace, targetPlace, startingAngle, rules.getMovementSpeed()
							* (1 + (movementSpeedCorrection / 100)), rules.getRotationSpeed()
							* (1 + (rotationSpeedCorrection / 100)));

					QuickLog.debug("Path to traverse: " + pathToTraverse);

					if (pathToTraverse.getRotationAngle() > 5 || pathToTraverse.getRotationAngle() < 5) {
						QuickLog.debug("Rotating for " + pathToTraverse.getRotationDuration() / 1000 + " seconds...");
						dispatcher.sendCommand(new Command("rotateTankWithSpeed",
								(pathToTraverse.getRotationAngle() > 0 ? 1 : -1)));
						Thread.sleep((long) (pathToTraverse.getRotationDuration()));
						dispatcher.sendCommand(new Command("stop", "tankRotation"));

						// calibrate rotation speed
						if (pathToTraverse.getRotationDuration() > 5000) {

							// allow for the latest context to be sent
							Thread.sleep(500);

							Place endingPlace = lastRecievedContext.getOwnTank().getPlace();
							Path pathActuallyTraversed = new Path(startingPlace, endingPlace, startingAngle,
									rules.getMovementSpeed(), rules.getRotationSpeed());

							double rotationSpeedDifference = (((pathActuallyTraversed.getRotationAngle() - pathToTraverse
									.getRotationAngle()) / pathToTraverse.getRotationAngle()) * 100);
							QuickLog.info("Planned angle: " + pathToTraverse.getRotationAngle() + ", actual angle: "
									+ pathActuallyTraversed.getRotationAngle() + ", difference: "
									+ rotationSpeedDifference + "%.");
							rotationSpeedCorrection = rotationSpeedCorrection + (rotationSpeedDifference / 10);
							QuickLog.info("New rotation speed correction: " + rotationSpeedCorrection + "%");
						}

					}

					if (!startingPlace.isNearby(targetPlace)) {
						QuickLog.debug("Moving for " + pathToTraverse.getMovementDuration() / 1000 + " seconds...");
						dispatcher.sendCommand(new Command("moveForwardWithSpeed", 1));
						Thread.sleep((long) (pathToTraverse.getMovementDuration()));
						dispatcher.sendCommand(new Command("stop", "moving"));

						// calibrate movement speed
						if (pathToTraverse.getMovementDuration() > 5000) {

							// allow for the latest context to be sent
							Thread.sleep(500);

							Place endingPlace = lastRecievedContext.getOwnTank().getPlace();
							Path pathActuallyTraversed = new Path(startingPlace, endingPlace, startingAngle,
									rules.getMovementSpeed(), rules.getRotationSpeed());

							double movementSpeedDifference = (((pathActuallyTraversed.getDistance() - pathToTraverse
									.getDistance()) / pathToTraverse.getDistance()) * 100);
							QuickLog.info("Planned movement: " + pathToTraverse.getDistance() + ", actual movement: "
									+ pathActuallyTraversed.getDistance() + ", difference: " + movementSpeedDifference
									+ "%.");
							movementSpeedCorrection = movementSpeedCorrection + (movementSpeedDifference / 10);
							QuickLog.info("New movement speed correction: " + movementSpeedCorrection + "%");
						}
					}

				} else {
					QuickLog.debug("Threads not yet in sync. Waiting 1 second...");
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
			targetPlace = new Place(500, 500);
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

		QuickLog.debug("Processing error with reason: " + error.getReason());

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
