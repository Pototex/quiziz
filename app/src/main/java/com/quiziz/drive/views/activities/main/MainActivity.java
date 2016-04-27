package com.quiziz.drive.views.activities.main;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.quiziz.drive.QuizizApplication;
import com.quiziz.drive.R;
import com.quiziz.drive.model.Chapter;
import com.quiziz.drive.model.Configuration;
import com.quiziz.drive.model.Question;
import com.quiziz.drive.model.Score;
import com.quiziz.drive.util.BillingHelper;
import com.quiziz.drive.util.InternetConnectionManager;
import com.quiziz.drive.util.QuizizConstants;
import com.quiziz.drive.views.fragments.answer.AnswerFragment;
import com.quiziz.drive.views.fragments.chapter.ChapterFragment;
import com.quiziz.drive.views.fragments.configuration.ConfigurationFragment;
import com.quiziz.drive.views.fragments.question.QuestionFragment;
import com.quiziz.drive.views.fragments.result.ResultFragment;
import com.quiziz.drive.views.fragments.setup.SetupFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements
            QuestionFragment.OnQuestionFragmentListener,
            ResultFragment.OnResultFragmentListener,
            ConfigurationFragment.OnConfigurationFragmentListener,
            SetupFragment.OnSetupFragmentListener,
            AnswerFragment.OnAnswerFragmentListener,
            ChapterFragment.OnChapterFragmentListener{

    private final static String TAG = "MainActivity";
    private QuestionFragment mQuestionFragment;
    private AnswerFragment mAnswerFragment;
    private ResultFragment mResultFragment;
    private ConfigurationFragment mConfigurationFragment;
    private ChapterFragment mChapterFragment;
    private SetupFragment mSetupFragment;
    private List<Chapter> mChapters;
    private BillingHelper mBillingHelper;
    private Configuration mConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBillingHelper = BillingHelper.createInstance(this);
        setContentView(R.layout.activity_main);
        mChapters = getIntent().getParcelableArrayListExtra(QuizizConstants.ChapterBundle.CHAPTERS_BUNDLE_KEY);
        mConfiguration = new Configuration(getApplication());
        addConfigurationFragment();
        setUpInAppBilling();
    }

    private void setUpInAppBilling(){
        if(InternetConnectionManager.isOnline(this))
            mBillingHelper.setUpInAppBillingToGetProductDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    public void addConfigurationFragment() {
        mConfigurationFragment = new ConfigurationFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(QuizizConstants.ChapterBundle.CHAPTERS_BUNDLE_KEY, (ArrayList)mChapters);

        mConfigurationFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main, mConfigurationFragment);
        transaction.commit();
    }

    @Override
    public void addSetupFragment() {
        mSetupFragment = new SetupFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main, mSetupFragment);
        transaction.addToBackStack(QuizizConstants.SECOND_LEVEL);
        transaction.commit();
    }

    @Override
    public void addChapterFragment() {
        mChapterFragment = new ChapterFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(QuizizConstants.ChapterBundle.CHAPTERS_BUNDLE_KEY, (ArrayList)mChapters);

        mChapterFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main, mChapterFragment);
        transaction.addToBackStack(QuizizConstants.SECOND_LEVEL);
        transaction.commit();
    }

    @Override
    public void addQuestionFragment(ArrayList<Question> questions){
        mQuestionFragment = new QuestionFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(QuizizConstants.QuestionBundle.QUESTIONS_BUNDLE_KEY, questions);

        mQuestionFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main, mQuestionFragment);
        transaction.commit();
    }

    @Override
    public void addAnswerFragment(ArrayList<Question> questions, List<String> ads, int adIndex, Score score) {
        mAnswerFragment = new AnswerFragment();

        Question question = new Question();
        question.setResult(true);
        questions.add(question);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(QuizizConstants.QuestionBundle.QUESTIONS_BUNDLE_KEY, questions);
        bundle.putStringArrayList(QuizizConstants.AdBundle.ADS_BUNDLE_KEY, (ArrayList<String>)ads);
        bundle.putInt(QuizizConstants.AdBundle.AD_INDEX_BUNDLE_KEY, adIndex);
        bundle.putParcelable(QuizizConstants.AnswerBundle.SCORE_BUNDLE_KEY, score);

        mAnswerFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main, mAnswerFragment);
        transaction.addToBackStack(QuizizConstants.SECOND_LEVEL);
        transaction.commit();
    }

    @Override
    public void addResultFragment(ArrayList<Question> questions, List<String> ads, int adIndex) {
        mResultFragment = new ResultFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(QuizizConstants.QuestionBundle.QUESTIONS_BUNDLE_KEY, questions);
        bundle.putParcelableArrayList(QuizizConstants.ChapterBundle.CHAPTERS_BUNDLE_KEY, (ArrayList)mChapters);
        bundle.putStringArrayList(QuizizConstants.AdBundle.ADS_BUNDLE_KEY, (ArrayList<String>)ads);
        bundle.putInt(QuizizConstants.AdBundle.AD_INDEX_BUNDLE_KEY, adIndex);

        mResultFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main, mResultFragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBillingHelper != null)
            mBillingHelper.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!mBillingHelper.getIabHelper().handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
        else
            Log.d(TAG, "onActivityResult handled by iabHelper.");
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentManager().findFragmentByTag(QuizizConstants.SECOND_LEVEL);
        if(fragment != null)
            getFragmentManager().popBackStack(QuizizConstants.SECOND_LEVEL, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        else
            super.onBackPressed();
    }

    @Override
    public void exit() {
        List<Integer> chapters = mConfiguration.getChapters(true);
        if(chapters == null || chapters.isEmpty())
            Toast.makeText(this, getString(R.string.no_selected_chapters), Toast.LENGTH_SHORT).show();
        else {
            int count = getFragmentManager().getBackStackEntryCount();
            Log.d(TAG, "Pop back stack count: " + count);
            if (count == 0) {
                super.onBackPressed();
            } else {
                getFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void setGoogleAnalytics(String screenName) {
        QuizizApplication application = (QuizizApplication)getApplication();
        if(application != null)
            application.sendTrackerToGoogleAnalytics(screenName);
    }
}
