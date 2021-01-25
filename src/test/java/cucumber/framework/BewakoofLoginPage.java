package cucumber.framework;

import driverFactory.DriverFactory;
import driverFactory.StartTest;
import io.cucumber.java.en.And;

public class BewakoofLoginPage {
    DriverFactory driverFactory = StartTest.getDriverInstance();

    @And("Login to Bewakoof using {string} and {string}")
    public void iLoginToBewakoofUsingUsernameAndPassword(String username, String password) throws Exception {
        driverFactory.clickOnHyperLink("Login");
        driverFactory.enterText("txt_Username", "BewakoofLoginPage", username);
        driverFactory.click("btn_Continue", "BewakoofLoginPage");
        driverFactory.enterText("txt_Password", "BewakoofLoginPage", password);
        driverFactory.click("btn_Login", "BewakoofLoginPage");
    }
}
