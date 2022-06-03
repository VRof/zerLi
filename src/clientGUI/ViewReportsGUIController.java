package clientGUI;

import client.ClientController;
import client.NewWindowFrameController;
import clientClasses.Message;
import clientClasses.WantedReport;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ViewReportsGUIController implements Initializable {


    @FXML
    private ChoiceBox<String> shopName;

    @FXML
    private ChoiceBox<String> reportType;

    @FXML
    private DatePicker toDate;
    @FXML
    private DatePicker fromDate;

    @FXML
    private ImageView searchBtn;

    @FXML
    private ImageView backBtn;

    @FXML
    private ImageView viewMonthlyReportsBtn;

    @FXML
    private Label errorLbl;

    public ClientController cc = ClientController.getClientController();
    private WantedReport wr;
    public static ViewReportsGUIController controller;
    private String userType = "";

    public ObservableList<String> getShops() {
        return shops;
    }

    ObservableList<String> shops;

    /**
     * Method(initialize) initialize the window of view orders add shops depends on the user who logged in
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
       controller = this;
        errorLbl.setText("");
        CachedRowSet cachedMsg = null;
        Message msg = new Message(); //msg to be sent to server
        msg.setCommand("viewReports");
        msg.setMsg("aa");
        cc.send(msg);
        List<String> tempShops = new ArrayList<>();

        while (cc.awaitResponse) ; //wait for data from server
        cachedMsg = (CachedRowSet) (cc.messageFromServer.getMsg()); //message received from server (row from cancellationrequests table)
        userType = cc.messageFromServer.getCommand();
        try {
            while (cachedMsg.next()) { //get user data
                shopName.getItems().add(cachedMsg.getString("shop"));
                tempShops.add(cachedMsg.getString("shop"));

            }
        } catch (SQLException e) {
            System.out.println("Error read data from server " + e);
        }
        shops = FXCollections.observableArrayList(tempShops);
        reportType.getItems().add("Income report");
        reportType.getItems().add("Orders report");
        reportType.getItems().add("Complaint report");

        reportType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(reportType.getSelectionModel().getSelectedItem().equals("Complaint report")){
                    toDate.setDisable(true);
                    fromDate.setDisable(true);
                }else{
                    toDate.setDisable(false);
                    fromDate.setDisable(false);
                }
            }
        });



    }

    /**
     * Method(clickedSearchBtn) activates when search button is pressed
     * checks if the fields are filled and displays the desired report
     * @param event
     * @throws Exception
     */
    @FXML
    void clickedSearchBtn(MouseEvent event) throws Exception {


        if(reportType.getSelectionModel().isEmpty()||shopName.getSelectionModel().isEmpty()) {
            errorLbl.setText("Error check input!");
        }
        else
            if(reportType.getSelectionModel().getSelectedItem().equals("Complaint report")){
                wr = new WantedReport();
                wr.setReportType(reportType.getValue());
                wr.setShopName(shopName.getValue());
                NewWindowFrameController reportsWindow = new NewWindowFrameController("ComplaintReportGUI");
                reportsWindow.start(new Stage());
                Stage stage = (Stage) errorLbl.getScene().getWindow();
                ClientController.savedWindows.setViewReportsWindow(stage);
                stage.hide();

            }

        else{
            if(fromDate.getValue() ==null || toDate.getValue()==null){
                errorLbl.setText("Error check input!");
            }else {
                wr = new WantedReport();
                wr.setReportType(reportType.getValue());
                wr.setShopName(shopName.getValue());
                wr.setFrom(fromDate.getValue());
                wr.setTo(toDate.getValue());
                if (!wr.getReportType().equals("Complaint report")) {
                    NewWindowFrameController reportsWindow = new NewWindowFrameController("SpeceficReportGUI");
                    reportsWindow.start(new Stage());
                    Stage stage = (Stage) errorLbl.getScene().getWindow();
                    ClientController.savedWindows.setViewReportsWindow(stage);
                    stage.hide();
                }
            }

        }
    }

    /**
     * Method(clickedViewMonthlyReportsBtn) when clicked on button moves the user to a new window
     * @param event
     * @throws Exception
     */
    @FXML
    void clickedViewMonthlyReportsBtn(MouseEvent event) throws Exception {
        NewWindowFrameController reportsWindow = new NewWindowFrameController("viewMonthlyReports");
        reportsWindow.start(new Stage());
        Stage stage = (Stage) errorLbl.getScene().getWindow();
        ClientController.savedWindows.setViewReportsWindow(stage);
        stage.hide();

    }

    /**
     * Method(clickedBackBtn) return to the previous window depends on what user were connected because its a shared window
     * between 2 users
     * @param event
     * @throws Exception
     */
    @FXML
    void clickedBackBtn(MouseEvent event) throws Exception {
        if(userType.equals("shopmanager")) {
            new NewWindowFrameController("ShopManagerGUI").start(new Stage());
            Stage stage = (Stage) errorLbl.getScene().getWindow();
            stage.hide();
        }else{
            new NewWindowFrameController("CEOGUI").start(new Stage());
            Stage stage = (Stage) errorLbl.getScene().getWindow();
            stage.hide();
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
    void enteredViewMonthlyReportsBtn(MouseEvent event) {
        cc.enteredButton(viewMonthlyReportsBtn);
    }

    @FXML
    void leavedBackBtn(MouseEvent event) {
        cc.leavedButton(backBtn);
    }

    @FXML
    void leavedSearchBtn(MouseEvent event) {
        cc.leavedButton(searchBtn);
    }

    @FXML
    void leavedViewMonthlyReportsBtn(MouseEvent event) {
        cc.leavedButton(viewMonthlyReportsBtn);
    }

    /**
     * Method(getWr) returns details about wanted report
     * @return
     */
    public WantedReport getWr() {
        return wr;
    }

}
