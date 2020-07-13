package org.framework.pageObjects;

import static org.framework.logger.LoggingManager.logMessage;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.WithTimeout;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class SiteDebugInfoPage {

	WebDriver driver;

	public SiteDebugInfoPage(WebDriver driver) throws InterruptedException {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		logMessage("Initializing the " + this.getClass().getSimpleName() + " elements");
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
		Thread.sleep(1000);
	}

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointccpa.app:id/tvConsentUUID")
	public WebElement CCPAConsentUUID;

	@AndroidFindBy(id = "com.sourcepointccpa.app:id/tvEUConsent")
	public WebElement CCPAEUConsent;

	@AndroidFindBy(id = "com.sourcepointccpa.app:id/tv_consentsNotAvailable")
	public WebElement CCPAConsentNotAvailable;

	@AndroidFindBy(id = "com.sourcepointccpa.app:id/consentRecyclerView")
	public List<WebElement> CCPAConsentView;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "//android.widget.ImageButton[@content-desc='Navigate up']")
	public WebElement BackButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointccpa.app:id/action_showPM")
	public WebElement CCPAShowPMLink;

	ArrayList<String> consentData = new ArrayList<>();

	public ArrayList<String> storeConsent(WebElement consentUUID, WebElement euConsent) {
		consentData.add(consentUUID.getText());
		consentData.add(euConsent.getText());
		return consentData;
	}

	public ArrayList<String> storeConsent(WebElement consentUUID) {
		consentData.add(consentUUID.getText());
		return consentData;
	}

	public boolean isConsentViewDataPresent(List<WebElement> consentView) {
		if (consentView.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkForNoPurposeConsentData(WebElement consentNotAvailable) {
		return consentNotAvailable.isDisplayed();
	}
}
