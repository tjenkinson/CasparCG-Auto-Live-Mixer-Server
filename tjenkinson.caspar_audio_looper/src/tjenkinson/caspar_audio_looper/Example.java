/*
 * Run the casper server and then audio looper server first.
 * 
 * The audio looper server requires the following parameters:
 * [caspar address] [caspar port] [caspar channel] [caspar layer 1] [caspar layer 2] [caspar layer 3] [caspar layer 4] [audio looper port][audio structure xml file path]
 * 
 * The following is an example:
 * java Driver.java 127.0.0.1 5250 1 5 6 7 8 5150 "examplexml.xml"
 */

package tjenkinson.caspar_audio_looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Example {

	
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
        out.println("ADD 1 1 1");
        System.out.println("return code: "+in.readLine());
        pressEnter();
        out.println("ADD 2 1 0");
        System.out.println("return code: "+in.readLine());
        out.println("ADD 1 1 1");
        System.out.println("return code: "+in.readLine());
        pressEnter();
        out.println("ADD 3 1 0");
        System.out.println("return code: "+in.readLine());
        socket.close();
	}
	
	private static void pressEnter() throws IOException, InterruptedException {
		System.out.println("Press enter to continue");
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		bufferRead.readLine();
	}
}
