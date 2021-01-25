package cucumber.framework;

import driverFactory.DriverFactory;
import driverFactory.StartTest;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;

public class BewakoofHomePage {
    DriverFactory driverFactory = StartTest.getDriverInstance();

    @When("Navigate to {string} category")
    public void iNavigateToCategories(String categories) throws Exception {
        driverFactory.findElement(By.xpath("//div[@class='tagStyle']/following-sibling::p[text()='" + categories + "']")).click();
        driverFactory.log("Clicked on " + categories);
    }
}
