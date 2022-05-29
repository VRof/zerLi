package clientClasses;

import javafx.scene.control.Hyperlink;

import java.net.URL;

public class SurveyResult {

    private int surveyid;
    private String surveyinfo;
    private String link;

    public SurveyResult(int surveyid, String surveyinfo, String link) {
        this.surveyid = surveyid;
        this.surveyinfo = surveyinfo;
        this.link = link;
    }

    public int getSurveyid() {
        return surveyid;
    }

    public void setSurveyid(int surveyid) {
        this.surveyid = surveyid;
    }

    public String getSurveyinfo() {
        return surveyinfo;
    }

    public void setSurveyinfo(String surveyinfo) {
        this.surveyinfo = surveyinfo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
