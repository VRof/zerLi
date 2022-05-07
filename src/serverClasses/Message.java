package serverClasses;

import java.io.Serializable;

public class Message implements Serializable {
    private String command;
    private Object msg;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
