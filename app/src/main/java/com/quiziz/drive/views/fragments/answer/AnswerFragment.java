package com.quiziz.drive.views.fragments.answer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.quiziz.drive.R;
import com.quiziz.drive.model.Question;
import com.quiziz.drive.model.Score;
import com.quiziz.drive.util.QuizizConstants;
import com.quiziz.drive.views.fragments.answer.adapter.AnswerArrayAdapter;
import com.quiziz.drive.views.fragments.result.ResultFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by pototo on 15/03/16.
 */
public class AnswerFragment extends Fragment
    implements AnswerArrayAdapter.OnAnswerArrayAdapterListener{

    private static final String TAG = "AnswerFragment";

    private List<Question> mQuestions;
    private ListView mAnswerListView;
    private List<String> mAdList;
    private int mPreviousAdIndex;
    private WebView mAdWebView;
    private PackageManager mPackageManager;
    private Map<String, ImageView> mImageViewMap;
    private Score mScore;
    private OnAnswerFragmentListener mCallback;

    public interface OnAnswerFragmentListener{
        void addQuestionFragment(ArrayList<Question> questions);
        void addConfigurationFragment();
        void setGoogleAnalytics(String screenName);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mCallback = (OnAnswerFragmentListener)activity;
        }catch(ClassCastException exception){
            throw new ClassCastException(activity.toString() + " must implement OnResultFragmentListener.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQuestions = getArguments().getParcelableArrayList(QuizizConstants.QuestionBundle.QUESTIONS_BUNDLE_KEY);
        mAdList = getArguments().getStringArrayList(QuizizConstants.AdBundle.ADS_BUNDLE_KEY);
        mPreviousAdIndex = getArguments().getInt(QuizizConstants.AdBundle.AD_INDEX_BUNDLE_KEY);
        mScore = getArguments().getParcelable(QuizizConstants.AnswerBundle.SCORE_BUNDLE_KEY);
        mPackageManager = getActivity().getPackageManager();
        Log.d(TAG, "Questions list size: " + mQuestions.size());
        if(mCallback != null)
            mCallback.setGoogleAnalytics(QuizizConstants.GA_ANSWER_TRACKER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_answer, container, false);
        if(rootView != null){
            setAdWebView(rootView);
            setAnswerListView(rootView);
        }
        return rootView;
    }

    private void setAdWebView(View rootView) {
        mAdWebView = (WebView)rootView.findViewById(R.id.answer_ad_web_view);
        randomAdsOrder();
        if(mAdWebView != null) {
            Log.d(TAG, "Ad's url to show: " +  mAdList.get(mPreviousAdIndex));
            mAdWebView.loadUrl(mAdList.get(mPreviousAdIndex));
        }
    }

    private void randomAdsOrder() {
        mPreviousAdIndex++;
        if (mPreviousAdIndex > QuizizConstants.MAX_AD_INDEX) {
            mPreviousAdIndex = 0;
            Log.d(TAG, "Random ad list");
            if(mAdList != null)
                Collections.shuffle(mAdList);
        }
    }

    private void setAnswerListView(View rootView){
        mAnswerListView = (ListView)rootView.findViewById(R.id.answer_list_view);
        if(mAnswerListView == null || mQuestions == null)
            return;
        AnswerArrayAdapter adapter = new AnswerArrayAdapter(getActivity(), mQuestions);
        adapter.onAttach(this);
        mAnswerListView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setResultScoreTextView(TextView textView) {
        textView.setText(mScore.getScore());
    }

    @Override
    public void setResultScoreFaceImageView(ImageView imageView) {
        imageView.setImageResource(mScore.getFaceImageId());
    }

    @Override
    public void setResultScoreMessageTextView(TextView textView) {
        textView.setText(mScore.getScoreMessage());
    }

    @Override
    public void setResultAmountOfRightQuestionsTextView(TextView textView) {
        textView.setText(mScore.getRightQuestions());
    }

    @Override
    public void setGoToBeginTextView(TextView goToBeginTextView) {
        if(goToBeginTextView != null){
            goToBeginTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mCallback != null)
                        mCallback.addConfigurationFragment();
                }
            });
        }
    }

    @Override
    public void setRepeatIncorrectAnswerTextView(TextView repeatIncorrectAnswerTextView) {
        if (repeatIncorrectAnswerTextView != null) {

            int score = Integer.valueOf(mScore.getScore());
            if(score == ResultFragment.ONE_HUNDRED)
                repeatIncorrectAnswerTextView.setVisibility(View.GONE);
            else
                repeatIncorrectAnswerTextView.setVisibility(View.VISIBLE);

            repeatIncorrectAnswerTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeLastItem();
                    List<Question> incorrectAnswers = Question.getIncorrectAnswers(mQuestions);
                    if (mCallback != null && incorrectAnswers != null && !incorrectAnswers.isEmpty())
                        mCallback.addQuestionFragment((ArrayList<Question>) incorrectAnswers);
                }
            });
        }
    }

    @Override
    public void setRepeatTestAgainTextView(TextView repeatTestAgainTextView) {
        if(repeatTestAgainTextView != null){
            repeatTestAgainTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeLastItem();
                    if(mCallback != null)
                        mCallback.addQuestionFragment((ArrayList<Question>)mQuestions);
                }
            });
        }
    }


    private void setOnClickListenerToAllImageViews(){
        List<ResolveInfo> apps = getAvailableAppsToShareInfo();
        for(ResolveInfo resolveInfo : apps){
            final String packageName = resolveInfo.activityInfo.packageName;
            ImageView imageView = mImageViewMap.get(packageName);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(packageName.contains("facebook"))
                        startFacebookIntent();
                    else
                        startDefaultShareIntent(packageName);
                }
            });
        }
    }

    @Override
    public void setImageViewMap(ImageView facebook, ImageView twitter, ImageView whatsapp){
        mImageViewMap = new HashMap();
        mImageViewMap.put(QuizizConstants.FACEBOOK_PACKAGE_NAME, facebook);
        mImageViewMap.put(QuizizConstants.TWITTER_PACKAGE_NAME, twitter);
        mImageViewMap.put(QuizizConstants.WHATSAPP_PACKAGE_NAME, whatsapp);
        setOnClickListenerToAllImageViews();
    }

    private List<ResolveInfo> getAvailableAppsToShareInfo() {
        List<ResolveInfo> apps = new ArrayList<ResolveInfo>();
        List<ResolveInfo> tmpList = mPackageManager.queryIntentActivities(buildSimpleIntent(), PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo resolveInfo : tmpList)
            if(QuizizConstants.APPS.contains(resolveInfo.activityInfo.packageName))
                apps.add(resolveInfo);
        return apps;
    }

    private Intent buildSimpleIntent(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType(QuizizConstants.SHARE_TEXT_PLAIN_INTENT);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, QuizizConstants.SUBJECT_TEXT);
        shareIntent.putExtra(Intent.EXTRA_TEXT, QuizizConstants.SHARE_TEXT);
        return shareIntent;
    }

    public void startFacebookIntent() {
        ShareDialog dialog = new ShareDialog(getActivity());
        if(ShareDialog.canShow(ShareLinkContent.class)){
            ShareLinkContent.Builder builder = new ShareLinkContent.Builder()
                    .setContentTitle(getString(R.string.app_name))
                    .setContentDescription((Html.fromHtml(QuizizConstants.SHARE_TEXT)).toString())
                    .setContentUrl(Uri.parse(QuizizConstants.SHARE_URL));
            ShareLinkContent content = builder.build();
            dialog.show(content);
        }
    }

    public void startDefaultShareIntent(String packageName){
        Intent intent = buildSimpleIntent();
        intent.setPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void removeLastItem() {
        int size = mQuestions.size();
        int lastIndex = size - 1;
        Iterator<Question> iterator = mQuestions.iterator();
        int index = 0;
        while(iterator.hasNext()){
            iterator.next();
            if (index == lastIndex)
                iterator.remove();
            index++;
        }
    }
}
