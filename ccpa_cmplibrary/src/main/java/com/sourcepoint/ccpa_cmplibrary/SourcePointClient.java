package com.sourcepoint.ccpa_cmplibrary;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class SourcePointClient {
    private static final String LOG_TAG = "SOURCE_POINT_CLIENT";

    private OkHttpClient httpClient = new OkHttpClient();

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

    void getMessage(String consentUUID, String meta, CCPAConsentLib.OnLoadComplete onLoadComplete) {
        String url = messageUrl(consentUUID, meta);
        Log.i(LOG_TAG, "sending get-msgUrl request to: " + url);

        Request request = new Request.Builder().url(url).build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(LOG_TAG, "Failed to load resource " + url + " due to " +   "url load failure :  " + e.getMessage());
                onLoadComplete.onFailure(new ConsentLibException(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String messageJson = response.body().string();
                    Log.i(LOG_TAG , messageJson);
                    onLoadComplete.onSuccess(messageJson);
                }else {
                    Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + response.code() + ": " + response.message());
                    onLoadComplete.onFailure(new ConsentLibException(response.message()));
                }
            }
        });
    }

    void sendConsent(int  actionType, JSONObject params, CCPAConsentLib.OnLoadComplete onLoadComplete) throws UnsupportedEncodingException, JSONException {
        String url = consentUrl(actionType);
        Log.i(LOG_TAG, "sending consent to: " + url);
        params.put("requestUUID", getRequestUUID());
        Log.i(LOG_TAG, params.toString());

        final MediaType mediaType= MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, params.toString());

        Request request = new Request.Builder().url(url).post(body)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(LOG_TAG, "Failed to load resource " + url + " due to " +   "url load failure :  " + e.getMessage());
                onLoadComplete.onFailure(new ConsentLibException(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String messageJson = response.body().string();
                    Log.i(LOG_TAG , messageJson);
                    onLoadComplete.onSuccess(messageJson);
                }else {
                    Log.d(LOG_TAG, "Failed to load resource " + url + " due to " + response.code() + ": " + response.message());
                    onLoadComplete.onFailure(new ConsentLibException(response.message()));
                }
            }
        });
    }

}
