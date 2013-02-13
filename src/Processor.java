public class Processor extends Thread {

	private Dispatcher dispatcher;
	private boolean running;
	private int moveSpeed;
	private int rotationSpeed;
	private int turretRotationSpeed;
	private int fireInterval;
	private int ballisticsTravelSpeed;
	private int fieldOfView;
	private int turretFieldOfView;
	private int hitPoints;
	private int ballisticDamage;
	private int enemyHitScore;
	private int enemyKillScore;
	private int[] heatMap;

	/**
	 * @param args
	 */
	public Processor(Dispatcher dispatcher, int moveSpeed, int rotationSpeed, int turretRotationSpeed,
			int fireInterval, int ballisticsTravelSpeed, int fieldOfView, int turretFieldOfView, int hitPoints,
			int ballisticDamage, int enemyHitScore, int enemyKillScore) {
		this.dispatcher = dispatcher;
		this.moveSpeed = moveSpeed;
		this.rotationSpeed = rotationSpeed;
		this.turretRotationSpeed = turretRotationSpeed;
		this.fireInterval = fireInterval;
		this.ballisticsTravelSpeed = ballisticsTravelSpeed;
		this.fieldOfView = fieldOfView;
		this.turretFieldOfView = turretFieldOfView;
		this.hitPoints = hitPoints;
		this.ballisticDamage = ballisticDamage;
		this.enemyHitScore = enemyHitScore;
		this.enemyKillScore = enemyKillScore;
		this.running = true;
	}

	public void processContext(Context context) {

		// TODO: update private stats based on context

		// TODO: update heatmap

	}
	
	public void run() {

		// TODO: insert fancy tank ops here

		System.out.println("DEBUG: running the thread...");
		while (running) {
			try {
				dispatcher.sendCommand(new Command("moveForwardWithSpeed", "0.1"));
				dispatcher.sendCommand(new Command("rotateTank", "125"));
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
