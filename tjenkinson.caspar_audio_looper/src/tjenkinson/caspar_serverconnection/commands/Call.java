package tjenkinson.caspar_serverconnection.commands;

public class Call extends CaspCmd {
	
	public Call(String cmdString) {
		setCmdString("CALL "+cmdString);
	}
}
