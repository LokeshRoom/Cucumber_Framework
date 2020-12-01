package objectutils;

import com.esotericsoftware.yamlbeans.YamlReader;
import org.json.JSONObject;
import org.openqa.selenium.By;

import java.io.FileReader;
import java.util.Map;

public class ObjectReader {

    protected static JSONObject readYaml(String fileName, String absolutePath) throws Exception {
        YamlReader yamlReader = new YamlReader(new FileReader(absolutePath + fileName));
        Map objectMap = (Map) yamlReader.read();
        return new JSONObject(objectMap);
    }

    protected static String getUrl(JSONObject object, String urlKey) {
        return object.getJSONObject("Objects").getString(urlKey);
    }

    protected static By getElement(JSONObject object, String element, String page) throws Exception {
        String locator = object.getJSONObject("Objects").getJSONObject(page).getJSONObject(element).keys().next();
        String value = object.getJSONObject("Objects").getJSONObject(page).getJSONObject(element).getString(locator);
        By locatorValue = null;
        switch (locator.toLowerCase()) {
            case "classname":
                locatorValue = By.className(value);
                break;
            case "cssselector":
                locatorValue = By.cssSelector(value);
                break;
            case "id":
                locatorValue = By.id(value);
                break;
            case "tagname":
                locatorValue = By.tagName(value);
                break;
            case "xpath":
                locatorValue = By.xpath(value);
                break;
            case "linktext":
                locatorValue = By.linkText(value);
                break;
            case "name":
                locatorValue = By.name(value);
                break;
            case "partiallinktext":
                locatorValue = By.partialLinkText(value);
                break;
            default:
                throw new Exception("objectTypeException: Please enter correct object locator type");
        }
        return locatorValue;
    }

}
