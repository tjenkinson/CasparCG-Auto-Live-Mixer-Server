package tjenkinson.caspar_serverconnection.commands;

public class MixerSaturation extends CaspCmd {
	
	public MixerSaturation(String a, String b) {
		setCmdString("MIXER "+a+" SATURATION "+b);
	}
}
