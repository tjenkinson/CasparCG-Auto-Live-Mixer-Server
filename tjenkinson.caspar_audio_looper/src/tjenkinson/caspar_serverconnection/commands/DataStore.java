package tjenkinson.caspar_serverconnection.commands;

public class DataStore extends CaspCmd {
	
	public DataStore(String cmdString) {
		setCmdString("DATA STORE "+cmdString);
	}
}
