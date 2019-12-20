Table of Contents
=================
   * [Setup](#setup)
   * [Usage](#usage)
   * [Docs](#docs)
   * [Development](#development)
      * [How to build the `ccpa_cmplibrary` module from source](#how-to-build-the-ccpa_cmplibrary-module-from-source)
      * [How to import the master version of `ccpa_cmplibrary` into existing an Android app project for development](#how-to-import-the-master-version-of-ccpa_cmplibrary-into-existing-an-android-app-project-for-development)
      * [How to publish a new version into JCenter](#how-to-publish-a-new-version-into-jcenter)

# Setup
To use `ccpa_cmplibrary` in your app, include `com.sourcepoint.ccpa_cmplibrary:ccpa_cmplibrary:x.y.z` as a dependency to your project's build.gradle.
```
...
dependencies {
    implementation 'com.sourcepoint.ccpa_cmplibrary:ccpa_cmplibrary:2.4.3'
}
...

...
dependencies {
    implementation 'com.sourcepoint.ccpa_cmplibrary:ccpa_cmplibrary:3.1.0'
}

```

# Usage
* In your main activity, create an instance of `CCPAConsentLib` class using `CCPAConsentLib.newBuilder()` class function passing the configurations and callback handlers to the builder and call `.run()` on the instantiated `CCPAConsentLib` object to load a message and get consents or call `.loadPM` to load a privacy menager message like following:

```java
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private CCPAConsentLib ccpaConsentLib;

    private CCPAConsentLib buildAndRunConsentLib(Boolean showPM) throws ConsentLibException {
        return CCPAConsentLib.newBuilder(22, "ccpa.mobile.demo", 6099,"5df9105bcf42027ce707bb43",this)
                .setStage(true)
                .setViewGroup(findViewById(android.R.id.content))
                .setShowPM(showPM)
                .setOnMessageReady(consentLib -> Log.i(TAG, "onMessageReady"))
                .setOnConsentReady(consentLib -> {
                    Log.i(TAG, "onConsentReady");
                    UserConsent consent = consentLib.userConsent;
                    if(consent.status == UserConsent.ConsentStatus.rejectedNone){
                        Log.i(TAG, "There are no rejected vendors/purposes.");
                    } else if(consent.status == UserConsent.ConsentStatus.rejectedAll){
                        Log.i(TAG, "All vendors/purposes were rejected.");
                    } else {
                        for (String vendorId : consent.rejectedVendors) {
                            Log.i(TAG, "The vendor " + vendorId + " was rejected.");
                        }
                        for (String purposeId : consent.rejectedCategories) {
                            Log.i(TAG, "The category " + purposeId + " was rejected.");
                        }
                    }
                })
                .setOnErrorOccurred(c -> Log.i(TAG, "Something went wrong: ", c.error))
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            ccpaConsentLib = buildAndRunConsentLib(false);
            ccpaConsentLib.run();
        } catch (ConsentLibException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.review_consents).setOnClickListener(_v -> {
            try {
                ccpaConsentLib = buildAndRunConsentLib(true);
                ccpaConsentLib.showPm();
            } catch (ConsentLibException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ccpaConsentLib != null ) { ccpaConsentLib.destroy(); }
    }
}
```
# Docs
For the complete documentation, open `./docs/index.html` in the browser.

# Development
## How to build the `ccpa_cmplibrary` module from source
Note: skip this step and jump to next section if you already have the compiled the compiled `ccpa_cmplibrary-release.aar` binary.

* Clone and open `android-cmp-app` project in Android Studio
* Build the project
* Open `Gradle` menu from right hand side menu in Android Studio and select `assemble` under `:ccpa_cmplibrary > Tasks > assemble`
<img width="747" alt="screen shot 2018-11-05 at 4 52 27 pm" src="https://user-images.githubusercontent.com/2576311/48029062-4c950000-e11b-11e8-8d6f-a50c9f37e25b.png">

* Run the assemble task by selecting `android-cmp-app:ccpa_cmplibrary [assemble]` (should be already selected) and clicking the build icon (or selecting Build > Make Project) from the menus.
* The release version of the compiled binary should be under `ccpa_cmplibrary/build/outputs/aar/ccpa_cmplibrary-release.aar` directory. Copy this file and import it to your project using the steps below.

## How to import the master version of `ccpa_cmplibrary` into existing an Android app project for development

* Open your existing Android project in Android Studio and select the File > New > New Module menu item.
* Scroll down and select `Import .JAR/.AAR Package` and click next.
* Browse and select the distributed `ccpa_cmplibrary-release.aar` binary file (or the one you generated using the instructions in the last section)
 * In your project's `app/build.gradle` file make sure you have `ccpa_cmplibrary-release` as a dependency and also add `com.google.guava:guava:20.0` as a dependency:
```
dependencies {
    ...
    implementation project(":ccpa_cmplibrary-release")
    implementation("com.google.guava:guava:20.0")
}
```

* Make sure in your project's `settings.gradle` file you have:
```
include ':app', ':ccpa_cmplibrary-release'
```

* Open `app/src/main/AndroidManifest.xml` and add `android.permission.INTERNET` permission if you do not have the permission in your manifest:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.your-app">
    <uses-permission android:name="android.permission.INTERNET" />

    <application>
        ...
    </application>
</manifest>
```

## How to publish a new version into JCenter
- Make sure you have bumped up the library version in `ccpa_cmplibrary/build.gradle` but changing the line `def VERSION_NAME = x.y.z`
- Open Gradle menu from right hand side menu in Android Studio
- Run the following three tasks in order from the list of tasks under `ccpa_cmplibrary` by double clicking on each:
  - `build:clean`
  - `build:assembleRelease`
  - `other:bundleZip`

- If everything goes fine, you should have a `ccpa_cmplibrary-x.y.z` file in `ccpa_cmplibrary/build` folder.
- At this time, you have to create a new version manually with the same version name you chose above in BinTray.
- Select the version you just created and click on "Upload Files", select the generated `ccpa_cmplibrary-x.y.z` file and once appeared in the files list, check `Explode this archive` and click on Save Changes.
- Now you need to push the new version to JCenter: go to the version page in BinTray, you will see a notice in the page "Notice: You have 3 unpublished item(s) for this version (expiring in 6 days and 22 hours) ", click on "Publish" in front of the notice. It will take few hours before your request to publish to JCenter will be approved.
