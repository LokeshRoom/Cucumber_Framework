package driverFactory;

import com.aventstack.extentreports.*;
import io.cucumber.core.api.Scenario;
import io.cucumber.java.After;
import repoting.ExtentReporterUtils;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.github.bonigarcia.wdm.WebDriverManager;
import objectutils.ObjectReader;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.BeforeSuite;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class DriverFactory extends ObjectReader {

    private WebDriver driver;
    private JSONObject yamlJsonObject;
    ExtentHtmlReporter extentHtmlReporter;
    ExtentReports extentReports=ExtentReporterUtils.getExtentReports();


    private static Logger logger = LogManager.getLogger("driverFactory");

    ExtentTest extentTest;

    public DriverFactory(Scenario scenario) throws Exception {
        switch (StartTest.getBrowser()) {
            case CHROME:
                WebDriverManager.chromedriver().setup();
                System.setProperty("webdriver.chrome.silentOutput", "true");
                driver = new ChromeDriver();
                break;
            case IE:
                WebDriverManager.iedriver().setup();
                driver = new InternetExplorerDriver();
                break;
            case EDGE:
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null");
                driver = new FirefoxDriver();
                break;
            case REMOTE_CHROME:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions=new ChromeOptions();
                chromeOptions.setHeadless(true);
                driver=new ChromeDriver(chromeOptions);
        }
        extentSetup();
        extentTest = extentReports.createTest(ExtentReporterUtils.scenario.getName());
        driver.manage().window().maximize();
        this.setYamlJsonObject(StartTest.getYamlFile(), System.getProperty("user.dir") + "\\src\\test\\ObjectRepository\\");
        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        File file = new File(System.getProperty("user.dir") + "\\src\\main\\java\\log4j2.xml");
        context.setConfigLocation(file.toURI());
    }

    public void setExtentTest(ExtentTest extentTest) {
        this.extentTest = extentTest;
    }

    void pass(String message) throws IOException {
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        String scrst = screenshot.getScreenshotAs(OutputType.BASE64);
        extentTest.log(Status.PASS, message,MediaEntityBuilder.createScreenCaptureFromBase64String(scrst).build());
        //extentTest.addScreenCaptureFromBase64String(scrst);
        logger.info(message);
    }

    void fail(String message, Exception e) throws Exception {
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        String scrst = screenshot.getScreenshotAs(OutputType.BASE64);
        extentTest.log(Status.FAIL, message + "\n" + e,MediaEntityBuilder.createScreenCaptureFromBase64String(scrst).build());
        //extentTest.addScreenCaptureFromBase64String(scrst);
        logger.info(message);
        tearDown();
        throw new Exception(e);
    }

    String getScreenshot() throws IOException {
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        File scrst = screenshot.getScreenshotAs(OutputType.FILE);
        File dest = new File(System.getProperty("user.dir") + "\\target\\screenshots\\scrst.png");
        FileUtils.copyFile(scrst, dest);
        return System.getProperty("user.dir") + "\\target\\screenshots\\scrst.png";
    }

    public JSONObject getYamlJsonObject() {
        return yamlJsonObject;
    }

    public void setYamlJsonObject(String fileName, String path) throws Exception {
        this.yamlJsonObject = ObjectReader.readYaml(fileName, path);
    }

    private WebElement findElement(String element, String page) throws Exception {
        return driver.findElement(ObjectReader.getElement(yamlJsonObject, element, page));
    }

    public void goToUrl(String urlObject) throws Exception {
        String url = ObjectReader.getUrl(yamlJsonObject, urlObject);
        try {
            driver.get(url);
            pass("Launched Url: " + url);
        } catch (Exception e) {
            fail("Failed to launch url: " + url + "/n", e);
        }
    }

    public void navigateToUrl(String url) throws Exception {
        try {
            driver.navigate().to(url);
            pass("Navigate to URL: " + url);
        } catch (Exception e) {
            fail("Failed to navigate to Url" + url + "/n", e);
        }
    }

    public void enterText(String element, String page, CharSequence text) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            webElement.sendKeys(text);
            pass("Enterted Text: " + text + " in  object: " + element + " page: " + page);
        } catch (Exception e) {
            fail("Enterted Text: " + text + " in  object: " + element + " of page: " + page, e);
        }
    }

    public void click(String element, String page) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            webElement.click();
            pass("Clicked on element: " + element + "on page: " + page);
        } catch (Exception e) {
            fail("Clicked on element: " + element + "in page: " + page, e);
        }
    }

    public void doubleClick(String element, String page) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            Actions actions = new Actions(driver);
            actions.doubleClick(webElement).perform();
            pass("Double clicked on Element: " + element + "in page: " + page);
        } catch (Exception e) {
            fail("Failed to double click failed on Element:" + e, e);
        }
    }

    public void rightClick(String element, String page) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            Actions actions = new Actions(driver);
            actions.contextClick(webElement).build().perform();
            pass("Right clicked on Element" + element + "in page: " + page);
        } catch (Exception e) {
            fail("Right click failed on Element:" + element, e);
        }
    }

    public void selectFromDropDownByText(String element, String page, String option) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            Select select = new Select(webElement);
            select.selectByVisibleText(option);
            pass("Selected " + option + " from dropdown: " + element + " on page:" + page);
        } catch (Exception e) {
            fail("Failed to select " + option + " from dropdown: " + element + " on page:" + page, e);
        }
    }

    public void selectFromDropdownByIndex(String element, String page, int index) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            Select select = new Select(webElement);
            select.selectByIndex(index);
            pass("Selected " + index + " from dropdown: " + element + " on page:" + page);
        } catch (Exception e) {
            fail("Failed to select " + index + " from dropdown: " + element + " on page:" + page, e);
        }
    }

    public void selectFromDropdownByValue(String element, String page, String value) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            Select select = new Select(webElement);
            select.selectByValue(value);
            pass("Selected " + value + " from dropdown: " + element + " on page:" + page);
        } catch (Exception e) {
            fail("Failed to select " + value + " from dropdown: " + element + " on page:" + page, e);

        }
    }

    public void javaScriptExecutorClick(String element, String page) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            JavascriptExecutor je = (JavascriptExecutor) driver;
            je.executeScript("arguments[].click()", webElement);
            pass("JavaScriptExecutor clicked on Element: " + element + " in page:" + page);
        } catch (Exception e) {
            fail("JavaScriptExecutor failed to click on Element: " + element + " in page:" + page, e);
        }
    }

    public void extentSetup() {
        String date = ((new SimpleDateFormat("ddMMMyyHHmmss")).format(new Date()));
        Map<String, String> map = (Map) System.getProperties();
        extentHtmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "\\Reports\\Automation Summary Report " + date + ".html");
        ExtentReporterUtils.setExtentHtmlReporter(extentHtmlReporter);
        extentReports = new ExtentReports();
        ExtentReporterUtils.setExtentReports(extentReports);
        extentReports.attachReporter(extentHtmlReporter);
        extentHtmlReporter.config().setTheme(Theme.DARK);
        extentHtmlReporter.config().setDocumentTitle("BDD Automation Reports");
        extentHtmlReporter.config().setReportName("Selenium Framework Test Reports");
        extentReports.setSystemInfo("Os", map.get("os.name"));
        extentReports.setSystemInfo("User", map.get("user.name"));
        extentReports.setSystemInfo("Java Version", map.get("java.version"));
        extentReports.setSystemInfo("Environment", StartTest.getEnv());
        System.out.println("------------------------------Tests Started------------------------------");
    }

    public void tearDown(){
        driver.quit();
        extentReports.flush();

    }

    public void assertTextExists(String assertText) throws Exception {
        try {
            WebElement webElement = driver.findElement(By.xpath("//*[normalize-space(text()) = '"+assertText+"']"));
            if (webElement.isDisplayed())
            pass("Text exists in Page: " + assertText);
            else
                fail("Text doesn't exists in Page: " + assertText, new Exception(assertText+" doesn't exists in page"));

        } catch (Exception e) {
            fail("Failed to assert text in page", e);
        }
    }
}
