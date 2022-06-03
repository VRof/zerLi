package clientGUI;

import client.ClientController;
import client.MonthlyReport;
import clientClasses.Message;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ViewMonthlyReportsGUIController implements Initializable {

    @FXML
    private ChoiceBox<String> shopName;

    @FXML
    private ChoiceBox<String> reportType;

    @FXML
    private ImageView searchBtn;

    @FXML
    private ImageView backBtn;

    @FXML
    private Label titleLbl;

    @FXML
    private Label errorLbl;

    @FXML
    private ChoiceBox<Integer> months;

    @FXML
    private TextArea reportDetails;

    public ClientController cc = ClientController.getClientController();

    /**
     * Method(initialize) initialize the window with relevant details some from the previous window
     * and number of months and report types
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
        shopName.setItems(ViewReportsGUIController.controller.getShops());
        for (int i = 1; i <=12 ; i++) {
            months.getItems().add(i);
        }
        reportType.getItems().add("Income report");
        reportType.getItems().add("Order report");

    }

    /**
     * Method(clickedBackBtn) method that moves the user to the previous window "view reports GUI"
     * after clicking on back button and also hides the current window
     * @param event
     */
    @FXML
    void clickedBackBtn(MouseEvent event) {
        Stage stage = (Stage) errorLbl.getScene().getWindow();
        stage.hide();
        ClientController.savedWindows.getViewReportsWindow().show();
    }

    /**
     * Method(clickedSearchBtn) activates when search button is clicked if the details that user filled are not correct it shows relevant
     * meassage otherwise the method contacts server to get relevant details for reports and displays them
     * @param event
     * @throws SQLException
     */
    @FXML
    void clickedSearchBtn(MouseEvent event) throws SQLException {
        reportDetails.clear();
        MonthlyReport mr = new MonthlyReport();
        CachedRowSet cachedMsg = null;
        Message m = new Message();
        if(months.getSelectionModel().isEmpty()||reportType.getSelectionModel().isEmpty()||shopName.getSelectionModel().isEmpty()) {
            errorLbl.setText("error input");
            return;
        }
        mr.setMonth(months.getSelectionModel().getSelectedItem());
        mr.setType(reportType.getSelectionModel().getSelectedItem());
        mr.setShop(shopName.getSelectionModel().getSelectedItem());
        m.setCommand("viewMonthlyReport");
        m.setMsg(mr);
        cc.send(m);
        while (cc.awaitResponse) ; //wait for data from server

        cachedMsg = (CachedRowSet) (cc.messageFromServer.getMsg());


        while(cachedMsg.next()){
            errorLbl.setText("");
            reportDetails.appendText(cachedMsg.getString("reportdetails"));
            reportDetails.appendText("\n-------------------------------");
        }
        String data=reportDetails.getText().trim(); //read contents of text area into 'data'
        if(data.equals("")) {
            errorLbl.setText("No such report available");
        }

    }

    @FXML
    void enteredBackBtn(MouseEvent event) {
       cc.enteredButton(backBtn);
    }

    @FXML
    void enteredSearchBtn(MouseEvent event) {
    cc.enteredButton(searchBtn);
    }

    @FXML
    void leavedBackBtn(MouseEvent event) {
    cc.leavedButton(backBtn);
    }

    @FXML
    void leavedSearchBtn(MouseEvent event) {
     cc.leavedButton(searchBtn);
    }


}
