package com.sourcepointccpa.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.sourcepoint.ccpa_cmplibrary.CCPAConsentLib;
import com.sourcepoint.ccpa_cmplibrary.ConsentLibBuilder;
import com.sourcepoint.ccpa_cmplibrary.UserConsent;
import com.sourcepointccpa.app.R;
import com.sourcepointccpa.app.SourcepointApp;
import com.sourcepointccpa.app.adapters.ConsentListRecyclerView;
import com.sourcepointccpa.app.common.Constants;
import com.sourcepointccpa.app.database.entity.Property;
import com.sourcepointccpa.app.database.entity.TargetingParam;
import com.sourcepointccpa.app.models.Consents;
import com.sourcepointccpa.app.repository.PropertyListRepository;
import com.sourcepointccpa.app.utility.Util;
import com.sourcepointccpa.app.viewmodel.ConsentViewViewModel;
import com.sourcepointccpa.app.viewmodel.ViewModelUtils;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class ConsentViewActivity extends BaseActivity<ConsentViewViewModel> {

    private final String TAG = "ConsentViewActivity";
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;

    private List<Consents> mVendorConsents = new ArrayList<>();
    private List<Consents> mPurposeConsents = new ArrayList<>();

    private CCPAConsentLib mConsentLib;
    private TextInputEditText mConsentUUID;
    private RecyclerView mConsentRecyclerView;
    private List<Consents> mConsentList = new ArrayList<>();
    private ConsentListRecyclerView mConsentListRecyclerAdapter;
    private TextView mTitle, mConsentNotAvailable;
    private SharedPreferences preferences;
    private ConstraintLayout mConstraintLayout;

    private ViewGroup mainViewGroup;

    private void showMessageWebView(WebView webView) {
        webView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        webView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        webView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        webView.bringToFront();
        webView.requestLayout();
        mainViewGroup.addView(webView);
    }

    private void removeWebView(WebView webView) {
        if (webView.getParent() != null)
            mainViewGroup.removeView(webView);
    }

    private CCPAConsentLib buildConsentLib(Property property, Activity activity) {

        ConsentLibBuilder consentLibBuilder = CCPAConsentLib.newBuilder(property.getAccountID(), property.getProperty(), property.getPropertyID(), property.getPmID(), activity)
                // optional, used for running stage campaigns
                .setStage(property.isStaging())
                .setShowPM(property.isShowPM())
                .setMessageTimeOut(15000)
                .setOnConsentUIReady(ccpaConsentLib -> {
                    getSupportActionBar().hide();
                    hideProgressBar();
                    Log.d(TAG, "setOnConsentUIReady");
                    showMessageWebView(ccpaConsentLib.webView);
                })
                .setOnConsentUIFinished(ccpaConsentLib -> {
                    getSupportActionBar().show();
                    removeWebView(ccpaConsentLib.webView);
                    Log.d(TAG, "setOnConsentUIFinished");
                })
                // optional, callback triggered when message choice is selected when called choice
                // type will be available as Integer at cLib.choiceType
                .setOnMessageChoiceSelect(ccpaConsentLib -> {
                    Log.i(TAG, "Choice type selected by user: " + ccpaConsentLib.choiceType.toString());
                    Log.d(TAG, "setOnMessageChoiceSelect");
                })
                // optional, callback triggered when consent data is captured when called
                .setOnConsentReady(ccpaConsentLib -> {
                            mConsentList.clear();
                            mVendorConsents.clear();
                            mPurposeConsents.clear();
                            runOnUiThread(this::showProgressBar);
                            getConsentsFromConsentLib(ccpaConsentLib);

                            Log.d(TAG, "setOnConsentReady");
                            runOnUiThread(this::showPropertyDebugInfo);
                        }
                )
                .setOnError(ccpaConsentLib -> {
                    hideProgressBar();
                    Log.d(TAG, "setOnError");
                    showAlertDialog("" + ccpaConsentLib.error.consentLibErrorMessage);
                    Log.d(TAG, "Something went wrong: ", ccpaConsentLib.error);
                });

        //get and set targeting param
        List<TargetingParam> list = property.getTargetingParamList();//getTargetingParamList(property);
        for (TargetingParam tps : list) {
            consentLibBuilder.setTargetingParam(tps.getKey(), tps.getValue());
            Log.d(TAG, "" + tps.getKey() + " " + tps.getValue());
        }

        if (!TextUtils.isEmpty(property.getAuthId())) {
            //consentLibBuilder.setAuthId(property.getAuthId());
            Log.d(TAG, "AuthID : " + property.getAuthId());
            Log.d(TAG, "AuthID : " + "feature not available for ccpa currently");
        } else {
            Log.d(TAG, "AuthID Not available : " + property.getAuthId());
        }
        // generate ConsentLib at this point modifying builder will not do anything
        return consentLibBuilder.build();
    }

    private void getConsentsFromConsentLib(CCPAConsentLib ccpaConsentLib) {
        UserConsent consent = ccpaConsentLib.userConsent;
        if (consent.status == UserConsent.ConsentStatus.rejectedNone) {
            mConsentNotAvailable.setText("There are no rejected vendors/purposes.");
            Log.i(TAG, "There are no rejected vendors/purposes.");
        } else if (consent.status == UserConsent.ConsentStatus.rejectedAll) {
            mConsentNotAvailable.setText("All vendors/purposes were rejected.");
            Log.i(TAG, "All vendors/purposes were rejected.");
        } else {
            if (consent.rejectedVendors.size() > 0) {
                Consents vendorHeader = new Consents("0", "Rejected Vendor Ids", "Header");
                mVendorConsents.add(vendorHeader);
                for (String vendorId : consent.rejectedVendors) {
                    Log.i(TAG, "The vendor " + vendorId + " was rejected.");
                    Consents vendorConsent = new Consents(vendorId, vendorId, "vendorConsents");
                    mVendorConsents.add(vendorConsent);
                }
            }
            if (consent.rejectedCategories.size() > 0) {
                Consents purposeHeader = new Consents("0", "Rejected Purpose Ids", "Header");
                mPurposeConsents.add(purposeHeader);
                for (String purposeId : consent.rejectedCategories) {
                    Log.i(TAG, "The category " + purposeId + " was rejected.");
                    Consents purposeConsents = new Consents(purposeId, purposeId, "purposeConsents");
                    mPurposeConsents.add(purposeConsents);
                }
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_view);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.tool_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitle = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        mConstraintLayout = findViewById(R.id.parentLayout);
        mConstraintLayout.setVisibility(View.GONE);

        getSupportActionBar().hide();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mConsentUUID = findViewById(R.id.tvConsentUUID);
        mConsentNotAvailable = findViewById(R.id.tv_consentsNotAvailable);

        mConsentRecyclerView = findViewById(R.id.consentRecyclerView);
        mConsentListRecyclerAdapter = new ConsentListRecyclerView(mConsentList);

        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        mConsentRecyclerView.addItemDecoration(itemDecor);
        mConsentRecyclerView.setAdapter(mConsentListRecyclerAdapter);
        mainViewGroup = findViewById(android.R.id.content);

        Bundle data = getIntent().getExtras();
        Property property = data.getParcelable(Constants.PROPERTY);
        if (data.getParcelableArrayList(Constants.CONSENTS) != null) {
            mConsentList = data.getParcelableArrayList(Constants.CONSENTS);
            setConsents(true);
        } else {
            if (Util.isNetworkAvailable(this)) {
                showProgressBar();
                buildConsentLib(property, this).run();
            } else showAlertDialog(getString(R.string.network_check_message));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_showPM:
                getSupportActionBar().hide();
                buildAndShowConsentLibPM();
                break;
            case android.R.id.home:
                this.finish();
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.consent_view_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void buildAndShowConsentLibPM() {
        Bundle data = getIntent().getExtras();
        Property property = data.getParcelable(Constants.PROPERTY);

        mConsentLib = buildConsentLib(property, this);
        if (Util.isNetworkAvailable(this)) {
            showProgressBar();
            mConsentLib.showPm();
        } else showAlertDialog(getString(R.string.network_check_message));
    }

    @Override
    ViewModel getViewModel() {
        PropertyListRepository propertyListRepository = ((SourcepointApp) getApplication()).getPropertyListRepository();
        return new ConsentViewViewModel(propertyListRepository);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewModelProvider.Factory viewModelFactory = ViewModelUtils.createFor(viewModel);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModel.getClass());
    }

    private void showProgressBar() {

        if (mProgressDialog == null) {

            mProgressDialog = new ProgressDialog(ConsentViewActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.getWindow().setTransitionBackgroundFadeDuration(1000);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();

        } else if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void hideProgressBar() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mProgressDialog != null) {
            hideProgressBar();
            mProgressDialog = null;
        }

        if (mAlertDialog != null) {
            mAlertDialog = null;
        }
    }

    // method to show alert/error dialog
    private void showAlertDialog(String message) {
        hideProgressBar();
        if (!isDestroyed()) {
            if (mAlertDialog == null) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConsentViewActivity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> {
                                    dialog.cancel();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setConsents(false);
                                        }
                                    });
                                }
                        );
                mAlertDialog = alertDialog.create();
            }

            if (!mAlertDialog.isShowing())
                mAlertDialog.show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ConsentViewActivity.this, PropertyListActivity.class);
        startActivity(intent);
        ConsentViewActivity.this.finish();
    }

    // method to set consents to recycler view
    private void setConsents(boolean isNewProperty) {
        hideProgressBar();
        showEUConsentAndConsentUUID();
        showActionBar();

        if (!isNewProperty) {
            mConsentList.addAll(mVendorConsents);
            mConsentList.addAll(mPurposeConsents);
        }

        if (mConsentList.size() > 0) {
            mConsentNotAvailable.setVisibility(View.GONE);
            mConsentListRecyclerAdapter.setConsentList(mConsentList);
            mConsentListRecyclerAdapter.notifyDataSetChanged();
        } else {
            Log.d(TAG, "mConsentList is empty");
            mConsentNotAvailable.setVisibility(View.VISIBLE);
        }
    }

    //method to show action bar
    private void showActionBar() {
        getSupportActionBar().show();
        mConstraintLayout.setVisibility(View.VISIBLE);
        mTitle.setText(getResources().getString(R.string.property_info_screen_title));
    }

    // method to show consent UUID and EUConsent
    private void showEUConsentAndConsentUUID() {
        if (preferences.getString(Constants.CONSENT_UUID_KEY, null) != null) {
            mConsentUUID.setText(preferences.getString(Constants.CONSENT_UUID_KEY, null));
        }
    }

    // show debug info of property
    private void showPropertyDebugInfo() {
        setConsents(false);
    }
}