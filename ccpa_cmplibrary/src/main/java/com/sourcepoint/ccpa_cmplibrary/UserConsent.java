package com.sourcepoint.ccpa_cmplibrary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserConsent {

    public enum ConsentStatus {
        rejectedAll,
        rejectedSome,
        rejectedNone,
        consentedAll,
    }

    public ConsentStatus status;
    public ArrayList<String> rejectedVendors ;
    public ArrayList<String> rejectedCategories;
    public JSONObject jsonConsents = new JSONObject();

    public UserConsent() throws JSONException{
        this.rejectedVendors = new ArrayList();
        this.rejectedCategories = new ArrayList();
        this.status = ConsentStatus.rejectedNone;
        jsonConsents.put("rejectedVendors", new JSONArray(this.rejectedVendors));
        jsonConsents.put("rejectedCategories", new JSONArray(this.rejectedCategories));
        jsonConsents.put("status", this.status.name());
    }

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
        jsonConsents = jConsent;
    }

    public UserConsent(ConsentStatus status) throws JSONException {
        this.status = status;
        setJsonConsents();
    }

    public JSONObject getJsonConsents(){
        return jsonConsents;
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
        if(statusName.equals(ConsentStatus.consentedAll.name())) return ConsentStatus.consentedAll;
        throw new ConsentLibException("ConsentStatus string not valid.");
    }
}
