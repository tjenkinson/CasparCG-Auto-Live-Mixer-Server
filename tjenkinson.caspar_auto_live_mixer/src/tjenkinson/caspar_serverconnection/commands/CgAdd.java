package tjenkinson.caspar_serverconnection.commands;

public class CgAdd extends CaspCmd {
	
	public CgAdd(String a, String b) {
		setCmdString("CG "+a+" ADD "+b);
	}
}
