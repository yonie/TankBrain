public class TankBrain {

	static final String remoteHost = "localhost";
	static final int downstreamPort = 1360;
	static final int upstreamPort = 1359;
	static final String versionString = "0.1.0";
	static final String userName = "Ronald";
	static final int tankColorRed = 20;
	static final int tankColorGreen = 20;
	static final int tankColorBlue = 20;

	public static void main(String[] args) {
		System.out.println("DEBUG: Begin... ");
		Dispatcher dispatcher = new Dispatcher(remoteHost, downstreamPort,
				upstreamPort, versionString, userName, tankColorRed,
				tankColorGreen, tankColorBlue);
		System.out.println("DEBUG: End.");
	}
}
