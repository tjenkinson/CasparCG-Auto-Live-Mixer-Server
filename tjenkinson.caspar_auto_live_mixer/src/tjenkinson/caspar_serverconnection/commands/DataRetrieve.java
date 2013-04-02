package tjenkinson.caspar_serverconnection.commands;

public class DataRetrieve extends CaspCmd {
	
	public DataRetrieve(String cmdString) {
		setCmdString("DATA RETRIEVE "+cmdString);
	}
}
