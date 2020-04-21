package com.sourcepoint.test_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.sourcepoint.ccpa_cmplibrary.CCPAConsentLib;
import com.sourcepoint.ccpa_cmplibrary.UserConsent;
import com.sourcepoint.gdpr_cmplibrary.GDPRConsentLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "**MainActivity";

    private ViewGroup mainViewGroup;

    private PropertyConfig config;

    private void showMessageWebView(View webView) {
        webView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        webView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        webView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        webView.bringToFront();
        webView.requestLayout();
        mainViewGroup.addView(webView);
    }
    private void removeWebView(View view) {
        if(view != null && view.getParent() != null)
            mainViewGroup.removeView(view);
    }

    private CCPAConsentLib buildCCPAConsentLib() {
        return CCPAConsentLib.newBuilder(config.accountId, config.propertyName, config.propertyId, config.pmId,this)
                .setTargetingParam("SDK_TYPE","CCPA")
                .setOnConsentUIReady(consentLib -> {
                    showMessageWebView(consentLib.webView);
                    Log.i(TAG, "onConsentUIReady");
                })
                .setOnConsentUIFinished(consentLib -> {
                    removeWebView(consentLib.webView);
                    Log.i(TAG, "onConsentUIFinished");
                })
                .setOnConsentReady(consentLib -> {
                    Log.i(TAG, "onConsentReady");
                    UserConsent consent = consentLib.userConsent;
                    if(consent.status == UserConsent.ConsentStatus.rejectedNone || consent.status == UserConsent.ConsentStatus.consentedAll){
                        Log.i(TAG, "There are no rejected vendors/purposes.");
                    } else if(consent.status == UserConsent.ConsentStatus.rejectedAll){
                        Log.i(TAG, "All vendors/purposes were rejected.");
                    } else {
                        for (String vendorId : consent.rejectedVendors) {
                            Log.i(TAG, "The vendor " + vendorId + " was rejected.");
                        }
                        for (String purposeId : consent.rejectedCategories) {
                            Log.i(TAG, "The category " + purposeId + " was rejected.");
                        }
                    }
                })
                .setOnError(consentLib -> {
                    Log.e(TAG, "Something went wrong: ", consentLib.error);
                })
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildCCPAConsentLib().run();
        //buildGDPRConsentLib().run();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViewGroup = findViewById(android.R.id.content);
        config = getConfig(R.raw.ccpa_mobile_demo);
        findViewById(R.id.review_consents).setOnClickListener(_v -> {
            buildCCPAConsentLib().showPm();
            //buildGDPRConsentLib().showPm();
        });
    }

    private PropertyConfig getConfig(int configResource){
        PropertyConfig config = null;
        try {
            config = new PropertyConfig(new JSONObject(new Scanner(getResources().openRawResource(configResource)).useDelimiter("\\A").next()));
        } catch (JSONException e) {
            Log.e(TAG, "Unable to parse config file.", e);
        }
        return config;
    }
}