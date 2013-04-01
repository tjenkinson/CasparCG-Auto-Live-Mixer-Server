package tjenkinson.caspar_audio_looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketManager {

	private ServerSocket serverSocket = null;
	private Server mainProgramObj = null;
	
	public ServerSocketManager(Server mainProgramObj, int port) {
		this.mainProgramObj = mainProgramObj;
		try {
		    serverSocket = new ServerSocket(port);
		} 
		catch (IOException e) {
			// TODO: do something better than exiting
		    System.exit(1);
		}
		
		// keep accepting new connections
		while(true) {
			try {
				// gets socket to client when client connects and hands this over to a new thread. then waits for any more connections
				(new Thread(new ClientSocketConnection(serverSocket.accept()))).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class ClientSocketConnection implements Runnable {
		
		private Socket client = null;
		private PrintWriter out = null;
		private BufferedReader in = null;
		
		public ClientSocketConnection(Socket client) {
			this.client = client;
			try {
				out = new PrintWriter(this.client.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			
			String inputLine = null;
			try {
				while ((inputLine = in.readLine()) != null) {   
				    // structure: [command type:string] params
					
					int returnCode = 1;
					String[] tokens = inputLine.split("[ ]+");
					if (tokens.length >= 1) {
						// not sure why it only works with .equals instead of == . if someone could explain that would be great!
						if (tokens[0].equals("ADD") && tokens.length == 4) {
							// structure: ADD [section no:int] [play count:int] [keep playing:1|0]
							Action action = mainProgramObj.createAction(Integer.parseInt(tokens[1], 10), Integer.parseInt(tokens[2], 10), tokens[3].equals(("1")));
							// if valid
							if (action != null) {
								mainProgramObj.addAction(action);
								returnCode = 0;
							}
						}
						else if (tokens[0].equals("CLEAR")) {
							mainProgramObj.clearActions();
							returnCode = 0;
						}
					}
					out.println(returnCode);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// client has closed the socket
			out.close();
			try {
				in.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
