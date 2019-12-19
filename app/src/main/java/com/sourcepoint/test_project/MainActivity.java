package com.sourcepoint.test_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sourcepoint.cmplibrary.CCPAConsentLib;
import com.sourcepoint.cmplibrary.ConsentLibException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private CCPAConsentLib CCPAConsentLib;

    private CCPAConsentLib buildAndRunConsentLib(Boolean showPM) throws ConsentLibException {
        return CCPAConsentLib.newBuilder(22, "ccpa.mobile.demo", 6099,"5df9105bcf42027ce707bb43",this)
                .setStage(true)
                .setViewGroup(findViewById(android.R.id.content))
                .setShowPM(showPM)
                .setOnMessageReady(consentLib -> Log.i(TAG, "onMessageReady"))
//                .setOnConsentReady(CCPAConsentLib -> CCPAConsentLib.getCustomVendorConsents(results -> {
////                    HashSet<CustomVendorConsent> consents = (HashSet) results;
////                    for(CustomVendorConsent consent : consents)
////                        Log.i(TAG, "Consented to: "+consent);
////                }))
                .setOnErrorOccurred(c -> Log.i(TAG, "Something went wrong: ", c.error))
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            CCPAConsentLib = buildAndRunConsentLib(false);
            CCPAConsentLib.run();
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
                CCPAConsentLib = buildAndRunConsentLib(true);
                CCPAConsentLib.showPm();
            } catch (ConsentLibException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(CCPAConsentLib != null ) { CCPAConsentLib.destroy(); }
    }
}