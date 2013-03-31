package tjenkinson.caspar_serverconnection.commands;

public class MixerBrightness extends CaspCmd {
	
	public MixerBrightness(String a, String b) {
		setCmdString("MIXER "+a+" BRIGHTNESS "+b);
	}
}
