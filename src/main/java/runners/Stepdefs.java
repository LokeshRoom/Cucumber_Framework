package runners;

import driverFactory.Browsers;
import driverFactory.DriverFactory;
import driverFactory.StartTest;
import io.cucumber.core.api.Scenario;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import repoting.ExtentReporterUtils;


public class Stepdefs {

    public StartTest startTest;
    public Scenario scenario= ExtentReporterUtils.scenario;

    @When("I launch {string} browser in {string} environment using {string} Object Repository")
    public void iLaunchBrowserInEnviromentUsingObjectRepository(String browser, String env, String objectRepo) throws Exception {
     Browsers browsers=null;
     if (browser.equalsIgnoreCase("chrome"))
         browsers=Browsers.CHROME;
     else if (browser.equalsIgnoreCase("ie"))
         browsers=Browsers.IE;
     else if(browser.equalsIgnoreCase("firefox"))
         browsers=Browsers.FIREFOX;
     else if(browser.equalsIgnoreCase("edge"))
         browsers=Browsers.EDGE;
     else if(browser.equalsIgnoreCase("remote_chrome"))
         browsers=Browsers.REMOTE_CHROME;
     else
         throw new Exception("Provide correct browser value");
        startTest=new StartTest(browsers,env,objectRepo);
        startTest.scenario=scenario;
    }


    @Given("I open {string} page")
    public void iOpenPage(String objectUrl) throws Exception {
        DriverFactory driverFactory = StartTest.getDriverInstance();
        driverFactory.goToUrl(objectUrl);
    }

    @Given("I close browser")
    public void tearDown(){
        DriverFactory driverFactory = StartTest.getDriverInstance();
        driverFactory.tearDown();
    }
    @Before
    public void setScenario(Scenario scenario){
        ExtentReporterUtils.scenario =scenario;
    }
}
