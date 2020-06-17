package org.framework.pageObjects;

import static org.framework.logger.LoggingManager.logMessage;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.framework.enums.PlatformName;
import org.framework.helpers.Page;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HidesKeyboard;
import io.appium.java_client.MobileDriver;
//import io.appium.java_client.WebElement;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.WithTimeout;
import io.appium.java_client.pagefactory.iOSFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.appium.java_client.touch.offset.PointOption;

public class NewSitePage extends Page {
	WebDriver driver;

	public NewSitePage(WebDriver driver) throws InterruptedException {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		logMessage("Initializing the " + this.getClass().getSimpleName() + " elements");
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
		Thread.sleep(1000);
	}

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointccpa.app:id/toolbar_title")
	public WebElement CCPANewSitePageHeader;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text='Account ID")
	public WebElement AccountIDLabel;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointccpa.app:id/action_saveProperty")
	public WebElement CCPASaveButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointccpa.app:id/etAccountID")
	public WebElement CCPAAccountID;

	@AndroidFindBy(id = "com.sourcepointccpa.app:id/etPropertyName")
	public WebElement CCPASiteName;

	@AndroidFindBy(id = "com.sourcepointccpa.app:id/toggleStaging")
	public WebElement CCPAToggleButton;

	@AndroidFindBy(id = "com.sourcepointccpa.app:id/etAuthID")
	public WebElement CCPAAuthID;

	@AndroidFindBy(id = "com.sourcepointccpa.app:id/etKey")
	public WebElement CCPAParameterKey;

	@AndroidFindBy(id = "com.sourcepointccpa.app:id/etValue")
	public WebElement CCPAParameterValue;

	@AndroidFindBy(id = "com.sourcepointccpa.app:id/btn_addParams")
	public WebElement CCPAParameterAddButton;

	@WithTimeout(time = 50, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "android:id/message")
	public List<WebElement> ErrorMessage;

	@WithTimeout(time = 50, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "android:id/button1")
	public WebElement OKButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointccpa.app:id/etPropertyId")
	public WebElement CCPASiteId;

	@AndroidFindBy(id = "com.sourcepointccpa.app:id/etPMId")
	public WebElement CCPAPMId;

	@AndroidFindBy(id = "com.sourcepointccpa.app:id/toggleShowPM")
	public WebElement CCPAShowPrivacyManager;

	boolean paramFound = false;

	public void selectCampaign(WebElement ele, String staggingValue) throws InterruptedException {

		if (staggingValue.equals("ON")) {
			Point point = ele.getLocation();
			TouchAction touchAction = new TouchAction((PerformsTouchActions) driver);

			touchAction.tap(PointOption.point(point.x + 20, point.y + 20)).perform();
		}
		Thread.sleep(3000);
	}

	public void addTargetingParameter(WebElement paramKey, WebElement paramValue, String key, String value)
			throws InterruptedException {

		((HidesKeyboard) driver).hideKeyboard();
		paramKey.sendKeys(key);

		((HidesKeyboard) driver).hideKeyboard();
		paramValue.sendKeys(value);

		((HidesKeyboard) driver).hideKeyboard();
	}

	public String getError() throws InterruptedException {
		boolean check = false;
		// waitForElement(ErrorMessage, 10);
		Thread.sleep(5000);
		int i = ErrorMessage.size();

		String errorMsg = ErrorMessage.get(ErrorMessage.size() - 1).getText();
		return errorMsg;
	}

	public boolean verifyError() throws InterruptedException {
		boolean check = false;
		// waitForElement(ErrorMessage, 10);
		Thread.sleep(3000);
		int i = ErrorMessage.size();

		String errorMsg = ErrorMessage.get(ErrorMessage.size() - 1).getText();
		if (errorMsg.equals("Please enter targeting parameter key and value")) {
			check = true;
		}
		return check;
	}

//	public boolean verifyError() {
//		return paramFound;
//
//	}

	public void waitForElement(WebElement ele, int timeOutInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.visibilityOf(ele));
	}

	public boolean verifyErrorMsg(String udid) throws InterruptedException {
		boolean check = false;
		// waitForElement(ErrorMessage, 10);
		Thread.sleep(3000);
		int i = ErrorMessage.size();

		String errorMsg = ErrorMessage.get(ErrorMessage.size() - 1).getText();

		if (errorMsg.equals("Please enter property details")) {
			check = true;
		}
		return check;
	}
}
