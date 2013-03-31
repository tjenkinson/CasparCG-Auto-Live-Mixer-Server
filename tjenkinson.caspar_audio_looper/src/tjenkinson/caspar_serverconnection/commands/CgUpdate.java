package tjenkinson.caspar_serverconnection.commands;

public class CgUpdate extends CaspCmd {
	
	public CgUpdate(String a, String b) {
		setCmdString("CG "+a+" UPDATE "+b);
	}
}
