package server;

import client.Quarter;
import clientClasses.CancellationRequest;
import clientClasses.Message;
import clientClasses.WantedReport;
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
    private int userid;
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
        AutoGenerateMonthlyReports monthlyOrders = new AutoGenerateMonthlyReports();
        monthlyOrders.run();
//        Timer orderTimer = new Timer();
//        orderTimer.scheduleAtFixedRate(monthlyOrders, 0, 60 * 1000*60*24*30);
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
            case "reviewCancellation":
                reviewCancellation(msg,client);
                break;
            case "confirmCancellation":
                confirmCancellation(msg,client);
                break;
            case "reviewOrdersToConfirm":
                reviewOrdersToConfirm(msg,client);
                break;
            case "confirmOrder":
                confirmOrder(msg,client);
                break;
            case "viewReports":
                viewReports(msg,client);
                break;
            case "viewSpecificReports":
                viewSpecificReports(msg,client);
                break;
            case "viewComplaint":
                viewComplaint(msg,client);
                break;
            case "viewOverallIncome":
                viewOverallIncome(msg,client);
                break;
            case "manageCustomers":
                manageCustomers(msg,client);
                break;
            case "confirmFreeze":
                confirmFreeze(msg,client);
                break;
            case "confirmUnfreeze":
                confirmUnfreeze(msg,client);
                break;
            case "manageUsers":
                manageUsers(msg,client);
                break;
            case "approveCustomer":
                approveCustomer(msg,client);
                break;
            case "showUsersPermissionsTable":
                showUsersPermissionsTable(msg,client);
                break;
            case "changePermission":
                changePermission(msg,client);
                break;
            case "getName":
                getName(msg,client);
                break;
            case "activeDiscount":
                activeDiscount(msg,client);
                break;
            case "ifDiscount":
                ifDiscount(client);
                break;
            case "DistinctiveDiscount":
                DistinctiveDiscount(client);
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
                    "FROM users u, userorders uo, orders o" +
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
            SQL = "INSERT INTO cancellationrequests(orderID,firstName,lastName,status,price,DeliveryDate,requestDate,shop) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = dbConn.prepareStatement(SQL);
            ps.setInt(1,orderId);
            ps.setString(2,orderData.getFirstname());
            ps.setString(3,orderData.getLastname());
            ps.setString(4,orderData.getStatus());
            ps.setDouble(5,orderData.getPrice());
            ps.setTimestamp(7,timeNow);
            ps.setTimestamp(6,orderData.getOrderDate());
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
     * DistinctiveDiscount - ends the discount, return origin price to catalog, update all filed as in origin
     * @param client - receives specific client connection
     */
    private void DistinctiveDiscount(ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message message = new Message();
        String SQL = "Update catalog SET price = priceorigin";
        String SQL1 = "Update catalog SET discount = 0";
        String SQL2 = "Update catalog SET priceorigin = 0";
        try { dbConn.createStatement().executeUpdate(SQL); dbConn.createStatement().executeUpdate(SQL1); dbConn.createStatement().executeUpdate(SQL2);}
        catch (SQLException e) { System.out.println("Error Activating Discount : " + SQL + " " + e);
            try { client.sendToClient("Activating failed"); }
            catch (IOException e1) { System.out.println("sending data to client " + client + " error " + e1); }
        }
        try {
            message.setCommand("Discount Dis Activated"); client.sendToClient(message); }
        catch (IOException e) { System.out.println("sending data to client " + client + " error " + e); }
    }

    /**
     * ifDiscount - checks if discount is available in catalog, or there is no discount.
     * @param client - receives specific client connection
     */
    private void ifDiscount(ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        String SQL = "SELECT discount FROM catalog WHERE id = '1'";
        Message message = new Message();
        float num = 0;
        try {
            ResultSet rs = dbConn.createStatement().executeQuery(SQL);
            RowSetFactory factory = RowSetProvider.newFactory();
            CachedRowSet cachedMsg = factory.createCachedRowSet();
            cachedMsg.populate(rs);
            rs.close();
            try { while (cachedMsg.next()) { num = cachedMsg.getFloat("discount"); }}
            catch (SQLException e) { System.out.println("Error read data from server " + e); }
            message.setMsg(num);
            message.setCommand("");
            client.sendToClient(message);
        }
        catch (SQLException | IOException e) { System.out.println("SQL request from client " + client + " error " + e);
            try { client.sendToClient("error in sql request or server"); }
            catch (IOException e1) { System.out.println("sending data to client " + client + " error " + e1); }
        }
    }

    /**
     * activeDiscount - activating discount for catalog, change prices, save origin price and save the amount of discount
     * @param msg -  receives message from client connection
     * @param client - receives specific client connection
     */
    private void activeDiscount(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message message = (Message) msg;
        String SQL = "Update catalog SET discount = '"+ (float)message.getMsg()/100+"'";
        String SQL1 = "Update catalog SET priceorigin = price";
        String SQL2 = "Update catalog SET price = '"+ (100 -(float)message.getMsg())/100 +"' * price ";
        try { dbConn.createStatement().executeUpdate(SQL); dbConn.createStatement().executeUpdate(SQL1); dbConn.createStatement().executeUpdate(SQL2);}
        catch (SQLException e) { System.out.println("Error Activating Discount : " + SQL + " " + e);
            try { client.sendToClient("Activating failed"); }
            catch (IOException e1) { System.out.println("sending data to client " + client + " error " + e1); }
        }
        try { message.setCommand("Discount activated"); client.sendToClient(message); }
        catch (IOException e) { System.out.println("sending data to client " + client + " error " + e); }
    }

    /**
     * insertSurveyAnswers - insert survey answers for specific client into Data Base
     * @param msg -  receives message from client connection
     * @param client - receives specific client connection
     */
    private void insertSurveyAnswers(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Object data[] = (Object[])((Message) msg).getMsg();
        Message message = new Message();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        int datanum[] = (int[]) data[0];
        String SQL = "INSERT INTO surveyanswers (idsurvey,a1,a2,a3,a4,a5,a6,surveydate) VALUES ('"+ datanum[0] + "','" + datanum[1] + "','" + datanum[2] + "','" + datanum[3] + "','"
                                                                                        + datanum[4] +"','" + datanum[5] + "','" + datanum[6] +"','"+ timestamp +"')";
        try { dbConn.createStatement().executeUpdate(SQL); }
        catch (SQLException e) { System.out.println("Error insert survey result to table : " + SQL + " " + e);
            try { client.sendToClient("inserting failed"); }
            catch (IOException e1) { System.out.println("sending data to client " + client + " error " + e1); }
        }
        try { message.setCommand("Answer Inserted"); client.sendToClient(message); }
        catch (IOException e) { System.out.println("sending data to client " + client + " error " + e); }
    }

    /**
     * setSurveyQuestions - collect the survey question and return it asa CachedRowSet message to client
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
     * getQuantityRows - calculate amount of rows in Data Base and return the value to client
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
     * confirmDelivery - server receives message from client about confirming specific deliver order
     * @param msg - receives message from client connection
     * @param client - receives specific client connection
     */
    private void confirmDelivery(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        CachedRowSet cachedMsg;
        Message message = (Message) msg;
        ResultSet rs;
        RowSetFactory factory;
        int[] orderData = {((int[]) message.getMsg())[0], ((int[])message.getMsg())[1]};
        int customerID = 0;
        float price = 0;
        String SQL = "Update delivery SET confirmed = 'yes' WHERE deliveryGuyId = '" + orderData[0] + "' AND orderNumber = '" + orderData[1] + "'";
        String SQL1 = "Update orders SET status = 'delivered' WHERE orderNumber = '" + orderData[1] + "' AND status = 'pending for delivery' ";
        String SQL2 ="SELECT confirmDate FROM orders WHERE orderNumber ='"+orderData[1]+"'";
        Timestamp time = null,TimeNow;
        try { dbConn.createStatement().executeUpdate(SQL); dbConn.createStatement().executeUpdate(SQL1);
            rs = dbConn.createStatement().executeQuery(SQL2);
            factory= RowSetProvider.newFactory();
            cachedMsg = factory.createCachedRowSet(); cachedMsg.populate(rs);
            rs.close();
            try { while (cachedMsg.next()) { time = cachedMsg.getTimestamp("confirmDate"); }}
            catch (SQLException e) { System.out.println("Error read data from server " + e); }
            TimeNow = new Timestamp(System.currentTimeMillis());
            long diff = TimeNow.getTime() - time.getTime();
            if ((diff/1000)/3600 > 3){
                message.setCommand("yes");
                try{
                    SQL2 ="SELECT price FROM orders WHERE orderNumber ='"+orderData[1]+"'";
                    rs = dbConn.createStatement().executeQuery(SQL2);
                    factory = RowSetProvider.newFactory();
                    cachedMsg = factory.createCachedRowSet(); cachedMsg.populate(rs);
                    rs.close();
                    try {
                        while (cachedMsg.next()) { price = cachedMsg.getFloat("price");}
                        updateBalance(orderData[1],price);}
                    catch (SQLException e) { System.out.println("Error read data from server " + e); }
                } catch (Exception e) { System.out.println("Error read data from server " + e); }
            }
            else message.setCommand("no");
        }
        catch (SQLException e) { System.out.println("Error update delivery table : " + SQL + " " + e);
            try { client.sendToClient("confirm failed"); }
            catch (IOException e1) { System.out.println("sending data to client " + client + " error " + e1); }
        }
        try {
            SQL1 = "SELECT userid FROM userorders uo WHERE uo.orderid = "+orderData[1]+";";
            try { rs = dbConn.createStatement().executeQuery(SQL1); rs.first(); customerID = (rs.getInt("userid"));
            }catch(SQLException e){ System.out.println("sql ERROR :"+e); }
            SQL1 = "SELECT * FROM users WHERE userid = "+customerID+";";
            try {
                rs = dbConn.createStatement().executeQuery(SQL1);
                factory = RowSetProvider.newFactory();
                cachedMsg = factory.createCachedRowSet(); cachedMsg.populate(rs);
                rs.close();
                message.setCommand(""); message.setMsg(cachedMsg);
            }catch(SQLException e){ System.out.println("sql ERROR :"+e); } client.sendToClient(message); }
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
        try { message.setCommand("Complaint Inserted"); client.sendToClient( message); }
        catch (IOException e) { System.out.println("sending data to client " + client + " error " + e); }
    }

 /////////////////////////////// End Of Habib Ibrahim
    private void viewOverallIncome(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message m = new Message();
        Message message = (Message) msg;
        Quarter quarter = (Quarter) message.getMsg();
        Quarter qr = new Quarter();
        double m1=0,m2=0,m3=0;
        switch (quarter.getQuarter()) {//year , Quarter
            case 1:
                qr.setMonth1("Jan");
                qr.setMonth2("Feb");
                qr.setMonth3("Mar");
                try {
                    String SQL1 = "SELECT MONTH(orderDate) as date,price FROM orders o WHERE o.shop = '" + quarter.getShop() + "' AND o.orderDate between '" + quarter.getYear() + "/01/01' and '" + quarter.getYear() + "/03/31' AND o.confirmed = 'yes' AND o.status != 'cancelled';";
                    ResultSet rs = dbConn.createStatement().executeQuery(SQL1);
                    rs.beforeFirst();
                    while (rs.next()) {
                        if (rs.getInt("date") == 1) {
                            m1=m1+rs.getDouble("price");
                        } else {
                            if (rs.getInt("date") == 2) {
                                m2=m2+rs.getDouble("price");
                            } else {
                                if (rs.getInt("date") == 3)
                                    m3=m3+rs.getDouble("price");
                            }
                        }
                    }

                }catch(SQLException e){
                }
                qr.setResult1(m1);
                qr.setResult2(m2);
                qr.setResult3(m3);
                try{
                    m.setCommand("income");
                    m.setMsg(qr);
                    client.sendToClient(m);
                }catch(IOException e){

                }

                break;
            case 2:
                qr.setMonth1("Apr");
                qr.setMonth2("May");
                qr.setMonth3("Jun");
                try {
                    String SQL1 = "SELECT MONTH(orderDate) as date,price FROM orders o WHERE o.shop = '" + quarter.getShop() + "' AND o.orderDate between '" + quarter.getYear() + "/04/01' and '" + quarter.getYear() + "/06/30' AND o.confirmed = 'yes' AND o.status != 'cancelled';";
                    ResultSet rs = dbConn.createStatement().executeQuery(SQL1);
                    rs.beforeFirst();
                    while (rs.next()) {
                        if (rs.getInt("date") == 4) {
                            m1=m1+rs.getDouble("price");
                        } else {
                            if (rs.getInt("date") == 5) {
                                m2=m2+rs.getDouble("price");
                            } else {
                                if (rs.getInt("date") == 6)
                                    m3=m3+rs.getDouble("price");
                            }
                        }
                    }

                }catch(SQLException e){
                }
                qr.setResult1(m1);
                qr.setResult2(m2);
                qr.setResult3(m3);
                try{
                    m.setCommand("income");
                    m.setMsg(qr);
                    client.sendToClient(m);
                }catch(IOException e){

                }

                break;
            case 3:
                qr.setMonth1("Jul");
                qr.setMonth2("Aug");
                qr.setMonth3("Sep");
                try {
                    String SQL1 = "SELECT MONTH(orderDate) as date,price FROM orders o WHERE o.shop = '" + quarter.getShop() + "' AND o.orderDate between '" + quarter.getYear() + "/07/01' and '" + quarter.getYear() + "/09/30' AND o.confirmed = 'yes' AND o.status != 'cancelled';";
                    ResultSet rs = dbConn.createStatement().executeQuery(SQL1);
                    rs.beforeFirst();
                    while (rs.next()) {
                        if (rs.getInt("date") == 7) {
                            m1=m1+rs.getDouble("price");
                        } else {
                            if (rs.getInt("date") == 8) {
                                m2=m2+rs.getDouble("price");
                            } else {
                                if (rs.getInt("date") == 9)
                                    m3=m3+rs.getDouble("price");
                            }
                        }
                    }

                }catch(SQLException e){
                }
                qr.setResult1(m1);
                qr.setResult2(m2);
                qr.setResult3(m3);
                try{
                    m.setCommand("income");
                    m.setMsg(qr);
                    client.sendToClient(m);
                }catch(IOException e){

                }

                break;
            case 4:
                qr.setMonth1("Oct");
                qr.setMonth2("Nov");
                qr.setMonth3("Dec");
                try {
                    String SQL1 = "SELECT MONTH(orderDate) as date,price FROM orders o WHERE o.shop = '" + quarter.getShop() + "' AND o.orderDate between '" + quarter.getYear() + "/10/01' and '" + quarter.getYear() + "/12/31' AND o.confirmed = 'yes' AND o.status != 'cancelled';";
                    ResultSet rs = dbConn.createStatement().executeQuery(SQL1);
                    rs.beforeFirst();
                    while (rs.next()) {
                        if (rs.getInt("date") == 10) {
                            m1=m1+rs.getDouble("price");
                        } else {
                            if (rs.getInt("date") == 11) {
                                m2=m2+rs.getDouble("price");
                            } else {
                                if (rs.getInt("date") == 12)
                                    m3=m3+rs.getDouble("price");
                            }
                        }
                    }
                }catch(SQLException e){
                }
                qr.setResult1(m1);
                qr.setResult2(m2);
                qr.setResult3(m3);
                try{
                    m.setCommand("income");
                    m.setMsg(qr);
                    client.sendToClient(m);
                }catch(IOException e){
                }
                break;
        }
    }

    private void viewComplaint(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message m = new Message();
        Message message = (Message) msg;
        Quarter quarter = (Quarter) message.getMsg();
        Quarter qr = new Quarter();
        double m1=0,m2=0,m3=0;


    switch (quarter.getQuarter()) {//year , Quarter
            case 1:
                qr.setMonth1("Jan");
                qr.setMonth2("Feb");
                qr.setMonth3("Mar");
                try {
                    String SQL1 = "SELECT MONTH(date) as date FROM complaints WHERE shop = '" + quarter.getShop() + "' AND date between '" + quarter.getYear() + "/01/01' and '" + quarter.getYear() + "/03/31' AND complaintDone = 'done';";
                    ResultSet rs = dbConn.createStatement().executeQuery(SQL1);
                        rs.beforeFirst();
                    while (rs.next()) {
                        if (rs.getInt("date") == 1) {
                            m1++;
                        } else {
                            if (rs.getInt("date") == 2) {
                                m2++;
                            } else {
                                if (rs.getInt("date") == 3)
                                    m3++;
                            }
                        }
                    }

                }catch(SQLException e){
                }
                qr.setResult1(m1);
                qr.setResult2(m2);
                qr.setResult3(m3);
                try{
                    m.setCommand("quarter1");
                    m.setMsg(qr);
                    client.sendToClient(m);
                }catch(IOException e){

                }

                break;
            case 2:
                qr.setMonth1("Apr");
                qr.setMonth2("May");
                qr.setMonth3("Jun");
                try {
                    String SQL1 = "SELECT MONTH(date) as date FROM complaints WHERE shop = '" + quarter.getShop() + "' AND date between '" + quarter.getYear() + "/04/01' and '" + quarter.getYear() + "/06/30' AND complaintDone = 'done';";
                    ResultSet rs = dbConn.createStatement().executeQuery(SQL1);
                    rs.beforeFirst();
                    while (rs.next()) {
                        if (rs.getInt("date") == 4) {
                            m1++;
                        } else {
                            if (rs.getInt("date") == 5) {
                                m2++;
                            } else {
                                if (rs.getInt("date") == 6)
                                    m3++;
                            }
                        }
                    }

                }catch(SQLException e){
                }
                qr.setResult1(m1);
                qr.setResult2(m2);
                qr.setResult3(m3);
                try{
                    m.setCommand("quarter1");
                    m.setMsg(qr);
                    client.sendToClient(m);
                }catch(IOException e){

                }

                break;
            case 3:
                qr.setMonth1("Jul");
                qr.setMonth2("Aug");
                qr.setMonth3("Sep");
                try {
                    String SQL1 = "SELECT MONTH(date) as date FROM complaints WHERE shop = '" + quarter.getShop() + "' AND date between '" + quarter.getYear() + "/07/01' and '" + quarter.getYear() + "/09/30' AND complaintDone = 'done';";
                    ResultSet rs = dbConn.createStatement().executeQuery(SQL1);
                    rs.beforeFirst();
                    while (rs.next()) {
                        if (rs.getInt("date") == 7) {
                            m1++;
                        } else {
                            if (rs.getInt("date") == 8) {
                                m2++;
                            } else {
                                if (rs.getInt("date") == 9)
                                    m3++;
                            }
                        }
                    }

                }catch(SQLException e){
                }
                qr.setResult1(m1);
                qr.setResult2(m2);
                qr.setResult3(m3);
                try{
                    m.setCommand("quarter1");
                    m.setMsg(qr);
                    client.sendToClient(m);
                }catch(IOException e){

                }

                break;
            case 4:
                qr.setMonth1("Oct");
                qr.setMonth2("Nov");
                qr.setMonth3("Dec");
                try {
                    String SQL1 = "SELECT MONTH(date) as date FROM complaints WHERE shop = '" + quarter.getShop() + "' AND date between '" + quarter.getYear() + "/10/01' and '" + quarter.getYear() + "/12/31' AND complaintDone = 'done';";
                    ResultSet rs = dbConn.createStatement().executeQuery(SQL1);
                    rs.beforeFirst();
                    while (rs.next()) {
                        if (rs.getInt("date") == 10) {
                            m1++;
                        } else {
                            if (rs.getInt("date") == 11) {
                                m2++;
                            } else {
                                if (rs.getInt("date") == 12)
                                    m3++;
                            }
                        }
                    }
                }catch(SQLException e){
                }
                qr.setResult1(m1);
                qr.setResult2(m2);
                qr.setResult3(m3);
                try{
                    m.setCommand("quarter1");
                    m.setMsg(qr);
                    client.sendToClient(m);
                }catch(IOException e){
                }
                break;
        }
    }

    private void viewSpecificReports(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message m = new Message();
        Message message = (Message) msg;
        WantedReport wr ;
        wr = (WantedReport)message.getMsg();
        CachedRowSet cachedMsg = null;
                switch (wr.getReportType()) {
            case "Orders report":
                try {
                    String SQL = "SELECT orderNumber,price,dOrder,deliveryDate,orderDate,status,confirmed FROM orders o WHERE o.shop= " + '"' + wr.getShopName() + '"' +"AND orderDate between '"+wr.getFrom()+"' and '"+wr.getTo()+"';";
                    ResultSet rs = dbConn.createStatement().executeQuery(SQL);
                    RowSetFactory factory = RowSetProvider.newFactory();
                    cachedMsg = factory.createCachedRowSet();
                    cachedMsg.populate(rs);
                    rs.close();

                    m.setCommand("orders report");
                    m.setMsg(cachedMsg);
                    client.sendToClient(m);
                }catch(SQLException | IOException e){
                    System.out.println("Erorr "+e);
                }
                break;
            case "Income report":
                String SQL1 = "SELECT orderNumber,price FROM orders o WHERE o.shop= " + '"' + wr.getShopName() + '"' +"AND orderDate between '"+wr.getFrom()+"' and '"+wr.getTo()+"'"+
                        "And o.confirmed = 'yes' and o.status != 'cancelled'"+";";
                try {
                    ResultSet rs = dbConn.createStatement().executeQuery(SQL1);
                    RowSetFactory factory = RowSetProvider.newFactory();
                    cachedMsg = factory.createCachedRowSet();
                    cachedMsg.populate(rs);
                    rs.close();

                    m.setCommand("income report");
                    m.setMsg(cachedMsg);
                    client.sendToClient(m);
                }catch(SQLException | IOException e){
                    System.out.println("Erorr "+e);
                }
                break;
        }



    }

    private void viewReports(Object msg, ConnectionToClient client) {

        Connection dbConn = SqlConnector.getConnection();
        Message m = new Message();
        String SQL = "SELECT usertype FROM login s WHERE s.userid="+userid+";";
        String userType = "";
        CachedRowSet cachedMsg;
        try{
            ResultSet rs = dbConn.createStatement().executeQuery(SQL);
            rs.first();
            userType = rs.getString("usertype");

        }catch(SQLException e){
            System.out.println("sql error" + e);
        }
        if(userType.equals("ceo")){
            String SQL1 ="SELECT shop FROM shopmanager;";
            try{
                ResultSet rs = dbConn.createStatement().executeQuery(SQL1);
                RowSetFactory factory = RowSetProvider.newFactory();
                cachedMsg = factory.createCachedRowSet();
                cachedMsg.populate(rs);
                rs.close();
                m.setCommand("ceo");
                m.setMsg(cachedMsg);
                client.sendToClient(m);

            }catch(SQLException | IOException e){
                System.out.println("sql error" + e);
            }

        }
        else{
            String SQL2 ="SELECT shop FROM shopmanager s WHERE s.userid="+userid+";";
            try{
                ResultSet rs = dbConn.createStatement().executeQuery(SQL2);
                RowSetFactory factory = RowSetProvider.newFactory();
                cachedMsg = factory.createCachedRowSet();
                cachedMsg.populate(rs);
                rs.close();
                m.setCommand("shopmanager");
                m.setMsg(cachedMsg);
                client.sendToClient(m);


            }catch(SQLException | IOException e){
                System.out.println("sql error" + e);
            }
        }//if(username.equals("manager"))
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
        userid = -1;
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
        try { userLoginData.setCommand("logged in"); userLoginData.setMsg(cachedMsg);
                 client.sendToClient(userLoginData); //send login data to client
        } catch (IOException e) {
            System.out.println("sending data to client " + client + " error " + e);

        }
    }

    private void reviewCancellation(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        CancellationRequest cnlRequest = new CancellationRequest();// (CancellationRequest) msg;
        String shop="";
        Message m = new Message();
        String cancelled = "cancelled";
        String SQL1 = "SELECT shop FROM shopmanager s WHERE s.userid="+userid+";";//returns the shop of the manager

        CachedRowSet cachedMsg = null;
        try {
            ResultSet rs = dbConn.createStatement().executeQuery(SQL1);
            rs.first();
            shop = (rs.getString("shop"));
        }catch(SQLException e){
            System.out.println("sql ERROR :"+e);
        }
        String SQL = "SELECT * FROM cancellationrequests c WHERE c.status != "+'"' + cancelled + '"'+ "AND c.shop= "+'"' + shop + '"'+";";
        try {
            ResultSet rs = dbConn.createStatement().executeQuery(SQL);
            RowSetFactory factory = RowSetProvider.newFactory();
            cachedMsg = factory.createCachedRowSet();
            cachedMsg.populate(rs);
            rs.close();

            m.setCommand("get cancellation table");
            m.setMsg(cachedMsg);
            client.sendToClient(m);
            //}
        } catch (SQLException | IOException e) {
            System.out.println("SQL request from client " + client + " error " + e);
            try {
                client.sendToClient("error in sql request or server");
            } catch (IOException e1) {
                System.out.println("sending data to client " + client + " error " + e1);
            }
        }


    }

    private void confirmCancellation(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();

        Message msgToClient=new Message();
        Message m=(Message) msg;
        int key=(int)m.getMsg();
        String SQL = "UPDATE cancellationrequests SET status = 'cancelled' WHERE (`orderID` = "+key +");";
        String SQL1 = "UPDATE orders SET status = 'cancelled' WHERE (`orderNumber` = "+key +");";
        try {
            //ResultSet rs =
            dbConn.createStatement().executeUpdate(SQL);
            dbConn.createStatement().executeUpdate(SQL1);
            calculateRefund(key,client);
        } catch (SQLException | IOException e) {
            System.out.println("SQL request from client " + client + " error " + e);
            try {
                client.sendToClient("error in sql request or server");
            } catch (IOException e1) {
                System.out.println("sending data to client " + client + " error " + e1);
            }
        }
    }

    private void calculateRefund(int orderID,ConnectionToClient client) throws IOException, SQLException {
        double timeDiff = 0.0;
        double price =0.0;
        Message msgToClient=new Message();
        Connection dbConn = SqlConnector.getConnection();
        String SQL = "SELECT TIMESTAMPDIFF(SECOND,requestDate, deliveryDate) diff,price FROM cancellationrequests c WHERE c.orderID="+orderID+";";
        try {
            ResultSet rs = dbConn.createStatement().executeQuery(SQL);
            rs.first();
            timeDiff = (rs.getDouble("diff"));
            rs.findColumn("price");
            price = rs.getDouble("price");

        }catch(SQLException e){
            System.out.println("sql ERROR :"+e);
        }
        if(timeDiff>=10800) {
            msgToClient.setCommand("refund");
            msgToClient.setMsg(price);
            client.sendToClient(msgToClient);

        }else if((0<=timeDiff)&&(timeDiff<=3600)){
            msgToClient.setCommand("refund");
            msgToClient.setMsg(0.0);
            client.sendToClient(msgToClient);

        }else {
            msgToClient.setCommand("refund");
            msgToClient.setMsg(price/2);
            client.sendToClient(msgToClient);
        }
        updateBalance( orderID,(double)msgToClient.getMsg());
    }

    private void updateBalance(int orderID,double refund){
        int customerIDForRefund=0;
        double balance = 0;
        Connection dbConn = SqlConnector.getConnection();
        String SQL1 = "SELECT userid FROM userorders uo WHERE uo.orderid = "+orderID+";";//return userid of user to be refunded
        try {
            ResultSet rs = dbConn.createStatement().executeQuery(SQL1);
            rs.first();
            customerIDForRefund = (rs.getInt("userid"));
        }catch(SQLException e){
            System.out.println("sql ERROR :"+e);
        }
        String SQL2 = "SELECT balance FROM balance u WHERE u.userid = "+customerIDForRefund+";";
        try {
            ResultSet rs = dbConn.createStatement().executeQuery(SQL2);
            rs.first();
            balance = (rs.getDouble("balance"));
        }catch(SQLException e){
            System.out.println("sql ERROR :"+e);
        }
        balance = balance+refund;
        String SQL3 = "UPDATE balance SET `balance` = "+balance+" WHERE (`userid` ="+customerIDForRefund+");";
        try {
            dbConn.createStatement().executeUpdate(SQL3);
        }catch(SQLException e){
            System.out.println("sql ERROR :"+e);
        }
    }

    private void reviewOrdersToConfirm(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message m = new Message();
        CachedRowSet cachedMsg = null;

        String SQL = "SELECT o.orderNumber, u.firstname, u.lastname, o.deliveryDate, u.phonenumber, o.price FROM orders o, users u, userorders uo  WHERE o.orderNumber=uo.orderid AND uo.userid=u.userid AND o.confirmed ="+'"' + "no" + '"'+";";
        try {
            ResultSet rs = dbConn.createStatement().executeQuery(SQL);
            RowSetFactory factory = RowSetProvider.newFactory();
            cachedMsg = factory.createCachedRowSet();
            cachedMsg.populate(rs);
            rs.close();
            m.setCommand("tableForOrdersToConfirm");
            m.setMsg(cachedMsg);
            client.sendToClient(m);
        }
        catch (SQLException | IOException e) {
            System.out.println("SQL request from client " + client + " error " + e);
            try {
                client.sendToClient("error in sql request or server");
            } catch (IOException e1) {
                System.out.println("sending data to client " + client + " error " + e1);
            }
        }
    }

    private void confirmOrder(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message msgToClient=new Message();
        Message m=(Message) msg;
        int key=(int)m.getMsg();
        String SQL = "UPDATE orders SET confirmed = 'yes' WHERE (`orderNumber` = "+key +");";
        try {
            dbConn.createStatement().executeUpdate(SQL);
            msgToClient.setCommand("confirmed");
            msgToClient.setMsg("");
            client.sendToClient(msgToClient);
        } catch (SQLException | IOException e ) {
            System.out.println("SQL request from client " + client + " error " + e);
            try {
                client.sendToClient("error in sql request or server");
            } catch (IOException e1) {
                System.out.println("sending data to client " + client + " error " + e1);
            }
        }

    }

    /**
     * manageCustomers method gets an information from 2 tables DB and sends it to the client who asked for it.
     * @param msg
     * @param client
     */
    private void manageCustomers(Object msg, ConnectionToClient client) {

        Connection dbConn = SqlConnector.getConnection();
        Message m = new Message();

        String SQL1 = "SELECT u.userid,u.firstname,u.lastname,u.phonenumber,u.email,b.balance,l.status FROM users u,login l,balance b WHERE b.userid=u.userid AND u.userid=l.userid AND l.usertype="+'"' + "customer" + '"'+";";
        CachedRowSet cachedMsg = null;
        try {
            ResultSet rs = dbConn.createStatement().executeQuery(SQL1);
            RowSetFactory factory = RowSetProvider.newFactory();
            cachedMsg = factory.createCachedRowSet();
            cachedMsg.populate(rs);
            rs.close();

            m.setCommand("customersToFreeze/UnFreeze");
            m.setMsg(cachedMsg);
            client.sendToClient(m);
        } catch (SQLException | IOException e) {
            System.out.println("SQL request from client " + client + " error " + e);
            try {
                client.sendToClient("error in sql request or server");
            } catch (IOException e1) {
                System.out.println("sending data to client " + client + " error " + e1);
            }
        }


    }

    /**
     * confirmFreeze method updates the login status field in the DB to 'frozen' for userid sent by client.
     * @param msg
     * @param client
     */
    private void confirmFreeze(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message msgToClient=new Message();
        Message m=(Message) msg;
        int key=(int)m.getMsg();// the key is userid
        String SQL = "UPDATE login SET status = 'frozen' WHERE (`userid` = "+key +");";
        try {
            dbConn.createStatement().executeUpdate(SQL);
            msgToClient.setCommand("frozen");
            msgToClient.setMsg("");
            client.sendToClient(msgToClient);
        } catch (SQLException | IOException e ) {
            System.out.println("SQL request from client " + client + " error " + e);
            try {
                client.sendToClient("error in sql request or server");
            } catch (IOException e1) {
                System.out.println("sending data to client " + client + " error " + e1);
            }
        }
    }
    /**
     * confirmUnfreeze method updates the login status field in the DB to 'active' for userid sent by client.
     * @param msg
     * @param client
     */
    private void confirmUnfreeze(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message msgToClient=new Message();
        Message m=(Message) msg;
        int key=(int)m.getMsg();// the key is userid
        String SQL = "UPDATE login SET status = 'active' WHERE (`userid` = "+key +");";
        try {
            dbConn.createStatement().executeUpdate(SQL);
            msgToClient.setCommand("unfrozen");
            msgToClient.setMsg("");
            client.sendToClient(msgToClient);
        } catch (SQLException | IOException e ) {
            System.out.println("SQL request from client " + client + " error " + e);
            try {
                client.sendToClient("error in sql request or server");
            } catch (IOException e1) {
                System.out.println("sending data to client " + client + " error " + e1);
            }
        }

    }

    /**
     * manageUsers method is responsible for accessing the DB
     * and sending the information regarding the customers in registration
     * table to the client who asked for it
     * @param msg
     * @param client
     */
    private void manageUsers(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message m = new Message();
        String SQL1 = "SELECT r.userid,r.firstname,r.lastname,r.telephoneNumber,r.email,r.type FROM registration r;";
        CachedRowSet cachedMsg;
        try {
            ResultSet rs = dbConn.createStatement().executeQuery(SQL1);
            RowSetFactory factory = RowSetProvider.newFactory();
            cachedMsg = factory.createCachedRowSet();
            cachedMsg.populate(rs);
            rs.close();

            m.setCommand("showCustomersFromRegistration");
            m.setMsg(cachedMsg);
            client.sendToClient(m);
        } catch (SQLException | IOException e) {
            System.out.println("SQL request from client " + client + " error " + e);
            try {
                client.sendToClient("error in sql request or server");
            } catch (IOException e1) {
                System.out.println("sending data to client " + client + " error " + e1);
            }
        }


    }

    /**
     * approveCustomer method gets the customer's details from the client,
     * inserts the customer to users list (users list is for customers),
     * then it inserts the user to login table and removes it from registration table because it's already approved.
     * @param msg
     * @param client
     */
    private void approveCustomer(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message m=(Message) msg;
        Object[] o= (Object[]) m.getMsg();
        int key=(int)o[0];
        String savedUsername="'"+(String) o[1]+"'", savedPassword="'"+(String) o[2]+"'";

        String SQL_InsertToUsers= "INSERT INTO users (userid, firstname, lastname, email,phonenumber,creditcardnumber,creditcardcvv,creditcardexpiredate)" +
                " SELECT r.userid,r.firstname,r.lastname,r.email,r.telephoneNumber,r.creditCardNumber,r.creditCardCVV,r.creditCardExpiryDate FROM registration r WHERE r.userid="+key+";";

        String SQL_InsertToLogin ="INSERT INTO login (userid, username, password, usertype,status)" +
                " SELECT r.userid,"+savedUsername+","+savedPassword+",r.type,+"+"'active'"+" FROM registration r WHERE r.userid="+key+";";

        String SQL_DeleteFromRegistration ="DELETE FROM registration WHERE userid="+key+";";
        String SQL_AddBalance="INSERT INTO balance (userid) VALUES ("+key+")";

        try {
            dbConn.createStatement().execute(SQL_InsertToUsers);
            dbConn.createStatement().execute(SQL_InsertToLogin);
            dbConn.createStatement().execute(SQL_DeleteFromRegistration);
            dbConn.createStatement().execute(SQL_AddBalance);

            m.setCommand("approved");
            m.setMsg("");
            client.sendToClient(m);
        } catch (SQLException | IOException e) {
            System.out.println("SQL request from client " + client + " error " + e);
            try {
                client.sendToClient("error in sql request or server");
            } catch (IOException e1) {
                System.out.println("sending data to client " + client + " error " + e1);
            }
        }
    }

    /**
     * showUsersPermissionsTable method is responsible for accessing the DB,
     * getting the users' info from login table and sending it to the client who asked for it.
     *
     * @param msg
     * @param client
     */
    private void showUsersPermissionsTable(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message m = new Message();

        String SQL1 = "SELECT l.userid,l.username,l.usertype FROM login l WHERE l.usertype != 'customer' AND l.usertype != 'manager' AND l.usertype != 'ceo';";
        CachedRowSet cachedMsg;
        try {
            ResultSet rs = dbConn.createStatement().executeQuery(SQL1);
            RowSetFactory factory = RowSetProvider.newFactory();
            cachedMsg = factory.createCachedRowSet();
            cachedMsg.populate(rs);
            rs.close();

            m.setCommand("showedUsersPermTable");
            m.setMsg(cachedMsg);
            client.sendToClient(m);
        } catch (SQLException | IOException e) {
            System.out.println("SQL request from client " + client + " error " + e);
            try {
                client.sendToClient("error in sql request or server");
            } catch (IOException e1) {
                System.out.println("sending data to client " + client + " error " + e1);
            }
        }

    }

    /**
     * changePermission method changes the permission of user according to what it got from client,
     * it does so by accessing the DB - login table - and changing the usertype field in it.
     * @param msg
     * @param client
     */
    private void changePermission(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message m=(Message) msg;
        Object[] o= (Object[]) m.getMsg();
        int key=(int)o[0];
        String savedType="'"+(String) o[1]+"'";

        String SQL_UpdateLogin= "UPDATE login SET usertype = "+savedType+" WHERE (`userid` = "+key +");";

        try {
            dbConn.createStatement().executeUpdate(SQL_UpdateLogin);
            m.setCommand("Type updated");
            m.setMsg("");
            client.sendToClient(m);
        } catch (SQLException | IOException e) {
            System.out.println("SQL request from client " + client + " error " + e);
            try {
                client.sendToClient("error in sql request or server");
            } catch (IOException e1) {
                System.out.println("sending data to client " + client + " error " + e1);
            }
        }
    }

    /**
     * getName method gets an userid and sends the first and last name that belongs to that userid to the client asked for it.
     * @param msg
     * @param client
     */
    private void getName(Object msg, ConnectionToClient client) {
        Connection dbConn = SqlConnector.getConnection();
        Message msgToClient=new Message();
        Message m=(Message) msg;
        int key=(int)m.getMsg();// the key is userid
        String SQL = "SELECT u.firstname, u.lastname FROM users u WHERE (`userid` = "+key +");";
        CachedRowSet cachedMsg;
        try {
            ResultSet rs = dbConn.createStatement().executeQuery(SQL);
            RowSetFactory factory = RowSetProvider.newFactory();
             cachedMsg = factory.createCachedRowSet();
            cachedMsg.populate(rs);
            rs.close();
            msgToClient.setCommand("firstname+lastname");
            msgToClient.setMsg(cachedMsg);
            client.sendToClient(msgToClient);
        } catch (SQLException | IOException e ) {
            System.out.println("SQL request from client " + client + " error " + e);
            try {
                client.sendToClient("error in sql request or server");
            } catch (IOException e1) {
                System.out.println("sending data to client " + client + " error " + e1);
            }
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