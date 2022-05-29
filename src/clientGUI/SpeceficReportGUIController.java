package clientGUI;

import client.ClientController;
import clientClasses.Message;
import clientClasses.WantedReport;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SpeceficReportGUIController implements Initializable {

    @FXML
    private Label reportsLbl;

    @FXML
    private ImageView backBtn;

    @FXML
    private TextArea reportDetails;

    @FXML
    private TextArea reportType;
    public ClientController cc = ClientController.getClientController();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    WantedReport wr = ViewReportsGUIController.controller.getWr();

        reportsLbl.setText("Report for " + wr.getShopName() + " shop, from "+wr.getFrom()+" to "+wr.getTo()+":");
        reportType.setText(wr.getReportType());
        ViewReport(wr.getReportType(),wr.getFrom(),wr.getTo(),wr.getShopName());

    }

    private void ViewReport(String reportType, LocalDate from, LocalDate to, String shopName) {

        Message m = new Message();
        WantedReport wr = new WantedReport();
        wr.setReportType(reportType);
        wr.setTo(to);
        wr.setFrom(from);
        wr.setShopName(shopName);
        CachedRowSet cachedMsg = null;
        m.setCommand("viewSpecificReports");
        m.setMsg((Object)wr);
        cc.send(m);

        while (cc.awaitResponse) ; //wait for data from server
        cachedMsg = (CachedRowSet) (cc.messageFromServer.getMsg()); //message received from server (row from cancellationrequests table)
/*orderNumber,price,dOrder,deliveryDate,orderDate,status,confirmed*/
        if(cc.messageFromServer.getCommand().equals("orders report")) {
            try {
                while (cachedMsg.next()) {
                    reportDetails.appendText("Order number : " + cachedMsg.getInt("orderNumber") + "\n" + "Order price : " + cachedMsg.getDouble("price") + "\n"
                            + "Order details : " + cachedMsg.getString("dOrder") + "\n" + "Delivery Date : " + cachedMsg.getDate("deliveryDate") + "\n" +
                            "Order date : " + cachedMsg.getDate("orderDate") + "\n" + "Order status : " + cachedMsg.getString("status") + "\n" +
                            "Confirmed ? " + cachedMsg.getString("confirmed") + "\n");
                    reportDetails.appendText("______________________________________________________________\n");
                }
            } catch (Exception e) {
            }
        }
        if(cc.messageFromServer.getCommand().equals("income report")) {
            double overallIncome = 0;
            try {
                while (cachedMsg.next()) {
                    reportDetails.appendText("Order number : " + cachedMsg.getInt("orderNumber") + "\n" + "Order price : " + cachedMsg.getDouble("price") + "\n");
                    reportDetails.appendText("______________________________________________________________\n");
                    overallIncome= overallIncome + cachedMsg.getDouble("price");
                }
                reportDetails.appendText("Overall income : " + overallIncome);
            } catch (Exception e) {
            }

        }
    }

    @FXML
    void clickedBackBtn(MouseEvent event) {
        Stage stage = (Stage) reportsLbl.getScene().getWindow();
        stage.hide();
        ClientController.savedWindows.getViewReportsWindow().show();
    }

    @FXML
    void enteredbackBtn(MouseEvent event) {
        cc.enteredButton(backBtn);

    }

    @FXML
    void leavedbackBtn(MouseEvent event) {
        cc.leavedButton(backBtn);
    }
}