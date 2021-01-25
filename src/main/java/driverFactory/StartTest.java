package driverFactory;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.cucumber.core.api.Scenario;
import io.cucumber.java.After;
import io.cucumber.java.Before;


public class StartTest {

    private static Browsers browser;
    private static String env;
    private static String yamlFile;
    private static ExtentReports extentReports;
    public Scenario scenario;
    public static boolean setScreenshotforEachStep = true;

    public static DriverFactory getDriverInstance() {
        return driverFactory;
    }

    private static DriverFactory driverFactory;

    public StartTest(){}
    public StartTest(Browsers browser, String env, String yamlFile) throws Exception {
        setBrowser(browser);
        setEnv(env);
        setYamlFile(yamlFile);
        driverFactory = new DriverFactory(scenario);
    }

    public static Browsers getBrowser() {
        return browser;
    }

    public static void setBrowser(Browsers browser) throws Exception {
        if ((System.getProperty("browser") == null))
            StartTest.browser = browser;
        else {
            try {
                StartTest.browser = Browsers.valueOf(System.getProperty("browser").toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new Exception("wrong.browser.exception: Please provide correct browser value from global variable");
            }
        }
        if (browser == null) {
            throw new Exception("wrong.browser.exception: Please provide browser value");
        }
    }

    public static String getEnv() {
        return env;
    }

    public static void setEnv(String env) {
        StartTest.env = env;
    }

    public static String getYamlFile() {
        return yamlFile;
    }

    public static void setYamlFile(String yamlFile) {
        StartTest.yamlFile = yamlFile;
    }



    public void onStart() {
       driverFactory.extentSetup();
    }
    @After
    public void tearDown(){
        driverFactory.tearDown();
    }

}





