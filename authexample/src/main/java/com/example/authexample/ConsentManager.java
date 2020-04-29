package com.example.authexample;

import android.app.Activity;
import android.util.Log;

import com.sourcepoint.ccpa_cmplibrary.CCPAConsentLib;
import com.sourcepoint.ccpa_cmplibrary.ConsentLibBuilder;
import com.sourcepoint.ccpa_cmplibrary.UserConsent;

import java.util.ArrayList;

abstract class ConsentManager {
    private static final String TAG = "ConsentManager";

    private Activity activity;
    ArrayList<String> consentListViewData = new ArrayList<>();

    abstract void onConsentsReady(ArrayList<String> consentListViewData);

    ConsentManager(Activity activity) {
        this.activity = activity;
    }

    private ConsentLibBuilder getConsentLib(Boolean pm) {
        return CCPAConsentLib.newBuilder(22, "mobile.demo", 2372,"5c0e81b7d74b3c30c6852301",activity)
            .setStage(true)
            .setShowPM(pm)
            .setMessageTimeOut(30000)
            .setOnConsentReady(consentLib -> {
                Log.d(TAG, "Interaction complete");
                consentListViewData.add("consentUUID: "+consentLib.consentUUID);
                addConsentDataToList(consentLib.userConsent);
                onConsentsReady(consentListViewData);
            })
            .setOnConsentUIReady(_c -> Log.d(TAG, "Message Ready"))
            .setOnError(c -> Log.d(TAG, "Error Occurred: "+c.error));
    }

    void loadMessage(Boolean pm) {
        getConsentLib(pm).build().run();
    }

    void loadMessage(boolean pm, String authId) {
        getConsentLib(pm).setAuthId(authId).build().run();
    }

    private void addConsentDataToList(UserConsent userConsent) {
        if (userConsent.status == UserConsent.ConsentStatus.rejectedNone) {
            consentListViewData.add("There are no rejected vendors/purposes.");
        } else if (userConsent.status == UserConsent.ConsentStatus.rejectedAll) {
            consentListViewData.add("All vendors/purposes were rejected.");
        } else {
            if (userConsent.rejectedVendors.size() > 0) {
                consentListViewData.add("Rejected Vendor Ids");
                for (String vendorId : userConsent.rejectedVendors) {
                    consentListViewData.add("Vendor Id : " + vendorId);
                }
            }
            if (userConsent.rejectedCategories.size() > 0) {
                consentListViewData.add("Rejected Category Ids");
                for (String purposeId : userConsent.rejectedCategories) {
                    consentListViewData.add("Purpose Id : " + purposeId);
                }
            }
        }
    }
}

