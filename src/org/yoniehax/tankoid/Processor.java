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
	private double rotationSpeedCorrection;

	// aggressiveness of the correction algorithm, lower is more aggressive
	final double correctionAggression = 10;

	// processor modes
	final int TEST = 0;
	final int RANDOM = 1;
	final int QUIET = 2;
	final int BUSY = 3;

	/**
	 * The <b>Processor</b> is the brains of the Tank. It determines where to
	 * move, whether to shoot, and so on. The processor keeps track of
	 * operations' time skew and corrects where necessary. To operate, the
	 * Processor needs a Dispatcher that is the communication link to the
	 * server, and game Rules to live by.
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
		this.mode = TEST;

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
						long rotationDuration = (long) pathToTraverse
								.getRotationDuration(100 + rotationSpeedCorrection);
						QuickLog.debug("Rotating for " + rotationDuration + " milliseconds...");
						dispatcher.sendCommand(new Command("rotateTankWithSpeed",
								(pathToTraverse.getRotationAngle() > 0 ? 1 : -1)));
						Thread.sleep(rotationDuration);
						dispatcher.sendCommand(new Command("stop", "tankRotation"));
					}

					// calibrate rotation speed if duration was long enough
					if (pathToTraverse.getRotationDuration() > 5000) {

						// allow for the latest tank updates to be processed
						Thread.sleep(1000);

						double plannedAngle = pathToTraverse.getRotationAngle();

						// compensate for positive/negative angles when turning
						if (plannedAngle < 0)
							plannedAngle += 360;
						double actualAngle = ownTank.getAngle() - pathToTraverse.getStartingAngle();
						if (actualAngle < 0)
							actualAngle += 360;

						// difference between planned and actual angles
						double rotationSpeedDifference = (((actualAngle - plannedAngle) / plannedAngle) * 100);
						QuickLog.info("Planned angle: " + plannedAngle + ", actual angle: " + actualAngle
								+ ", difference: " + rotationSpeedDifference + "%.");

						// update the correction
						rotationSpeedCorrection = rotationSpeedCorrection
								+ (rotationSpeedDifference / correctionAggression);
						QuickLog.info("New rotation speed correction: " + rotationSpeedCorrection + "%");
					}

					// we only move if we are not nearby our target place
					if (!startingPlace.isNearby(targetPlace)) {
						long movementDuration = (long) pathToTraverse
								.getMovementDuration(100 + movementSpeedCorrection);
						QuickLog.debug("Moving for " + movementDuration + " milliseconds...");
						dispatcher.sendCommand(new Command("moveForwardWithSpeed", 1));
						Thread.sleep(movementDuration);
						dispatcher.sendCommand(new Command("stop", "moving"));

						// calibrate movement speed if duration was long enough
						if (pathToTraverse.getMovementDuration() > 5000) {

							// allow for the latest tank updates to be processed
							Thread.sleep(1000);

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
							movementSpeedCorrection = movementSpeedCorrection
									+ (movementSpeedDifference / correctionAggression);
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
		if (mode == TEST) {
			// have the tank alternate between x=800 and x=200
			if (targetPlace == null)
				targetPlace = new Place(500, 500);
			else if (targetPlace.getX() < 500)
				targetPlace = new Place(800, 500);
			else
				targetPlace = new Place(200, 500);
		} else if (mode == RANDOM)
			targetPlace = heatMap.findRandomPlace();
		else if (mode == QUIET)
			targetPlace = heatMap.findQuietPlace();
		else
			targetPlace = heatMap.findMostCrowdedPlace();
	}

}
