package tjenkinson.caspar_auto_live_mixer;

import java.io.IOException;

public class Driver {

	
	public static void main(String[] args) throws IOException {
		if (args.length != 9) {
			System.exit(1);
		}
		// args[0] caspar server address
		// args[1] caspar server port
		// args[2] caspar channel
		// args[3] caspar layer 1
		// args[4] caspar layer 2
		// args[5] caspar layer 3
		// args[6] caspar layer 4
		// args[7] audioLooper port
		// args[8] should contain the path to the xml file containing the sections
		
		new Server(args[0], Integer.parseInt(args[1], 10), Integer.parseInt(args[2], 10), Integer.parseInt(args[3], 10), Integer.parseInt(args[4], 10), Integer.parseInt(args[5], 10), Integer.parseInt(args[6], 10), Integer.parseInt(args[7], 10), args[8]);
	}

}
