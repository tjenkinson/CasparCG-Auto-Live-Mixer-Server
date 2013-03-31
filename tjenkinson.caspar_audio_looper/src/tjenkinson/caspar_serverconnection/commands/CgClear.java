package tjenkinson.caspar_serverconnection.commands;

public class CgClear extends CaspCmd {
	
	public CgClear(String a) {
		setCmdString("CG "+a+" CLEAR");
	}
}
