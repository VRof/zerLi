package client;

import javafx.stage.Stage;

public class SavedWindows {
    private Stage loginWindow;
    private Stage UserWindow;

    ///
    public Stage getUserWindow() {
        return UserWindow;
    }

    public void setUserWindow(Stage userWindow) {
        UserWindow = userWindow;
    }
    ///
    public Stage getLoginWindow() {
        return loginWindow;
    }

    public void setLoginWindow(Stage loginWindow) {
        this.loginWindow = loginWindow;
    }
}
