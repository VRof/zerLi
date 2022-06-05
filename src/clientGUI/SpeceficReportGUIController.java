package clientGUI;

import client.ClientController;
import commonClasses.Message;
import commonClasses.WantedReport;
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

/**
 *
 *  controller class for specific report window
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

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


    /**
     * Method(initialize) initialize the window with relevant details that were filled in previous window
     * reports GUI and call method ViewReports
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    WantedReport wr = ViewReportsGUIController.controller.getWr();

        reportsLbl.setText("Report for " + wr.getShopName() + " shop, from "+wr.getFrom()+" to "+wr.getTo()+":");
        reportType.setText(wr.getReportType());
        ViewReport(wr.getReportType(),wr.getFrom(),wr.getTo(),wr.getShopName());

    }

    /**
     * Method(ViewReport) contacts server with a request for a report with the relevant details'
     * user wish to see and display the data into a text area
     * @param reportType - the type of report user want to see
     * @param from - from this date
     * @param to - till this date
     * @param shopName - name of shop that the user want to see the report for
     */
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

    /**
     * Method(clickedBackBtn) method that moves the user to the previous window "view reports GUI"
     * after clicking on back button and also hides the current window
     * @param event mouse click
     */
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