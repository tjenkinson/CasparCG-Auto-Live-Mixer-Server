package tjenkinson.caspar_serverconnection.commands;

public class Pause extends CaspCmd {
	
	public Pause(String cmdString) {
		setCmdString("PAUSE "+cmdString);
	}
}
