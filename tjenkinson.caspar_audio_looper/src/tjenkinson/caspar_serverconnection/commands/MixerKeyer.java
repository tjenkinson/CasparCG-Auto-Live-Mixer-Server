package tjenkinson.caspar_serverconnection.commands;

public class MixerKeyer extends CaspCmd {
	
	public MixerKeyer(String a, String b) {
		setCmdString("MIXER "+a+" KEYER "+b);
	}
}
