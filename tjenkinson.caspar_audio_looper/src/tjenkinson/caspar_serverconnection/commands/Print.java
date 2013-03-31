package tjenkinson.caspar_serverconnection.commands;

public class Print extends CaspCmd {
	
	public Print(String cmdString) {
		setCmdString("PRINT "+cmdString);
	}
}
