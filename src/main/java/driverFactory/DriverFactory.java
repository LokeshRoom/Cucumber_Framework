package driverFactory;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.cucumber.core.api.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import objectutils.ObjectReader;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import repoting.ExtentReporterUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class DriverFactory extends ObjectReader {

    private static Logger logger = LogManager.getLogger("driverFactory");
    public boolean highlightElement = false;
    public boolean setMoveToElement = true;
    ExtentHtmlReporter extentHtmlReporter;
    ExtentReports extentReports = ExtentReporterUtils.getExtentReports();
    ExtentTest extentTest;
    boolean frameSetMoveToElement = false;
    private WebDriver driver;
    private JSONObject yamlJsonObject;
    private Boolean setScreenShotForEachStep = false;
    private int waitTimeout = 15;

    public DriverFactory(Scenario scenario) throws Exception {
        switch (StartTest.getBrowser()) {
            case CHROME:
                WebDriverManager.chromedriver().setup();
                System.setProperty("webdriver.chrome.silentOutput", "true");
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-notifications");
                driver = new ChromeDriver(options);
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
                System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
                driver = new FirefoxDriver();
                break;
            case REMOTE_CHROME:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setHeadless(true);
                chromeOptions.addArguments("--disable-notifications");
                driver = new ChromeDriver(chromeOptions);
        }
        extentSetup();
        extentTest = extentReports.createTest(ExtentReporterUtils.scenario.getName());
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(waitTimeout, TimeUnit.SECONDS);
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
        extentTest.log(Status.PASS, message, MediaEntityBuilder.createScreenCaptureFromBase64String(scrst).build());
        //extentTest.addScreenCaptureFromBase64String(scrst);
        logger.info(message);
    }

    void fail(String message, Exception e) throws Exception {
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        String scrst = screenshot.getScreenshotAs(OutputType.BASE64);
        extentTest.log(Status.FAIL, message + "\n" + e, MediaEntityBuilder.createScreenCaptureFromBase64String(scrst).build());
        //extentTest.addScreenCaptureFromBase64String(scrst);
        logger.info(message);
        tearDown();
        throw new Exception(e);
    }

    public void log(String message) throws IOException {
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        String scrst = screenshot.getScreenshotAs(OutputType.BASE64);
        extentTest.log(Status.PASS, message, MediaEntityBuilder.createScreenCaptureFromBase64String(scrst).build());
        //extentTest.addScreenCaptureFromBase64String(scrst);
        logger.info(message);
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
            fail("Failed to click on element: " + element + "in page: " + page, e);
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

    public void javaScriptExecutorClick(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page, appendValueToLocator);
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

    public void tearDown() {
        //driver.quit();
        extentReports.flush();

    }

    public void assertTextExists(String assertText) throws Exception {
        try {
            WebElement webElement = driver.findElement(By.xpath("//*[normalize-space(text()) = \"" + assertText + "\"]"));
            if (webElement.isDisplayed())
                pass("Text exists in Page: " + assertText);
            else
                fail("Text doesn't exists in Page: " + assertText, new Exception(assertText + " doesn't exists in page"));

        } catch (Exception e) {
            fail("Failed to assert text in page", e);
        }
    }

    public String getText(String element, String pageName) throws Exception {
        try {
            WebElement webElement = this.findElement(element, pageName);
            return webElement.getText() == null ? webElement.getAttribute("innerText") : webElement.getText();
        } catch (Exception e) {
            fail("Failed to get element text with exception", e);
            return null;
        }
    }

    public void assertElementText(String element, String page, String expectedText, String... appendValueToLocator) throws Exception {
        String actualText = " ";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToLocator);
            actualText = webElement.getText();
            if (actualText.equals(" ") || actualText == null || actualText.equals(""))
                actualText = webElement.getAttribute("innerText");

            if (actualText.equals(expectedText) && webElement.isDisplayed())
                pass("Actual Text [" + actualText + "] of object " + element + " is matching with expected text [" + expectedText + "]");
            else throw new Exception("Actual Text is not matching with expected text or Element is not displaying");
        } catch (Exception e) {
            fail("Actual Text [" + actualText + "] of object " + element + " is not matching with expected text [" + expectedText + "]", e);
        }
    }

    public void assertElementText(String element, String page, String expectedText, Boolean trueOrFalse, String... appendValueToLocator) throws Exception {
        String actualText = "";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToLocator);
            actualText = webElement.getText();
            if (actualText.equals(" ") || actualText.equals(""))
                actualText = webElement.getAttribute("innerText");
            if (trueOrFalse) {
                if (actualText.equals(expectedText) && webElement.isDisplayed())
                    pass("Actual Text [" + actualText + "] of object " + element + " is matching with expected text [" + expectedText + "]");
                else throw new Exception("Actual Text is not matching with expected text or Element is not displaying");
            } else {
                if (!actualText.equals(expectedText))
                    pass("Actual Text [" + actualText + "] of object " + element + " is not matching with expected text [" + expectedText + "]");
                else throw new Exception("Actual Text is matching with expected text");
            }

        } catch (Exception e) {
            if (trueOrFalse)
                fail("Actual Text [" + actualText + "] of object " + element + " is not matching with expected text [" + expectedText + "]", e);
            else
                fail("Actual Text [" + actualText + "] of object " + element + " is matching with expected text [" + expectedText + "]", e);
        }
    }

    public void assertElementDisplayed(String element, String page, boolean trueOrFalse, String... appendValueToLocator) throws Exception {
        try {
            if (trueOrFalse) {
                if (this.isDisplayed(element, page, appendValueToLocator))
                    pass("Element " + element + " is Displaying " + "in " + page);
                else
                    throw new NoSuchElementException("Element is not displaying");
            } else {
                driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                if (this.getWebElements(element, page, appendValueToLocator).size() == 0)
                    pass("Element " + element + " is not Displaying: " + "in " + page);
                else if (!this.isDisplayed(element, page, appendValueToLocator))
                    pass("Element " + element + " is not Displaying: " + "in " + page);
                else
                    throw new Exception("Element is displaying which is not expected");
                driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
            if (trueOrFalse)
                fail("Element " + element + " is not Displaying: " + "in " + page, e);
            else
                fail("Element " + element + " is Displaying: " + "in " + page, e);
        }
    }

    public String getCssValue(String element, String page, String value, String... appendValueToXpath) throws Exception {
        String actualText = " ";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            actualText = webElement.getCssValue(value);

            log("Css value  of an element " + element + ": " + actualText);
        } catch (StaleElementReferenceException e) {
            wait(4);
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            actualText = webElement.getCssValue(value);
        } catch (Exception e) {
            fail("Get css value failed for an element " + element, e);
        }
        return actualText;

    }


    public void assertAttributeValue(String element, String page, String attribute, String expectedValue, String... appendValueToXpath) throws Exception {
        String actualValue = " ";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            actualValue = webElement.getAttribute(attribute);
            if (actualValue.equals(expectedValue) && webElement.isDisplayed())
                pass("Actual value: [" + actualValue + "] of " + attribute + " is matching expected [" + expectedValue + "]");
            else
                throw new Exception("Actual value is not matching with expected value or element is not displaying");
        } catch (Exception e) {
            fail("Actual Value: [" + actualValue + "] is not matching with expected [" + expectedValue + "] of " + element + "'s attribute" + attribute, e);
        }
    }

    public void assertCheckbox(String element, String page, boolean checkedOrNot, String... appendValueToXpath) throws Exception {
        String actualValue = " ";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            actualValue = webElement.getAttribute("checked");
            if (checkedOrNot) {
                if (actualValue.equalsIgnoreCase("true"))
                    pass("Check box :" + element + "] in " + page + " is checked");
                else
                    fail("Check box :" + element + "] in " + page + " is not checked", new Exception("Checkbox is not checked"));
            } else {
                if (actualValue.equalsIgnoreCase("false"))
                    pass("Check box :" + element + "] in " + page + " is not checked");
                else
                    fail("Check box :" + element + "] in " + page + " is checked which is not expected", new Exception("Checkbox is checked"));

            }

        } catch (Exception e) {
            fail("Checkbox assertion failed", e);
        }
    }


    public void assertAttributeContainsValue(String element, String page, String attribute, String expectedValue, String... appendValueToXpath) throws Exception {
        String actualValue = " ";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            actualValue = webElement.getAttribute(attribute);
            if (actualValue.contains(expectedValue) && webElement.isDisplayed())
                pass("Actual value [" + actualValue + "] is matching with expected value [" + expectedValue + "]");
            else
                throw new Exception("Actual value [" + actualValue + "] is not matching with expected value [" + expectedValue + "] or Element is not displaying");
        } catch (Exception e) {
            fail("Actual value [" + actualValue + "] is not matching with expected value [" + expectedValue + "]", e);
        }
    }


    public void assertElementContainsText(String element, String page, String expectedValue, String... appendValueToXpath) throws Exception {
        String actualText = " ";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            actualText = webElement.getText();
            if (actualText.equals(" ") || actualText == null || actualText.equals(""))
                actualText = webElement.getAttribute("innerText");

            if (actualText.contains(expectedValue) && webElement.isDisplayed())
                pass("Actual Text [" + actualText + "] contains the expected text[" + expectedValue + "]");
            else
                throw new Exception("Actual Text [" + actualText + "] is not matching with expected text [" + expectedValue + "] or Element is not displaying");
        } catch (Exception e) {
            fail("Actual Text [" + actualText + "] is not matching with expected text [" + expectedValue + "]", e);
        }
    }


    public void assertTextDisplayed(String text) throws Exception {
        try {
            waitUntilpageLoaded();
            waitUntilElementLoaded(driver.findElement(By.xpath("//*[normalize-space(text())=\"" + text + "\"]")), 5);
            if (driver.findElement(By.xpath("//*[normalize-space(text())=\"" + text + "\"]")).isDisplayed())
                pass("Test exists in page: " + text);
            else throw new Exception();
        } catch (Exception e) {
            fail("Text " + text + "doesn't exists in page", e);
        }
    }


    public void assertTextDisplayed(String text, boolean trueOrFalse) throws Exception {
        try {
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            if (trueOrFalse) {
                waitUntilpageLoaded();
                waitUntilElementLoaded(driver.findElement(By.xpath("//*[normalize-space(text())=\"" + text + "\"]")), 5);
                if (driver.findElement(By.xpath("//*[normalize-space(text())=\"" + text + "\"]")).isDisplayed())
                    pass("Text exists in page: " + text);
                else throw new Exception();
            } else {
                waitUntilpageLoaded();
                if (!driver.findElement(By.xpath("//*[normalize-space(text())=\"" + text + "\"]")).isDisplayed())
                    pass("Text not exists in page: " + text);
                else fail("Text exists in page", new Exception());
            }
            driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        } catch (Exception e) {
            if (!trueOrFalse)
                pass("Text not exists in page: " + text);
            else
                fail("Text doesn't exists in page", e);
            driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        }
    }


    public void refresh() throws Exception {
        try {
            driver.navigate().refresh();
        } catch (Exception e) {
            fail("Failed to refresh page", e);
        }
    }


    public void navigateBack() throws Exception {
        try {
            driver.navigate().back();
        } catch (Exception e) {
            fail("Failed to navigate back", e);
        }
    }


    public void assertAttributeValue(String xpath, String attribute, String expectedValue) throws Exception {
        String actualValue = " ";
        try {
            WebElement webElement = driver.findElement(By.xpath(xpath));
            actualValue = webElement.getAttribute(attribute);
            if (actualValue.equals(expectedValue) && webElement.isDisplayed())
                pass("Actual value: [" + actualValue + "] of " + attribute + " is matching expected value [" + expectedValue + "]");
            else throw new Exception("Actual value is not matching with expected value or Element is not displaying");
        } catch (Exception e) {
            fail("Actual Value: [" + actualValue + "] is not matching with expected value [" + expectedValue + "] of " + xpath + "'s attribute" + attribute, e);
        }
    }


    public <T> void assertEquals(T actual, T expected) throws Exception {
        try {
            if (expected != null) {
                if (!actual.equals(expected))
                    throw new Exception("Actual Value: [" + actual + "] is not Matching with expected value: [" + expected + "]");
                pass("Actual Text [" + actual.toString() + "] is matching with expected value [" + expected.toString() + "]");
            } else {
                if (!(actual == null))
                    throw new Exception("Actual Value: [" + actual + "] is not Matching with expected value: [" + expected + "]");
                pass("Actual Text [" + actual + "] is matching with expected value [" + expected + "]");
            }
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }


    public void assertContains(String expected, String actual) throws Exception {
        try {

            if (actual.contains(expected))
                pass("Actual Text [" + actual + "] is having  expected text [" + expected + "]");
            else
                throw new Exception("Actual Text [" + actual + "] is not containing the expected text [" + expected + "]");
        } catch (Exception e) {
            fail("Actual Text [" + actual + "] is not containing the expected text [" + expected + "]", e);
        }
    }


    public void switchToFrameWhichHasElement(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            boolean flag = false;

            List<WebElement> iframes = driver.findElements(By.xpath("//iframe"));
            for (WebElement frame : iframes) {
                if (flag)
                    break;
                driver.switchTo().frame(frame);
                try {
                    this.findElement(element, page, appendValueToLocator);
                    flag = true;
                } catch (Exception e) {
                }
                List<WebElement> innerFrames = driver.findElements(By.xpath("//iframe"));
                if (innerFrames.size() > 0 & !flag) {
                    for (WebElement innerFrame : innerFrames) {
                        try {
                            driver.switchTo().frame(innerFrame);
                            this.findElement(element, page);
                            flag = true;
                            break;
                        } catch (Exception e) {
                        }
                    }
                }
                if (!flag)
                    this.switchToDefaultContent();
            }
            if (!flag)
                throw new Exception("Element not found " + element);
            pass("Switched to Frame which has " + element + " of " + page);
            frameSetMoveToElement = setMoveToElement;
            setMoveToElement = false;

        } catch (Exception e) {
            fail("Failed to switch frame of" + element + " of " + page, e);
        }
    }


    public void switchToFrameWhichHasElement(By by) throws Exception {
        try {
            driver.switchTo().frame(driver.findElement(by));
            pass("Switched to Frame which has " + by + " element");
            frameSetMoveToElement = setMoveToElement;
            setMoveToElement = false;
        } catch (Exception e) {
            fail("Failed to switch frame of" + by + "element", e);
        }
    }


    public void switchToFrame(String nameOrId) throws Exception {
        try {
            driver.switchTo().frame(nameOrId);
            pass("Switched to Frame: " + nameOrId);
            frameSetMoveToElement = setMoveToElement;
            setMoveToElement = false;
        } catch (Exception e) {
            fail("Failed to switch frame: " + nameOrId, e);
        }
    }


    public void switchToDefaultContent() throws Exception {
        try {
            driver.switchTo().defaultContent();
            setMoveToElement = frameSetMoveToElement;
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }


    public void hover(String element, String page) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            Actions actions = new Actions(driver);
            actions.moveToElement(webElement).build().perform();
            pass("Hovered on " + element + " on " + page);
        } catch (Exception e) {
            fail("Failed to hover on " + element + " in " + page, e);
        }
    }


    public String getPageUrl() throws Exception {
        String url = driver.getCurrentUrl();
        log("Current page Url: " + url);
        return url;
    }


    public void clickOnHyperLink(String linkText) throws Exception {
        try {
            waitUntilpageLoaded();
            driver.findElement(By.linkText(linkText)).click();
            pass("Clicked on: " + linkText + " link");
        } catch (Exception e) {
            fail("Failed to Click on " + linkText + " link ", e);
        }
    }


    public void assertDropDownList(String element, String page, List<String> expectedDropDownOptions, String... appendValueToLocator) throws Exception {
        try {
            waitUntilpageLoaded();
            WebElement webElement = this.findElement(element, page);
            Select select = new Select(webElement);
            List<WebElement> webElements = select.getOptions();
            List<String> actualDropDownOptions = new ArrayList<>();
            for (WebElement option : webElements) {
                actualDropDownOptions.add(option.getText());
            }
            if (expectedDropDownOptions.equals(actualDropDownOptions))
                pass("Provided Options " + expectedDropDownOptions.toString() + " are exists in element " + element + " in" + page);
            else
                throw new Exception("Provided Options are not exists in element " + element + " in" + page + "\n" +
                        "Expected Options: " + expectedDropDownOptions + "\n Actual Options: " + actualDropDownOptions);
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }


    public void assertDropDownList(String element, String page, List<String> expectedDropDownOptions, boolean retainOrder, String... appendValueToLocator) throws Exception {
        try {
            waitUntilpageLoaded();
            WebElement webElement = this.findElement(element, page);
            Select select = new Select(webElement);
            List<WebElement> webElements = select.getOptions();
            List<String> actualDropDownOptions = new ArrayList<>();
            for (WebElement option : webElements) {
                actualDropDownOptions.add(option.getText());
            }
            if (retainOrder) {
                if (expectedDropDownOptions.equals(actualDropDownOptions))
                    pass("Provided Options " + expectedDropDownOptions.toString() + " are exists in element " + element + " in" + page);
                else {
                    if (expectedDropDownOptions.containsAll(actualDropDownOptions))
                        throw new Exception("Provided Options are exists in element " + element + " in" + page + " but not in Order" + "\n" +
                                "Expected Options: " + expectedDropDownOptions + "\n Actual Options: " + actualDropDownOptions);
                    else
                        throw new Exception("Provided Options are not exists in element " + element + " in" + page + "\n" +
                                "Expected Options: " + expectedDropDownOptions + "\n Actual Options: " + actualDropDownOptions);
                }
            } else {
                if (expectedDropDownOptions.containsAll(actualDropDownOptions))
                    pass("Provided Options " + expectedDropDownOptions.toString() + " are exists in element " + element + " in" + page);
                else
                    throw new Exception("Provided Options are not exists in element " + element + " in" + page + "\n" +
                            "Expected Options: " + expectedDropDownOptions + "\n Actual Options: " + actualDropDownOptions);

            }
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }


    public String getSelectedOptionFromDropDownList(String element, String page, String... appendValueToLocator) throws Exception {
        String option = "";
        try {
            waitUntilpageLoaded();
            WebElement webElement = this.findElement(element, page, appendValueToLocator);
            Select select = new Select(webElement);
            option = select.getFirstSelectedOption().getText();
            log("Selected option of Element: " + element + " is " + option);
        } catch (Exception e) {
            fail("Failed to get selected option: " + option + " of Element: " + element, e);
        }
        return option;
    }


    public void clickIfElementPresent(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            boolean flag = false;
            waitUntilpageLoaded();
            int size = this.getWebElementsSize(element, page, appendValueToLocator);
            if (size > 0) {
                if (this.isDisplayed(element, page, appendValueToLocator) &&
                        this.isEnabled(element, page, appendValueToLocator)) {
                    this.javaScriptExecutorClick(element, page, appendValueToLocator);
                    flag = true;
                }
            }
            if (flag)
                pass("Clicked on element: " + element + " in " + page);
            else
                pass("Element: " + element + " not exist in " + page);
        } catch (Exception e) {
            fail("Failed to click on Element: " + element + " in " + page, e);
        }
    }

    public boolean isDisplayed(String element, String page, String... appendValueToLocator) throws Exception {
        boolean flag = false;
        try {
            waitUntilpageLoaded();
            flag = driver.findElement(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator))
                    .isDisplayed();
            if (flag)
                log("Element " + element + " is displaying in " + page);
            else
                log("Element " + element + " is not displaying in " + page);
        } catch (Exception e) {
            log("Element " + element + " is not displaying in " + page);
        }
        return flag;
    }

    public boolean isSelected(String element, String page, String... appendValueToLocator) throws Exception {
        boolean flag = false;
        try {
            waitUntilpageLoaded();
            flag = this.findElement(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator))
                    .isSelected();
            if (flag)
                log("Element " + element + " is selected in " + page);
            else
                log("Element " + element + " is not selected in " + page);
        } catch (Exception e) {
            log("Element " + element + " is not selected in " + page);
        }
        return flag;
    }

    public void assertImageLoaded(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            WebElement webElement = this.findElement(element, page, appendValueToLocator);
            HttpGet request = new HttpGet(webElement.getAttribute("src"));
            HttpResponse response = client.execute(request);
            int responseCode = response.getStatusLine().getStatusCode();
            boolean flag = responseCode >= 200 && responseCode <= 299;
            if (flag)
                pass("Image Loaded Successfully: " + element + " in Page: " + page);
            else
                throw new Exception("Image not Loaded: " + element + " in Page: " + page);
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }

    public boolean isEnabled(String element, String page, String... appendValueToLocator) throws Exception {
        boolean flag = false;
        try {
            waitUntilpageLoaded();
            flag = this.findElement(element, page, appendValueToLocator).isEnabled();
            if (flag)
                log("Element " + element + " is enabled in " + page);
            else
                log("Element " + element + " is not enabled in " + page);
        } catch (Exception e) {
            log("Element " + element + " is not enabled in " + page);
        }
        return flag;
    }

    public void openNewWindow(String... url) throws Exception {
        String launchUrl = "";
        if (url.length > 0)
            launchUrl = url[0];
        try {
            ((JavascriptExecutor) driver).executeScript("window.open(arguments[0])", launchUrl);
            pass("Launched url in new Window: " + launchUrl);
        } catch (Exception e) {
            fail("Failed to Launch new Window with Url: " + launchUrl, e);
        }
    }

    public void assertFail(String message) throws Exception {
        fail(message, new Exception(message));
    }

    public void assertPass(String message) throws IOException {
        pass(message);
    }

    public void wait(int seconds) throws InterruptedException {
        long time = seconds * 1000;
        Thread.sleep(time);
    }

    public void exit() {
        /*for (WebDriver driver : drivers) {
            driver.quit();
        }*/
        extentReports.flush();
    }

    public void afterTest() {
        System.out.println("-----------------------------Tests Completed-----------------------------");
    }

    public void acceptAlert() throws Exception {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            pass("Accepted popup Alert");
        } catch (Exception e) {
            fail("Failed to accept alert", e);
        }
    }


    public void declineAlert() throws Exception {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            pass("Declined popup Alert");
        } catch (Exception e) {
            fail("Failed to decline alert", e);
        }
    }


    public String getAlertText() throws Exception {
        boolean flagM = setMoveToElement;
        boolean flagS = StartTest.setScreenshotforEachStep;
        setMoveToElement = false;
        StartTest.setScreenshotforEachStep = false;

        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            pass("Alert text is:" + alertText);
            setMoveToElement = flagM;
            StartTest.setScreenshotforEachStep = flagS;
            return alertText;
        } catch (Exception e) {

            fail("Failed to get alert text", e);
        }
        return null;
    }


    public void setAlertText(String text) throws Exception {
        try {
            Alert alert = driver.switchTo().alert();
            alert.sendKeys(text);
            pass("Alert text set to:" + text);
        } catch (Exception e) {
            fail("Failed to set alert text: " + text, e);
        }
    }

    public boolean isAlertPresent() throws Exception {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException Ex) {
            return false;
        } catch (Exception e) {
            fail("Failed due to alert", e);
            return false;
        }

    }

    public void scrollDown(int pixels) throws Exception {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(" + String.valueOf(pixels) + ", document.body.scrollHeight)");
        } catch (Exception e) {
            fail("Failed to scroll down", e);
        }
    }

    public void scrollByArrow(int noOfTimes) throws Exception {
        try {
            for (int i = 0; i < noOfTimes; i++) {
                driver.findElement(By.cssSelector("body")).sendKeys(Keys.ARROW_DOWN);
            }
        } catch (Exception e) {
            fail("Failed to scroll down", e);
        }
    }

    public void scrollToElement(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            waitUntilElementVisible(element, page, waitTimeout, appendValueToLocator);
            WebElement webElement = this.findElement(element, page, appendValueToLocator);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", webElement);

            pass("Scrolled to element: " + element + " in " + page);
        } catch (Exception e) {
            fail("Failed to scroll to " + element + " in " + page, e);
        }
    }

    public void waitUntilElementVisible(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            int i = 0;
            //boolean flag = true;
            while (!this.isDisplayed(element, page, appendValueToLocator)) {
                i++;
                wait(1);
                if (i > 5) {
                    //flag = false;
                    break;
                    //throw new NoSuchElementException(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator).toString());
                }
            }
        } catch (Exception e) {
            fail("Element is not displaying : " + element + " in page: " + page, e);
        }
    }

    public void waitUntilElementVisible(String element, String page, int timeoutSeconds, String... appendValueToLocator) throws Exception {
        try {
            int i = 0;
            //boolean flag = true;
            while (!this.isDisplayed(element, page, appendValueToLocator)) {
                i++;
                wait(1);
                if (i > timeoutSeconds) {
                    //flag = false;
                    break;
                    //throw new NoSuchElementException(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator).toString());
                }
            }
        } catch (Exception e) {
            fail("Element is not displaying : " + element + " in page: " + page, e);
        }
    }

    private void waitUntilpageLoaded() throws InterruptedException {
        JavaScriptWaits javaScriptWaits = new JavaScriptWaits(driver);
        javaScriptWaits.waitAllRequest();
    }

    private WebElement findElement(String element, String page, String... appendValueToLocator) throws Exception {
        waitUntilpageLoaded();
        WebElement element1 = driver.findElement(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator));
        if (setMoveToElement) {
            Actions actions = new Actions(driver);
            actions.moveToElement(element1).build().perform();
            // ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element1);
        }
        if (highlightElement) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);",
                    element1, " border: 2px solid red;");
        }
        return element1;
    }

    private WebElement findElementFrame(String element, String page, String... appendValueToLocator) throws Exception {
        waitUntilpageLoaded();
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        WebElement element1 = driver.findElement(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator));
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        return element1;
    }

    public WebElement findElement(By by) throws Exception {
        WebElement element1 = null;
        try {
            waitUntilpageLoaded();
            element1 = driver.findElement(by);
            /*Actions actions = new Actions(driver);
            actions.moveToElement(element1).build().perform();*/
            return element1;
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
        return element1;
    }


    public int getWebElementsSize(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            waitUntilpageLoaded();
            return driver.findElements(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator)).size();
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
        return 0;
    }


    public List<WebElement> getWebElements(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            waitUntilpageLoaded();
            return driver.findElements(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator));
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
        return null;
    }


    public String getTilte() {
        return driver.getTitle();
    }


    public String getAttributeValue(String element, String page, String attribute, String... appendValueToXpath) throws Exception {
        String attributeValue = "";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            attributeValue = webElement.getAttribute(attribute);
            log("Attribute Value of " + element + " is [" + attributeValue + "]");
        } catch (Exception e) {
            fail("Failed to get attribute value of element: " + element + " in " + page, e);
        }
        return attributeValue;
    }

    public void waitUntilElementLoaded(String element, String page, String... appendValueToLocator) throws Exception {
        new WebDriverWait(driver, 10).until(driver ->
        {
            try {
                return ExpectedConditions.visibilityOf(driver.findElement(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator)));
            } catch (Exception e) {
                try {
                    fail(e.getMessage(), e);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            return null;
        });
    }

    public void waitUntilElementLoaded(WebElement element, int seconds) throws Exception {
        new WebDriverWait(driver, seconds).until(driver ->
        {
            try {
                return ExpectedConditions.visibilityOf(element);
            } catch (Exception e) {
                try {
                    fail(e.getMessage(), e);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            return null;
        });
    }
}
