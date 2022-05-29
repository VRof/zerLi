package client;

import javafx.stage.Stage;

public class SavedWindows {
    private Stage loginWindow;
    private Stage viewReportsWindow;
    private Stage incomeReportWindow;

    public Stage getIncomeReportWindow() {
        return incomeReportWindow;
    }

    public void setIncomeReportWindow(Stage incomeReportWindow) {
        this.incomeReportWindow = incomeReportWindow;
    }


    public Stage getViewReportsWindow() {
        return viewReportsWindow;
    }

    public void setViewReportsWindow(Stage viewReportsWindow) {
        this.viewReportsWindow = viewReportsWindow;
    }



    public Stage getLoginWindow() {
        return loginWindow;
    }

    public void setLoginWindow(Stage loginWindow) {
        this.loginWindow = loginWindow;
    }
}
