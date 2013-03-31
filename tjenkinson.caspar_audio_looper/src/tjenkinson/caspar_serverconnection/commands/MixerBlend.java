package tjenkinson.caspar_serverconnection.commands;

public class MixerBlend extends CaspCmd {
	
	public MixerBlend(String a, String b) {
		setCmdString("MIXER "+a+" BLEND "+b);
	}
}
