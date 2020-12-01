package cucumber.framework;

import driverFactory.DriverFactory;
import driverFactory.StartTest;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.Keys;

public class GooglePage {

    DriverFactory driverFactory = StartTest.getDriverInstance();

    @When("I search for {string} in Google homepage")
    public void iSearchFor(String searchText) throws Exception {
        driverFactory.enterText("txt_SearchBox","Google_HomePage",searchText);
        driverFactory.enterText("txt_SearchBox","Google_HomePage", Keys.ENTER);

    }

    @Then("I should get results with {string}")
    public void iShouldGetResultsWith(String searchText) throws Exception {
        driverFactory.assertTextExists(searchText);
    }
}
