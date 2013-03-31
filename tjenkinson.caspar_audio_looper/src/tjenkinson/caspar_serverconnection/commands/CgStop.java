package tjenkinson.caspar_serverconnection.commands;

public class CgStop extends CaspCmd {
	
	public CgStop(String a, String b) {
		setCmdString("CG "+a+" STOP "+b);
	}
}
