package com.sourcepointccpa.metaapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestData {
    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static String accountID = "808";
    public static String propertyID = "7050";
    public static String propertyName = "ccpa.automation.testing.com";
    public static String pmID = "5ed09c33a785184c33f91f64";
    public static String keyParam = "region";
    public static String valueParamRegion = "ca";
    public static String valueParamUSRegion = "us";
    public static String keyParamShowOnce = "displayMode";
    public static String valueParamShowOnce = "appLaunch";
    public static String authIdValue;

    public static String authID(){
        Date date = new Date();
        authIdValue = sdf.format(date);
        return authIdValue;
    }

    public static String[] CONSENT_LIST={"Category 1", "Category 2", "Category 3"};
    public static String [] PARTIAL_CONSENT_LIST={"Category 1", "Category 2"};

    public static String SHOW_MESSAGE_ALWAYS = "show message always";
    public static String SHOW_MESSAGE_ONCE = "show message once";
    public static String MESSAGE = "message";
    public static String PRIVACY_MANAGER = "privacy_manager";
    public static String NO_AUTHENTICATION = "no";
    public static String UNIQUE_AUTHENTICATION = "unique";
    public static String EXISTING_AUTHENTICATION = "same";
    public static String REJECT_ALL = "Reject All";
    public static String PM_REJECT_ALL = "Reject All";
    public static String PM_SAVE_AND_EXIT = "Save & Exit";
    public static String PM_CANCEL = "Cancel";
    public static String PRIVACY_SETTINGS = "Privacy Settings";
    public static String CONSENTS_ARE_DISPLAYED =  "consents are displayed";
    public static String CONSENTS_ARE_NOT_DISPLAYED =  "Vendor and Purpose consents are not available.";
    public static String ACTION_MESSAGE = "All vendors/purposes were rejected.";
    public static String RESET_ACTION = "reset";
    public static String EDIT_ACTION = "edit";
    public static String DELETE_ACTION = "delete";
    public static String YES = "YES";
    public static String NO = "NO";
    public static String PARAM_VALUE = "param value";
    public static String STAGING_CAMPAIGN = "staging";
    public static String WRONG_CAMPAIGN = "public";
    public static String ALL_FIELDS = "all fields";
    public static String ALL_FIELDS_BLANK = "all fields blank";
    public static String NO_ACCOUNT_ID = "account id blank";
    public static String NO_PROPERTY_ID = "property id blank";
    public static String NO_PROPERTY_NAME = "property name blank";
    public static String NO_PM_ID = "pm id blank";
    public static String NO_PARAMETER_KEY = "parameter key blank";
    public static String NO_PARAMETER_VALUE = "parameter value blank";
    public static String NO_PARAMETER_KEY_VALUE = "parameter key value blank";
    public static String WRONG_ACCOUNT_ID =  "wrong account id";
    public static String WRONG_PROPERTY_ID = "wrong property id";
    public static String WRONG_PROPERTY_NAME = "wrong property name";
    public static String WRONG_PRIVACY_MANAGER = "wrong privacy manager";
    public static String MANDATORY_FIELDS = "Please enter Account ID, Property ID, Property Name, Privacy Manager ID";
    public static String TARGETING_PARAMETER_FIELDS = "Please enter Account ID, Property ID, Property Name, Privacy Manager ID";
    public static String PROPERTY_EXITS_ERROR = "Property details already exists.";
    public static String UNABLE_TO_LOAD_PM_ERROR = "Unable to load PM, No response from SDK";
}
