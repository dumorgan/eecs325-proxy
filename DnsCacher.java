import java.net.*;
import java.util.*;


/**
 * This classed is used to control the caching of DNS within the proxy
 * */
public class DnsCacher {
	
	private Hashtable<String, ipAndTimeStamp> addresses; //hashtable containing each url (keys) and its correspondent IP and timestamp (values)
	
	
	/**
	 * Class constructor, takes no parameters
	 * */
	public DnsCacher() {
		this.addresses = new Hashtable<String, ipAndTimeStamp>();
	}
	
	/**
	 * Getter for the hashtable
	 * 
	 * @return The hashtable containing the pairs of urls with its associated IP and timestamp
	 * */
	public Hashtable<String, ipAndTimeStamp> getAddresses() {
		return this.addresses;
	}
	
	/**
	 * This method adds a new key/value to the hashtable 
	 * */
	public void addToAddress(String url) {
		try {
			//DNS lookup to find the ip
			String fullAddress = InetAddress.getByName(url).toString();
			String ip = fullAddress.split("/")[1];
			//adds the element to the hash table
			this.getAddresses().put(url, new ipAndTimeStamp(ip,System.currentTimeMillis()));
		}
		catch (Exception e) {
			if (e instanceof UnknownHostException) {
				System.out.println("Host: " + url + " not found");
			}
		}
		
	}
	
	
}
 