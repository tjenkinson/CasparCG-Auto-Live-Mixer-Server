package tjenkinson.caspar_serverconnection.commands;

public class CgRemove extends CaspCmd {
	
	public CgRemove(String a, String b) {
		setCmdString("CG "+a+" REMOVE "+b);
	}
}
