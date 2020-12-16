package com.sourcepoint.ccpa_cmplibrary;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConsentAction {

    public int actionType;
    private Map pubData = new HashMap();

    ConsentAction(int actionType){
        this.actionType = actionType;
    }

    public void setPubData(Map pubData){
        this.pubData = pubData;
    }

    public JSONObject getPubData(){
        return new JSONObject(pubData);
    }
}
