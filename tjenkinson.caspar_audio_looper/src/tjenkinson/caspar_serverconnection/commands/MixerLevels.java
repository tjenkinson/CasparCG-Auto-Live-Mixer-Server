package tjenkinson.caspar_serverconnection.commands;

public class MixerLevels extends CaspCmd {
	
	public MixerLevels(String a, String b) {
		setCmdString("MIXER "+a+" LEVELS "+b);
	}
}
