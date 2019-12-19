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
    public JSONObject jsonConsents = new JSONObject();

    public UserConsent(JSONArray rejectedVendors, JSONArray rejectedCategories) throws JSONException {
        this.status = ConsentStatus.rejectedSome;
        this.rejectedVendors = json2StrArr(rejectedVendors);
        this.rejectedCategories = json2StrArr(rejectedCategories);
        if(this.rejectedVendors.isEmpty() && this.rejectedCategories.isEmpty())
            this.status = ConsentStatus.rejectedNone;
        setJsonConsents();

    }

    public UserConsent(JSONObject jConsent) throws JSONException, ConsentLibException {
        status = statusFromStr(jConsent.getString("status"));
        this.rejectedVendors = json2StrArr(jConsent.getJSONArray("rejectedVendors"));
        this.rejectedCategories = json2StrArr(jConsent.getJSONArray("rejectedCategories"));
        setJsonConsents();
    }

    public UserConsent(ConsentStatus status) throws JSONException {
        this.status = status;
        setJsonConsents();
    }

    private ArrayList<String> json2StrArr(JSONArray jArray) throws JSONException {
        ArrayList<String> listData = new ArrayList();
        if (jArray != null) {
            for (int i=0;i<jArray.length();i++){
                listData.add(jArray.getString(i));
            }
        }
        return listData;
    }

    private void setJsonConsents() throws JSONException {
        jsonConsents.put("status", status.name());
        jsonConsents.put("rejectedVendors", new JSONArray(rejectedVendors));
        jsonConsents.put("rejectedCategories", new JSONArray(rejectedCategories));
    }

    private ConsentStatus statusFromStr(String statusName) throws ConsentLibException {
        if(statusName.equals(ConsentStatus.rejectedAll.name())) return ConsentStatus.rejectedAll;
        if(statusName.equals(ConsentStatus.rejectedNone.name())) return ConsentStatus.rejectedNone;
        if(statusName.equals(ConsentStatus.rejectedSome.name())) return ConsentStatus.rejectedSome;
        throw new ConsentLibException("ConsentStatus string not valid.");
    }
}
