package com.quiziz.drive.views.activities.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.quiziz.drive.model.AppInfo;
import com.quiziz.drive.util.InternetConnectionManager;
import com.quiziz.drive.util.QuizizConstants;
import com.quiziz.drive.views.activities.intro.IntroActivity;
import com.quiziz.drive.views.activities.main.MainActivity;
import com.quiziz.drive.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pototo on 24/02/16.
 */
public class SplashActivity extends Activity
    implements AppInfo.OnAppInfoListener{

    private AppInfo mAppInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAppInfo = new AppInfo();
        mAppInfo.onAttach(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(InternetConnectionManager.isOnline(this))
            mAppInfo.getAppInfoInBackground();
        else
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostExecute(AppInfo appInfo) {
        Intent introIntent = new Intent(this, IntroActivity.class);
        introIntent.putExtra(QuizizConstants.IntroBundle.INTRO_BUNDLE_KEY, appInfo.getDetail());
        startActivity(introIntent);
        finish();
    }
}
