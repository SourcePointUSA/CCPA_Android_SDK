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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.TextView;

import com.sourcepoint.cmplibrary.CCPAConsentLib;
import com.sourcepoint.cmplibrary.ConsentLibBuilder;
import com.sourcepoint.cmplibrary.ConsentLibException;
import com.sourcepoint.cmplibrary.UserConsent;
import com.sourcepointccpa.app.SourcepointApp;
import com.sourcepointccpa.app.R;
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
    private boolean isShow = false;
    private boolean onConsentReadyCalled = false;
    private boolean isShowOnceOrError = false;
    private boolean isPropertySaved = false;

    private List<Consents> mVendorConsents = new ArrayList<>();
    private List<Consents> mPurposeConsents = new ArrayList<>();
    private String mError = "";

    private CCPAConsentLib mConsentLib;
    private TextInputEditText mConsentUUID;
    private TextInputEditText mMetaData;
    private RecyclerView mConsentRecyclerView;
    private List<Consents> mConsentList = new ArrayList<>();
    private ConsentListRecyclerView mConsentListRecyclerAdapter;
    private TextView mTitle, mConsentNotAvailable;
    private SharedPreferences preferences;
    private ConstraintLayout mConstraintLayout;

    private CCPAConsentLib buildConsentLib(Property property, Activity activity) throws ConsentLibException {


        ConsentLibBuilder consentLibBuilder = CCPAConsentLib.newBuilder(property.getAccountID(), property.getProperty(), property.getPropertyID(), property.getPmID(), activity)
                // optional, used for running stage campaigns
                .setStage(property.isStaging())
                .setShowPM(property.isShowPM())
                .setViewGroup(findViewById(android.R.id.content))
                //optional message timeout default timeout is 5 seconds
                .setMessageTimeOut(15000)
                .setOnMessageReady(ccpaConsentLib -> {
                    hideProgressBar();
                    Log.d(TAG, "OnMessageReady");

                    isShow = true;
                    saveToDatabase();
                    Log.i(TAG, "The message is about to be shown.");
                })
                // optional, callback triggered when message choice is selected when called choice
                // type will be available as Integer at cLib.choiceType
                .setOnMessageChoiceSelect(ccpaConsentLib -> {
                    Log.i(TAG, "Choice type selected by user: " + ccpaConsentLib.choiceType.toString());
                    Log.d(TAG, "setOnMessageChoiceSelect");
                })
                // optional, callback triggered when consent data is captured when called
                .setOnConsentReady(ccpaConsentLib -> {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // showActionBar();
                                    showProgressBar();
                                }
                            });
                            onConsentReadyCalled = true;
                            UserConsent consent = ccpaConsentLib.userConsent;


                            if (consent.status == UserConsent.ConsentStatus.rejectedNone) {
                                Log.i(TAG, "There are no rejected vendors/purposes.");
                                mConsentNotAvailable.setText("There are no rejected vendors/purposes.");
                            } else if (consent.status == UserConsent.ConsentStatus.rejectedAll) {
                                Log.i(TAG, "All vendors/purposes were rejected.");
                                mConsentNotAvailable.setText("All vendors/purposes were rejected.");
                            } else {
                               if(consent.rejectedVendors.size() > 0){
                                   Consents vendorHeader = new Consents("0", "Rejected Vendor Consents Ids", "Header");
                                   mVendorConsents.add(vendorHeader);
                                   for (String vendorId : consent.rejectedVendors) {
                                       Log.i(TAG, "The vendor " + vendorId + " was rejected.");
                                       Consents vendorConsent = new Consents(vendorId, vendorId, "vendorConsents");
                                       mVendorConsents.add(vendorConsent);
                                   }
                               }
                               if(consent.rejectedCategories.size() > 0 ){
                                   Consents purposeHeader = new Consents("0", "Rejected Purpose Consents Ids", "Header");
                                   mPurposeConsents.add(purposeHeader);
                                   for (String purposeId : consent.rejectedCategories) {
                                       Log.i(TAG, "The category " + purposeId + " was rejected.");
                                       Consents purposeConsents = new Consents(purposeId, purposeId, "purposeConsents");
                                       mPurposeConsents.add(purposeConsents);
                                   }
                               }
                            }
                            Log.d(TAG, "setOnInteractionComplete");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isShow && onConsentReadyCalled) {
                                        isShowOnceOrError = true;
                                    }
                                    showPropertyDebugInfo();
                                }
                            });
                        }
                )
                .setOnErrorOccurred(ccpaConsentLib -> {
                    hideProgressBar();
                    Log.d(TAG, "setOnErrorOccurred");
                    showAlertDialog("" + ccpaConsentLib.error.getMessage(), false);
                    Log.d(TAG, "Something went wrong: ", ccpaConsentLib.error);
                });

        //get and set targeting param
        List<TargetingParam> list = property.getTargetingParamList();//getTargetingParamList(property);
        for (TargetingParam tps : list) {
            consentLibBuilder.setTargetingParam(tps.getKey(), tps.getValue());
            Log.d(TAG, "" + tps.getKey() + " " + tps.getValue());
        }

        if (!TextUtils.isEmpty(property.getAuthId())) {
            consentLibBuilder.setAuthId(property.getAuthId());
            Log.d(TAG, "AuthID : " + property.getAuthId());
        } else {
            Log.d(TAG, "AuthID Not available : " + property.getAuthId());
        }
        // generate ConsentLib at this point modifying builder will not do anything
        return consentLibBuilder.build();
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
       // mMetaData = findViewById(R.id.tvMetaData);
        mConsentNotAvailable = findViewById(R.id.tv_consentsNotAvailable);

        mConsentRecyclerView = findViewById(R.id.consentRecyclerView);
        mConsentListRecyclerAdapter = new ConsentListRecyclerView(mConsentList);

        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        mConsentRecyclerView.addItemDecoration(itemDecor);
        mConsentRecyclerView.setAdapter(mConsentListRecyclerAdapter);

        Bundle data = getIntent().getExtras();
        Property property = data.getParcelable(Constants.PROPERTY);


        try {
            mConsentLib = buildConsentLib(property, this);
            if (Util.isNetworkAvailable(this)) {
                showProgressBar();
                mConsentLib.run();
            } else showAlertDialog(getString(R.string.network_check_message), false);
        } catch (Exception e) {
            showAlertDialog("" + e.toString(), false);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_showPM:
                resetFlag();
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

    private void buildAndShowConsentLibPM(){
        Bundle data = getIntent().getExtras();
        Property property = data.getParcelable(Constants.PROPERTY);
        mConsentLib.destroy();
        try {
            mConsentLib = buildConsentLib(property, this);
            if (Util.isNetworkAvailable(this)) {
                showProgressBar();
                mConsentLib.showPm();
            }else showAlertDialog(getString(R.string.network_check_message), false);
        } catch (ConsentLibException e) {
            showAlertDialog("" + e.toString(), false);
            e.printStackTrace();
        }
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

    private void addProperty(Property property) {
        viewModel.addProperty(property);
    }

    private void updateProperty(Property property) {
        viewModel.updateProperty(property);
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
    private void showAlertDialog(String message, boolean isPropertyList) {
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
                                            if (!isPropertyList) {
                                                if (isPropertySaved) {
                                                    onBackPressed();
                                                } else {
                                                    ConsentViewActivity.this.finish();
                                                }
                                            } else {
                                                setConsents();
                                            }
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
    private void setConsents() {
        hideProgressBar();
        showEUConsentAndConsentUUID();
        showActionBar();

        mConsentList.addAll(mVendorConsents);
        mConsentList.addAll(mPurposeConsents);
        if (isShowOnceOrError) {
            saveToDatabase();
        }

        if (mConsentList.size() > 0) {
            mConsentNotAvailable.setVisibility(View.GONE);
            mConsentRecyclerView.setVisibility(View.VISIBLE);
            mConsentListRecyclerAdapter.setConsentList(mConsentList);
            mConsentListRecyclerAdapter.notifyDataSetChanged();
        } else {
            mConsentRecyclerView.setVisibility(View.GONE);
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
        if (preferences.getString(Constants.EU_CONSENT_KEY, null) != null) {
           // mMetaData.setText(preferences.getString(Constants.META_DATA, null));
        }
    }

    // show debug info of property
    private void showPropertyDebugInfo() {
        if (isShowOnceOrError) {
            showAlertDialogForShowMessageOnce(getResources().getString(R.string.no_message_matching_scenario), true);
        } else {
            setConsents();
        }

    }

    // method to update or add property to database
    private void saveToDatabase() {
        Bundle bundle = getIntent().getExtras();
        Property property;
        if (bundle != null && !isPropertySaved) {
            property = bundle.getParcelable(Constants.PROPERTY);
            if (bundle.containsKey("Update")) {
                if (property != null && bundle.getString("Update") != null)
                    property.setId(Integer.parseInt(bundle.getString("Update")));
                updateProperty(property);
                isPropertySaved = true;
            } else if (bundle.containsKey("Add")) {
                addProperty(property);
                isPropertySaved = true;
            } else {
                Log.d(TAG, "No need to add or update as its from propertyList");
            }
        } else {
            Log.d(TAG, "Data not present to update or add");
        }

    }

    private void showAlertDialogForShowMessageOnce(String message, boolean isPropertyList) {
        hideProgressBar();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConsentViewActivity.this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Clear Cookies", (dialog, which) -> {
                    dialog.cancel();
                    showAlertDialogForCookiesCleared(isPropertyList);
                })
                .setNegativeButton("Show property Info", (dialog, which) -> {
                    dialog.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isPropertyList) {
                                ConsentViewActivity.this.finish();
                            } else {
                                setConsents();
                            }
                        }
                    });
                });
        AlertDialog mAlertDialog = alertDialog.create();
        mAlertDialog.show();
    }

    private void showAlertDialogForCookiesCleared(boolean isPropertyList) {
        SpannableString cookieConfirmation = new SpannableString(getResources().getString(R.string.cookie_confirmation_message));
        cookieConfirmation.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 12, 21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        cookieConfirmation.setSpan(new RelativeSizeSpan(1.2f), 12, 21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConsentViewActivity.this)
                .setMessage(cookieConfirmation)
                .setCancelable(false)
                .setPositiveButton("YES", (dialog, which) -> {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.commit();
                    clearCookies(isPropertyList);

                })
                .setNegativeButton("NO", (dialog, which) -> {
                    dialog.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isPropertyList) {
                                ConsentViewActivity.this.finish();
                            } else {
                                setConsents();
                            }
                        }
                    });
                });
        AlertDialog mAlertDialog = alertDialog.create();
        mAlertDialog.show();
    }

    private void clearCookies(boolean isPropertyList) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(value -> {
            Log.d(TAG, "Cookies cleared : " + value.toString());
            if (value) {
                resetFlag();
                Bundle data = getIntent().getExtras();
                Property property = data.getParcelable(Constants.PROPERTY);
                try {
                    mConsentLib = buildConsentLib(property, this);
                    if (Util.isNetworkAvailable(this)) {
                        showProgressBar();
                        mConsentLib.run();
                    } else showAlertDialog(getString(R.string.network_check_message), false);
                } catch (Exception e) {
                    showAlertDialog("" + e.toString(), false);
                    e.printStackTrace();
                }
            } else {
                showAlertDialog(getString(R.string.unable_to_clear_cookies), isPropertyList);
            }
        });

    }

    private void resetFlag() {
        isShow = onConsentReadyCalled = isShowOnceOrError = false;
        mConsentList.clear();
        mVendorConsents.clear();
        mPurposeConsents.clear();
    }
}