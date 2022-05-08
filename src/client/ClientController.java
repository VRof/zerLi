package client;

import java.io.IOException;

import clientClasses.Message;
import clientClasses.UserLoginData;
import ocsf.client.*;

public class ClientController extends AbstractClient {
	private static ClientController clientController;
	public static boolean awaitResponse = false;
	public static Message messageFromServer;
	public static UserLoginData userLoginData;
	public ClientController(String host, int port) throws IOException {
		super(host, port);
		openConnection();
		clientController = this;
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		awaitResponse = false;
		messageFromServer = (Message) msg;
		System.out.println("received command : " + messageFromServer.getCommand() + " data: "  + messageFromServer.getMsg());
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


	public void logout(){
		clientClasses.Message msg = new clientClasses.Message();
		msg.setCommand("disconnect");
		msg.setMsg((Object)(userLoginData.getUserid()));
		send(msg);
	}
	
	public static ClientController getClientController() {
		return clientController;
	}
}
