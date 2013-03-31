package tjenkinson.caspar_serverconnection.commands;

public class Version extends CaspCmd {
	
	public Version(String a) {
		setCmdString("VERSION "+a);
	}
}
