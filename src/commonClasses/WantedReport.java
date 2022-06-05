package commonClasses;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 *  report data class
 *
 * <p> Project Name: Zer-Li (Java Application Flower Store) </p>
 *
 * @author Habib Ibrahim, Vitaly Rofman, Ibrahim Daoud, Yosif Hosen
 * @version  V1.00  2022
 */

public class WantedReport implements Serializable {
    private String shopName;
    private String reportType;
    private LocalDate from;
    private LocalDate to;

    public WantedReport(String shopName, String reportType, LocalDate from, LocalDate to) {
        this.shopName = shopName;
        this.reportType = reportType;
        this.from = from;
        this.to = to;
    }

    public WantedReport() {
    }


    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }
}
