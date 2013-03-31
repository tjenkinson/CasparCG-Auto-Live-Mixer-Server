package tjenkinson.caspar_serverconnection.commands;

public class Add extends CaspCmd {
	
	public Add(String cmdString) {
		setCmdString("REMOVE "+cmdString);
	}
}
