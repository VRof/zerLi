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

import clientClasses.Message;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import serverGUI.ServerWindowController;

public class ServerControl extends AbstractServer {
    public static List<ConnectionToClient> clientsList = new ArrayList<>(); //list of connected users to server (used in table of connected clients in serverGUI)
    private List<Integer> connectedClientdIdList = new ArrayList<>(); //list of id's of connected users to server to prevent multiply login of the same user
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
        Message message = (Message) msg;
        System.out.println("Message received: command: " + message.getCommand() + " data: " + message.getMsg() + " from " + client);

        switch (message.getCommand()){
            case "login":
                login(msg, client);
                break;
            case "disconnect":
                disconnect(msg, client);
                break;
            case "logout":
                logout(msg,client);
                break;
        }

    }

    private void logout(Object msg, ConnectionToClient client) {
        Message message = (Message) msg;
        int userid = -1;
        userid = (int) message.getMsg();
        for (int i = 0; i < connectedClientdIdList.size(); i++) { //remove user from the list of connected users
            if (connectedClientdIdList.get(i) == userid) {
                connectedClientdIdList.remove(i);
            }
        }
        try {
            message.setCommand("logout accepted");
            client.sendToClient( message);
        } catch (IOException e) {
            System.out.println("Error sending msg to server");
        }
    }

    public static void closeAll() throws SQLException {
        if (sv != null && SqlConnector.getConnection() != null && (!SqlConnector.getConnection().isClosed() || sv.isListening())) {
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

    private void login(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message message = (Message) msg;
        Message userLoginData = new Message();
        String[] userdata = {((String[]) message.getMsg())[0], ((String[]) message.getMsg())[1]}; //username,password
        String SQL = "SELECT * FROM login l WHERE l.username = " + "\"" + userdata[0] + "\"" + " AND " + "l.password = " + "\"" + userdata[1] + "\"" + ";";
        CachedRowSet cachedMsg = null;
        ResultSet rs;
        int userid = -1;
        try {
            rs = dbConn.createStatement().executeQuery(SQL);
            //  rs.next();

            if(rs.next() == false){
                userLoginData.setCommand("wrong");
                client.sendToClient(userLoginData);
                return;
            }

            userid = rs.getInt("userid");
            rs.beforeFirst(); //reset rs pointer
            RowSetFactory factory = RowSetProvider.newFactory();
            cachedMsg = factory.createCachedRowSet();
            cachedMsg.populate(rs);
            rs.close();

        } catch (SQLException | IOException e) {
            System.out.println("SQL request from client " + client + " error " + e);
            try {
                client.sendToClient("error in sql request or server");
            } catch (IOException e1) {
                System.out.println("sending data to client " + client + " error " + e1);
            }
        }


        for (int i = 0; i < connectedClientdIdList.size(); i++) { //check if the client is already connected
            if (connectedClientdIdList.get(i) == userid) { //if connected
                try {
                    userLoginData.setCommand("already logged in");
                    client.sendToClient(userLoginData); //send null to client
                    return;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
      //  if (userid != -1)
            connectedClientdIdList.add(userid); //add new user id to the list
        try {
            userLoginData.setCommand("logged in");
            userLoginData.setMsg(cachedMsg);
            client.sendToClient(userLoginData); //send login data to client
        } catch (IOException e) {
            System.out.println("sending data to client " + client + " error " + e);

        }
    }


    private void disconnect(Object msg, ConnectionToClient client) {
        Message message = (Message) msg;
        int userid = -1;
        if(message.getMsg()!=null)
             userid = (int) message.getMsg();
        ServerWindowController.clientDisconected(client); //change status in server window to "disconnected"
        for (int i = 0; i < connectedClientdIdList.size(); i++) { //remove user from the list of connected users
            if (connectedClientdIdList.get(i) == userid) {
                connectedClientdIdList.remove(i);
            }
        }
        try {
            message.setCommand("disconnect accepted");
            client.sendToClient(message);
        } catch (IOException e) {
            System.out.println("Error sending msg to server");
        }
    }

    public static void setConnection(ServerControl svconn) {
        sv = svconn;
    }
}
