package org.yoniehax.tankoid;

import java.text.DecimalFormat;

import org.yoniehax.helper.QuickLog;

public class Processor extends Thread {

	private Dispatcher dispatcher;
	private Rules rules;
	private boolean running;
	private HeatMap heatMap;
	private Tank ownTank;
	private int mapSize;
	private int mode;
	private Place targetPlace;

	private double movementSpeedCorrection;
	private double rotationSpeedCorrection;

	private double rotationThrottle;
	private double moveThrottle;

	// aggressiveness of the correction algorithm, lower is more aggressive
	final double correctionAggression = 10;

	// determines how often the processor calculates commands, in seconds
	final double clock = 0.1;

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
		this.mode = RANDOM;
	}

	/**
	 * Sets the game map dimensions.
	 * 
	 * @param x
	 *            width of the game map.
	 * @param y
	 *            height of the game map.
	 */
	public void setMapSize(int size) {
		this.mapSize = size;
		heatMap = new HeatMap(mapSize, 10);
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

					// set initial target
					if (targetPlace == null) {
						QuickLog.info("Setting initial target!");
						setNewTarget();
					}

					// check if we need to set a new target
					while (targetPlace.isNearby(startingPlace)) {
						QuickLog.info("Target place (" + targetPlace + ") nearby: (" + startingPlace
								+ "). Setting new target!");
						setNewTarget();
					}

					// TODO: remove speed, duration, speed correction etc.

					// determine path, include speed correction
					Path pathToTraverse = new Path(startingPlace, targetPlace, startingAngle, rules.getMovementSpeed()
							* (1 - (movementSpeedCorrection / 100)), rules.getRotationSpeed());

					QuickLog.debug("Path to traverse: " + pathToTraverse);

					// throttles based on distance / angle to still cover
					rotationThrottle = Math.min(1, Math.abs(pathToTraverse.getRotationAngle())
							/ (clock * pathToTraverse.getRotationSpeed()));
					moveThrottle = Math.min(1, Math.min(
							1 / (Math.abs(pathToTraverse.getRotationAngle() / pathToTraverse.getRotationSpeed())),
							(pathToTraverse.getDistance() / (pathToTraverse.getMovementSpeed() * clock * 2))));

					QuickLog.debug("Rotation throttle: " + new DecimalFormat("#.##").format(rotationThrottle)
							+ ", move throttle: " + new DecimalFormat("#.##").format(moveThrottle));

					dispatcher.sendCommand(new Command("rotateTankWithSpeed",
							(pathToTraverse.getRotationAngle() > 0 ? rotationThrottle : -rotationThrottle)));
					dispatcher.sendCommand(new Command("moveForwardWithSpeed", moveThrottle));
					Thread.sleep((long) Math.round(clock * 1000));
					dispatcher.sendCommand(new Command("stop", "tankRotation"));
					dispatcher.sendCommand(new Command("stop", "moving"));

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

	/**
	 * Returns the player's Tank object.
	 * 
	 * @return the player's Tank object.
	 */
	public Tank getOwnTank() {
		return ownTank;
	}

	/**
	 * Returns the current size of the game map.
	 * 
	 * @return the current size of the game map.
	 */
	public int getMapSize() {
		return mapSize;
	}

	/**
	 * Returns the current target.
	 * 
	 * @return the current target.
	 */
	public Place getTargetPlace() {
		return targetPlace;
	}

	/**
	 * Returns the latest rotation throttle.
	 * 
	 * @return the latest rotation throttle.
	 */
	public double getRotationThrottle() {
		return rotationThrottle;
	}

	/**
	 * Returns the latest movement throttle.
	 * 
	 * @return the latest movement throttle.
	 */
	public double getMoveThrottle() {
		return moveThrottle;
	}

	/**
	 * Returns the current heatMap.
	 * 
	 * @return the current heatMap.
	 */
	public HeatMap getHeatMap() {
		return heatMap;
	}
}
