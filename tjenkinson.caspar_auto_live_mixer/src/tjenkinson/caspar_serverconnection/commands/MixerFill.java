package tjenkinson.caspar_serverconnection.commands;

public class MixerFill extends CaspCmd {
	
	public MixerFill(String a, String b) {
		setCmdString("MIXER "+a+" FILL "+b);
	}
}
