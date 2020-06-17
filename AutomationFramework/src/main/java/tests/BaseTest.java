package tests;

import org.framework.appium.AppiumServer;
import org.framework.drivers.AndroidDriverBuilder;
import org.framework.enums.PlatformName;
import org.framework.enums.PlatformType;
import org.framework.helpers.Page;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileDriver;

import static org.framework.logger.LoggingManager.logMessage;

import java.io.IOException;

public class BaseTest extends Page {
	public WebDriver driver = null;

	@Parameters({ "platformType", "platformName", "model" })
	@BeforeMethod
	public void setupDriver(String platformType, String platformName, @Optional String model) throws IOException {
		try {
			if (platformType.equalsIgnoreCase(PlatformType.MOBILE.toString())) {
				setupMobileDriver(platformName, model);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setupMobileDriver(String platformName, String model) throws IOException {
		try {
			if (platformName.equalsIgnoreCase(PlatformName.ANDROID.toString())) {
				driver = new AndroidDriverBuilder().setupDriver(model);
				logMessage(model + " driver has been created for execution");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@AfterMethod
	public void teardownDriver() {
		try {
			driver.quit();
			logMessage("Driver has been quit from execution");
		} catch (Exception e) {
			System.out.println("Exception occured - " + e.getMessage());
			throw e;
		}
	}

}