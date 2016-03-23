import java.net.*;
import java.io.*;


/**
 * Class that implements a proxy server
 * 
 * @author Eduardo Morgan
 * */
public class proxyd {
		
		public static volatile DnsCacher cacheManager;
		/**
		 * Constructor
		 * 
		 * @param portNumber the port number to connect to the client
		 * */
		public proxyd(int portNumber) {
			
			System.out.println("Starting a connection to port: " + portNumber);
			cacheManager = new DnsCacher();
			runServer(portNumber);
			
		}
		
		public synchronized DnsCacher getCacheManager() {
			return this.getCacheManager();
		}
		
		/**
		 * This static method runs the server
		 * 
		 * @param portNumber  
		 * */
		private static void runServer(int portNumber) {
			
			ServerSocket serverSocket;
			//creates the server socket
			try {
				serverSocket = new ServerSocket(portNumber);
				System.out.println("Server started on port: " + portNumber);
				
				//runs the threads
				while (true) {
					ProxyThread thread = new ProxyThread(serverSocket.accept(), cacheManager);
					System.out.println("Successfully connected to browser");
					thread.start();
				}
			}
			catch (IOException e) {
				System.out.println("Error when establishing connection to port " + portNumber);
			}
					
		}
		
		
		/**
		 * This is the main method, it basically instantiates a ProxyServer
		 * */
		public static void main (String []args) {
			
			proxyd proxy = new proxyd(Integer.parseInt(args[1]));
		}
}
