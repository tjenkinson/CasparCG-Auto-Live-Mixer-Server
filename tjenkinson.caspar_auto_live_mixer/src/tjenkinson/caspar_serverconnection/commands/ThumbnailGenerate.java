package tjenkinson.caspar_serverconnection.commands;

public class ThumbnailGenerate extends CaspCmd {
	
	public ThumbnailGenerate(String a) {
		setCmdString("THUMBNAIL GENERATE "+a);
	}
}
