package com.sourcepoint.ccpa_cmplibrary;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class CCPAConsentLibTest {

    private static final String IAB_CONSENT_CMP_PRESENT = "IABConsent_CMPPresent";
    private static final String IAB_CONSENT_SUBJECT_TO_GDPR = "IABConsent_SubjectToGDPR";
    private static final String IAB_CONSENT_CONSENT_STRING = "IABConsent_ConsentString";
    private static final String IAB_CONSENT_PARSED_PURPOSE_CONSENTS = "IABConsent_ParsedPurposeConsents";
    private static final String IAB_CONSENT_PARSED_VENDOR_CONSENTS = "IABConsent_ParsedVendorConsents";
    private static final String EU_CONSENT_KEY = "euconsent";
    private static final String CONSENT_UUID_KEY = "consentUUID";
    private SharedPreferences sharedPrefs;
    private Context context;
    private CCPAConsentLib CCPAConsentLib;

//    @Before
//    public void setUP(){
//        CCPAConsentLib = Mockito.mock(CCPAConsentLib.class);
//        context = Mockito.mock(Context.class);
//        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext());
//
//    }
//
//    /*Test method for removing all consent data*/
//    @Test
//    public void clearAllConsentData() throws NoSuchFieldException {
//        CCPAConsentLib.clearAllConsentData();
//
//        assertEquals(sharedPrefs.contains(IAB_CONSENT_CMP_PRESENT),false);
//        assertEquals(sharedPrefs.contains(IAB_CONSENT_SUBJECT_TO_GDPR),false);
//        assertEquals(sharedPrefs.contains(IAB_CONSENT_CONSENT_STRING),false);
//        assertEquals(sharedPrefs.contains(IAB_CONSENT_PARSED_VENDOR_CONSENTS),false);
//        assertEquals(sharedPrefs.contains(IAB_CONSENT_PARSED_PURPOSE_CONSENTS),false);
//        assertEquals(sharedPrefs.contains(EU_CONSENT_KEY),false);
//        assertEquals(sharedPrefs.contains(CONSENT_UUID_KEY),false);
//    }
//
//
//    @Test
//    public void testIAB_CONSENT_CMP_PRESENT(){
//        CCPAConsentLib.clearAllConsentData();
//        assertEquals(sharedPrefs.contains(IAB_CONSENT_CMP_PRESENT), false);
//    }
//
//    @Test
//    public void testIAB_CONSENT_SUBJECT_TO_GDPR(){
//        CCPAConsentLib.clearAllConsentData();
//        assertEquals(sharedPrefs.contains(IAB_CONSENT_SUBJECT_TO_GDPR) , false);
//    }
//
//    @Test
//    public void testIAB_CONSENT_CONSENT_STRING(){
//        CCPAConsentLib.clearAllConsentData();
//        assertEquals(sharedPrefs.contains(IAB_CONSENT_CONSENT_STRING) , false);
//    }
//
//    @Test
//    public void testIAB_CONSENT_PARSED_PURPOSE_CONSENTS(){
//        CCPAConsentLib.clearAllConsentData();
//        assertEquals(sharedPrefs.contains(IAB_CONSENT_PARSED_PURPOSE_CONSENTS) , false);
//    }
//
//    @Test
//    public void testIAB_CONSENT_PARSED_VENDOR_CONSENTS(){
//        CCPAConsentLib.clearAllConsentData();
//        assertEquals(sharedPrefs.contains(IAB_CONSENT_PARSED_VENDOR_CONSENTS) , false);
//    }
//
//    @Test
//    public void testEU_CONSENT_KEY(){
//        CCPAConsentLib.clearAllConsentData();
//        assertEquals(sharedPrefs.contains(EU_CONSENT_KEY) , false);
//    }
//
//    @Test
//    public void testCONSENT_UUID_KEY(){
//        CCPAConsentLib.clearAllConsentData();
//        assertEquals(sharedPrefs.contains(CONSENT_UUID_KEY) , false);
//    }

}