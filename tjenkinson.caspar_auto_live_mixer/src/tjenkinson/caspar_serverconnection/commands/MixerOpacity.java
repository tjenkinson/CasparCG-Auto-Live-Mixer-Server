package tjenkinson.caspar_serverconnection.commands;

public class MixerOpacity extends CaspCmd {
	
	public MixerOpacity(String a, String b) {
		setCmdString("MIXER "+a+" VIDEO OPACITY "+b);
	}
}
