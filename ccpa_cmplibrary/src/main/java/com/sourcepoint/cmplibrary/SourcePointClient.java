package com.sourcepoint.cmplibrary;

import android.text.TextUtils;
import android.util.Log;

import com.google.common.annotations.VisibleForTesting;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

class SourcePointClient {
    private static final String LOG_TAG = "SOURCE_POINT_CLIENT";

    private static AsyncHttpClient http = new AsyncHttpClient();

    private static final String baseMsgUrl = "https://wrapper-api.sp-prod.net/ccpa/message-url?accountId=22&requestUUID=test1&alwaysDisplayDNS=false&propertyId=6099&propertyHref=ccpa.mobile.demo&env=stage";

    private static final String baseSendConsentUrl = "https://wrapper-api.sp-prod.net/ccpa/consent/1?env=stage";

    private EncodedParam accountId, property, propertyId;
    private Boolean isStagingCampaign;
    private String requestUUID = "";

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
            EncodedParam accountID,
            EncodedParam property,
            EncodedParam propertyId,
            boolean isStagingCampaign
    ) {
        this.isStagingCampaign = isStagingCampaign;
        this.accountId = accountID;
        this.propertyId = propertyId;
        this.property = property;
    }

    //TODO: extract url from user params
    private String messageUrl(String consentUUID, String meta) {
        return baseMsgUrl;
    }

    private String consentUrl(){
        return baseSendConsentUrl;
    }

    @VisibleForTesting
    void setHttpDummy(AsyncHttpClient httpClient) {
        http = httpClient;
    }


    void getMessage(String consentUUID, String meta, CCPAConsentLib.OnLoadComplete onLoadComplete) {
        //TODO inject real params to messageUrl
        String url = messageUrl("", "");
        http.get(url, new ResponseHandler(url, onLoadComplete) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
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

    void sendConsent(JSONObject params, CCPAConsentLib.OnLoadComplete onLoadComplete) throws UnsupportedEncodingException, JSONException {
        String url = consentUrl();
        params.put("requestUUID", getRequestUUID());
        StringEntity entity = new StringEntity(params.toString());
        http.post(null, url, entity, "application/json",  new ResponseHandler(url, onLoadComplete) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
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
