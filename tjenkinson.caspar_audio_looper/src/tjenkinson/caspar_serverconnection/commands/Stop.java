package tjenkinson.caspar_serverconnection.commands;

public class Stop extends CaspCmd {
	
	public Stop(String cmdString) {
		setCmdString("STOP "+cmdString);
	}
}
