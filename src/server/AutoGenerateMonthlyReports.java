package server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.TimerTask;

public class AutoGenerateMonthlyReports extends TimerTask {
    @Override
    public void run() {
        Connection dbConn = SqlConnector.getConnection();

        LocalDate nowDate = LocalDate.now();
        int year = nowDate.getYear();
        int month = 0;
        if(nowDate.getDayOfMonth()==1){//started new month!!!!!!!!!!!!!!!!!!!!!!!!1
            if(nowDate.getMonthValue()==1){
                month = 12;
            }else {
                month = nowDate.getMonthValue()-1;
            }
            try{
                String SQL1 = "select shop from shopmanager;";
                ResultSet rs = dbConn.createStatement().executeQuery(SQL1);
                rs.beforeFirst();
                while(rs.next()){
                String SQL = "SELECT shop,orderNumber,price,dOrder,deliveryDate,orderDate,status,confirmed FROM orders WHERE orderDate between'"+year+"/"+month+"/1' AND '"+year+"/"+month+"/30' AND shop ='"+rs.getString("shop")+"';";
                    ResultSet rs1 = dbConn.createStatement().executeQuery(SQL);
                    rs1.beforeFirst();
                    String orderReport = "";
                    while(rs1.next()){
                        orderReport =orderReport+ "\norder number: " + rs1.getString("orderNumber")
                                +"\nprice: "+rs1.getDouble("price")
                                +"\norder details:"+rs1.getString("dOrder")+"\ndelivery date: " + rs1.getDate("deliveryDate");
                    }
                        String SQLin = "INSERT INTO reports Values (NULL,'order report','"+year+"/"+month+"/1','"+rs.getString("shop")+"','"+orderReport+"');";
                        dbConn.createStatement().execute(SQLin);
                }

            }catch(SQLException e){

            }



        }




    }
}
