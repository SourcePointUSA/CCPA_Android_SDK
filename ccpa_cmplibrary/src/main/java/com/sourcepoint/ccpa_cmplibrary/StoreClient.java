package com.sourcepoint.ccpa_cmplibrary;

import android.content.SharedPreferences;

import org.json.JSONObject;

public class StoreClient {

    /**
     * They key used to store the IAB Consent string for the user in the shared preferences
     */
    public static final String CONSENT_UUID_KEY = "sp.ccpa.consentUUID";

    public static final String META_DATA_KEY = "sp.ccpa.metaData";

    public static final String AUTH_ID_KEY = "sp.ccpa.authId";

    public static final String USER_CONSENT_KEY = "sp.ccpa.userConsent";

    private SharedPreferences.Editor editor;

    private SharedPreferences pref;

    public static final String DEFAULT_EMPTY_CONSENT_STRING = "";

    public static final String DEFAULT_META_DATA = "{}";

    public static final String DEFAULT_AUTH_ID = null;

    StoreClient(SharedPreferences pref){
        this.editor = pref.edit();
        this.pref = pref;
    }

    public void setConsentUuid(String consentUuid){
        editor.putString(CONSENT_UUID_KEY, consentUuid);
        editor.commit();
    }

    public void setMetaData(String  metaData){
        editor.putString(META_DATA_KEY, metaData);
        editor.commit();
    }

    public void setAuthId(String authId){
        editor.putString(AUTH_ID_KEY, authId);
        editor.commit();
    }

    public void setUserConsents(UserConsent userConsent) {
        editor.putString(USER_CONSENT_KEY, userConsent.getJsonConsents().toString());
        editor.commit();
    }

    public String getMetaData() {
        return pref.getString(META_DATA_KEY, DEFAULT_META_DATA);
    }

    public String getConsentUUID() {
        return pref.getString(CONSENT_UUID_KEY, "");
    }

    public String getAuthId() {
        return pref.getString(AUTH_ID_KEY, DEFAULT_AUTH_ID);
    }

    public UserConsent getUserConsent() throws ConsentLibException {
        try {
            String uStr = pref.getString(USER_CONSENT_KEY, null);
            return uStr != null ? new UserConsent(new JSONObject(uStr)) : new UserConsent();
        } catch (Exception e) {
            throw new ConsentLibException(e, "Error trying to recover UserConsents for sharedPrefs");
        }
    }

    public void clearAllData(){
        clearInternalData();
    }

    public void clearInternalData(){
        editor.remove(CONSENT_UUID_KEY);
        editor.remove(META_DATA_KEY);
        editor.remove(AUTH_ID_KEY);
        editor.commit();
    }
}
