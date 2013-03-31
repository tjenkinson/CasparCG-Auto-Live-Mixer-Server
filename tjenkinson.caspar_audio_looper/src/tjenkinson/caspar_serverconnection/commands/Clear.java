package tjenkinson.caspar_serverconnection.commands;

public class Clear extends CaspCmd {
	
	public Clear(String cmdString) {
		setCmdString("CLEAR "+cmdString);
	}
}
