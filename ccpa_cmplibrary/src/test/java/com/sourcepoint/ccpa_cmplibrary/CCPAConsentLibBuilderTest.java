package com.sourcepoint.ccpa_cmplibrary;

import android.app.Activity;
import android.view.ViewGroup;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class CCPAConsentLibBuilderTest {
    private ConsentLibBuilder consentLibBuilder;
    private CCPAConsentLib.Callback callback;

    @Before
    public void initConsentLibBuilder() {
        consentLibBuilder = new ConsentLibBuilder(123, "example.com", 321, "abcd", mock(Activity.class));
        callback = c -> {  };
    }

    public Field getDeclaredFieldAccess(String fieldName) throws Exception{
        Field member = ConsentLibBuilder.class.getDeclaredField(fieldName);
        member.setAccessible(true);
        return member;
    }

    @Test
    public void setMessageTimeout() {
        consentLibBuilder.setMessageTimeOut(20000);
        assertEquals(20000, consentLibBuilder.defaultMessageTimeOut);
    }

    @Test
    public void setPage() {
        String page = "page";
        consentLibBuilder.setPage(page);
        assertEquals(page, consentLibBuilder.page);
    }

    @Test
    public void setOnAction() {
        consentLibBuilder.setOnAction(callback);
        assertEquals(callback, consentLibBuilder.onAction);
    }

    @Test
    public void setOnInteractionComplete() {
        consentLibBuilder.setOnConsentUIReady(callback);
        assertEquals(callback, consentLibBuilder.onConsentUIReady);
    }

    @Test
    public void setOnMessageReady() {
        consentLibBuilder.setOnConsentUIReady(callback);
        assertEquals(callback, consentLibBuilder.onConsentUIReady);
    }

    @Test
    public void setOnErrorOccurred() {
        consentLibBuilder.setOnError(callback);
        assertEquals(callback, consentLibBuilder.onError);
    }

    @Test
    public void setStage() {
        boolean stage = true;
        consentLibBuilder.setStage(stage);
        assertEquals(stage, consentLibBuilder.stagingCampaign);
    }

    @Test
    public void setInternalStage() {
        boolean stage = true;
        consentLibBuilder.setInternalStage(stage);
        assertEquals(stage, consentLibBuilder.staging);
    }

    @Test
    public void setInAppMessagePageUrl() {
        String inAppMessageUrl = "inAppMessageUrl";
        consentLibBuilder.setInAppMessagePageUrl(inAppMessageUrl);
        assertEquals(inAppMessageUrl, consentLibBuilder.msgDomain);
    }

    @Test
    public void setMmsDomain() {
        String mmsDomain = "mmsDomain";
        consentLibBuilder.setMmsDomain(mmsDomain);
        assertEquals(mmsDomain, consentLibBuilder.mmsDomain);
    }

    @Test
    public void setCmpDomain() {
        String cmpDomain = "cmpDomain";
        consentLibBuilder.setCmpDomain(cmpDomain);
        assertEquals(cmpDomain, consentLibBuilder.cmpDomain);
    }

    @Test
    public void setTargetingParamString() throws Exception {
        String key = "key";
        String stringValue = "stringValue";
        consentLibBuilder.setTargetingParam(key, stringValue);

        Field localMember = getDeclaredFieldAccess("targetingParams");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, stringValue);

        assertEquals(jsonObject.toString() , localMember.get(consentLibBuilder).toString());

    }

    @Test
    public void targetingParamStringIsEncoded() throws Exception {
        String key = "key";
        String stringValue = "stringValue";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, stringValue);
        consentLibBuilder.setTargetingParam(key, stringValue);

        Method method = ConsentLibBuilder.class.getDeclaredMethod("setTargetingParamsString");
        method.setAccessible(true);
        method.invoke(consentLibBuilder);

        assertEquals("{\"key\":\"stringValue\"}", consentLibBuilder.targetingParamsString);
    }

    @Test
    public void setTargetingParamIntValue() throws Exception{

        String key = "key";
        int intValue = 2;
        consentLibBuilder.setTargetingParam(key, intValue);

        Field localMember = getDeclaredFieldAccess("targetingParams");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, intValue);

        assertEquals(jsonObject.toString() , localMember.get(consentLibBuilder).toString());
    }

    @Test
    public void setTargetingParamStringValue() throws Exception{

        String key = "key";
        String stringValue = "stringValue";
        consentLibBuilder.setTargetingParam(key, stringValue);

        Field localMember = getDeclaredFieldAccess("targetingParams");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, stringValue);

        assertEquals(jsonObject.toString() , localMember.get(consentLibBuilder).toString());
    }

    @Test
    public void setDebugLevel(){
        consentLibBuilder.setDebugLevel(CCPAConsentLib.DebugLevel.DEBUG);
        assertEquals(CCPAConsentLib.DebugLevel.DEBUG , consentLibBuilder.debugLevel);
    }
}