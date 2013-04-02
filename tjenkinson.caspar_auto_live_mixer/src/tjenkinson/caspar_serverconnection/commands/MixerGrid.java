package tjenkinson.caspar_serverconnection.commands;

public class MixerGrid extends CaspCmd {
	
	public MixerGrid(String a, String b) {
		setCmdString("MIXER "+a+" GRID "+b);
	}
}
