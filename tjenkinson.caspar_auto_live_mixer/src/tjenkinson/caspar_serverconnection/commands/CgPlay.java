package tjenkinson.caspar_serverconnection.commands;

public class CgPlay extends CaspCmd {
	
	public CgPlay(String a, String b) {
		setCmdString("CG "+a+" PLAY "+b);
	}
}
