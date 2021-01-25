package cucumber.framework;

import driverFactory.DriverFactory;
import driverFactory.StartTest;
import io.cucumber.java.en.And;
import org.openqa.selenium.By;

public class BewakoofProductPage {
    public static String item;
    public static String price;
    DriverFactory driverFactory = StartTest.getDriverInstance();

    @And("Add first item to cart with size {string}")
    public void addFirstItemToCartWithSize(String size) throws Exception {
        driverFactory.click("lnk_Product1", "BewakoofProductPage");
        item = driverFactory.getText("lbl_ProductName", "BewakoofProductPage");
        price = driverFactory.getText("lbl_Price", "BewakoofProductPage");
        driverFactory.findElement(By.xpath("//div[contains(@class,'eachSize')]//span[text()='" + size + "']")).click();
        driverFactory.log("Clicked on Size: " + size);
        driverFactory.click("btn_AddToCart", "BewakoofProductPage");
    }
}
