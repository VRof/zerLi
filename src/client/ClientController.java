package client;

import java.io.IOException;
import javax.sql.rowset.CachedRowSet;

import ocsf.client.*;

public class ClientController extends AbstractClient {
	private static ClientController clientController;
	public static boolean awaitResponse = false;
	public static CachedRowSet cachedMsg;
	public ClientController(String host, int port) throws IOException {
		super(host, port);
		openConnection();
		clientController = this;
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		awaitResponse = false;
		System.out.println("recieved :"+msg);
		try {
		cachedMsg = (CachedRowSet)msg;
		} catch(ClassCastException e) {
			System.out.println();
		}
	}
	
	
	public void send(Object msg) {

	       	awaitResponse = true;
	    	try {
				sendToServer(msg);
			} catch (IOException e) {
			      System.out.println("Could not send message to server: Terminating client." + e);
			      System.exit(1);
			}
			// wait for response
			while (awaitResponse) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
	}
	
	public static ClientController getClientController() {
		return clientController;
	}
}
