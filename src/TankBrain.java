public class TankBrain {

	// the remote host at which the server resides
	static final String remoteHost = "localhost";

	// the port to use for downstream communication
	static final int downstreamPort = 1360;

	// the port to use for upstream communication
	static final int upstreamPort = 1359;

	// version string
	static final int majorVersion = 0;
	static final int minorVersion = 2;
	static final int revisionVersion = 0;

	// the user name to log on to the system
	static final String userName = "Ronald";

	// tank colors
	static final int tankColorRed = 20;
	static final int tankColorGreen = 20;
	static final int tankColorBlue = 20;

	public static void main(String[] args) {
		System.out.println("DEBUG: Begin...");
		Dispatcher dispatcher = new Dispatcher(remoteHost, downstreamPort,
				upstreamPort, majorVersion, minorVersion, revisionVersion,
				userName, tankColorRed, tankColorGreen, tankColorBlue);
		System.out.println("DEBUG: End.");
	}
}
