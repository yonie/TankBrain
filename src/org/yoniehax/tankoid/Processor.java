package org.yoniehax.tankoid;

import org.yoniehax.helper.QuickLog;

public class Processor extends Thread {

	private Dispatcher dispatcher;
	private Rules rules;
	private boolean running;
	private HeatMap heatMap;
	private Tank ownTank;
	private int heatMapSize;
	private int mode;
	private Place targetPlace;

	private double movementSpeedCorrection;

	// processor modes
	final int TEST = 0;
	final int RANDOM = 1;
	final int QUIET = 2;
	final int BUSY = 3;

	/**
	 * The <b>Processor</b> is the brains of the Tank. It determines where to
	 * move, whether to shoot, and so on. To operate the Processor needs a
	 * Dispatcher that is the communication link to the server, and game Rules
	 * to live by.
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

		// TODO: dynamic array dimensions based on server
		this.heatMapSize = 1001;

		heatMap = new HeatMap(heatMapSize);
	}

	/**
	 * Updates the processor with the latest game StatusUpdate.
	 * 
	 * @param statusUpdate
	 *            the latest StatusUpdate.
	 */
	public void processTankStatusUpdate(Tank ownTank) {

		this.ownTank = ownTank;

		// save own coordinates in heatMap
		heatMap.increment(ownTank.getPlace());
	}

	/**
	 * Starts the main Processor thread. This thread is responsible for doing
	 * all ongoing calculations, Path finding and determining steps to take.
	 */
	public void run() {

		QuickLog.debug("Running the thread...");

		while (running) {

			try {

				if (ownTank != null) {

					Place startingPlace = ownTank.getPlace();
					double startingAngle = ownTank.getAngle();

					// check if we need to set a new target
					while (targetPlace == null || targetPlace.isNearby(startingPlace)) {
						QuickLog.info("Setting new target!");
						setNewTarget();
					}

					// determine path, include speed correction
					Path pathToTraverse = new Path(startingPlace, targetPlace, startingAngle, rules.getMovementSpeed()
							* (1 - (movementSpeedCorrection / 100)), rules.getRotationSpeed());

					QuickLog.info("Path to traverse: " + pathToTraverse);

					// we only rotate for angles bigger than 5 degrees
					if (pathToTraverse.getRotationAngle() > 5 || pathToTraverse.getRotationAngle() < 5) {
						QuickLog.debug("Rotating for " + pathToTraverse.getRotationDuration() / 1000 + " seconds...");
						dispatcher.sendCommand(new Command("rotateTankWithSpeed",
								(pathToTraverse.getRotationAngle() > 0 ? 1 : -1)));
						Thread.sleep((long) (pathToTraverse.getRotationDuration()));
						dispatcher.sendCommand(new Command("stop", "tankRotation"));
					}

					// we only move if we are not nearby our target place
					if (!startingPlace.isNearby(targetPlace)) {
						QuickLog.debug("Moving for " + pathToTraverse.getMovementDuration() / 1000 + " seconds...");
						dispatcher.sendCommand(new Command("moveForwardWithSpeed", 1));
						Thread.sleep((long) (pathToTraverse.getMovementDuration()));
						dispatcher.sendCommand(new Command("stop", "moving"));

						// calibrate movement speed
						if (pathToTraverse.getMovementDuration() > 5000) {

							// allow for the latest tank update to be processed
							Thread.sleep(250);

							Place endingPlace = ownTank.getPlace();
							Path pathActuallyTraversed = new Path(startingPlace, endingPlace, startingAngle,
									rules.getMovementSpeed(), rules.getRotationSpeed());

							// difference between planned and actual movement
							double movementSpeedDifference = (((pathActuallyTraversed.getDistance() - pathToTraverse
									.getDistance()) / pathToTraverse.getDistance()) * 100);
							QuickLog.info("Planned movement: " + pathToTraverse.getDistance() + ", actual movement: "
									+ pathActuallyTraversed.getDistance() + ", difference: " + movementSpeedDifference
									+ "%.");

							// update the correction
							movementSpeedCorrection = movementSpeedCorrection + (movementSpeedDifference / 10);
							QuickLog.info("New movement speed correction: " + movementSpeedCorrection + "%");
						}
					}

				} else {

					// we did not yet receive any tank update yet
					QuickLog.debug("Still waiting to receive initial tank update...");
					Thread.sleep(1000);

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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

		QuickLog.debug("Processing error with reason: " + error.getReason());

		// TODO: do some fancy error handling here

	}

	/**
	 * Sets the mode the processor uses. This can be either <i>TEST</i>,
	 * <i>RANDOM</i>, <i>QUIET</i> or <i>BUSY</i>.
	 * 
	 * @param mode
	 *            the mode the processor uses
	 */
	public void setMode(int mode) {
		this.mode = mode;
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

	/*
	 * Sets a new target based on the current mode.
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

}
