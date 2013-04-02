package tjenkinson.caspar_serverconnection.commands;

public class Set extends CaspCmd {
	
	public Set(String cmdString) {
		setCmdString("SET "+cmdString);
	}
}
