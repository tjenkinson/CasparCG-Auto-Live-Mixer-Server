package tjenkinson.caspar_serverconnection.commands;

public class MixerClip extends CaspCmd {
	
	public MixerClip(String a, String b) {
		setCmdString("MIXER "+a+" CLIP "+b);
	}
}
