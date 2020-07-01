package com.sourcepoint.ccpa_cmplibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.ccpa_cmplibrary.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

abstract public class ConsentWebView extends WebView {
    private static final String TAG = "ConsentWebView";

    @SuppressWarnings("unused")
    private class MessageInterface {

        @JavascriptInterface
        public void log(String tag, String msg){
            Log.i(tag, msg);
        }

        @JavascriptInterface
        public void log(String msg){
            Log.i("JS", msg);
        }

        // called when message is about to be shown
        @JavascriptInterface
        public void onMessageReady() {
            Log.d("onMessageReady", "called");
            ConsentWebView.this.onMessageReady();
        }

        // called when a choice is selected on the message
        @JavascriptInterface
        public void onAction(int choiceType) {
            Log.d("onAction", "called");
            ConsentWebView.this.onAction(choiceType);
        }

        // called when a choice is selected on the message
        @JavascriptInterface
        public void onSavePM(String payloadStr) throws JSONException {
            Log.d("onSavePM", "called");
            JSONObject payloadJson = new JSONObject(payloadStr);
            ConsentWebView.this.onSavePM(
                    new UserConsent(
                            payloadJson.getJSONArray("rejectedVendors"),
                            payloadJson.getJSONArray("rejectedCategories")
                    )
            );
        }

        //called when an error is occurred while loading web-view
        @JavascriptInterface
        public void onError(String errorType) {                               ;
            ConsentWebView.this.onError(new ConsentLibException("Something went wrong in the javascript world."));
        }
        // xhr logger
        @JavascriptInterface
        public void xhrLog(String response){
            Log.d("xhrLog" , response);
        }

    }

    public ConsentWebView(Context context) {
        super(getFixedContext(context));
        setup();
    }

    // Method created for avoiding crashes when inflating the webview on android Lollipop
    public static Context getFixedContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return context.createConfigurationContext(context.getResources().getConfiguration());
        }
        return context;
    }

    private boolean doesLinkContainImage(HitTestResult testResult) {
        return testResult.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE;
    }

    private String getLinkUrl(HitTestResult testResult) {
        if (doesLinkContainImage(testResult)) {
            Handler handler = new Handler();
            Message message = handler.obtainMessage();
            requestFocusNodeHref(message);
            return (String) message.getData().get("url");
        }

        return testResult.getExtra();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void setup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (0 != (getContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                setWebContentsDebuggingEnabled(true);
                enableSlowWholeDocumentDraw();
            }
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }

        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getSettings().setJavaScriptEnabled(true);
        this.setBackgroundColor(Color.TRANSPARENT);
        this.requestFocus();
        setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        view.evaluateJavascript("javascript:" + getFileContent(getResources().openRawResource(R.raw.javascript_receiver)), null);
                    }else {
                        view.loadUrl("javascript:" + getFileContent(getResources().openRawResource(R.raw.javascript_receiver)));
                    }
                }catch (Exception e){
                    onError(new ConsentLibException(e));
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.d(TAG, "onReceivedError: " + error.toString());
                onError(new ConsentLibException.ApiException(error.toString()));
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.d(TAG, "onReceivedError: Error " + errorCode + ": " + description);
                onError(new ConsentLibException.ApiException(description));
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                Log.d(TAG, "onReceivedSslError: Error " + error);
                onError(new ConsentLibException.ApiException(error.toString()));
            }

            @Override
            public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                String message = "The WebView rendering process crashed!";
                Log.e(TAG, message);
                onError(new ConsentLibException(message));
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loadLinkOnExternalBrowser(url);
                return true;
            }
        });
        setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getLinkUrl(view.getHitTestResult())));
                view.getContext().startActivity(browserIntent);
                return false;
            }
        });
        setOnKeyListener((view, keyCode, event) -> {
            WebView webView = (WebView) view;
            if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode ) {
                  if( webView.canGoBack()) {
                      webView.goBack();
                  }else {
                      ConsentWebView.this.onAction(CCPAConsentLib.ActionTypes.DISMISS);
                  }
                return true;
            }
            return false;
        });
        addJavascriptInterface(new MessageInterface(), "JSReceiver");
        resumeTimers();
    }

    private String getFileContent(InputStream is) throws IOException {

        BufferedReader br = new BufferedReader( new InputStreamReader(is, "UTF-8" ));
        StringBuilder sb = new StringBuilder();
        String line;
        while(( line = br.readLine()) != null ) {
            sb.append( line );
            sb.append( '\n' );
        }
        return sb.toString();
    }

    private void loadLinkOnExternalBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.getContext().startActivity(intent);
    }

    abstract public void onMessageReady();

    abstract public void onError(ConsentLibException error);

    abstract public void onAction(int choiceType);

    abstract public void onSavePM(UserConsent userConsent);


    public void loadConsentMsgFromUrl(String url) {

        // On API level >= 21, the JavascriptInterface is not injected on the page until the *second* page load
        // so we need to issue blank load with loadData
        loadData("", "text/html", null);
        Log.d(TAG, "Loading Webview with: " + url);
        Log.d(TAG, "User-Agent: " + getSettings().getUserAgentString());
        loadUrl(url);
    }

    public void display() {
        setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        bringToFront();
        requestLayout();
    }
}
