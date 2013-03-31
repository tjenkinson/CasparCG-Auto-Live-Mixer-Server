package tjenkinson.caspar_serverconnection.commands;

public class CaspReturn {
	
	private int status = 0;
	private String response = null;
	private long requestTime = 0;
	
	public CaspReturn(int status, String response, long requestTime) {
		this.status = status;
		this.response = response;
		this.requestTime = requestTime;
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getResponse() {
		return response;
	}
	
	public long getRequestTime() {
		return requestTime;
	}
}
