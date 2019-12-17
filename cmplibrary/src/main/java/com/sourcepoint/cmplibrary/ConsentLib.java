package com.sourcepoint.cmplibrary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ViewGroup;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Entry point class encapsulating the Consents a giving user has given to one or several vendors.
 * It offers methods to get custom vendors consents.
 * <pre>{@code
 *
 * }
 * </pre>
 */
public class ConsentLib {
    /**
     * If the user has consent data stored, reading for this key in the shared preferences will return true
     */
    @SuppressWarnings("WeakerAccess")
    public static final String CONSENT_CCPA_PRESENT = "Consent_CCPAPresent";

    /**
     * If the user is subject to CCPA, reading for this key in the shared preferences will return "1" otherwise "0"
     */
    @SuppressWarnings("WeakerAccess")
    public static final String CONSENT_SUBJECT_TO_CCPA = "IABConsent_SubjectToGDPR";

    @SuppressWarnings("WeakerAccess")
    public static final String CONSENT_KEY = "consent";

    @SuppressWarnings("WeakerAccess")
    public static final String CONSENT_UUID_KEY = "consentUUID";

    public enum DebugLevel {DEBUG, OFF}

    public enum MESSAGE_OPTIONS {
        SHOW_PRIVACY_MANAGER,
        UNKNOWN
    }

    public String euconsent, consentUUID;

    private static final int MAX_PURPOSE_ID = 24;

    private Boolean shouldCleanConsentOnError;

    /**
     * After the user has chosen an option in the WebView, this attribute will contain an integer
     * indicating what was that choice.
     */
    @SuppressWarnings("WeakerAccess")
    public MESSAGE_OPTIONS choiceType = null;

    public ConsentLibException error = null;

    private static final String TAG = "ConsentLib";
    private static final String SP_PREFIX = "_sp_";
    private static final String SP_PROPERTY_ID = SP_PREFIX + "property_id";
    private final static String CUSTOM_CONSENTS_KEY = SP_PREFIX + "_custom_consents";

    private Activity activity;
    private final String property;
    private final int accountId, propertyId;
    private final ViewGroup viewGroup;
    private final Callback onAction, onConsentReady, onError;
    private Callback onMessageReady;
    private final EncodedParam encodedTargetingParams, encodedAuthId, encodedPMId;
    private final boolean weOwnTheView, isShowPM;

    //default time out changes
    private boolean onMessageReadyCalled = false;
    private long defaultMessageTimeOut;

    private CountDownTimer mCountDownTimer = null;

    private final SourcePointClient sourcePoint;

    private final SharedPreferences sharedPref;

    @SuppressWarnings("WeakerAccess")
    public ConsentWebView webView;

    public interface Callback {
        void run(ConsentLib c);
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
        public static final int MSG_DISMISS = 15;
        public static final int PM_COMPLETE = 1;
        public static final int PM_CANCEL = 2;
    }

    /**
     * @return a new instance of ConsentLib.Builder
     */
    public static ConsentLibBuilder newBuilder(Integer accountId, String property, Integer propertyId, String pmId , Activity activity) {
        return new ConsentLibBuilder(accountId, property, propertyId, pmId, activity);
    }

