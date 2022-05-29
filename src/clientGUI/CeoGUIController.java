package clientGUI;

import client.ClientController;
import client.NewWindowFrameController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CeoGUIController implements Initializable {

    @FXML
    private Label welcomeCeoLbl;

    @FXML
    private ImageView logoutBtn;

    @FXML
    private ImageView viewAnnualReport;

    @FXML
    private ImageView viewSpecificShopReportBtn;
    public ClientController cc =ClientController.getClientController();


    @FXML
    void clickedLogoutBtn(MouseEvent event) {
        Stage stage = (Stage)welcomeCeoLbl.getScene().getWindow();
        cc.logout(stage);
    }

    @FXML
    void clickedViewAnnualReport(MouseEvent event) throws Exception {
        NewWindowFrameController customerWindow = new NewWindowFrameController("IncomeReportGUI");
        customerWindow.start(new Stage());
        Stage stage = (Stage) welcomeCeoLbl.getScene().getWindow();
        stage.hide();

    }

    @FXML
    void clickedViewSpecificShopReportBtn(MouseEvent event) throws Exception {
        NewWindowFrameController customerWindow = new NewWindowFrameController("ViewReportsGUI");
        customerWindow.start(new Stage());
        Stage stage = (Stage) welcomeCeoLbl.getScene().getWindow();
        stage.hide();

    }

    @FXML
    void enteredLogoutBtn(MouseEvent event) {
        cc.enteredButton(logoutBtn);

    }

    @FXML
    void enteredViewAnnualReport(MouseEvent event) {
        cc.enteredButton(viewAnnualReport);

    }

    @FXML
    void enteredViewSpecificShopReportBtn(MouseEvent event) {
        cc.enteredButton(viewSpecificShopReportBtn);
    }

    @FXML
    void leavedLogoutBtn(MouseEvent event) {
        cc.leavedButton(logoutBtn);

    }

    @FXML
    void leavedViewAnnualReport(MouseEvent event) {
        cc.leavedButton(viewAnnualReport);
    }

    @FXML
    void leavedViewSpecificShopReportBtn(MouseEvent event) {
        cc.leavedButton(viewSpecificShopReportBtn);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //we can add and option to get the name of the ceo
        welcomeCeoLbl.setText("Welcome here you can chose your desired reports");
    }
}
