package clientGUI;

import client.ClientController;
import client.NewWindowFrameController;
import clientClasses.Message;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
/**
 * CeoGUIController class is responsible for allowing the CEO pick a desired action
 * by clicking a button, either viewing specific shop reports or viewing annual report,
 * both of the buttons will direct the CEO to new GUI.
 * <p> Project Name: Zer-Li (Flowers store in Java) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */
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

    /**
     * Method(initialize) to initialize the window when opened and set text for label
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
        CachedRowSet cachedMsg;
        String first="",last="";
        //*********
        //1.send msg to server
        Message msg = new Message(); //msg to be sent to server
        msg.setCommand("getName");
        msg.setMsg(ClientController.userLoginData.getUserid());
        cc.send(msg);
        //2.receive from server
        while (cc.awaitResponse) ; //wait for data from server
        cachedMsg = (CachedRowSet) (cc.messageFromServer.getMsg());
        try {
            while (cachedMsg.next()){
                first = cachedMsg.getString("firstname");
                last =  cachedMsg.getString("lastname");
            }
        }
        catch(SQLException e){}
        welcomeCeoLbl.setText("Hello"+" "+first+" "+last);
    }

    /**
     * method(clickedLogoutBtn)logout activated when logout button is pressed
     * @param event
     */
    @FXML
    void clickedLogoutBtn(MouseEvent event) {
        Stage stage = (Stage)welcomeCeoLbl.getScene().getWindow();
        cc.logout(stage);
    }

    /**
     * method(clickedViewAnnualReport) opens aa new qindow income report GUI after pressing the button
     * view annual report clicked
     * @param event
     * @throws Exception
     */
    @FXML
    void clickedViewAnnualReport(MouseEvent event) throws Exception {
        NewWindowFrameController customerWindow = new NewWindowFrameController("IncomeReportGUI");
        customerWindow.start(new Stage());
        Stage stage = (Stage) welcomeCeoLbl.getScene().getWindow();
        stage.hide();

    }

    /**
     * Method(clickedViewSpecificShopReportBtn) opens a new window view reports GUI when
     * view specific report is pressed
     * @param event
     * @throws Exception
     */
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
}
