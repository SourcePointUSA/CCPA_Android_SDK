package com.example.authexample;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.sourcepoint.ccpa_cmplibrary.CCPAConsentLib;
import com.sourcepoint.ccpa_cmplibrary.ConsentLibBuilder;
import com.sourcepoint.ccpa_cmplibrary.UserConsent;

import java.util.ArrayList;

abstract class ConsentManager {
    private static final String TAG = "ConsentManager";

    private Activity activity;
    private ViewGroup mainViewGroup;

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

    abstract void onConsentsReady(ArrayList<String> consentData, String consentUUID);

    ConsentManager(Activity activity, ViewGroup viewGroup) {
        this.activity = activity;
        this.mainViewGroup = viewGroup;
    }

    private ConsentLibBuilder getConsentLib() {
        return CCPAConsentLib.newBuilder(22, "ccpa.mobile.demo", 6099,"5df9105bcf42027ce707bb43",activity)
            .setTargetingParam("SDK_TYPE","CCPA")
            .setMessageTimeOut(30000)
            .setOnConsentReady(consentLib ->  {
                Log.d(TAG, "OnConsentReady");
                onConsentsReady(addConsentDataToList(consentLib.userConsent) , consentLib.consentUUID);
            })
            .setOnConsentUIReady(c -> {
                Log.d(TAG, "OnConsentUIReady");
                showMessageWebView(c.webView);
            })
            .setOnConsentUIFinished(c -> {
                Log.d(TAG, "OnConsentUIFinished");
                    removeWebView(c.webView);
                })
            .setOnError(c -> Log.d(TAG, "Error Occurred: "+c.error));
    }

    void loadMessage() {
            getConsentLib().build().run();
    }

    void loadMessage(String authId) {
            getConsentLib().setAuthId(authId).build().showPm();
    }

    private ArrayList<String> addConsentDataToList(UserConsent userConsent) {
        ArrayList<String> consentListViewData = new ArrayList<>();
        consentListViewData.add("Consent status : "+userConsent.status);
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
        return consentListViewData;
    }
}

