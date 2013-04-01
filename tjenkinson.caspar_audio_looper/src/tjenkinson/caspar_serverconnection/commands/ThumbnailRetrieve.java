package tjenkinson.caspar_serverconnection.commands;

public class ThumbnailRetrieve extends CaspCmd {
	
	public ThumbnailRetrieve(String a) {
		setCmdString("THUMBNAIL RETRIEVE "+a);
	}
}
