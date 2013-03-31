package tjenkinson.caspar_serverconnection.commands;

public class Load extends CaspCmd {
	
	public Load(String cmdString) {
		setCmdString("LOAD "+cmdString);
	}
}
