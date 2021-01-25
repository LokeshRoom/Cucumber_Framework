package cucumber.framework;

import driverFactory.DriverFactory;
import driverFactory.StartTest;
import io.cucumber.java.en.And;

public class BewakoofCartPage {
    DriverFactory driverFactory = StartTest.getDriverInstance();

    @And("verify product added in cart with correct {string}")
    public void verifyItemAddedToCartWithCorrectSize(String size) throws Exception {
        driverFactory.click("btn_GoToBag", "BewakoofProductPage");
        driverFactory.assertElementContainsText("lbl_Price", "BewakoofCartPage", BewakoofProductPage.price);
        driverFactory.assertTextExists(BewakoofProductPage.item);
        driverFactory.assertElementText("lbl_Size", "BewakoofCartPage", size);
        while (driverFactory.isDisplayed("lnk_Remove", "BewakoofCartPage")) {
            driverFactory.click("lnk_Remove", "BewakoofCartPage");
        }
        driverFactory.assertTextExists("Nothing in the bag");
    }
}
