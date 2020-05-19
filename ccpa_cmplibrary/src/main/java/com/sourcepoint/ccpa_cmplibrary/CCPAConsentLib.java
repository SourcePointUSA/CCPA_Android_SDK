package com.sourcepoint.ccpa_cmplibrary;

import android.app.Activity;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;

/**
 * Entry point class encapsulating the Consents a giving user has given to one or several vendors.
 * It offers methods to get custom vendors consents.
 * <pre>{@code
 *
 * }
 * </pre>
 */
public class CCPAConsentLib {

    @SuppressWarnings("WeakerAccess")
    public static final String CONSENT_UUID_KEY = "sp.ccpa.consentUUID";

    private  final StoreClient storeClient;

    private final String pmId;

    private final String PM_BASE_URL = "https://ccpa-inapp-pm.sp-prod.net";

    private final String CCPA_ORIGIN = "https://ccpa-service.sp-prod.net";


    private String metaData;

    public enum DebugLevel {DEBUG, OFF}

    public enum MESSAGE_OPTIONS {
        SHOW_PRIVACY_MANAGER,
        UNKNOWN
    }

    public String consentUUID;

    /**
     * After the user has chosen an option in the WebView, this attribute will contain an integer
     * indicating what was that choice.
     */
    @SuppressWarnings("WeakerAccess")
    public MESSAGE_OPTIONS choiceType = null;

    public ConsentLibException error = null;

    public UserConsent userConsent;

    private static final String TAG = "CCPAConsentLib";

    private Activity activity;
    private final String property;
    private final int accountId, propertyId;
    private final ViewGroup viewGroup;
    private Callback onAction, onConsentReady, onError;
    private Callback onConsentUIReady, onConsentUIFinished;

    private final boolean weOwnTheView, isShowPM;

    //default time out changes
    private boolean onMessageReadyCalled = false;
    private long defaultMessageTimeOut;

    private CountDownTimer mCountDownTimer = null;

    private final SourcePointClient sourcePoint;

    @SuppressWarnings("WeakerAccess")
    public ConsentWebView webView;

    public interface Callback {
        void run(CCPAConsentLib c);
    }

    public interface OnLoadComplete {
        void onSuccess(Object result);

        default void onFailure(ConsentLibException exception) {
            Log.d(TAG, "default implementation of onFailure, did you forget to override onFailure ?");
            exception.printStackTrace();
        }
    }

    public class ActionTypes {
        public static final int SHOW_PM = 12;
        public static final int MSG_REJECT = 13;
        public static final int MSG_ACCEPT = 11;
        public static final int DISMISS = 15;
        public static final int PM_COMPLETE = 1;
    }

    /**
     * @return a new instance of CCPAConsentLib.Builder
     */
    public static ConsentLibBuilder newBuilder(Integer accountId, String property, Integer propertyId, String pmId , Activity activity) {
        return new ConsentLibBuilder(accountId, property, propertyId, pmId, activity);
    }

    CCPAConsentLib(ConsentLibBuilder b) {
        activity = b.activity;
        property = b.property;
        accountId = b.accountId;
        propertyId = b.propertyId;
        pmId = b.pmId;
        isShowPM = b.isShowPM;
        onAction = b.onAction;
        onConsentReady = b.onConsentReady;

        onError = b.onError;
        onConsentUIReady = b.onConsentUIReady;
        onConsentUIFinished = b.onConsentUIFinished;
        viewGroup = b.viewGroup;

        weOwnTheView = viewGroup != null;
        // configurable time out
        defaultMessageTimeOut = b.defaultMessageTimeOut;

        storeClient = new StoreClient(PreferenceManager.getDefaultSharedPreferences(activity));
        sourcePoint = new SourcePointClient(b.accountId, b.property + "/" + b.page, propertyId, b.stagingCampaign, b.targetingParamsString, b.authId);

        webView = buildWebView();

        setConsentData(b.authId);
    }

    void setConsentData(String newAuthId){

        if(didConsentUserChange(newAuthId, storeClient.getAuthId())) storeClient.clearAllData();

        metaData = storeClient.getMetaData();

        consentUUID = storeClient.getConsentUUID();

        storeClient.setAuthId(newAuthId);
    }

    private boolean didConsentUserChange(String newAuthId, String oldAuthId){
        return oldAuthId != null && newAuthId != null && !newAuthId.equals(oldAuthId);
    }

