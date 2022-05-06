package server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import serverGUI.ServerWindowController;

public class ServerControl extends AbstractServer {
	public static List<ConnectionToClient> clientsList = new ArrayList<ConnectionToClient>();
	private static ServerControl sv;

	public ServerControl(int port) {
		super(port);
	}

	@Override
	protected void clientConnected(ConnectionToClient client) {
		ServerWindowController.addClientToTable(client); //add new client to client table
	}

	@Override
	synchronized protected void clientDisconnected(ConnectionToClient client) {
		ServerWindowController.clientDisconected(client); //set client as disconnected
	}

	@Override
	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + sv.getPort());
	}

	@Override
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		Connection dbConn = SqlConnector.getConnection();
		System.out.println("Message received: " + msg + " from " + client);
		String[] dbRequest = ((String) msg).split("\\s+");

		if (dbRequest[0].equals("getOrders")) {
			String SQL = "SELECT * FROM orders;";
			CachedRowSet cachedMsg = null;
			ResultSet rs;
			try {
				rs = dbConn.createStatement().executeQuery(SQL);
				RowSetFactory factory = RowSetProvider.newFactory();
				cachedMsg = factory.createCachedRowSet();
				cachedMsg.populate(rs);
				rs.close();
			} catch (SQLException e) {
				System.out.println("SQL request from client " + client + " error " + e);
				try {
					client.sendToClient("error in sql request or server");
				} catch (IOException e1) {
					System.out.println("sending data to client " + client + " error " + e1);
				}
			}
			try {
				client.sendToClient(cachedMsg);
			} catch (IOException e) {
				System.out.println("sending data to client " + client + " error " + e);

			}

		} else if (dbRequest[0].equals("update")) {
			String SQL = ((String) msg);
			SQL = SQL.substring("update ".length(), SQL.length());
			try {
				dbConn.createStatement().executeUpdate(SQL);
			} catch (SQLException e) {
				System.out.println("Error update orders table : " + SQL + " " + e);
				try {
					client.sendToClient("update failed");
				} catch (IOException e1) {
					System.out.println("sending data to client " + client + " error " + e1);
				}
			}
			try {
				client.sendToClient((Object) "updated");
			} catch (IOException e) {
				System.out.println("sending data to client " + client + " error " + e);
			}
		} else if (dbRequest[0].equals("disconnect")) {
			ServerWindowController.clientDisconected(client);
			try {
				client.sendToClient((Object) client + " disconnect accepted");
			} catch (IOException e) {
				System.out.println("Error sending msg to server");
			}
		}

	}

	public static void closeAll() throws SQLException {
		if (sv != null && SqlConnector.getConnection()!=null &&  (!SqlConnector.getConnection().isClosed() || sv.isListening())) {
			SqlConnector.getConnection().close();
			try {
				sv.close();
			} catch (IOException e) {
				System.out.println("Can't close connection on port " + sv.getPort());
			}
		} else {
			System.out.println("Not connected!");
		}
	}

	public static void setConnection(ServerControl svconn) {
		sv = svconn;
	}

}
