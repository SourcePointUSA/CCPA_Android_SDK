## 1.3.3 (August, 03, 2020)
* add .setOnAction(Runnable r) to consentLib builder: this callback will be fired on any user consent action

## 1.3.2 (July, 13, 2020)
* add CCPAConsentLib.clearConsentData() to public API
* fix consent dialog not showing on emulators

## 1.3.1 (June, 29, 2020)
* fix app crash on null activity

## 1.3.0 (June, 12, 2020)
* Store the IABUSPrivacy_String as spec'ed by the CCPA IAB
* ccpaApplies public attribute added to CCPAConsentLib class

## 1.2.0 (April, 30, 2020)
* Authenticated consent flow implemented 🗝️

## 1.1.5 (April, 23, 2020)
* concurrency crash on timeout error fixed

## 1.1.4 (March, 24, 2020)
* UserConsent.ConsentStatus.consentedAll added: this allow to use acceptedAll actions on consent messages

## 1.1.3 (March, 21, 2020)
* Fixed message aways being shown despite consent gate config

## 1.1.2 (January, 29, 2020)
* Fixed sharedPreferences data clean before saving consent

## 1.1.1 (January, 23, 2020)
* AndroidManifest updated - solved issue integrating with remote GDPR dependency.

## 1.1.0 (January, 21, 2020)
* setViewGroup method was removed ✂️; now the user must add the consentLibWebView to the parent view group as shown in the updated example in the readme file.
* Call backs renamed for simplification and UI callback onUIFinished added to facilitate interaction with the consentLibWebView. This changes are also reflected in the readme file example.
* setTargetingParams and setStagingCampaign methods added to consentLibBuilder in order to enable the user to set params on our sp scenarios and chose between stage and public scenarios as well. Additional example was added on [wiki](https://github.com/SourcePointUSA/CCPA_Android_SDK/wiki/Sending-arbitrary-key-value-pairs-to-the-scenario-(TargetingParams) (edited) ) for this use case.
* Modulo and classes renamed for compatibility with GDPR sdk. Usage example of both GDPR and CCPA SDKs together was added to [wiki](https://github.com/SourcePointUSA/CCPA_Android_SDK/wiki/Integrating-the-CCPA-and-GDPR-SDKs-together) as well.
* No more errors thrown from consentLibBuilder in order to simplify instantiation of consentLib. All errors are to be dealt in  the onError callback.

## 1.0.1 (December, 26, 2019)
* Message flow implemented