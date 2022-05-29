package server;

import clientClasses.Message;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import serverClasses.OrderCancellationData;
import serverGUI.ServerWindowController;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServerControl extends AbstractServer {
    public static List<ConnectionToClient> clientsList = new ArrayList<>(); //list of connected users to server (used in table of connected clients in serverGUI)
    private List<Integer> connectedClientdIdList = new ArrayList<>(); //list of id's of connected users to server to prevent multiply login of the same user
    private static ServerControl sv;
    private int port;

    public ServerControl(int port) {
        super(port);
        this.port = port;
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

        switch (message.getCommand()) {
            case "login":
                login(msg, client);
                break;
            case "disconnect":
                disconnect(msg, client);
                break;
            case "logout":
                logout(msg, client);
                break;
            case "getUserData":
                getUserData(msg, client);
                break;
            case "getCatalog":
                sendCatalog(msg, client);
                break;
            case "getItemPicture":
                sendItemPicture(msg, client);
                break;
            case "getOrders":
                getOrders(msg, client);
                break;
            case "CreateCancellationRequest":
                CreateCancellationRequest(msg, client);
                break;

        }

    }

    private void getUserData(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        CachedRowSet rowSet;
        int userid = (int) ((Message) msg).getMsg();
        String SQL = "SELECT * FROM users AS u, balance AS b WHERE u.userid = " + userid + " AND b.userid = " + userid + " ;";
        try {
            ResultSet rs = dbConn.createStatement().executeQuery(SQL);
            RowSetFactory factory = RowSetProvider.newFactory();
            rowSet = factory.createCachedRowSet();
            rowSet.populate(rs);

            Message msgToClient = new Message();
            msgToClient.setCommand("user data");
            msgToClient.setMsg((Object) rowSet);
            client.sendToClient(msgToClient);
        } catch (Exception e) {
            System.out.println("error getting user data " + e);
            e.printStackTrace();
        }


    }

    private void CreateCancellationRequest(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        int orderId = (int) ((Message) msg).getMsg();
        OrderCancellationData orderData = new OrderCancellationData();
        Timestamp timeNow = new Timestamp(System.currentTimeMillis());
        try {
            //get firstname and lastname of user that ordered:
            String SQL = "SELECT firstname, lastname " +
                    "FROM users AS u, userorders AS uo, orders AS o" +
                    " WHERE o.orderNumber = " + orderId + " AND uo.orderid = " + orderId + " AND uo.userid = u.userid;";
            ResultSet rs = dbConn.createStatement().executeQuery(SQL);
            rs.next();
            orderData.setFirstname(rs.getString("firstname"));
            orderData.setLastname(rs.getString("lastname"));
            //--------------------------------------------------------------------------------------------------
            //---------------------get other required data------------------------------------------------
            SQL = "SELECT status,price,orderDate,shop " +
                    "FROM orders " +
                    "WHERE orders.orderNumber = " + orderId + " ;";
            rs = dbConn.createStatement().executeQuery(SQL);
            rs.next();
            orderData.setOrderDate(rs.getTimestamp("orderDate"));
            orderData.setStatus(rs.getString("status"));
            orderData.setPrice(rs.getDouble("price"));
            orderData.setShop(rs.getString("shop"));
            //--------------------------------------------------------------------------------------------
            //----------create new row in cancellationrequest table:
            SQL = "INSERT INTO cancellationrequests(orderID,firstname,lastname,status,price,requestDate,orderDate,shop) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = dbConn.prepareStatement(SQL);
            ps.setInt(1,orderId);
            ps.setString(2,orderData.getFirstname());
            ps.setString(3,orderData.getLastname());
            ps.setString(4,orderData.getStatus());
            ps.setDouble(5,orderData.getPrice());
            ps.setTimestamp(6,timeNow);
            ps.setTimestamp(7,orderData.getOrderDate());
            ps.setString(8,orderData.getShop());
            ps.executeUpdate();
            //-----------------------------------------------------------------------
            //-------------------------update orders table
            SQL = "UPDATE orders SET orders.status = 'pending cancellation' WHERE orders.orderNumber = " + orderId + ";";
            dbConn.createStatement().executeUpdate(SQL);
            //-----------------------------------------------------------
            Message msgToClient = new Message();
            msgToClient.setCommand("cancellation request created");
            client.sendToClient(msgToClient);
        } catch (SQLException | IOException e) {
            System.out.println("error creating cancellation request " + e);
            Message msgToClient = new Message();
            msgToClient.setCommand("error creating cancellation request");
            try {
                client.sendToClient(msgToClient);
            } catch (IOException ex) {
                System.out.println("error send to client " + client + ex);
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void getOrders(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        CachedRowSet rowSet;
        int userid = (int) ((Message) msg).getMsg();
        String SQL = "SELECT o.* FROM orders AS o, userorders AS u WHERE u.userid = " + userid + " AND o.orderNumber = u.orderid;";
        try {
            ResultSet rs = dbConn.createStatement().executeQuery(SQL);
            RowSetFactory factory = RowSetProvider.newFactory();
            rowSet = factory.createCachedRowSet();
            rowSet.populate(rs);

            Message msgToClient = new Message();
            msgToClient.setCommand("orders data");
            msgToClient.setMsg((Object) rowSet);
            client.sendToClient(msgToClient);
        } catch (Exception e) {
            System.out.println("error getting or sending items from catalog to client " + e);
            e.printStackTrace();
        }

    }


    private void sendItemPicture(Object msg, ConnectionToClient client) {
        Message msgFromClient = (Message) (msg);
        try {
            Message msgToClient = new Message();
            msgToClient.setCommand("image " + (msgFromClient.getMsg()) + " sent");
            byte[] arr = Files.readAllBytes(Path.of((String) (msgFromClient.getMsg())));
            msgToClient.setMsg((Object) arr);
            client.sendToClient(msgToClient);
        } catch (IOException e) {
            System.out.println("can't open file " + (String) (msgFromClient.getMsg()) + " requested by client " + client);
            e.printStackTrace();
        }
    }

    private void sendCatalog(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        CachedRowSet rowSet;

        String SQL = "SELECT * FROM catalog;";
        try {
            ResultSet rs = dbConn.createStatement().executeQuery(SQL);
            RowSetFactory factory = RowSetProvider.newFactory();
            rowSet = factory.createCachedRowSet();
            rowSet.populate(rs);

            Message msgToClient = new Message();
            msgToClient.setCommand("sending catalog");
            msgToClient.setMsg((Object) rowSet);
            client.sendToClient(msgToClient);
        } catch (Exception e) {
            System.out.println("error getting or sending items from catalog to client " + e);
            e.printStackTrace();

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
            client.sendToClient(message);
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
            if (rs.next() == false) {
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
        if (status.equals("active"))
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
        if (message.getMsg() != null)
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
