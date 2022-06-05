package clientClasses;

import java.sql.Timestamp;

public class Complaint {

    private int Id;
    private String done;
    private Timestamp Time;

    public Complaint(int id, String done, Timestamp time) {
        Id = id;
        this.done = done;
        Time = time;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }

    public Timestamp getTime() {
        return Time;
    }

    public void setTime(Timestamp time) {
        Time = time;
    }



}
