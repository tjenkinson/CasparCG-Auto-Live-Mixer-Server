package tjenkinson.caspar_serverconnection.commands;

public class Play extends CaspCmd {
	
	public Play(String cmdString) {
		setCmdString("PLAY "+cmdString);
	}
}
