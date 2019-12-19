package com.sourcepoint.test_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sourcepoint.cmplibrary.CCPAConsentLib;
import com.sourcepoint.cmplibrary.ConsentLibException;
import com.sourcepoint.cmplibrary.UserConsent;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private CCPAConsentLib CCPAConsentLib;

    private CCPAConsentLib buildAndRunConsentLib(Boolean showPM) throws ConsentLibException {
        return CCPAConsentLib.newBuilder(22, "ccpa.mobile.demo", 6099,"5df9105bcf42027ce707bb43",this)
                .setStage(true)
                .setViewGroup(findViewById(android.R.id.content))
                .setShowPM(showPM)
                .setOnMessageReady(consentLib -> Log.i(TAG, "onMessageReady"))
                .setOnConsentReady(consentLib -> {
                    Log.i(TAG, "onConsentReady");
                    UserConsent consent = consentLib.userConsent;
                    if(consent.status == UserConsent.ConsentStatus.rejectedNone){
                        Log.i(TAG, "There are no rejected consents.");
                    } else if(consent.status == UserConsent.ConsentStatus.rejectedAll){
                        Log.i(TAG, "All consents were rejected.");
                    } else {
                        Iterator v = consent.rejectedVendors.iterator();
                        while(v.hasNext()){
                            Log.i(TAG, "The vendor " + v.next() + " was rejected.");
                        }
                        Iterator c = consent.rejectedCategories.iterator();
                        while(c.hasNext()){
                            Log.i(TAG, "The category " + c.next() + " was rejected.");
                        }
                    }
                })
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