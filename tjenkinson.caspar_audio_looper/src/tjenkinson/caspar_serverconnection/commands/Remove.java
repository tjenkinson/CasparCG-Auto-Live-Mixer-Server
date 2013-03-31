package tjenkinson.caspar_serverconnection.commands;

public class Remove extends CaspCmd {
	
	public Remove(String cmdString) {
		setCmdString("ADD "+cmdString);
	}
}
