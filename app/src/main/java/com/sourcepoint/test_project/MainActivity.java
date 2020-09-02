package com.sourcepoint.test_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.sourcepoint.ccpa_cmplibrary.CCPAConsentLib;
import com.sourcepoint.ccpa_cmplibrary.UserConsent;
import com.sourcepoint.gdpr_cmplibrary.GDPRConsentLib;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "**MainActivity";
    private static final String GDPRTag = "**GDPR";
    private static final String CCPATag = "**CCPA";

    private ViewGroup mainViewGroup;

    private void showView(View view) {
        if(view.getParent() == null){
            view.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            view.bringToFront();
            view.requestLayout();
            mainViewGroup.addView(view);
        }
    }
    private void removeView(View view) {
        if(view != null && view.getParent() != null)
            mainViewGroup.removeView(view);
    }

    private CCPAConsentLib buildCCPAConsentLib() {
        return CCPAConsentLib.newBuilder(22, "twosdks.demo", 7480, "5e6a7f997653402334162542",this)
                .setTargetingParam("SDK_TYPE", "CCPA")
                .setOnConsentUIReady(consentLib -> {
                    showView(consentLib.webView);
                    Log.i(CCPATag, "onConsentUIReady");
                })
                .setOnAction(consentLib -> {
                    Log.d(CCPATag, "user took the action: "+consentLib.choiceType);
                })
                .setOnConsentUIFinished(consentLib -> {
                    removeView(consentLib.webView);
                    Log.i(CCPATag, "onConsentUIFinished");
                })
                .setOnConsentReady(consentLib -> {
                    Log.i(CCPATag, "onConsentReady");
                    UserConsent consent = consentLib.userConsent;
                    Log.i(CCPATag, consent.consentString);
                    if(consent.status == UserConsent.ConsentStatus.rejectedNone || consent.status == UserConsent.ConsentStatus.consentedAll){
                        Log.i(CCPATag, "There are no rejected vendors/purposes.");
                    } else if(consent.status == UserConsent.ConsentStatus.rejectedAll){
                        Log.i(CCPATag, "All vendors/purposes were rejected.");
                    } else {
                        for (String vendorId : consent.rejectedVendors) {
                            Log.i(CCPATag, "The vendor " + vendorId + " was rejected.");
                        }
                        for (String purposeId : consent.rejectedCategories) {
                            Log.i(CCPATag, "The category " + purposeId + " was rejected.");
                        }
                    }
                })
                .setOnError(consentLib -> {
                    Log.e(CCPATag, "Something went wrong: ", consentLib.error);
                })
                .build();
    }

    private GDPRConsentLib buildGDPRConsentLib() {
        return GDPRConsentLib.newBuilder(22, "twosdks.demo", 7480, "227349",this)
                .setTargetingParam("SDK_TYPE", "GDPR")
                .setOnConsentUIReady(view -> {
                    showView(view);
                    Log.i(GDPRTag, "onConsentUIReady");
                })
                .setOnConsentUIFinished(view -> {
                    removeView(view);
                    Log.i(GDPRTag, "onConsentUIFinished");
                })
                .setOnConsentReady(consent -> {
                    Log.i(GDPRTag, "onConsentReady");
                    Log.i(GDPRTag, "uuid: " + consent.uuid );
                    Log.i(GDPRTag, "consentString: " + consent.consentString);
                    Log.i(GDPRTag, "TCData: " + consent.TCData);
                    Log.i(GDPRTag, "vendorGrants: " + consent.vendorGrants);
                    for (String vendorId : consent.acceptedVendors) {
                        Log.i(GDPRTag, "The vendor " + vendorId + " was accepted.");
                    }
                    for (String purposeId : consent.acceptedCategories) {
                        Log.i(GDPRTag, "The category " + purposeId + " was accepted.");
                    }
                    for (String purposeId : consent.legIntCategories) {
                        Log.i(GDPRTag, "The legIntCategory " + purposeId + " was accepted.");
                    }
                    for (String specialFeatureId : consent.specialFeatures) {
                        Log.i(GDPRTag, "The specialFeature " + specialFeatureId + " was accepted.");
                    }
                })
                .setOnError(error -> {
                    Log.e(GDPRTag, "Something went wrong: ", error);
                })
                .setOnAction(actionType  -> Log.i(GDPRTag , "ActionType: "+actionType.toString()))
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // build and run both SDKs
        buildGDPRConsentLib().run();
        buildCCPAConsentLib().run();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViewGroup = findViewById(android.R.id.content);
        findViewById(R.id.review_consents).setOnClickListener(_v -> {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            // check if either of the legislations applies and show the appropriate PM
            if(prefs.getBoolean("ccpaApplies", false)) {
                buildCCPAConsentLib().showPm();
            } else if(prefs.getBoolean("IABTCF_gdprApplies", false)) {
                buildGDPRConsentLib().showPm();
            } else {
                Log.d(TAG, "No legislation applies.");
            }
        });
    }
}