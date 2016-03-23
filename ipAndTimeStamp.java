/**
 * This is a simple class to store an IP address and a timestamp
 * 
 * @author Eduardo Morgan
 * */

public class ipAndTimeStamp {
	private String ip; //the ip address
	private long timeStamp; //the timestamp
	
	/**
	 * Constructor for the class
	 * 
	 * @param ip The ip address
	 * @param timeStamp The timestamp
	 * */
	public ipAndTimeStamp(String ip, long timeStamp) {
		this.ip = ip;
		this.timeStamp = timeStamp;
	}
	
	/**
	 * Getter for the IP address
	 * 
	 * @return the ip address
	 * */
	public String getIp() {
		return this.ip;
	}
	
	
	/**
	 * Getter for the timestamp
	 * 
	 * @return the timestamp
	 * */
	public long getTimeStamp() {
		return this.timeStamp;
	}
}
