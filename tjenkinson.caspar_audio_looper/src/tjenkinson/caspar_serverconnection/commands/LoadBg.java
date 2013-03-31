package tjenkinson.caspar_serverconnection.commands;

public class LoadBg extends CaspCmd {
	
	public LoadBg(String cmdString) {
		setCmdString("LOADBG "+cmdString);
	}
}
