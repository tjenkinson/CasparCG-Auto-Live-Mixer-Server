package tjenkinson.caspar_serverconnection.commands;

public class Swap extends CaspCmd {
	
	public Swap(String cmdString) {
		setCmdString("SWAP "+cmdString);
	}
}
