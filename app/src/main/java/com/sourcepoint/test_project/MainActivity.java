package com.sourcepoint.test_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sourcepoint.cmplibrary.ConsentLib;
import com.sourcepoint.cmplibrary.ConsentLibException;
import com.sourcepoint.cmplibrary.CustomVendorConsent;

import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ConsentLib consentLib;

    private ConsentLib buildAndRunConsentLib(Boolean showPM) throws ConsentLibException {
        return ConsentLib.newBuilder(22, "mobile.demo", 6099,"5df9105bcf42027ce707bb43",this)
                .setStage(true)
                .setViewGroup(findViewById(android.R.id.content))
                .setShowPM(showPM)
                .setOnMessageReady(consentLib -> Log.i(TAG, "onMessageReady"))
//                .setOnConsentReady(consentLib -> consentLib.getCustomVendorConsents(results -> {
//                    HashSet<CustomVendorConsent> consents = (HashSet) results;
//                    for(CustomVendorConsent consent : consents)
//                        Log.i(TAG, "Consented to: "+consent);
//                }))
                .setOnErrorOccurred(c -> Log.i(TAG, "Something went wrong: ", c.error))
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            consentLib = buildAndRunConsentLib(false);
            consentLib.run();
        } catch (ConsentLibException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.review_consents).setOnClickListener(_v -> {
            try {
                consentLib = buildAndRunConsentLib(true);
                consentLib.run();
            } catch (ConsentLibException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(consentLib != null ) { consentLib.destroy(); }
    }
}