    ConsentLib(ConsentLibBuilder b) throws ConsentLibException.BuildException {
        activity = b.activity;
        property = b.property;
        accountId = b.accountId;
        propertyId = b.propertyId;
        encodedPMId = new EncodedParam("_sp_PMId",b.pmId);
        isShowPM = b.isShowPM;
        encodedAuthId = b.authId;
        onAction = b.onAction;
        onConsentReady = b.onConsentReady;
        onError = b.onError;
        onMessageReady = b.onMessageReady;
        encodedTargetingParams = b.targetingParamsString;
        viewGroup = b.viewGroup;
        shouldCleanConsentOnError = b.shouldCleanConsentOnError;

        weOwnTheView = viewGroup != null;
        // configurable time out
        defaultMessageTimeOut = b.defaultMessageTimeOut;

        sourcePoint = new SourcePointClientBuilder(b.accountId, b.property + "/" + b.page, propertyId, b.staging)
                .setStagingCampaign(b.stagingCampaign)
                .setShowPM(b.isShowPM)
                .setCmpDomain(b.cmpDomain)
                .setMessageDomain(b.msgDomain)
                .setMmsDomain(b.mmsDomain)
                .build();

        // read consent from/store consent to default shared preferences
        // per gdpr framework: https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/852cf086fdac6d89097fdec7c948e14a2121ca0e/In-App%20Reference/Android/app/src/main/java/com/smaato/soma/cmpconsenttooldemoapp/cmpconsenttool/storage/CMPStorage.java
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        euconsent = sharedPref.getString(CONSENT_KEY, null);
        consentUUID = sharedPref.getString(CONSENT_UUID_KEY, null);

        webView = buildWebView();
    }

    private ConsentWebView buildWebView() {
        return new ConsentWebView(activity, defaultMessageTimeOut, isShowPM) {
            private boolean isDefined(String s) {
                return s != null && !s.equals("undefined") && !s.isEmpty();
            }

            @Override
            public void onMessageReady() {
                onMessageReadyCalled = true;
                Log.d("msgReady", "called");
                if (mCountDownTimer != null) mCountDownTimer.cancel();
                runOnLiveActivityUIThread(() -> ConsentLib.this.onMessageReady.run(ConsentLib.this));
                displayWebViewIfNeeded();
            }

            @Override
            public void onError(ConsentLibException error) {
                ConsentLib.this.error = error;
                if(shouldCleanConsentOnError) {
                    clearAllConsentData();
                }
                runOnLiveActivityUIThread(() -> ConsentLib.this.onError.run(ConsentLib.this));
                ConsentLib.this.finish();
            }

            @Override
            public void onAction(int choiceId, int choiceType) {
                Log.d(TAG, "onAction: choiceId:" + choiceId + "choiceType: " + choiceType);
                //noinspection SwitchStatementWithTooFewBranches
                switch (choiceType) {
                    case ActionTypes.SHOW_PM:
                        ConsentLib.this.choiceType = MESSAGE_OPTIONS.SHOW_PRIVACY_MANAGER;
                        onShowPm();
                        break;
                    case ActionTypes.MSG_ACCEPT:
                        onMsgAccepted();
                        break;
                    case ActionTypes.MSG_DISMISS:
                        onMsgClosed();
                        break;
                    case ActionTypes.MSG_REJECT:
                        onMsgRejected();
                        break;
                    case ActionTypes.PM_CANCEL:
                        onPmCanceled();
                        break;
                    case ActionTypes.PM_COMPLETE:
                        onPmCompleted();
                        break;
                    default:
                        ConsentLib.this.choiceType = MESSAGE_OPTIONS.UNKNOWN;
                        break;
                }
//                runOnLiveActivityUIThread(() -> ConsentLib.this.onAction.run(ConsentLib.this));
//                onConsentReady("", "");
            }
        };
    }

    private void onMsgAccepted(){
        //TODO acceptAll()
        sendConsent();
        ConsentLib.this.finish();
    }

    private void onMsgClosed(){
        ConsentLib.this.finish();
    }

    private void onMsgRejected(){
        //TODO rejectAll()
        sendConsent();
        ConsentLib.this.finish();
    }

    private void onPmCanceled(){
        ConsentLib.this.finish();
    }

    private void onPmCompleted(){
        //saveConsent(sourcePoint);
        sendConsent();
        ConsentLib.this.finish();
    }

    private void onShowPm(){
        webView.loadPM();
    }

    private boolean isDefined(String s) {
        return s != null && !s.equals("undefined") && !s.isEmpty();
    }

