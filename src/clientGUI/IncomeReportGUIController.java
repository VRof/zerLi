package clientGUI;


import client.ClientController;
import client.NewWindowFrameController;
import commonClasses.Message;
import commonClasses.Quarter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.sql.rowset.CachedRowSet;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */
public class IncomeReportGUIController implements Initializable {
    @FXML
    private ChoiceBox<Integer> quarter1;

    @FXML
    private ChoiceBox<String> shop1;

    @FXML
    private Label errorLbl;

    @FXML
    private ImageView backBtn;

    @FXML
    private ImageView showBtn;

    @FXML
    private ImageView clearBtn;


    @FXML
    private BarChart<String, Double> IncomeReport1;

    @FXML
    private CategoryAxis incomeMonths;


    @FXML
    private NumberAxis income1;


    @FXML
    private ChoiceBox<Integer> year1;


    public ClientController cc = ClientController.getClientController();
    public List<String> months1 = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
    int quarterCount;

    /**
     *Method(initialize) to setup the values in the window including shops that depend on the type of user connected
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
        errorLbl.setText("");
        quarterCount = 0;
        quarter1.getItems().add(1);
        quarter1.getItems().add(2);
        quarter1.getItems().add(3);
        quarter1.getItems().add(4);

        CachedRowSet cachedMsg = null;
        Message msg = new Message(); //msg to be sent to server
        msg.setCommand("viewReports");
        msg.setMsg("aa");
        cc.send(msg);

        while (cc.awaitResponse) ; //wait for data from server
        cachedMsg = (CachedRowSet) (cc.messageFromServer.getMsg()); //message received from server (row from cancellationrequests table)
        try {
            while (cachedMsg.next()) { //get user data
                shop1.getItems().add(cachedMsg.getString("shop"));
            }
        } catch (SQLException e) {
            System.out.println("Error read data from server " + e);
        }
        year1.getItems().add(2022);
        year1.getItems().add(2023);
        year1.getItems().add(2024);
        year1.getItems().add(2025);

        ObservableList<String> months = FXCollections.observableArrayList(months1);
        incomeMonths.setCategories(months);

    }

    /**.
     * Method(clickedShowBtn) method that contacts server to get relevant information from DB
     * and display them in a barchart that contains the overall income and months in quarter .
     * @param event
     */
    @FXML
    void clickedShowBtn(MouseEvent event)  {
       // incomeMonths.getCategories().clear();
        Message msg = new Message(); //msg to be sent to server
        Quarter quarterMsg, quarter;
        int m1 = 0,m2 = 0,m3=0;
        if(year1.getSelectionModel().isEmpty()||quarter1.getSelectionModel().isEmpty()||shop1.getSelectionModel().isEmpty()){
            errorLbl.setText("Input ERROR, check fields above");
        }else {
            errorLbl.setText("");
            quarterMsg = new Quarter(year1.getSelectionModel().getSelectedItem(), quarter1.getSelectionModel().getSelectedItem(), shop1.getSelectionModel().getSelectedItem());

            msg.setCommand("viewOverallIncome");
            msg.setMsg(quarterMsg);
            cc.send(msg);
            while (cc.awaitResponse) ; //wait for data from server
            quarter = (Quarter)cc.messageFromServer.getMsg(); //message received from server
            for (int i = 0; i < months1.size(); i++) {//loop to add the relevant months to use in the barchart
                if (quarter.getMonth1().equals(months1.get(i))) {
                    m1 = i;
                }
                if (quarter.getMonth2().equals(months1.get(i))) {
                    m2 = i;
                }
                if (quarter.getMonth3().equals(months1.get(i))) {
                    m3 = i;
                }
            }

        /*add the relevant details into series to display in a barchart*/
            XYChart.Series<String, Double> series1 = new XYChart.Series<>();
            series1.setName("Income for : " + shop1.getSelectionModel().getSelectedItem()+"\nQuarter :"+quarter1.getSelectionModel().getSelectedItem()+" Year : "+year1.getSelectionModel().getSelectedItem());
            series1.getData().add(new XYChart.Data(incomeMonths.getCategories().get(m1), quarter.getResult1()));
            series1.getData().add(new XYChart.Data(incomeMonths.getCategories().get(m2), quarter.getResult2()));
            series1.getData().add(new XYChart.Data(incomeMonths.getCategories().get(m3), quarter.getResult3()));
            IncomeReport1.getData().addAll(series1);//display the series in a barchart
        //counter for quarters displayed no more than 2 at once
            quarterCount++;
            if (quarterCount == 2) {
                quarterCount = 0;
                showBtn.setDisable(true);//disable button after showing relevant details
            }
        }
        }


    /**
     * Method(clickedClearBtn) clears the data from the barchart and enable the show button
     * @param event
     */
    @FXML
    void clickedClearBtn(MouseEvent event) {
        IncomeReport1.getData().clear();
        showBtn.setDisable(false);

    }


    /**
     * Method(clickedBackBtn) returns to the previous window when button back is clicked and hide the current one
     * @param event
     * @throws Exception
     */
    @FXML
    void clickedBackBtn(MouseEvent event) throws Exception {
        new NewWindowFrameController("CEOGUI").start(new Stage());
        Stage stage = (Stage) IncomeReport1.getScene().getWindow();
        stage.hide();

    }

    @FXML
    void enteredBackBtn(MouseEvent event) {
        cc.enteredButton(backBtn);
    }
    @FXML
    void enteredClearBtn(MouseEvent event) {
        cc.enteredButton(clearBtn);
    }

    @FXML
    void enteredShowBtn(MouseEvent event) {
        cc.enteredButton(showBtn);
    }


    @FXML
    void leavedBackBtn(MouseEvent event) {
        cc.leavedButton(backBtn);
    }
    @FXML
    void leavedClearBtn(MouseEvent event) {
        cc.leavedButton(clearBtn);
    }

    @FXML
    void leavedShowBtn(MouseEvent event) {
        cc.leavedButton(showBtn);
    }


}
