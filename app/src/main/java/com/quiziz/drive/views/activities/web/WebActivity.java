package com.quiziz.drive.views.activities.web;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.quiziz.drive.R;

/**
 * Created by pototo on 29/03/16.
 */
public class WebActivity extends AppCompatActivity{

    public static final String URL_EXTRA_PARAM = "urlExtraParam";

    private WebView mWebView;
    private String mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        setUrl();
        setWebView();
    }

    private void setUrl(){
        Bundle bundle = getIntent().getExtras();
        mUrl = bundle.getString(URL_EXTRA_PARAM);
    }

    private void setWebView() {
        mWebView = (WebView)findViewById(R.id.web_view);
        if(mWebView != null && mUrl != null && !mUrl.isEmpty()){
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            mWebView.loadUrl(mUrl);
        }
    }
}