    private void saveConsent(String consent, String consentUUID){
        SharedPreferences.Editor editor = sharedPref.edit();
        if (isDefined(consent)) {
            ConsentLib.this.euconsent = consent;
            editor.putString(CONSENT_KEY, consent);
        }
        if (isDefined(consentUUID)) {
            ConsentLib.this.consentUUID = consentUUID;
            editor.putString(CONSENT_UUID_KEY, consentUUID);
            Log.d("Consnet UUID = ", consentUUID);
        }
        if (isDefined(consent) && isDefined(consentUUID)) {
            editor.apply();
        }
    }

    /**
     * Communicates with SourcePoint to load the message. It all happens in the background and the WebView
     * will only show after the message is ready to be displayed (received data from SourcePoint).
     * The Following keys should will be available in the shared preferences storage after this method
     * is called:
     * <ul>
     * <li>{@link ConsentLib#CONSENT_CCPA_PRESENT}</li>
     * <li>{@link ConsentLib#CONSENT_SUBJECT_TO_CCPA}</li>
     * </ul>
     *
     * @throws ConsentLibException.NoInternetConnectionException - thrown if the device has lost connection either prior or while interacting with ConsentLib
     */
    public void run() throws ConsentLibException.NoInternetConnectionException {
        onMessageReadyCalled = false;
        mCountDownTimer = getTimer(defaultMessageTimeOut);
        mCountDownTimer.start();
        renderMessage();
        setSharedPreference(CONSENT_CCPA_PRESENT, true);
    }

    private void renderMessage() throws ConsentLibException.NoInternetConnectionException {
        if(webView == null) { webView = buildWebView(); }
        sourcePoint.getMessage(new OnLoadComplete() {
            @Override
            public void onSuccess(Object result) {
                try{
                    JSONObject jsonResult = (JSONObject) result;
                    String  msgUrl = jsonResult.getString("url");
                    consentUUID = jsonResult.getString("uuid");
                    webView.loadConsentMsgFromUrl(msgUrl);
                }
                //TODO call onFailure callbacks / throw consentlibException
                catch(JSONException e){
                    Log.d(TAG, "Failed reading message response params.");
                }
                catch(ConsentLibException e){
                    Log.d(TAG, "Sorry, no internet connection");
                }
            }

            @Override
            public void onFailure(ConsentLibException exception) {
                Log.d(TAG, "Failed getting message response params.");
            }
        });
    }

    private void sendConsent() {
        sourcePoint.sendConsent(new OnLoadComplete() {
            @Override
            public void onSuccess(Object result) {
                try{
                    JSONObject jsonResult = (JSONObject) result;
                    euconsent = jsonResult.getString("euconsent");
                    consentUUID = jsonResult.getString("uuid");
                    saveConsent(euconsent, consentUUID);
                }
                //TODO call onFailure callbacks / throw consentlibException
                catch(JSONException e){
                    Log.d(TAG, "Failed reading message response params.");
                }
                catch(Exception e){
                    Log.d(TAG, "Sorry, something went wrong");
                }
            }

            @Override
            public void onFailure(ConsentLibException exception) {
                Log.d(TAG, "Failed getting message response params.");
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
                    onMessageReady = null;
                    webView.onError(new ConsentLibException("a timeout has occurred when loading the message"));
                }
            }
        };
    }

    private void setSharedPreference(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }


    @SuppressWarnings("SameParameterValue")
    private void setSharedPreference(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /*call this method to clear sharedPreferences from app onError*/
    public void clearAllConsentData(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(CONSENT_CCPA_PRESENT);
        editor.remove(CONSENT_SUBJECT_TO_CCPA);
        editor.remove(CONSENT_UUID_KEY);
        editor.remove(CONSENT_KEY);
        editor.remove(CUSTOM_CONSENTS_KEY);
        editor.commit();
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
                        ((ViewGroup) webView.getParent()).removeView(webView); // <- fix
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

    private void finish() {
        runOnLiveActivityUIThread(() -> {
            removeWebViewIfNeeded();
            onConsentReady.run(ConsentLib.this);
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
