package org.framework.pageObjects;

import static org.framework.logger.LoggingManager.logMessage;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.framework.helpers.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.MobileBy;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.WithTimeout;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

public class ConsentViewPage extends Page {

	WebDriver driver;

	public ConsentViewPage(WebDriver driver) throws InterruptedException {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		logMessage("Initializing the " + this.getClass().getSimpleName() + " elements");
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
		Thread.sleep(1000);
	}

	@AndroidFindBy(xpath = "//android.view.View[@text='X']")
	public WebElement CloseButton;

	@AndroidFindBy(xpath = "//android.widget.Button[@text='Show Purposes']")
	public WebElement ShowPurposesButton;

	@WithTimeout(time = 80, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "(//android.view.View)")
	public List<WebElement> ConsentMessage;

	@AndroidFindBy(id = "android:id/button2")
	public WebElement ShowSiteInfoButton;

	@AndroidFindBy(id = "android:id/button1")
	public WebElement ClearCookiesButton;

	@WithTimeout(time = 50, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "android:id/message")
	public List<WebElement> DeleteCookiesMessage;

	@WithTimeout(time = 50, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "android:id/button1")
	public WebElement YESButton;

	@WithTimeout(time = 50, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "android:id/button2")
	public WebElement NOButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(className = "android.widget.Button")
	public List<WebElement> ConsentButtons;

	@WithTimeout(time = 50, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "(//android.widget.Button)")
	public List<WebElement> AllButtons;

	public WebElement eleButton;

	boolean errorFound = false;

	public WebElement eleButton(String buttonText) {
		for (WebElement button : AllButtons) {
			if (button.getAttribute("text") != null && button.getAttribute("text").equals(buttonText)) {
				eleButton = (WebElement) driver
						.findElement(By.xpath("//android.widget.Button[@text='" + buttonText + "']"));
			} else if (button.getAttribute("content-desc") != null
					&& button.getAttribute("content-desc").equals(buttonText)) {
				eleButton = (WebElement) driver
						.findElement(By.xpath("//android.widget.Button[@content-desc='" + buttonText + "']"));
			}
		}
		return eleButton;
	}

	ArrayList<String> consentMsg = new ArrayList<String>();

	public ArrayList<String> getConsentMessageDetails() throws InterruptedException {
		
		WebElement button = eleButton("Privacy Settings");
		waitForElement(button, timeOutInSeconds);
		
		for (WebElement msg : ConsentMessage) {
			consentMsg.add(msg.getText());
		}
		return consentMsg;
	}


	public void waitForElement(WebElement ele, int timeOutInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.visibilityOf(ele));
	}

	public void clickOnButton(String buttonName) {
		for (WebElement button : ConsentButtons) {
			if (button.getText().equals(buttonName)) {
				button.click();
				break;
			}
		}
	}

	public boolean verifyDeleteCookiesMessage() {
		return DeleteCookiesMessage.get(DeleteCookiesMessage.size() - 1).getText()
				.contains("Cookies for all properties will be");
	}

}
