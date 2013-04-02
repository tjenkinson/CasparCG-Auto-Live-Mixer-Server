package tjenkinson.caspar_serverconnection.commands;

public class MixerContrast extends CaspCmd {
	
	public MixerContrast(String a, String b) {
		setCmdString("MIXER "+a+" CONTRAST "+b);
	}
}
