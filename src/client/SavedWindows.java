package client;

import javafx.stage.Stage;

public class SavedWindows {
    private Stage loginWindow;
    private Stage UserWindow;
    private Stage viewOrdersWindow;
    private Stage clientGUIWindow;

    public Stage getClientGUIWindow() {
        return clientGUIWindow;
    }

    public void setClientGUIWindow(Stage clientGUIWindow) {
        this.clientGUIWindow = clientGUIWindow;
    }


    public Stage getViewOrdersWindow() {
        return viewOrdersWindow;
    }

    public void setViewOrdersWindow(Stage viewOrdersWindow) {
        this.viewOrdersWindow = viewOrdersWindow;
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
}
