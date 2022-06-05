package client;

import javafx.stage.Stage;

/**
 *
 *  new popup window frame creator, loads popup by its name, doesn't disconnect when closed
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

public class SavedWindows {
    private Stage loginWindow;
    private Stage UserWindow;
    private Stage clientGUIWindow;
    private Stage viewReportsWindow;
    private Stage incomeReportWindow;
    private Stage orderDetailsWindow;
    private Stage newOrderWindow;
    private Stage paymentWindow;

    public void setIncomeReportWindow(Stage incomeReportWindow) {
        this.incomeReportWindow = incomeReportWindow;
    }


    public Stage getViewReportsWindow() {
        return viewReportsWindow;
    }

    public void setViewReportsWindow(Stage viewReportsWindow) {
        this.viewReportsWindow = viewReportsWindow;
    }


    public Stage getClientGUIWindow() {
        return clientGUIWindow;
    }

    public void setClientGUIWindow(Stage clientGUIWindow) {
        this.clientGUIWindow = clientGUIWindow;
    }


    public Stage getUserWindow() {
        return UserWindow;
    }

    public void setUserWindow(Stage userWindow) {
        UserWindow = userWindow;
    }
    public Stage getLoginWindow() {
        return loginWindow;
    }

    public void setLoginWindow(Stage loginWindow) {
        this.loginWindow = loginWindow;
    }

    public Stage getOrderDetailsWindow() {
        return orderDetailsWindow;
    }

    public void setOrderDetailsWindow(Stage orderDetailsWindow) {
        this.orderDetailsWindow = orderDetailsWindow;
    }

    public Stage getNewOrderWindow() {
        return newOrderWindow;
    }

    public void setNewOrderWindow(Stage newOrderWindow) {
        this.newOrderWindow = newOrderWindow;
    }

    public Stage getPaymentWindow() {
        return paymentWindow;
    }

    public void setPaymentWindow(Stage paymentWindow) {
        this.paymentWindow = paymentWindow;
    }
}
