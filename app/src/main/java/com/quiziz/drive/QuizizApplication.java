package com.quiziz.drive;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.quiziz.drive.util.Config;

/**
 * Created by pototo on 03/03/16.
 */
public class QuizizApplication extends Application{
    private static final String TAG = "QuizizApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, Config.APPLICATION_ID, Config.CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    public synchronized Tracker getTracker(){
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        Tracker tracker = analytics.newTracker(R.xml.analytics_global_config);
        return tracker;
    }

    public void sendTrackerToGoogleAnalytics(final String screenName) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Google Analytics: " + screenName);
                Tracker tracker = getTracker();
                tracker.setScreenName(screenName);
                tracker.send(new HitBuilders.ScreenViewBuilder().build());
            }
        });
    }
}
