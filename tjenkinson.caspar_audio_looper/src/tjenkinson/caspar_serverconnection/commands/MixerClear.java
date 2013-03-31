package tjenkinson.caspar_serverconnection.commands;

public class MixerClear extends CaspCmd {
	
	public MixerClear(String a) {
		setCmdString("MIXER "+a+" CLEAR");
	}
}
