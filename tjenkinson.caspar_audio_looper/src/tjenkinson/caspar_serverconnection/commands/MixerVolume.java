package tjenkinson.caspar_serverconnection.commands;

public class MixerVolume extends CaspCmd {
	
	public MixerVolume(String a, String b) {
		setCmdString("MIXER "+a+" VOLUME "+b);
	}
}
