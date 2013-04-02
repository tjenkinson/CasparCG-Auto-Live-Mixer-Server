/*
 * Run the casper server and then CasparLiveAutoMixerServer.jar first.
 * 
 * The live auto mixer server requires the following parameters:
 * [caspar address] [caspar port] [caspar channel] [caspar layer 1] [caspar layer 2] [caspar layer 3] [caspar layer 4] [live auto mixer port][audio structure xml file path]
 * 
 * The following is an example:
 * CasparLiveAutoMixerServer.jar 127.0.0.1 5250 1 5 6 7 8 5150 "demo.xml"
 */

package tjenkinson.caspar_auto_live_mixer_demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Demo {

	
	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		
		Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        int audioLooperPort = 5150;
        String audioLooperAddress = "127.0.0.1";
        // Setup the socket to connect to the audio looper server
        try {
            socket = new Socket(audioLooperAddress, audioLooperPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.exit(1);
        } catch (IOException e) {
            System.exit(1);
        }

        out.println("ADD 0 1 0");
        System.out.println("return code: "+in.readLine());
        out.println("ADD 1 0 1");
        System.out.println("return code: "+in.readLine());
        pressEnter();
        
        out.println("ADD 2 1 0");
        System.out.println("return code: "+in.readLine());
        out.println("ADD 3 0 1");
        System.out.println("return code: "+in.readLine());
        pressEnter();
        
        out.println("ADD 4 1 0");
        System.out.println("return code: "+in.readLine());
        out.println("ADD 5 0 1");
        System.out.println("return code: "+in.readLine());
        pressEnter();
        
        out.println("ADD 6 1 0");
        System.out.println("return code: "+in.readLine());
        out.println("ADD 7 0 1");
        System.out.println("return code: "+in.readLine());
        pressEnter();
        
        out.println("ADD 8 1 0");
        System.out.println("return code: "+in.readLine());
        out.println("ADD 9 0 1");
        System.out.println("return code: "+in.readLine());
        pressEnter();
        
        out.println("ADD 10 1 0");
        System.out.println("return code: "+in.readLine());
        out.println("ADD 11 0 1");
        System.out.println("return code: "+in.readLine());
        pressEnter();
        
        out.println("ADD 12 1 0");
        System.out.println("return code: "+in.readLine());
        out.println("ADD 13 0 1");
        System.out.println("return code: "+in.readLine());
        pressEnter();
        
        out.println("ADD 14 1 0");
        System.out.println("return code: "+in.readLine());
        
        socket.close();
	}
	
	private static void pressEnter() throws IOException, InterruptedException {
		System.out.println("Moving on at next breakpoint! Press enter to continue");
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		bufferRead.readLine();
	}
}
