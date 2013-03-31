package tjenkinson.caspar_serverconnection.commands;

public class CgInvoke extends CaspCmd {
	
	public CgInvoke(String a, String b) {
		setCmdString("CG "+a+" INVOKE "+b);
	}
}