    private ConsentWebView buildWebView() {
        return new ConsentWebView(activity, defaultMessageTimeOut, isShowPM) {

            @Override
            public void onMessageReady() {
                Log.d("msgReady", "called");
                if (mCountDownTimer != null) mCountDownTimer.cancel();
                if(!onMessageReadyCalled) {
                    runOnLiveActivityUIThread(() -> CCPAConsentLib.this.onConsentUIReady.run(CCPAConsentLib.this));
                    onMessageReadyCalled = true;
                }
                displayWebViewIfNeeded();
            }

            @Override
            public void onError(ConsentLibException e) {
                onErrorTask(e);
            }

            @Override
            public void onSavePM(UserConsent u) {
                CCPAConsentLib.this.userConsent = u;
                try {
                    sendConsent(ActionTypes.PM_COMPLETE);
                } catch (Exception e) {
                    onErrorTask(e);
                }
            }

            @Override
            public void onAction(int choiceType) {
                try{
                    Log.d(TAG, "onAction:  " +  choiceType  + " + choiceType");
                    switch (choiceType) {
                        case ActionTypes.SHOW_PM:
                            CCPAConsentLib.this.choiceType = MESSAGE_OPTIONS.SHOW_PRIVACY_MANAGER;
                            onShowPm();
                            break;
                        case ActionTypes.MSG_ACCEPT:
                            onMsgAccepted();
                            break;
                        case ActionTypes.DISMISS:
                            onDismiss();
                            break;
                        case ActionTypes.MSG_REJECT:
                            onMsgRejected();
                            break;
                        default:
                            CCPAConsentLib.this.choiceType = MESSAGE_OPTIONS.UNKNOWN;
                            break;
                    }
                }catch (UnsupportedEncodingException e) {
                    onErrorTask(e);
                } catch (JSONException e) {
                    onErrorTask(e);
                }
            }
        };
    }

    private void onMsgAccepted() throws UnsupportedEncodingException, JSONException {
        userConsent = new UserConsent(UserConsent.ConsentStatus.consentedAll);
        sendConsent(ActionTypes.MSG_ACCEPT);
    }

    private void onDismiss(){
        webView.post(new Runnable() {
            @Override
            public void run() {
                if (webView.canGoBack()) webView.goBack();
                else finish();
            }
        });
    }

    private void onMsgRejected() throws UnsupportedEncodingException, JSONException {
        userConsent = new UserConsent(UserConsent.ConsentStatus.rejectedAll);
        sendConsent(ActionTypes.MSG_REJECT);
    }

