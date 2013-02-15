public class Processor extends Thread {

	private Dispatcher dispatcher;
	private Rules rules;
	private boolean running;
	private int[][] heatMap;
	private Context latestContext;
	private int gridOffset;
	private int gridSize;
	private int heatMapSize;

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

		// TODO: dynamic array dimensions
		this.gridSize = 30;
		this.gridOffset = 15;
		this.heatMapSize = 30;

		heatMap = new int[heatMapSize][heatMapSize];
	}

	/**
	 * Updates the processor with the latest context.
	 * 
	 * @param context
	 *            the latest context.
	 */
	public void processContext(Context context) {

		latestContext = context;

		updateHeatMap();
	}

	/**
	 * Starts the main processor thread. This thread is responsible for doing
	 * all ongoing calculations, path finding and determining steps to take.
	 */
	public void run() {

		System.out.println("DEBUG: Running the thread...");

		while (running) {
			try {

				Thread.sleep(1000);

				// TODO: insert fancy tank ops here

				HeatMapCoordinate mostCrowdedPlace = findMostCrowdedPlace();
				HeatMapCoordinate quietPlace = findQuietPlace();
				HeatMapCoordinate randomPlace = findRandomPlace();

				int angle = (int) Math.round(calculateAngleTo(randomPlace));
				dispatcher.sendCommand(new Command("stop", "moving"));
				dispatcher.sendCommand(new Command("rotateTank", "" + angle));
				Thread.sleep(3000);
				dispatcher.sendCommand(new Command("moveForwardWithSpeed", "0.1"));

				dumpHeatMap();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Finds a random place on the heatMap
	 */
	private HeatMapCoordinate findRandomPlace() {
		int x = (int) (Math.random() * heatMapSize);
		int y = (int) (Math.random() * heatMapSize);
		System.out.println("DEBUG: Found random place at " + x + "," + y);
		return new HeatMapCoordinate(x, y);
	}

	/**
	 * Processes an CommandExecutionError that occurred during execution of a
	 * previous Command.
	 * 
	 * @param error
	 *            The CommandExecutionError to process.
	 */
	public void processError(CommandExecutionError error) {

		// TODO: do some fancy error handling here

	}

	/**
	 * Sets whether the processor thread should be running.
	 * 
	 * @param running
	 *            true if the processor thread should be running.
	 */
	public void setRunning(Boolean running) {
		this.running = running;
	}

	/*
	 * This method calculates the angle to get to a specific place
	 */
	private double calculateAngleTo(HeatMapCoordinate quietPlace) {
		double currentAngle = latestContext.getOwnTank().tankAngle;
		System.out.println("DEBUG: Current angle: " + currentAngle);
		HeatMapCoordinate currentPos = new HeatMapCoordinate(latestContext.getOwnTank().xpos,
				latestContext.getOwnTank().ypos, gridOffset, gridSize, heatMapSize);
		int distanceX = quietPlace.getX() - currentPos.getX();
		int distanceY = quietPlace.getY() - currentPos.getY();
		double newAngle = Math.toDegrees(Math.atan2(distanceX, distanceY));
		System.out.println("DEBUG: New angle: " + currentAngle);
		return newAngle - currentAngle;
	}

	/*
	 * This method updates the heatMap based on the latest available Context.
	 */
	private void updateHeatMap() {

		// save own coordinates in heatMap
		// FIXME: save other tanks' coordinates instead of my own

		HeatMapCoordinate myPos = new HeatMapCoordinate(latestContext.getOwnTank().xpos,
				latestContext.getOwnTank().ypos, gridOffset, gridSize, heatMapSize);

		System.out.println("DEBUG: Incrementing heatMap on [" + myPos.getX() + "][" + myPos.getY() + "]");
		heatMap[myPos.getX()][myPos.getY()] += 1;
	}

	/*
	 * This method returns the most crowded place (with most hits) in the
	 * heatMap. If more than one place has the same amount of hits, the first
	 * found place will be returned.
	 */
	private HeatMapCoordinate findMostCrowdedPlace() {
		int mostCrowdedPlaceX = 0;
		int mostCrowdedPlaceY = 0;

		for (int x = 0; x < heatMapSize; x++) {
			for (int y = 0; y < heatMapSize; y++) {
				if (heatMap[x][y] > heatMap[mostCrowdedPlaceX][mostCrowdedPlaceY]) {
					mostCrowdedPlaceX = x;
					mostCrowdedPlaceY = y;
				}
			}
		}
		System.out.println("DEBUG: Found most crowded place at " + mostCrowdedPlaceX + "," + mostCrowdedPlaceY);
		return new HeatMapCoordinate(mostCrowdedPlaceX, mostCrowdedPlaceY);
	}

	/*
	 * This method returns a quiet place (with least hits) in the heatMap. If
	 * more than one place has the same amount of hits, the last found place
	 * will be returned.
	 */
	private HeatMapCoordinate findQuietPlace() {
		int quietPlaceX = 0;
		int quietPlaceY = 0;

		for (int x = 0; x < heatMapSize; x++) {
			for (int y = 0; y < heatMapSize; y++) {
				if (heatMap[x][y] <= heatMap[quietPlaceX][quietPlaceY]) {
					quietPlaceX = x;
					quietPlaceY = y;
				}
			}
		}
		System.out.println("DEBUG: Found quiet place at " + quietPlaceX + "," + quietPlaceY);
		return new HeatMapCoordinate(quietPlaceX, quietPlaceY);
	}

	/*
	 * This method dumps the current heatMap to System.out for debug purposes.
	 */
	private void dumpHeatMap() {
		// dump the heatMap to system.out
		for (int x = 0; x < heatMapSize; x++) {
			System.out.print("DEBUG: x=" + x + "\t");
			for (int y = 0; y < heatMapSize; y++) {
				if (heatMap[x][y] == 0)
					System.out.print("[  ]");
				else
					System.out.print("[" + (heatMap[x][y] < 10 ? " " + heatMap[x][y] : heatMap[x][y]) + "]");
			}
			System.out.println();
		}
	}

}
