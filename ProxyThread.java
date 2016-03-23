import java.io.*;
import java.lang.*;
import java.net.*;
import java.net.Socket;
/**
 * This class is the main part of the proxy server
 * It is a thread that runs the intermediate actions between the browser and the remote server
 * 
 * @author Eduardo Morgan
 * */
public class ProxyThread extends Thread {

	private Socket socket; //socket to the browser
	private static final int BUFFER_SIZE = 131072; //size of the buffer
	private static final long TIMEOUT_INTERVAL = 30000;
	private static DnsCacher cacheManager;
	
	/**
	 * Constructor for the thread
	 * 
	 * @param socket The socket to the browser client
	 * */
	public ProxyThread(Socket socket, DnsCacher cacheManager) {
		super("Proxy Thread");
		this.socket = socket;
		this.cacheManager = cacheManager;
	}
	
	/**
	 * Runs the thread
	 * */
	@Override
	public void run() {
		
		//gets the input from the browser
		try {
			//outputstream to send data back to the browser once the response from the server is received
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			
			//this buffered reader gets the HTTP request fromt the browser
			InputStream in = socket.getInputStream();
			
			//reads the request from the browser
			String HTTPRequest = "";
			byte nextByte;
			while ((nextByte = (byte)in.read()) != -1) {
				HTTPRequest = HTTPRequest + (char)nextByte;
				//as the connection is set to stay-alive by default
				//need to check for the end of the request 
				if (HTTPRequest.charAt(HTTPRequest.length()-1) == '\n' 
					&& HTTPRequest.charAt(HTTPRequest.length()-2) == '\r'
					&& HTTPRequest.charAt(HTTPRequest.length()-3) == '\n'
					&& HTTPRequest.charAt(HTTPRequest.length()-4) == '\r') {
						break;
				}
			}
			//prints the http request header to the console
			System.out.println("\nHTTP Request from browser");
			System.out.println(HTTPRequest);
			
			String[] lines = HTTPRequest.split("\n");
			String almostURL = lines[1].split(" ")[1];
			String url = "";
			for (int i = 0; i < almostURL.length()-1; i++)
				url = url + almostURL.charAt(i);

			//prints the requested url to the console
			System.out.println("URL requested: " + url);

			//checks if there is a connection header, if so, checks if it's set to keep-alive
			//if it is, sets to close
			boolean wrongConnField = false;
			for (int i = 0; i < lines.length; i++) {
				String[] thisLine = lines[i].split(" ");
				if (thisLine[0].compareTo("Connection:") == 0 || thisLine[0].compareTo("Proxy-connection:") == 0) {
					if (thisLine[1].compareTo("keep-alive\r") == 0) {
						lines[i] = "Connection: close";
						wrongConnField = true;
					}
				}
			}
			
			//creates the new header, if needed
			String header = "";
			if (wrongConnField) {
				for (int i = 0; i < lines[i].length(); i++) {
					header = header + lines[i] + "\n";
				}
				header = header + "\r\n";
			}
			else {
				header = HTTPRequest;
			}
			System.out.println("\nHTTP request forwarded");
			System.out.println(header);
			
			
			//this is the socket between the proxy and the remote server
			Socket socketToServer;
			//try-catch block to connect to final server
			try {
				//connects to host
				System.out.println("Connecting to host " + url + "...");
				
				//checks if the url is in the addresses hashtable
				if (cacheManager.getAddresses().containsKey(url)) {
					//if it is, checks for the timestamp values
					if (System.currentTimeMillis() - cacheManager.getAddresses().get(url).getTimeStamp() < TIMEOUT_INTERVAL) {
						socketToServer = new Socket(cacheManager.getAddresses().get(url).getIp(),80);
						System.out.println("Connected to a cached DNS address");
					}
					//updates the timestamp of the key
					else {
						cacheManager.getAddresses().remove(url);
						cacheManager.addToAddress(url);
						socketToServer = new Socket(url,80);
					}
				}
				//if the key is not on the hashtable, adds it and sets up the connection
				else {
					socketToServer = new Socket(url,80);
					cacheManager.addToAddress(url);
				}
				
				//creates a writer to send the HTTP requests
				PrintWriter msgSender = new PrintWriter(socketToServer.getOutputStream());
				//writes and flushes the request
				msgSender.print(header);
				msgSender.flush();
				
				//reads the binary data from the remote server
				byte[] b = new byte[BUFFER_SIZE];
				InputStream dataFromServer = socketToServer.getInputStream();
				//reads data from the server
				for (int index = 0; ((b[index] = (byte)dataFromServer.read()) != -1); index++)
					System.out.print((char)b[index]);
				
				//sends the data from the server to the client browser
				out.write(b);
				out.flush(); 
			}
			
			//feedback on errors on the console
			catch (Exception e) {
				if (e instanceof UnknownHostException) 
					System.out.println("Host " + url + " does not exist");
				System.out.println("Fail when trying to connect to host " + url);
			}
		}
		catch (Exception e) {
			
			
		}	
	}
}
