package repoting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.cucumber.core.api.Scenario;

public class ExtentReporterUtils {

    private static ExtentHtmlReporter extentHtmlReporter;
    public static Scenario scenario;

    private static ExtentReports extentReports = new ExtentReports();
    public ExtentTest extentTest;

    public static ExtentHtmlReporter getExtentHtmlReporter() {
        return extentHtmlReporter;
    }

    public static void setExtentHtmlReporter(ExtentHtmlReporter extentHtmlReport) {
        extentHtmlReporter = extentHtmlReport;
    }

    public static ExtentReports getExtentReports() {
        return extentReports;
    }

    public static void setExtentReports(ExtentReports extentReport) {
        extentReports = extentReport;
    }

    public ExtentTest getExtentTest() {
        return extentTest;
    }

    public void setExtentTest(ExtentTest extentTest) {
        this.extentTest = extentTest;
    }

}
