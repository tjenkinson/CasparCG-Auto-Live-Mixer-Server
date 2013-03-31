package tjenkinson.caspar_serverconnection.commands;

public abstract class CaspCmd {
	
	private String cmdString;
	
	protected void setCmdString(String cmdString) {
		this.cmdString = cmdString;
	}
	
	public String getCmdString() {
		return cmdString;
	}
}
