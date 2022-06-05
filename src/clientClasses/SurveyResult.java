package clientClasses;

/**
 *
 * survey result data class
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

public class SurveyResult {

    private int surveyid;
    private String surveyinfo;
    private String link;

    public SurveyResult(int surveyid, String surveyinfo, String link) {
        this.surveyid = surveyid;
        this.surveyinfo = surveyinfo;
        this.link = link;
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
