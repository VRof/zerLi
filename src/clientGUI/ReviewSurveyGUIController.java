package clientGUI;
import client.ClientController;
import clientClasses.SurveyResult;
import commonClasses.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javax.sql.rowset.CachedRowSet;
import java.awt.*;
import java.io.*;
import java.sql.SQLException;
import static client.ClientController.savedWindows;

/**
 *
 *  controller class for review survey window
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

public class ReviewSurveyGUIController {

    @FXML
    private TableView<SurveyResult> SurveyContentTable;

    @FXML
    private TableColumn<SurveyResult, Integer> col_surveyNum;

    @FXML
    private TableColumn<SurveyResult, String> col_SurveyInfo;

    @FXML
    private TableColumn<SurveyResult, String> col_linkForSurvey;

    @FXML
    private ImageView btn_back;

    private ObservableList<SurveyResult> SurveyList;

    private ClientController conn = ClientController.getClientController();
    private String valOrder = null;
    /**
     *  GUI Init
     */
    @FXML
    public void initialize() { this.getTableFromDB(); }

    /**
     *  clickedBackBtn - click in back button to get back to user original window
     *  @param event - mouse click
     */
    @FXML
    void clickedBackBtn(MouseEvent event) {
        Stage stage = ((Stage)btn_back.getScene().getWindow());
        stage.close();
        Stage window = savedWindows.getUserWindow();
        LoginGUIController.loginController.initialize();
        window.show();
    }

    /**
     *  enteredBackBtn - function for making shadow for button
     *  @param event - entering the button with the mouse
     *
     */
    @FXML
    void enteredBackBtn(MouseEvent event) { conn.enteredButton(btn_back);}

    /**
     *  leavedBackBtn - function to hide shadow for button
     *  @param event - leaving the button with the mouse
     *
     */
    @FXML
    void leavedBackBtn(MouseEvent event) { conn.leavedButton(btn_back);}


    /**
     *  setColumns - set up the columns of the table and declare clicking on table row as a lambda expression declaration
     */
    private void setColumns() {
        col_surveyNum.setCellValueFactory(new PropertyValueFactory<>("surveyid"));
        col_SurveyInfo.setCellValueFactory(new PropertyValueFactory<>("surveyinfo"));
        col_linkForSurvey.setCellValueFactory(new PropertyValueFactory<>("link"));
        SurveyContentTable.setEditable(true);
        SurveyContentTable.getSelectionModel().setCellSelectionEnabled(true);  // selects cell only, not the whole row
        SurveyContentTable.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                Message msg = new Message();
                Object msgFromServer;
                TablePosition pos = SurveyContentTable.getSelectionModel().getSelectedCells().get(0);
                int row = pos.getRow();
                int[] surveyData = new int[2];
                valOrder = String.valueOf(col_surveyNum.getCellData(row)); // get number of raw and value of the row
                surveyData[0] = ClientController.userLoginData.getUserid();
                surveyData[1] = Integer.parseInt(valOrder);
                msg.setCommand("getSurveyLink");
                msg.setMsg(surveyData);
                conn.send(msg);
                while(ClientController.awaitResponse);
                msgFromServer = ClientController.messageFromServer.getMsg();
                try {
                        byte[] filebByte = (byte[])msgFromServer;
                        File file = new File("src/client/files/currentSurvey.pdf");
                        FileOutputStream outStream = new FileOutputStream(file);
                        outStream.write(filebByte);
                        outStream.close();
                        Desktop desk = Desktop.getDesktop();
                        desk.open(file);
                } catch (IOException e) { throw new RuntimeException(e); }
            }});}

    /**
     *
     * getTableFromDB - get CachedRowSet from server and fill all values with sql table into user table
     */
    private void getTableFromDB() {
        this.setColumns();
        Message msg = new Message();
        CachedRowSet msgFromServer = null;
        SurveyList = FXCollections.observableArrayList();
        msg.setCommand("SurveyResults");
        msg.setMsg((Object)ClientController.userLoginData.getUserid());
        conn.send(msg);
        while(conn.awaitResponse);
        msgFromServer = (CachedRowSet) ClientController.messageFromServer.getMsg();
        try {
            while (msgFromServer.next()) {
                   SurveyResult survey = new SurveyResult(
                    msgFromServer.getInt("surveyid"),
                    msgFromServer.getString("surveyinfo"),
                    msgFromServer.getString("link"));
                    SurveyList.add(survey);
            }
           } catch (SQLException e) { System.out.println("Error read data from server " + e); }
            SurveyContentTable.setItems(SurveyList);
    }}

