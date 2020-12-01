package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;


@CucumberOptions(plugin = {"pretty"},features = {"src\\test\\Features"},glue = {"runners","cucumber.framework"})
public class RunCucumberTest extends AbstractTestNGCucumberTests {
}
