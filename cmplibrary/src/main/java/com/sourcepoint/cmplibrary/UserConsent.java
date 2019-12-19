package com.sourcepoint.cmplibrary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserConsent {

    public enum ConsentStatus {
        rejectedAll,
        rejectedSome,
        rejectedNone
    }

    public ConsentStatus status;
    public ArrayList<String> rejectedVendors = new ArrayList();
    public ArrayList<String> rejectedCategories = new ArrayList();
    public JSONObject jsonConsents;

    public UserConsent(JSONArray rejectedVendors, JSONArray rejectedCategories) throws JSONException {
        this.status = ConsentStatus.rejectedSome;
        this.rejectedVendors = json2StrArr(rejectedVendors);
        this.rejectedCategories = json2StrArr(rejectedCategories);
        if(this.rejectedVendors.isEmpty() && this.rejectedCategories.isEmpty())
            this.status = ConsentStatus.rejectedNone;
    }

    public UserConsent(ConsentStatus status){
        this.status = status;
    }


    public UserConsent(JSONObject jsonConsents) throws JSONException {
        this.status = ConsentStatus.rejectedSome;
        this.rejectedVendors = json2StrArr(jsonConsents.getJSONArray("rejectedVendors"));
        this.rejectedCategories = json2StrArr(jsonConsents.getJSONArray("rejectedCategories"));
        if(this.rejectedVendors.isEmpty() && this.rejectedCategories.isEmpty())
            this.status = ConsentStatus.rejectedNone;
        jsonConsents.put("status", this.status.name());
        this.jsonConsents = jsonConsents;
    }

    private String[] arrayFromList(ArrayList<String> l){
        String[] s = new String[l.size()];
        for (int i=0;i<l.size();i++){
            s[i] = l.get(i);
        }
        return s;
    }

    private ArrayList<String> json2StrArr(JSONArray jArray) throws JSONException {
        ArrayList<String> listdata = new ArrayList();
        if (jArray != null) {
            for (int i=0;i<jArray.length();i++){
                listdata.add(jArray.getString(i));
            }
        }
        return listdata;
    }
}
