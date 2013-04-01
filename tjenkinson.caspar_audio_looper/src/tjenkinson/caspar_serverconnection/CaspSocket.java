package tjenkinson.caspar_serverconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import tjenkinson.caspar_serverconnection.commands.CaspCmd;
import tjenkinson.caspar_serverconnection.commands.CaspReturn;

public class CaspSocket {
	
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private String caspAddress = null;
	private int caspPort = -1;
	private boolean connected = false;
	
	public CaspSocket(String caspAddress, int caspPort) throws IOException {
		this.caspAddress = caspAddress;
		this.caspPort = caspPort;
		open(); // make connection
	}
	
	private synchronized void open() throws UnknownHostException, IOException {
		if (connected) {
			return;
		}
		socket = new Socket(InetAddress.getByName(caspAddress), caspPort);
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		connected = true;
	}
	
	public synchronized void close() throws IOException {
		if (!connected) {
			return;
		}
		out.close();
		in.close();
		socket.close();
		socket = null;
		in = null;
		out = null;
		connected = false;
	}
	
	
	public synchronized CaspReturn runCmd(CaspCmd cmd) throws IOException {
		open(); // connect if not already
		out.println(new String(cmd.getCmdString().getBytes("UTF-8"))); // send command through socket
		long requestTime = new Date().getTime();
		
		String line = in.readLine();
		Matcher myMatcher = Pattern.compile("^\\d+").matcher(line);
		myMatcher.find();
		int status = Integer.parseInt(line.substring(myMatcher.start(), myMatcher.end()));
		
		Pattern endSequence = null;
		String response = null;
		Boolean getResponse = true;
		int charsRemove = 0;
		
		if (status == 200) { // several lines followed by empty line
			endSequence = Pattern.compile("\\r\\n\\r\\n$");
			charsRemove = 4;
		}
		else if (status == 201) { // one line of data returned
			endSequence = Pattern.compile("\\r\\n$");
			charsRemove = 2;
		}
		else {
			getResponse = false;
		}
		
		if (getResponse) {
			response = "";
			while(true) {
				char character = (char) in.read();
				response += character;
				if (endSequence.matcher(response).find()) { // if matched end sequence
					break;
				}		
			}
			response = response.substring(0, response.length()-charsRemove);
		}
		
		return new CaspReturn(status, response, requestTime);
	}
}
