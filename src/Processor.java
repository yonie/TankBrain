public class Processor extends Thread {

	private Dispatcher dispatcher;
	private Rules rules;
	private boolean running;
	private int[][] heatMap;
	private Context latestContext;
	int offsetX;
	int offsetY;
	int gridX;
	int gridY;

	/**
	 * @param args
	 */
	public Processor(Dispatcher dispatcher, Rules rules) {
		this.dispatcher = dispatcher;
		this.rules = rules;
		this.running = true;

		// TODO: dynamic array dimensions
		this.gridX = 20;
		this.gridY = 20;
		this.offsetX = 10;
		this.offsetY = 10;

		heatMap = new int[gridX + offsetX][gridY + offsetY];
	}

	public void processContext(Context context) {
		latestContext = context;
		// TODO: determine proper offset & granularity
		int x = (int) Math.round(context.getOwnTank().xpos) + offsetX;
		int y = (int) Math.round(context.getOwnTank().ypos) + offsetY;
		heatMap[x][y] += 1;
	}

	public void run() {

		System.out.println("DEBUG: Running the thread...");

		while (running) {
			try {

				// TODO: insert fancy tank ops here

				dispatcher.sendCommand(new Command("moveForwardWithSpeed", "0.1"));
				dispatcher.sendCommand(new Command("rotateTank", "125"));
				Thread.sleep(1000);
				
				// dump the heatMap to system.out
				for (int x = 0; x < gridX; x++) {
					System.out.print("DEBUG: x=[" + x + "]\t");
					for (int y = 0; y < gridY; y++) {
						System.out.print("[" + (heatMap[x][y] != 0 ? heatMap[x][y] : " ") + "]");
					}
					System.out.println();
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void processError(CommandExecutionError error) {

		// TODO: do some fancy error handling here

	}

	public void setRunning(Boolean running) {
		this.running = running;
	}

}
