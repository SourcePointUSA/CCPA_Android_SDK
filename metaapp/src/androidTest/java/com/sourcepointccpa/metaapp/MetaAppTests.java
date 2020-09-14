package com.sourcepointccpa.metaapp;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import androidx.test.rule.ActivityTestRule;

import com.sourcepointccpa.app.ui.SplashScreenActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MetaAppTests extends Utility{
    @Rule
    public ActivityTestRule<SplashScreenActivity> mActivityTestRule = new ActivityTestRule<>(SplashScreenActivity.class);

    @Before
    public void setup() {
        mActivityTestRule.getActivity();
    }

    @Test
    public void checkForConsentWithRejectActionFromMessage() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(REJECT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentsWithRejectActionFromPrivacyManager() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
        chooseActionFromPM(PM_REJECT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentWithSaveAndExitActionFromPrivacyManager() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
        selectConsents(PARTIAL_CONSENT_LIST);
        chooseAction(PM_SAVE_AND_EXIT);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));
    }

    @Test
    public void checkCancelActionFromPrivacyManager() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
        chooseAction(PM_CANCEL);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
    }

    @Test
    public void checkConsentFromDirectPrivacyManager() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
        chooseActionFromPM(PM_REJECT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        loadPrivacyManagerDirect();
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkCancelFromDirectPrivacyManager() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
        chooseActionFromPM(PM_REJECT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        loadPrivacyManagerDirect();
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        chooseAction(PM_CANCEL);
        Assert.assertTrue(checkFor(ACTION_MESSAGE));
    }

    @Test
    public void checkNoMessageDisplayWithShowMessageOnce() {
        addPropertyFor(SHOW_MESSAGE_ONCE, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        selectConsents(PARTIAL_CONSENT_LIST);
        chooseAction(PM_SAVE_AND_EXIT);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertFalse(checkWebViewDisplayedFor(MESSAGE));
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
    }

    @Test
    public void resetConsentDataAndCheckForMessageWithShowMessageOnce() {
        addPropertyFor(SHOW_MESSAGE_ONCE, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
        chooseActionFromPM(PM_REJECT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertFalse(checkWebViewDisplayedFor(MESSAGE));
        Assert.assertTrue(checkFor(ACTION_MESSAGE));
        navigateBackToListView();
        swipeAndChooseAction(RESET_ACTION, YES);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentDataAfterNoResetWithShowMessageAlways() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
        chooseActionFromPM(PM_REJECT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        navigateBackToListView();
        swipeAndChooseAction(RESET_ACTION, NO);
        tapOnProperty();
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentDataAfterResetWithShowMessageAlways() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
        chooseActionFromPM(PM_REJECT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        navigateBackToListView();
        swipeAndChooseAction(RESET_ACTION, YES);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void deleteProperty() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(REJECT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        navigateBackToListView();
        swipeAndChooseAction(DELETE_ACTION, NO);
        Assert.assertTrue(checkPropertyPresent());
        swipeAndChooseAction(DELETE_ACTION, YES);
        Assert.assertFalse(checkPropertyPresent());
    }

    @Test
    public void checkMessageAfterEditProperty() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(REJECT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        navigateBackToListView();
        swipeAndChooseAction(EDIT_ACTION, PARAM_VALUE );
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentWithAuthenticationToShowMessageAlways()  {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, UNIQUE_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
        chooseAction(PM_SAVE_AND_EXIT);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentWithAuthenticationToShowMessageOnce()  {
        addPropertyFor(SHOW_MESSAGE_ONCE, UNIQUE_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
        chooseActionFromPM(REJECT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertTrue(checkFor(ACTION_MESSAGE));
        loadPrivacyManagerDirect();
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentForPropertyWithDifferentAuthenticationAlwaysWithDifferentAuthID()  {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, UNIQUE_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(REJECT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        navigateBackToListView();
        addPropertyFor(SHOW_MESSAGE_ALWAYS, UNIQUE_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentWithSameAuthenticationWhenPropertyDeleteAndRecreate()  {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, UNIQUE_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(REJECT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        navigateBackToListView();
        swipeAndChooseAction(DELETE_ACTION, YES);
        addPropertyFor(SHOW_MESSAGE_ALWAYS, EXISTING_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }


    @Test
    public void checkNoMessageAfterLoggedInWithAuthIDWhenConsentAlreadyGiven() {
        addPropertyFor(SHOW_MESSAGE_ONCE, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(PRIVACY_SETTINGS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
        chooseActionFromPM(REJECT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        navigateBackToListView();
        swipeAndChooseAction(EDIT_ACTION, UNIQUE_AUTHENTICATION);
        Assert.assertFalse(checkWebViewDisplayedFor(MESSAGE));
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
    }
}
