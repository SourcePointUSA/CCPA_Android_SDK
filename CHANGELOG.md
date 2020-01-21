## 1.1.0 (January, 21, 2020)
* setViewGroup method was removed ✂️; now the user must add the consentLibWebView to the parent view group as shown in the updated example in the readme file.
* Call backs renamed for simplification and UI callback onUIFinished added to facilitate iteraction with the consentLibWebView. This changes are also reflected in the readme file example.
* setTargetingParams and setStagingCampaing methods added to consentLibBuilder in order to enable the user to set params on our sp scenarios and chose between stage and public scenarios as well. Additional example was added on wiki for this use case.
* Modulo and classes renamed for compatibility with GDPR sdk. Usage example of both GDPR and CCPA SDKs together was added to readme file as well.
* No more errors thrown from consentLibBuilder in order to simplify instatiation of consentLib. All errors are to be dealt in  the onError callback.
## 1.0.1 (December, 26, 2019)
* Message flow implemented