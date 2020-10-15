package com.sourcepointccpa.metaapp;

import androidx.test.rule.ActivityTestRule;

import com.sourcepointccpa.app.ui.SplashScreenActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class MetaAppValidationTests extends Utility{
    @Rule
    public ActivityTestRule<SplashScreenActivity> mActivityTestRule = new ActivityTestRule<>(SplashScreenActivity.class);

    @Before
    public void setup() {
        mActivityTestRule.getActivity();
    }

    @Test
    public void checkErrorForMandatoryFieldsWhileCreatingProperty() {
        tapOnAddProperty();
        addPropertyWith(ALL_FIELDS_BLANK);
        tapOnSave();
        Assert.assertTrue(checkErrorFor(MANDATORY_FIELDS));
        addPropertyWith(NO_ACCOUNT_ID);
        tapOnSave();
        Assert.assertTrue(checkErrorFor(MANDATORY_FIELDS));
        addPropertyWith(NO_PROPERTY_ID);
        tapOnSave();
        Assert.assertTrue(checkErrorFor(MANDATORY_FIELDS));
        addPropertyWith(NO_PROPERTY_NAME);
        tapOnSave();
        Assert.assertTrue(checkErrorFor(MANDATORY_FIELDS));
        addPropertyWith(NO_PM_ID);
        tapOnSave();
        Assert.assertTrue(checkErrorFor(MANDATORY_FIELDS));
    }

    @Test
    public void checkErrorForTargetingParamFieldsWhileCreatingProperty() {
        tapOnAddProperty();
        addPropertyWith(NO_PARAMETER_KEY_VALUE);
        Assert.assertTrue(checkErrorFor(TARGETING_PARAMETER_FIELDS));
        addPropertyWith(NO_PARAMETER_KEY);
        Assert.assertTrue(checkErrorFor(TARGETING_PARAMETER_FIELDS));
        addPropertyWith(NO_PARAMETER_VALUE);
        Assert.assertTrue(checkErrorFor(TARGETING_PARAMETER_FIELDS));
    }

    @Test
    public void checkNoMessageDisplayedForWrongCampaign() {
        tapOnAddProperty();
        addPropertyWith(WRONG_CAMPAIGN);
        tapOnSave();
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        Assert.assertFalse(checkWebViewDisplayedFor(MESSAGE));
    }

    @Test
    public void checkNoMessageDisplayedForWrongAccountId() {
        tapOnAddProperty();
        addPropertyWith(WRONG_ACCOUNT_ID);
        tapOnSave();
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        Assert.assertFalse(checkWebViewDisplayedFor(MESSAGE));
    }

    @Ignore("SP-2709:To get expected message, we are depend on the wrapper api, and it is not going to change any time soon")
    @Test
    public void checkNoMessageDisplayedForWrongPropertyId() {
        tapOnAddProperty();
        addPropertyWith(WRONG_PROPERTY_ID);
        tapOnSave();
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        Assert.assertFalse(checkWebViewDisplayedFor(MESSAGE));
    }

    @Test
    public void checkNoMessageDisplayedForWrongPropertyName() {
        tapOnAddProperty();
        addPropertyWith(WRONG_PROPERTY_NAME);
        tapOnSave();
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        Assert.assertFalse(checkWebViewDisplayedFor(MESSAGE));
    }

    @Test
    public void checkErrorMessageForDuplicateProperty() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(REJECT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        navigateBackToListView();
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkErrorFor(PROPERTY_EXITS_ERROR));
    }

    @Test
    public void checkMessageForWrongPrivacyManagerId(){
        tapOnAddProperty();
        addPropertyWith(WRONG_PRIVACY_MANAGER);
        tapOnSave();
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
    }

    @Test
    public void checkDirectPMLoadWithWrongPrivacyManagerId(){
        tapOnAddProperty();
        addPropertyWith(WRONG_PRIVACY_MANAGER);
        tapOnSave();
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        loadPrivacyManagerDirect();
        Assert.assertTrue(checkErrorFor(UNABLE_TO_LOAD_PM_ERROR));
    }
}
