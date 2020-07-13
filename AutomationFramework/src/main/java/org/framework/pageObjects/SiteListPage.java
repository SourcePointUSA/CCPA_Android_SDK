package org.framework.pageObjects;

import static org.framework.logger.LoggingManager.logMessage;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.framework.enums.PlatformName;
import org.framework.helpers.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.WithTimeout;

public class SiteListPage extends Page {

	WebDriver driver;

	public SiteListPage(WebDriver driver) throws InterruptedException {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		logMessage("Initializing the " + this.getClass().getSimpleName() + " elements");
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
		Thread.sleep(1000);
	}

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointccpa.app:id/action_addProperty")
	public WebElement CCPAAddButton;

	@AndroidFindBy(id = "com.sourcepointccpa.app:id/toolbar_title")
	public WebElement CCPASiteListPageHeader;

	@AndroidFindBy(id = "com.sourcepointccpa.app:id/websiteListRecycleView")
	public WebElement CCPASiteListView;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointccpa.app:id/edit_button")
	public WebElement CCPAEditButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointccpa.app:id/reset_button")
	public WebElement CCPAResetButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointccpa.app:id/delete_button")
	public WebElement CCPADeleteButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointccpa.app:id/item_view")
	public List<WebElement> CCPASiteList;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointccpa.app:id/propertyNameTextView")
	public WebElement CCPASiteName;

	@WithTimeout(time = 50, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "android:id/message")
	public List<WebElement> ErrorMessage;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "android:id/button1")
	public WebElement YESButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "android:id/button2")
	public WebElement NOButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointccpa.app:id/item_view")
	public WebElement propertyItemView;

	boolean siteFound = false;

	public boolean isSitePressent_ccpa(String siteName) throws InterruptedException {

		siteFound = false;
		try {
		if (driver.findElements(By.id("com.sourcepointccpa.app:id/propertyNameTextView")).size() > 0) {
			if (driver.findElement(By.id("com.sourcepointccpa.app:id/propertyNameTextView")).getText()
					.equals(siteName)) {
				siteFound = true;
			}
		}
		}catch(Exception e) {
			siteFound = false;
		}
		return siteFound;
	}

	public boolean isSitePressent(String siteName) throws InterruptedException {
		return siteFound;

	}

	public void tapOnSite_ccpa(String siteName, List<WebElement> siteList) throws InterruptedException {
		driver.findElement(By.id("com.sourcepointccpa.app:id/propertyNameTextView")).click();
	}

	public void swipeHorizontaly_ccpa(String siteName) throws InterruptedException {
		WebElement ele = driver.findElement(By.id("com.sourcepointccpa.app:id/propertyNameTextView"));
		waitForElement(ele, timeOutInSeconds);

		Point point = ele.getLocation();
		TouchAction action = new TouchAction((PerformsTouchActions) driver);

		int[] rightTopCoordinates = { ele.getLocation().getX() + ele.getSize().getWidth(), ele.getLocation().getY() };
		int[] leftTopCoordinates = { ele.getLocation().getX(), ele.getLocation().getY() };
		action.press(PointOption.point(rightTopCoordinates[0] - 1, rightTopCoordinates[1] + 1))
				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(3000)))
				.moveTo(PointOption.point(leftTopCoordinates[0] + 1, leftTopCoordinates[1] + 1)).release().perform();
	}

	public void waitForElement(WebElement ele, int timeOutInSeconds) {;
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.visibilityOf(ele));
	}

	public boolean verifyDeleteSiteMessage() {
		return ErrorMessage.get(ErrorMessage.size() - 1).getText().contains("Do you want to delete this property?");

	}

}
