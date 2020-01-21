package com.sourcepoint.cmplibrary;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

@SuppressWarnings("unused")
public class ConsentLibBuilder {
    private final JSONObject targetingParams = new JSONObject();

    Activity activity;
    int accountId, propertyId ;
    String property;
    String mmsDomain, cmpDomain, msgDomain;
    String page = "";
    ViewGroup viewGroup = null;
    CCPAConsentLib.Callback onAction, onConsentReady, onError, onMessageReady;
    boolean staging, stagingCampaign, newPM , isShowPM, shouldCleanConsentOnError;

    String targetingParamsString = null;
    String authId = null;
    String pmId = "";
    CCPAConsentLib.DebugLevel debugLevel = CCPAConsentLib.DebugLevel.OFF;
    long defaultMessageTimeOut = 10000;

    ConsentLibBuilder(Integer accountId, String property, Integer propertyId , String pmId , Activity activity) {
        this.accountId = accountId;
        this.propertyId =propertyId;
        this.property = property;
        this.pmId = pmId;
        this.activity = activity;
        mmsDomain = cmpDomain = msgDomain = null;
        staging = stagingCampaign = newPM = isShowPM = false;
        shouldCleanConsentOnError = true;
        CCPAConsentLib.Callback noOpCallback = new CCPAConsentLib.Callback() {
            @Override
            public void run(CCPAConsentLib c) {
            }
        };
        onAction = onConsentReady = onError = onMessageReady = noOpCallback;
    }

    /**
     *  <b>Optional</b> Sets the page name in which the WebView was shown. Used for logging only.
     * @param p - a string representing page, e.g "/home"
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setPage(String p) {
        page = p;
        return this;
    }

    /**
     *  <b>Optional</b> Sets the view group in which WebView will will be rendered into.
     *  If it's not called or called with null, the MainView will be used instead.
     *  In case the main view is not a ViewGroup, a BuildException will be thrown during
     *  when build() is called.
     * @param v - the view group
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setViewGroup(ViewGroup v) {
        viewGroup = v;
        return this;
    }

    // TODO: add what are the possible choices returned to the Callback
    /**
     *  <b>Optional</b> Sets the Callback to be called when the user selects an option on the WebView.
     *  The selected choice will be available in the instance variable CCPAConsentLib.choiceType
     * @param c - a callback that will be called when the user selects an option on the WebView
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setOnMessageChoiceSelect(CCPAConsentLib.Callback c) {
        onAction = c;
        return this;
    }

    /**
     *  <b>Optional</b> Sets the Callback to be called when the user finishes interacting with the WebView
     *  either by closing it, canceling or accepting the terms.
     *  At this point, the following keys will available populated in the sharedStorage:
     *  <ul>
     *      <li>{@link CCPAConsentLib#CONSENT_UUID_KEY}</li>
     *  </ul>
     * @param c - Callback to be called when the user finishes interacting with the WebView
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setOnConsentReady(CCPAConsentLib.Callback c) {
        onConsentReady = c;
        return this;
    }

    /**
     * Called when the Dialog message is about to be shown
     * @param callback to be called when the message is ready to be displayed
     * @return ConsentLibBuilder
     */
    public ConsentLibBuilder setOnMessageReady(CCPAConsentLib.Callback callback) {
        onMessageReady = callback;
        return this;
    }

    /**
     *  <b>Optional</b> Sets a Callback to be called when something goes wrong in the WebView
     * @param callback called when something wrong happens in the webview
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setOnErrorOccurred(CCPAConsentLib.Callback callback) {
        onError = callback;
        return this;
    }

    /**
     * <b>Optional</b> True for <i>staging</i> campaigns or False for <i>production</i>
     * campaigns. <b>Default:</b> false
     * @param st - True for <i>staging</i> campaigns or False for <i>production</i>
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setStage(boolean st) {
        stagingCampaign = st;
        return this;
    }

    public ConsentLibBuilder setShouldCleanConsentOnError(Boolean shouldCleanConsentOnError) {
        this.shouldCleanConsentOnError = shouldCleanConsentOnError;
        return this;
    }

    /**
     * <b>Optional</b> This parameter refers to SourcePoint's environment itself. True for staging
     * or false for production. <b>Default:</b> false
     * @param st - True for staging or false for production
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setInternalStage(boolean st) {
        staging = st;
        return this;
    }

    public ConsentLibBuilder enableNewPM(boolean newPM) {
        this.newPM = newPM;
        return this;
    }

    public ConsentLibBuilder setInAppMessagePageUrl(String inAppMessageUrl) {
        msgDomain = inAppMessageUrl;
        return this;
    }

    public ConsentLibBuilder setMmsDomain(String mmsDomain) {
        this.mmsDomain = mmsDomain;
        return this;
    }

    public ConsentLibBuilder setCmpDomain(String cmpDomain) {
        this.cmpDomain = cmpDomain;
        return this;
    }

    public ConsentLibBuilder setAuthId(String authId) throws ConsentLibException.BuildException {
        this.authId = new EncodedParam("authId", authId);
        return this;
    }

    public ConsentLibBuilder setShowPM(boolean isUserTriggered){
        this.isShowPM = isUserTriggered;
        return this;
    }

    /**
     * <b>Optional</b> True for <i>staging</i> campaigns or False for <i>production</i>
     * campaigns. <b>Default:</b> false
     * @param st - True for <i>staging</i> campaigns or False for <i>production</i>
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setStagingCampaign(boolean st) {
        stagingCampaign = st;
        return this;
    }

    public ConsentLibBuilder setTargetingParam(String key, Integer val)  {
        return setTargetingParam(key, (Object) val);
    }

    public ConsentLibBuilder setTargetingParam(String key, String val) {
        return setTargetingParam(key, (Object) val);
    }

    private ConsentLibBuilder setTargetingParam(String key, Object val) {
        try {
            this.targetingParams.put(key, val);
        } catch (JSONException e) {
            Log.e(TAG, "Error trying to parse targetting param: [" + key + ", " + val + "]", e);
        }
        return this;
    }

    private void setTargetingParamsString() {
        targetingParamsString = targetingParams.toString();
    }


    /**
     * <b>Optional</b> Sets the DEBUG level.
     * <i>(Not implemented yet)</i>
     * <b>Default</b>{@link CCPAConsentLib.DebugLevel#DEBUG}
     * @param l - one of the values of {@link CCPAConsentLib.DebugLevel#DEBUG}
     * @return ConsentLibBuilder - the next build step
     * @see ConsentLibBuilder
     */
    public ConsentLibBuilder setDebugLevel(CCPAConsentLib.DebugLevel l) {
        debugLevel = l;
        return this;
    }

    private void setTargetingParamsString() throws ConsentLibException {
        targetingParamsString = new EncodedParam("targetingParams", targetingParams.toString());
    }

    /**
     * The Android 4.x Browser throws an exception when parsing SourcePoint's javascript.
     * @return true if the API level is not supported
     */
    @SuppressWarnings("WeakerAccess")
    public boolean sdkNotSupported() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }

    /**
     * Run internal tasks and build the CCPAConsentLib. This method will validate the
     * data coming from the previous Builders and throw {@link ConsentLibException.BuildException}
     * in case something goes wrong.
     * @return CCPAConsentLib | ConsentLibNoOp
     * @throws ConsentLibException.BuildException - if any of the required data is missing or invalid
     */
    public CCPAConsentLib build() {

        setTargetingParamsString();
        return new CCPAConsentLib(this);
    }

    public ConsentLibBuilder setMessageTimeOut(long milliSecond){
        this.defaultMessageTimeOut = milliSecond;
        return this;
    }
}