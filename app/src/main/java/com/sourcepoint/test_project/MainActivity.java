package com.sourcepoint.test_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.sourcepoint.ccpa_cmplibrary.CCPAConsentLib;
import com.sourcepoint.ccpa_cmplibrary.UserConsent;
import com.sourcepoint.gdpr_cmplibrary.GDPRConsentLib;
import com.sourcepoint.gdpr_cmplibrary.GDPRUserConsent;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private CCPAConsentLib ccpaConsentLib;
    private GDPRConsentLib gdprConsentLib;

    private ViewGroup mainViewGroup;

    private void showMessageWebView(WebView webView) {
        webView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        webView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        webView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        webView.bringToFront();
        webView.requestLayout();
        mainViewGroup.addView(webView);
    }
    private void removeWebView(WebView webView) {
        if(webView.getParent() != null)
            mainViewGroup.removeView(webView);
    }

    private GDPRConsentLib buildGDPRConsentLib() {
        return GDPRConsentLib.newBuilder(22, "mobile.demo", 2372,"5c0e81b7d74b3c30c6852301",this)
                .setStagingCampaign(true)
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
                    GDPRUserConsent consent = consentLib.userConsent;
                    for (String vendorId : consent.acceptedVendors) {
                        Log.i(TAG, "The vendor " + vendorId + " was accepted.");
                    }
                    for (String purposeId : consent.acceptedCategories) {
                        Log.i(TAG, "The category " + purposeId + " was accepted.");
                    }
                })
                .setOnError(consentLib -> {
                    Log.e(TAG, "Something went wrong: ", consentLib.error);
                    Log.i(TAG, "ConsentLibErrorMessage: " + consentLib.error.consentLibErrorMessage);
                    removeWebView(consentLib.webView);
                })
                .build();
    }

    private CCPAConsentLib buildCCPAConsentLib() {
        return CCPAConsentLib.newBuilder(22, "ccpa.mobile.demo", 6099,"5df9105bcf42027ce707bb43",this)
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
                    if(consent.status == UserConsent.ConsentStatus.rejectedNone){
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
        buildGDPRConsentLib().run();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViewGroup = findViewById(android.R.id.content);
        findViewById(R.id.review_consents).setOnClickListener(_v -> {
            buildCCPAConsentLib().showPm();
            buildGDPRConsentLib().showPm();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ccpaConsentLib != null ) { ccpaConsentLib.destroy(); }
    }
}