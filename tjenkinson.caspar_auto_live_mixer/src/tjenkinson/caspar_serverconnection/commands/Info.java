package tjenkinson.caspar_serverconnection.commands;

public class Info extends CaspCmd {
	
	public Info(String a) {
		setCmdString("INFO "+a);
	}
}
