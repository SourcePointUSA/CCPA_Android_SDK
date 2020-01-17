package com.sourcepoint.test_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sourcepoint.ccpalibrary.CCPAConsentLib;
import com.sourcepoint.ccpalibrary.UserConsent;
import com.sourcepoint.cmplibrary.ConsentLib;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private CCPAConsentLib ccpaConsentLib;
    private ConsentLib consentLib;

    private CCPAConsentLib buildAndRunConsentLib(Boolean showPM) throws com.sourcepoint.ccpalibrary.ConsentLibException {
        return CCPAConsentLib.newBuilder(22, "ccpa.mobile.demo", 6099,"5df9105bcf42027ce707bb43",this)
                .setViewGroup(findViewById(android.R.id.content))
                .setOnMessageReady(consentLib -> Log.i(TAG, "onMessageReady"))
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
                .setOnErrorOccurred(c -> Log.i(TAG, "Something went wrong: ", c.error))
                .build();
    }
    private ConsentLib buildAndRunConsentLibGDPR()  throws com.sourcepoint.cmplibrary.ConsentLibException {
        return ConsentLib.newBuilder(22,"mobile.demo",2372,"5df9105bcf42027ce707bb43",this)
                .setViewGroup(findViewById(android.R.id.content))
                .setStage(true)
                .setTargetingParam("MyPrivacyManager","false")
                .setOnMessageReady(consentLib -> Log.i(TAG, "onMessageReady"))
                .setOnConsentReady(consentLib -> {Log.i(TAG, "onConsentReady");})
                .setOnErrorOccurred(c -> Log.i(TAG, "Something went wrong: ", c.error)).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            ccpaConsentLib = buildAndRunConsentLib(false);
            ccpaConsentLib.run();
            consentLib = buildAndRunConsentLibGDPR();
            consentLib.run();
        } catch (com.sourcepoint.ccpalibrary.ConsentLibException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.review_consents).setOnClickListener(_v -> {
            try {
                ccpaConsentLib = buildAndRunConsentLib(true);
                ccpaConsentLib.showPm();
                consentLib = buildAndRunConsentLibGDPR();
                consentLib.run();
            } catch (com.sourcepoint.ccpalibrary.ConsentLibException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ccpaConsentLib != null ) { ccpaConsentLib.destroy(); }
    }
}