    private void onShowPm(){
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(pmUrl());
            }
        });
    }

    /**
     * Communicates with SourcePoint to load the message. It all happens in the background and the WebView
     * will only show after the message is ready to be displayed (received data from SourcePoint).
     *
     * @throws ConsentLibException.NoInternetConnectionException - thrown if the device has lost connection either prior or while interacting with CCPAConsentLib
     */
    public void run() {
        try {
        onMessageReadyCalled = false;
        mCountDownTimer = getTimer(defaultMessageTimeOut);
        mCountDownTimer.start();
            renderMsgAndSaveConsent();
        } catch (Exception e) {
            onErrorTask(e);
        }
    }

    public void showPm() {
        webView.loadUrl(pmUrl());
    }

    private void loadConsentUI(String url){
        runOnLiveActivityUIThread(() -> {
            try {
                webView.loadConsentMsgFromUrl(url);
            } catch (ConsentLibException.NoInternetConnectionException e) {
                e.printStackTrace();
                onErrorTask(e);
            }
        });
    }

    private void renderMsgAndSaveConsent() {
        if(webView == null) { webView = buildWebView(); }
        sourcePoint.getMessage(consentUUID, metaData, new OnLoadComplete() {
            @Override
            public void onSuccess(Object result) {
                try{
                    JSONObject jsonResult = new JSONObject((String) result);
                    consentUUID = jsonResult.getString("uuid");
                    metaData = jsonResult.getString("meta");
                    userConsent = new  UserConsent(jsonResult.getJSONObject("userConsent"));
                    if(jsonResult.has("url")){
                        loadConsentUI(jsonResult.getString("url"));
                    }else{
                        finish();
                    }
                }
                //TODO call onFailure callbacks / throw consentlibException
                catch(JSONException e){
                    onErrorTask(e);
                }
                catch(ConsentLibException e){
                    onErrorTask(e);
                }
            }

            @Override
            public void onFailure(ConsentLibException e) {
                onErrorTask(e);
            }
        });
    }

    private boolean hasErrorOccurred(){
        return error != null;
    }

    private JSONObject paramsToSendConsent() throws JSONException {
        JSONObject params = new JSONObject();

        params.put("consents", userConsent.jsonConsents);
        params.put("accountId", accountId);
        params.put("propertyId", propertyId);
        params.put("privacyManagerId", pmId);
        params.put("uuid", consentUUID);
        params.put("meta", metaData);
        return params;
    }

    private void sendConsent(int actionType) throws JSONException, UnsupportedEncodingException {
        sourcePoint.sendConsent(actionType, paramsToSendConsent(), new OnLoadComplete() {
            @Override
            public void onSuccess(Object result) {
                try{
                    JSONObject jsonResult = new JSONObject((String) result);
                    userConsent = new  UserConsent(jsonResult.getJSONObject("userConsent"));
                    consentUUID = jsonResult.getString("uuid");
                    metaData = jsonResult.getString("meta");
                    finish();
                }
                catch(Exception e){
                    onErrorTask(e);
                }
            }

            @Override
            public void onFailure(ConsentLibException e) {
                onErrorTask(e);
            }
        });
    }

    private CountDownTimer getTimer(long defaultMessageTimeOut) {
        return new CountDownTimer(defaultMessageTimeOut, defaultMessageTimeOut) {
            @Override
            public void onTick(long millisUntilFinished) {     }
            @Override
            public void onFinish() {
                if (!onMessageReadyCalled) {
                    onErrorTask(new ConsentLibException("a timeout has occurred when loading the message"));
                }
            }
        };
    }

    private String pmUrl(){
        HashSet<String> params = new HashSet<>();
        params.add("privacy_manager_id=" + pmId);
        params.add("site_id=" + propertyId);
        params.add("ccpa_origin=" + CCPA_ORIGIN);
        if(consentUUID != null) params.add("ccpaUUID=" + consentUUID);

        return PM_BASE_URL + "?" + TextUtils.join("&", params);
    }

    private void runOnLiveActivityUIThread(Runnable uiRunnable) {
        if (activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(uiRunnable);
        }
    }

    private void displayWebViewIfNeeded() {
        if (weOwnTheView) {
            runOnLiveActivityUIThread(() -> {
                if (webView != null) {
                    if (webView.getParent() != null) {
                        ((ViewGroup) webView.getParent()).removeView(webView);
                    }
                    webView.display();
                    viewGroup.addView(webView);
                }
            });
        }
    }

    private void removeWebViewIfNeeded() {
        if (weOwnTheView && activity != null) destroy();
    }

    private void onErrorTask(Exception e){
        this.error = new ConsentLibException(e);
        cancelCounter();
        runOnLiveActivityUIThread(() -> CCPAConsentLib.this.onConsentUIFinished.run(CCPAConsentLib.this));
        runOnLiveActivityUIThread(() -> CCPAConsentLib.this.onError.run(CCPAConsentLib.this));
        resetCallbacks();
    }

    private void resetCallbacks(){
        onAction = onError = onConsentUIFinished = onConsentReady = onConsentUIReady = c -> {};
    }

    private void cancelCounter(){
        if (mCountDownTimer != null) mCountDownTimer.cancel();
    }


    void storeData(){
        storeClient.setConsentUuid(consentUUID);
        storeClient.setMetaData(metaData);
    }

    private boolean hasParent(View v) {
        return v != null && v.getParent() != null;
    }

    private void finish() {
        storeData();
        Log.i("uuid", consentUUID);
        if(hasParent(webView)) runOnLiveActivityUIThread(() -> CCPAConsentLib.this.onConsentUIFinished.run(CCPAConsentLib.this));
        runOnLiveActivityUIThread(() -> {
            removeWebViewIfNeeded();
            if(userConsent != null) onConsentReady.run(CCPAConsentLib.this);
            activity = null; // release reference to activity
        });
    }

    public void destroy() {
        if (mCountDownTimer != null) mCountDownTimer.cancel();
        if (webView != null) {
            if (viewGroup != null) {
                viewGroup.removeView(webView);
            }
            webView.destroy();
            webView = null;
        }
    }
}
