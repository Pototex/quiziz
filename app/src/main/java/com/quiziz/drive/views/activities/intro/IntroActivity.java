package com.quiziz.drive.views.activities.intro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.quiziz.drive.QuizizApplication;
import com.quiziz.drive.R;
import com.quiziz.drive.model.Chapter;
import com.quiziz.drive.util.InternetConnectionManager;
import com.quiziz.drive.util.QuizizConstants;
import com.quiziz.drive.views.activities.main.MainActivity;
import com.quiziz.drive.views.dialogs.intro.IntroDialog;
import com.quiziz.drive.views.dialogs.version.VersionDialog;

import java.util.ArrayList;

/**
 * Created by pototo on 17/03/16.
 */
public class IntroActivity extends AppCompatActivity
    implements Chapter.OnChapterListener{
    private TextView mGetIntoTextView;
    private IntroDialog mIntroDialog;
    private VersionDialog mVersionDialog;
    private Chapter mChapter;
    private RelativeLayout mCircleRelativeLayout;
    private String mAppDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        setGetIntoTextView();
        setCircleRelativeLayout();
        mIntroDialog = new IntroDialog();

        mChapter = new Chapter();
        mChapter.attach(this);

        mAppDetail = getIntent().getStringExtra(QuizizConstants.IntroBundle.INTRO_BUNDLE_KEY);
        mVersionDialog = new VersionDialog();
        mVersionDialog.setAppDetail(mAppDetail);

        setGoogleAnalytics();
    }

    private void setGoogleAnalytics() {
        QuizizApplication application = (QuizizApplication)getApplication();
        if(application != null)
            application.sendTrackerToGoogleAnalytics(QuizizConstants.GA_INTRO_TRACKER);
    }

    private void setCircleRelativeLayout() {
        mCircleRelativeLayout = (RelativeLayout)findViewById(R.id.circle_splash);
        if (mCircleRelativeLayout != null) {
            mCircleRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mVersionDialog != null)
                        mVersionDialog.show(getFragmentManager(), VersionDialog.VERSION_DIALOG_TAG);
                }
            });
        }
    }

    private void setGetIntoTextView() {
        mGetIntoTextView = (TextView)findViewById(R.id.get_into_splash);
        if(mGetIntoTextView != null) {
            mGetIntoTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGetIntoTextView.setEnabled(false);
                    if (InternetConnectionManager.isOnline(getApplication())) {
                        if (mIntroDialog != null)
                            mIntroDialog.show(getFragmentManager(), IntroDialog.INTRO_DIALOG_TAG);
                        mChapter.getChaptersInBackground();
                    } else {
                        mGetIntoTextView.setEnabled(true);
                        Toast.makeText(getApplication(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onPostExecute(ArrayList<Chapter> chapters) {
        if(mIntroDialog != null)
            mIntroDialog.dismiss();
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putParcelableArrayListExtra(QuizizConstants.ChapterBundle.CHAPTERS_BUNDLE_KEY, chapters);
        startActivity(mainIntent);
        finish();
    }
}
