package tjenkinson.caspar_serverconnection.commands;

public class DataRemove extends CaspCmd {
	
	public DataRemove(String cmdString) {
		setCmdString("DATA REMOVE "+cmdString);
	}
}
