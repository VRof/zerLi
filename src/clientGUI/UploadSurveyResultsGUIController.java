package clientGUI;

import client.ClientController;
import clientClasses.Message;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.Objects;

import static client.ClientController.savedWindows;

public class UploadSurveyResultsGUIController {

    @FXML
    private ChoiceBox<Integer> dr_surveyNumber;

    @FXML
    private TextField lbl_surveyDate;

    @FXML
    private Label lbl_q1;

    @FXML
    private Label lbl_q2;

    @FXML
    private Label lbl_q3;

    @FXML
    private Label lbl_q4;

    @FXML
    private Label lbl_q5;

    @FXML
    private Label lbl_q6;

    @FXML
    private ChoiceBox<Integer> dr_q1;

    @FXML
    private ChoiceBox<Integer> dr_q2;

    @FXML
    private ChoiceBox<Integer> dr_q3;

    @FXML
    private ChoiceBox<Integer> dr_q4;

    @FXML
    private ChoiceBox<Integer> dr_q5;

    @FXML
    private ChoiceBox<Integer> dr_q6;

    @FXML
    private ImageView btn_back;

    @FXML
    private Label lbl_error;

    @FXML
    private ImageView btn_uploadResult;

    private Message msg = new Message();
    private ClientController conn = ClientController.getClientController();

    /**
     *
     */
    @FXML
    public void initialize() throws SQLException {
        lbl_error.setText("");
        lbl_surveyDate.setText("");
        seUpWindow();
    }

    /**
     * seUpWindow -
     */
    private void seUpWindow(){
        for (Integer i = 1 ; i<=10 ; i++) {
            dr_q1.getItems().add(i); dr_q2.getItems().add(i);
            dr_q3.getItems().add(i); dr_q4.getItems().add(i);
            dr_q5.getItems().add(i); dr_q6.getItems().add(i);
        }
        lbl_q1.setText(""); lbl_q2.setText(""); lbl_q3.setText("");
        lbl_q4.setText(""); lbl_q5.setText(""); lbl_q6.setText("");
        msg.setCommand("getQuantityRows"); msg.setMsg("");
        conn.send(msg);
        while(conn.awaitResponse);
        int  msgFromServer = (int)(conn.messageFromServer.getMsg());
        for (Integer i = 1 ; i<=msgFromServer; i++) { dr_surveyNumber.getItems().add(i);}
        dr_surveyNumber.setOnAction(event -> { // when choice box val change, the server reloads data
            if(dr_surveyNumber.getValue()!=null) {
                try { this.setSurveyQuestions(dr_surveyNumber.getValue());}
                catch (RuntimeException e) { lbl_error.setText("Error loading survey Questions!"); }
                catch ( SQLException e) { throw new RuntimeException(e);}}});
    }

    /**
     * setSurveyQuestions -
     */
    private void setSurveyQuestions(Integer value) throws SQLException {
        msg.setCommand("setSurveyQuestions"); msg.setMsg(value);
        conn.send(msg);
        while(conn.awaitResponse);
        CachedRowSet msgFromServer = (CachedRowSet)(conn.messageFromServer.getMsg());
        try {
            msgFromServer.next();
            lbl_q1.setText(msgFromServer.getString("q1"));
            lbl_q2.setText(msgFromServer.getString("q2"));
            lbl_q3.setText(msgFromServer.getString("q3"));
            lbl_q4.setText(msgFromServer.getString("q4"));
            lbl_q5.setText(msgFromServer.getString("q5"));
            lbl_q6.setText(msgFromServer.getString("q6"));
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    /**
     *  clickedBackBtn - click in "<--" button to get back to user original window
     *  @param event - entering the button with the mouse
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
     * clickedUploadResultBtn -
     * @param event - entering the button with the mouse
     */
    @FXML
    void clickedUploadResultBtn(MouseEvent event) {
        int answers[] = new int[7];
        String date;
        Object message[] = new Object[2];
        if (dr_q1.getValue()!=null && dr_q2.getValue()!=null && dr_q3.getValue()!=null && dr_q4.getValue()!=null && dr_q5.getValue()!=null && dr_q6.getValue()!=null){
            if (!Objects.equals(lbl_surveyDate.getText(),"")){
                answers[0] = dr_surveyNumber.getValue();
                answers[1] = dr_q1.getValue(); answers[2] = dr_q2.getValue();
                answers[3] = dr_q3.getValue(); answers[4] = dr_q4.getValue();
                answers[5] = dr_q5.getValue(); answers[6] = dr_q6.getValue();
                date = lbl_surveyDate.getText();
                message[0] = answers; message[1] = date;
                msg.setCommand("insertSurveyAnswers"); msg.setMsg(message);
                conn.send(msg);
                while(conn.awaitResponse);
                lbl_error.setText("Answers inserted successfully!");
            } else{ lbl_error.setText("Please fill survey date!"); }
        } else{ lbl_error.setText("Please fill all answers!"); }
    }

    /**
     * enteredBackBtn - function for making shadow for button
     * @param event - entering the button with the mouse
     */
    @FXML
    void enteredBackBtn(MouseEvent event) { conn.enteredButton(btn_back); }

    /**
     * enteredUploadResultBtn - function for making shadow for button
     * @param event - entering the button with the mouse
     */
    @FXML
    void enteredUploadResultBtn(MouseEvent event) { conn.enteredButton(btn_uploadResult); }

    /**
     * leavedBackBtn - function to hide shadow for button
     * @param event - leaving the button with the mouse
     */
    @FXML
    void leavedBackBtn(MouseEvent event) { conn.leavedButton(btn_back); }

    /**
     * leavedUploadResultBtn - function to hide shadow for button
     * @param event - leaving the button with the mouse
     */
    @FXML
    void leavedUploadResultBtn(MouseEvent event) { conn.leavedButton(btn_uploadResult); }
}
