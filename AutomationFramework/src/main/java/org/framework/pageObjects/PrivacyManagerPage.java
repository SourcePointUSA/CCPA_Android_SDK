package org.framework.pageObjects;

import static org.framework.logger.LoggingManager.logMessage;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.framework.enums.PlatformName;
import org.framework.helpers.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.HidesKeyboard;
import io.appium.java_client.MobileBy;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.WithTimeout;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.appium.java_client.touch.offset.PointOption;

public class PrivacyManagerPage extends Page {

	WebDriver driver;

	public PrivacyManagerPage(WebDriver driver) throws InterruptedException {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		logMessage("Initializing the " + this.getClass().getSimpleName() + " elements");
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
		Thread.sleep(1000);
	}

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "//*[@resource-id='__genieContainer']")
	public WebElement PrivacyManagerView;

	public WebElement eleButton;

	@WithTimeout(time = 50, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "(//android.view.View)")
	public List<WebElement> AllButtons;

	// CCCPA application elements
	@AndroidFindBy(xpath = "//android.view.View[@text='Accept All']")
	public WebElement ccpa_AcceptAllButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "//andro"
			+ "id.widget.Button[@text='Save & Exit']")
	public WebElement ccpa_SaveAndExitButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "//android.view.View[@text='Reject All']")
	public WebElement ccpa_RejectAllButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "//android.widget.Button[@text='Cancel']")
	public WebElement ccpa_CancelButton;

	@AndroidFindBy(xpath = "(//android.view.View)")
	public List<WebElement> ONToggleButtons;

	public void scrollAndClick(String text) throws InterruptedException {
		driver.findElement(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\""
						+ text + "\").instance(0))"))
				.click();
	}

	boolean privacyManageeFound = false;

	public boolean isPrivacyManagerViewPresent() throws InterruptedException {
		try {
			waitForElement(ccpa_SaveAndExitButton, timeOutInSeconds);		
			if (driver.findElements(By.xpath("//*[@class='android.webkit.WebView']")).size() > 0)
				privacyManageeFound = true;

		} catch (Exception e) {
			privacyManageeFound = false;
		}
		return privacyManageeFound;
	}

	public WebElement eleButton(String udid, String buttonText) {
		eleButton = (WebElement) driver.findElement(By.id("+buttonText+"));
		return eleButton;

	}
	
	public void waitForElement(WebElement ele, int timeOutInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.visibilityOf(ele));
	}


}
