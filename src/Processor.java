public class Processor extends Thread {

	private Dispatcher dispatcher;
	private Rules rules;
	private boolean running;
	private int[][] heatMap;
	private Context latestContext;
	int offset;

	/**
	 * @param args
	 */
	public Processor(Dispatcher dispatcher, Rules rules) {
		this.dispatcher = dispatcher;
		this.rules = rules;
		this.running = true;

		// TODO: dynamic array dimensions
		this.offset = 1000;
		heatMap = new int[500 + offset][500 + offset];
	}

	public void processContext(Context context) {
		latestContext = context;
		// TODO: determine proper offset & granularity
		int x = (int) Math.round(context.getOwnTank().xpos) + offset;
		int y = (int) Math.round(context.getOwnTank().ypos) + offset;
		heatMap[x][y] += 1;
	}

	public void run() {

		// TODO: insert fancy tank ops here

		System.out.println("DEBUG: running the thread...");
		while (running) {
			try {
				dispatcher.sendCommand(new Command("moveForwardWithSpeed", "0.1"));
				dispatcher.sendCommand(new Command("rotateTank", "125"));
				for (int x = 0; x < 500 + offset; x++) {
					for (int y = 0; y < 500 + offset; y++) {
						if (heatMap[x][y] > 0)
							System.out.println("DEBUG: heatMap on [" + x + "][" + y + "] is " + heatMap[x][y]);
					}
				}
				Thread.sleep(1000);
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
