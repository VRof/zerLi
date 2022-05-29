package server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
            /**
             * start Habib
             */
            case "deliveryOrders":
                deliveryOrders(msg,client);
                break;
            case "SurveyResults":
                surveyResults(msg,client);
                break;
            case "confirmDelivery":
                confirmDelivery(msg,client);
                break;
            case "insertComplaint":
                insertComplaint(msg,client);
                break;
            case "getSurveyLink":
                getSurveyLink(msg,client);
                break;
            case "getQuantityRows":
                getQuantityRows(client);
                break;
            case "setSurveyQuestions":
                setSurveyQuestions(msg,client);
                break;
            case "insertSurveyAnswers":
                insertSurveyAnswers(msg,client);
                break;
            /**
             * end Habib
             */
        }
    }

    /////// Habib Ibrahim Part | Delivery Guy + Customer Services + Marketing Worker

    /**
     * insertSurveyAnswers -
     * @param msg -  receives message from client connection
     * @param client - receives specific client connection
     */
    private void insertSurveyAnswers(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Object data[] = (Object[])((Message) msg).getMsg();
        Message message = new Message();
        int datanum[] = (int[]) data[0];
        String SQL = "INSERT INTO survey (idsurvey,a1,a2,a3,a4,a5,a6,surveydate) VALUES ("+ datanum[0] + "," + datanum[1] + "," + datanum[2] + "," + datanum[3] + ","
                                                                                        + datanum[4] +"," + datanum[5] + "," + datanum[6] +","+ data[1].toString() +");";
        try { dbConn.createStatement().executeUpdate(SQL); }
        catch (SQLException e) { System.out.println("Error insert survey result to table : " + SQL + " " + e);
            try { client.sendToClient("inserting failed"); }
            catch (IOException e1) { System.out.println("sending data to client " + client + " error " + e1); }
        }
        try { message.setCommand("answer inserted"); client.sendToClient(message); }
        catch (IOException e) { System.out.println("sending data to client " + client + " error " + e); }
    }

    /**
     * setSurveyQuestions -
     * @param msg -  receives message from client connection
     * @param client - receives specific client connection
     */
    private void setSurveyQuestions(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message message = (Message)msg, newMSG = new Message();
        String SQL = "SELECT q1,q2,q3,q4,q5,q6 FROM surveyinfo WHERE surveyid = '" + message.getMsg() + "';";
        CachedRowSet cachedMsg;
        ResultSet rs;
        try {
            rs = dbConn.createStatement().executeQuery(SQL);
            RowSetFactory factory = RowSetProvider.newFactory();
            cachedMsg = factory.createCachedRowSet(); cachedMsg.populate(rs);
            rs.close();
            newMSG.setCommand(""); newMSG.setMsg(cachedMsg);
            client.sendToClient(newMSG);
        }
        catch (SQLException | IOException e) { System.out.println("SQL request from client " + client + " error " + e);
            try { client.sendToClient("error in sql request or server"); }
            catch (IOException e1) { System.out.println("sending data to client " + client + " error " + e1); }
        }
    }
    /**
     * getQuantityRows -
     * @param client - receives specific client connection
     */
    private void getQuantityRows(ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        String SQL = "SELECT COUNT(surveyid) as count FROM surveyinfo;";
        Message newMSG = new Message();
        CachedRowSet cachedMsg;
        ResultSet rs;
        int num = 0;
        try {
            rs = dbConn.createStatement().executeQuery(SQL);
            RowSetFactory factory = RowSetProvider.newFactory();
            cachedMsg = factory.createCachedRowSet(); cachedMsg.populate(rs);
            rs.close();
            try { while (cachedMsg.next()) { num = cachedMsg.getInt("count"); }}
            catch (SQLException e) { System.out.println("Error read data from server " + e); }
            newMSG.setCommand(""); newMSG.setMsg(num);
            client.sendToClient(newMSG);
        }
        catch (SQLException | IOException e) { System.out.println("SQL request from client " + client + " error " + e);
            try { client.sendToClient("error in sql request or server"); }
            catch (IOException e1) { System.out.println("sending data to client " + client + " error " + e1); }
        }
    }

    /**
     * getSurveyLink - client asks for specific survey , server send survey file as byte array.
     * @param msg -  receives message from client connection
     * @param client - receives specific client connection
     */
    private void getSurveyLink(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message message = (Message) msg;
        int[] surveyData = {((int[]) message.getMsg())[0], ((int[])message.getMsg())[1]};
        Message newMessage = (Message) msg;
        String SQL = "SELECT link FROM surveyresults WHERE surveyid = '" + surveyData[1] + "';";
        CachedRowSet cachedMsg;
        ResultSet rs;
        try {
            rs = dbConn.createStatement().executeQuery(SQL);
            RowSetFactory factory = RowSetProvider.newFactory();
            cachedMsg = factory.createCachedRowSet(); cachedMsg.populate(rs);
            rs.close();
            String survey = null;
            try { while (cachedMsg.next()) { survey = cachedMsg.getString("link"); }}
            catch (SQLException e) { System.out.println("Error read data from server " + e); }
            SQL = "src\\pictures\\" + survey;
            byte[] pdf = Files.readAllBytes(Path.of(SQL));
            newMessage.setCommand(""); newMessage.setMsg(pdf);
            client.sendToClient(newMessage);
        }
        catch (SQLException | IOException e) { System.out.println("SQL request from client " + client + " error " + e);
            try { client.sendToClient("error in sql request or server"); }
            catch (IOException e1) { System.out.println("sending data to client " + client + " error " + e1); } }
    }

    /**
     * surveyResults - this function made to collect data from server for Customer Services GUI TableView
     * @param msg - receives message from client connection
     * @param client - receives specific client connection
     */
    private void surveyResults(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message message = (Message) msg;
        String SQL = "SELECT * FROM surveyresults WHERE csid = '" + message.getMsg() + "';";
        Message newMSG = new Message();
        CachedRowSet cachedMsg;
        ResultSet rs;
        try {
            rs = dbConn.createStatement().executeQuery(SQL);
            RowSetFactory factory = RowSetProvider.newFactory();
            cachedMsg = factory.createCachedRowSet(); cachedMsg.populate(rs);
            rs.close();
            newMSG.setCommand(""); newMSG.setMsg(cachedMsg);
            client.sendToClient(newMSG);
        }
        catch (SQLException | IOException e) { System.out.println("SQL request from client " + client + " error " + e);
            try { client.sendToClient("error in sql request or server"); }
            catch (IOException e1) { System.out.println("sending data to client " + client + " error " + e1); }
        }
    }

    /**
     * deliveryOrders - this function made to collect data from server for DeliveryGuy GUI TableView
     * @param msg - receives message from client connection
     * @param client - receives specific client connection
     */
    private void deliveryOrders(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message message = (Message) msg;
        String SQL2 = "SELECT * FROM delivery WHERE deliveryGuyId = '" + message.getMsg() + "' AND confirmed = 'no' ;";
        Message newMSG = new Message();
        CachedRowSet cachedMsg;
        ResultSet rs;
        try {
            rs = dbConn.createStatement().executeQuery(SQL2);
            RowSetFactory factory = RowSetProvider.newFactory();
            cachedMsg = factory.createCachedRowSet(); cachedMsg.populate(rs);
            rs.close();
            newMSG.setCommand(""); newMSG.setMsg(cachedMsg);
            client.sendToClient(newMSG);
        }
        catch (SQLException | IOException e) { System.out.println("SQL request from client " + client + " error " + e);
            try { client.sendToClient("error in sql request or server"); }
            catch (IOException e1) { System.out.println("sending data to client " + client + " error " + e1);}
        }
    }

    /**
     *confirmDelivery -
     * @param msg - receives message from client connection
     * @param client - receives specific client connection
     */
    private void confirmDelivery(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message message = (Message) msg;
        int[] orderData = {((int[]) message.getMsg())[0], ((int[])message.getMsg())[1]};
        String SQL = "Update delivery SET confirmed = 'yes' WHERE deliveryGuyId = '" + orderData[0] + "' AND orderNumber = '" + orderData[1] + "'";
        String SQL1 = "Update orders SET status = 'delivered' WHERE orderNumber = '" + orderData[1] + "' AND status = 'pending for delivery' ";
        try { dbConn.createStatement().executeUpdate(SQL); dbConn.createStatement().executeUpdate(SQL1);}
        catch (SQLException e) { System.out.println("Error update delivery table : " + SQL + " " + e);
            try { client.sendToClient("confirm failed"); }
            catch (IOException e1) { System.out.println("sending data to client " + client + " error " + e1); }
        }
        try { message.setCommand("Simulation : Order delivered to client"); client.sendToClient(message); }
        catch (IOException e) { System.out.println("sending data to client " + client + " error " + e); }
    }

    /**
     * insertComplaint - inserting complaint to DataBase from Customer service
     * @param msg - receives message from client connection
     * @param client - receives specific client connection
     */
    private void insertComplaint(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message message = (Message) msg;
        Long datetime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(datetime);
        String[] orderData = {((String[]) message.getMsg())[0],((String[])message.getMsg())[1],((String[])message.getMsg())[2]};
        String SQL = "INSERT INTO complaints (complaintText,csid,shop,date) VALUES ('" + orderData[1] + "','" + orderData[0] + "','"+ orderData[2]+"','"+timestamp+"')";
        try { dbConn.createStatement().executeUpdate(SQL); }
        catch (SQLException e) { System.out.println("Error insert complaint table : " + SQL + " " + e);
            try { client.sendToClient("inserting failed"); }
            catch (IOException e1) { System.out.println("sending data to client " + client + " error " + e1); }
        }
        try { message.setCommand("complaint inserted"); client.sendToClient( message); }
        catch (IOException e) { System.out.println("sending data to client " + client + " error " + e); }
    }

 /////////////////////////////// End Of Habib Ibrahim
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
        String status = "";
        try {
            rs = dbConn.createStatement().executeQuery(SQL);
            //  rs.next();
            if(rs.next() == false){
                userLoginData.setCommand("wrong");
                client.sendToClient(userLoginData);
                return;
            }
            userid = rs.getInt("userid");
            status = rs.getString("status");
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
        if(status.equals("active"))
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
