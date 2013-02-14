public class Processor extends Thread {

	private Dispatcher dispatcher;
	private Rules rules;
	private boolean running;
	private int[][] heatMap;
	private Context latestContext;
	private int gridOffsetX;
	private int gridOffsetY;
	private int gridSizeX;
	private int gridSizeY;
	private int heatMapSizeX;
	private int heatMapSizeY;

	/**
	 * @param args
	 */
	public Processor(Dispatcher dispatcher, Rules rules) {
		this.dispatcher = dispatcher;
		this.rules = rules;
		this.running = true;

		// TODO: dynamic array dimensions
		this.gridSizeX = 30;
		this.gridSizeY = 30;
		this.gridOffsetX = 15;
		this.gridOffsetY = 15;
		this.heatMapSizeX = 40;
		this.heatMapSizeY = 40;

		heatMap = new int[heatMapSizeX][heatMapSizeY];
	}

	public void processContext(Context context) {
		latestContext = context;

		// save own coordinates in heatmap
		int x = (int) Math.round(((context.getOwnTank().xpos + gridOffsetX) / gridSizeX) * heatMapSizeX);
		int y = (int) Math.round(((context.getOwnTank().ypos + gridOffsetY) / gridSizeY) * heatMapSizeY);
		System.out.println("DEBUG: Incrementing heatMap on [" + x + "][" + y + "]");
		heatMap[x][y] += 1;
	}

	public void run() {

		System.out.println("DEBUG: Running the thread...");

		while (running) {
			try {

				// TODO: insert fancy tank ops here

				dispatcher.sendCommand(new Command("moveForwardWithSpeed", "0.1"));
				Thread.sleep(3000);
				dispatcher.sendCommand(new Command("stop", "moving"));
				dispatcher.sendCommand(new Command("rotateTank", "90"));
				Thread.sleep(3000);

				// dump the heatMap to system.out
				for (int x = 0; x < heatMapSizeX; x++) {
					System.out.print("DEBUG: x=" + x + "\t");
					for (int y = 0; y < heatMapSizeY; y++) {
						if (heatMap[x][y]==0) System.out.print("[  ]");
						else System.out.print("["+(heatMap[x][y] < 10 ? " "+heatMap[x][y] : heatMap[x][y]) + "]");
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
