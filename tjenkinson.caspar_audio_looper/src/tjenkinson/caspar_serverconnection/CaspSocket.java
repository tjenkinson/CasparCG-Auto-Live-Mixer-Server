package tjenkinson.caspar_serverconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import tjenkinson.caspar_serverconnection.commands.CaspCmd;
import tjenkinson.caspar_serverconnection.commands.CaspReturn;

public class CaspSocket {
	
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	
	public CaspSocket(String caspAddress, int caspPort) throws IOException {
		socket = new Socket(InetAddress.getByName(caspAddress), caspPort);
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));	
	}
	
	
	public synchronized CaspReturn runCmd(CaspCmd cmd) throws IOException {
		out.println(cmd.getCmdString());
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
			response = in.readLine();
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
