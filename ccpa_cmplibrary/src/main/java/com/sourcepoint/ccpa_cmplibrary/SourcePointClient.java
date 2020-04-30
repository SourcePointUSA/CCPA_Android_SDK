package com.sourcepoint.ccpa_cmplibrary;

import android.text.TextUtils;
import android.util.Log;

import com.google.common.annotations.VisibleForTesting;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

class SourcePointClient {
    private static final String LOG_TAG = "SOURCE_POINT_CLIENT";

    private static AsyncHttpClient http = new AsyncHttpClient();

    private static final String baseMsgUrl = "https://wrapper-api.sp-prod.net/ccpa/message-url";

    private static final String baseSendConsentUrl = "https://wrapper-api.sp-prod.net/ccpa/consent";
    private String targetingParams;

    private int accountId;
    private String property;
    private int propertyId;
    private Boolean isStagingCampaign;
    private String requestUUID = "";
    private String authId;

    private String getRequestUUID(){
        if(!requestUUID.isEmpty()) return requestUUID;
        requestUUID =  UUID.randomUUID().toString();
        return requestUUID;
    }



    class ResponseHandler extends JsonHttpResponseHandler {
        //TODO: decouple from consentLib -> interface OnloadComplete should be in a separate file out of consentLib class
        CCPAConsentLib.OnLoadComplete onLoadComplete;
        String url;

        ResponseHandler(String url, CCPAConsentLib.OnLoadComplete onLoadComplete) {
            this.onLoadComplete = onLoadComplete;
            this.url = url;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + statusCode + ": " + errorResponse);
            onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + statusCode + ": " + responseString);
            onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));
        }
    }

    SourcePointClient(
            int accountID,
            String property,
            int propertyId,
            boolean isStagingCampaign,
            String targetingParams,
            String authID
    ) {
        this.isStagingCampaign = isStagingCampaign;
        this.accountId = accountID;
        this.propertyId = propertyId;
        this.property = property;
        this.targetingParams = targetingParams;
        this.authId = authID;
    }

    //TODO: extract url from user params
    private String messageUrl(String consentUUID, String meta) {
        HashSet<String> params = new HashSet<>();
        params.add("propertyId=" + propertyId);
        params.add("accountId=" + accountId);
        params.add("propertyHref=http://" + property);
        params.add("requestUUID=" + requestUUID);
        params.add("alwaysDisplayDNS=" + "false");
        params.add("targetingParams=" + targetingParams);
        params.add("campaignEnv=" + (isStagingCampaign ? "stage" : "prod"));
        if(authId != null) params.add("authId=" + authId);
        if(consentUUID != null) {
            params.add("uuid=" + consentUUID);
            // cannot send meta without uuid for some mysterious reason
            if(meta != null) params.add("meta=" + meta);
        }
        return baseMsgUrl + "?" + TextUtils.join("&", params);
    }

    private String consentUrl(int actionType){
        return baseSendConsentUrl + "/" + actionType;
    }

    @VisibleForTesting
    void setHttpDummy(AsyncHttpClient httpClient) {
        http = httpClient;
    }


    void getMessage(String consentUUID, String meta, CCPAConsentLib.OnLoadComplete onLoadComplete) {
        String url = messageUrl(consentUUID, meta);
        Log.i(LOG_TAG, "sending get-msgUrl request to: " + url);
        http.get(url, new ResponseHandler(url, onLoadComplete) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(LOG_TAG, response.toString());
                onLoadComplete.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + statusCode + ": " + responseString);
                onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + statusCode + ": " + errorResponse);
                onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));
            }
        });
    }

    void sendConsent(int  actionType, JSONObject params, CCPAConsentLib.OnLoadComplete onLoadComplete) throws UnsupportedEncodingException, JSONException {
        String url = consentUrl(actionType);
        Log.i(LOG_TAG, "sending consent to: " + url);
        params.put("requestUUID", getRequestUUID());
        Log.i(LOG_TAG, params.toString());
        StringEntity entity = new StringEntity(params.toString());
        http.post(null, url, entity, "application/json",  new ResponseHandler(url, onLoadComplete) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(LOG_TAG, response.toString());
                onLoadComplete.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + statusCode + ": " + responseString);
                onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + statusCode + ": " + errorResponse);
                onLoadComplete.onFailure(new ConsentLibException(throwable.getMessage()));
            }
        });
    }

}
