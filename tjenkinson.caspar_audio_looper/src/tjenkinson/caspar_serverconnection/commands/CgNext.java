package tjenkinson.caspar_serverconnection.commands;

public class CgNext extends CaspCmd {
	
	public CgNext(String a, String b) {
		setCmdString("CG "+a+" NEXT "+b);
	}
}
