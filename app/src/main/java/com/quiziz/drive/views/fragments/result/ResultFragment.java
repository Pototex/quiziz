package com.quiziz.drive.views.fragments.result;

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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.quiziz.drive.R;
import com.quiziz.drive.model.Chapter;
import com.quiziz.drive.model.Configuration;
import com.quiziz.drive.model.Question;
import com.quiziz.drive.model.Score;
import com.quiziz.drive.util.BillingHelper;
import com.quiziz.drive.util.QuizizConstants;
import com.quiziz.drive.views.activities.web.WebActivity;
import com.quiziz.drive.views.dialogs.premium.PremiumDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pototo on 23/03/16.
 */
public class ResultFragment extends Fragment
    implements PremiumDialog.OnPremiumDialogListener{

    private static final String TAG = "ResultFragment";
    private static final int ZERO = 0;
    private static final int FIFTY = 50;
    private static final int SEVENTY = 70;
    private static final int EIGHTY_FIVE = 85;
    public static final int ONE_HUNDRED = 100;
    private static final int FOURTY = 40;

    private List<Question> mQuestions;
    private double mScore;
    private int mAmountOfRightAnswers;
    private int mPreviousAdIndex;
    private PackageManager mPackageManager;
    private TextView mResultScoreTextView;
    private ImageView mResultScoreFaceImageView;
    private TextView mResultScoreMessageTextView;
    private TextView mResultAmountOfRightQuestionsTextView;
    private ImageView mShareFacebookImageView;
    private ImageView mShareTwitterImageView;
    private ImageView mShareWhatsappImageView;
    private TextView mResultCheckAnswerTextView;
    private TextView mResultRepeatTestAgainTextView;
    private TextView mResultRepeatIncorrectAnswerTextView;
    private TextView mResultGoToBeginTextView;
    private OnResultFragmentListener mCallback;
    private Map<String, ImageView> mImageViewMap;
    private Configuration mConfiguration;
    private List<Chapter> mChapters;
    private WebView mAdWebView;
    private List<String> mAdList;
    private int mImageFaceId;
    private BillingHelper mBillingHelper;
    private PremiumDialog mPremiumDialog;

    public interface OnResultFragmentListener{
        void addAnswerFragment(ArrayList<Question> questions, List<String> ads, int adIndex, Score score);
        void addQuestionFragment(ArrayList<Question> questions);
        void addConfigurationFragment();
        void setGoogleAnalytics(String screenName);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mCallback = (OnResultFragmentListener)activity;
        }catch(ClassCastException exception){
            throw new ClassCastException(activity.toString() + " must implement OnResultFragmentListener.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBillingHelper = BillingHelper.getInstance(getActivity());
        mQuestions = getArguments().getParcelableArrayList(QuizizConstants.QuestionBundle.QUESTIONS_BUNDLE_KEY);
        mChapters = getArguments().getParcelableArrayList(QuizizConstants.ChapterBundle.CHAPTERS_BUNDLE_KEY);
        mAdList = getArguments().getStringArrayList(QuizizConstants.AdBundle.ADS_BUNDLE_KEY);
        mPreviousAdIndex = getArguments().getInt(QuizizConstants.AdBundle.AD_INDEX_BUNDLE_KEY);
        Log.d(TAG, "Questions list size: " + mQuestions.size());
        mPackageManager = getActivity().getPackageManager();
        mConfiguration = new Configuration(getActivity());
        mPremiumDialog = new PremiumDialog();
        mPremiumDialog.onAttach(this);
        if(mCallback != null)
            mCallback.setGoogleAnalytics(QuizizConstants.GA_RESULT_TRACKER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);
        setResultScoreTextView(rootView);
        setResultScoreFaceImageView(rootView);
        setResultScoreMessageTextView(rootView);
        setResultAmountOfRightQuestionsTextView(rootView);
        setResultCheckAnswerTextView(rootView);
        setResultRepeatTestAgainTextView(rootView);
        setResultRepeatIncorrectAnswerTextView(rootView);
        setResultGoToBeginTextView(rootView);

        setAdWebView(rootView);

        mShareFacebookImageView = (ImageView)rootView.findViewById(R.id.result_share_facebook);
        mShareTwitterImageView = (ImageView)rootView.findViewById(R.id.result_share_twitter);
        mShareWhatsappImageView = (ImageView)rootView.findViewById(R.id.result_share_whatsapp);
        setImageViewMap();
        setShareImagesViewDefaultListener();
        setOnClickListenerToAllImageViews();

        return rootView;
    }

    private void setShareImagesViewDefaultListener(){
        setBookmarkImageViewDefaultListener(mShareFacebookImageView, "Facebook");
        setBookmarkImageViewDefaultListener(mShareTwitterImageView, "Twitter");
        setBookmarkImageViewDefaultListener(mShareWhatsappImageView, "Whatsapp");
    }

    private void setBookmarkImageViewDefaultListener(ImageView imageView, final String appName){
        if(imageView != null){
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Debes instalar el app de " + appName +" para compartir el contenido.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setAdWebView(View rootView) {
        mAdWebView = (WebView)rootView.findViewById(R.id.result_ad_web_view);
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

    private void setImageViewMap(){
        mImageViewMap = new HashMap();
        mImageViewMap.put(QuizizConstants.FACEBOOK_PACKAGE_NAME, mShareFacebookImageView);
        mImageViewMap.put(QuizizConstants.TWITTER_PACKAGE_NAME, mShareTwitterImageView);
        mImageViewMap.put(QuizizConstants.WHATSAPP_PACKAGE_NAME, mShareWhatsappImageView);
    }

    private void setResultGoToBeginTextView(View rootView) {
        mResultGoToBeginTextView = (TextView)rootView.findViewById(R.id.result_go_to_begin);
        if(mResultGoToBeginTextView != null){
            mResultGoToBeginTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mCallback != null)
                        mCallback.addConfigurationFragment();
                }
            });
        }
    }

    private void setResultRepeatIncorrectAnswerTextView(View rootView) {
        mResultRepeatIncorrectAnswerTextView = (TextView)rootView.findViewById(R.id.result_repeat_incorrect_answer);
        if (mResultRepeatIncorrectAnswerTextView != null) {

            if(mScore == ONE_HUNDRED)
                mResultRepeatIncorrectAnswerTextView.setVisibility(View.GONE);
            else
                mResultRepeatIncorrectAnswerTextView.setVisibility(View.VISIBLE);

            mResultRepeatIncorrectAnswerTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!mConfiguration.isPremium())
                        mPremiumDialog.show(getActivity().getFragmentManager(), PremiumDialog.PREMIUM_DIALOG_TAG);
                    else{
                        List<Question> incorrectAnswers = Question.getIncorrectAnswers(mQuestions);
                        if (mCallback != null && incorrectAnswers != null && !incorrectAnswers.isEmpty())
                            mCallback.addQuestionFragment((ArrayList<Question>) incorrectAnswers);
                    }
                }
            });
        }
    }



    private void setResultRepeatTestAgainTextView(View rootView) {
        mResultRepeatTestAgainTextView = (TextView)rootView.findViewById(R.id.result_repeat_test_again);
        if(mResultRepeatTestAgainTextView != null){
            mResultRepeatTestAgainTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mCallback != null) {
                        Collections.shuffle(mQuestions);
                        mCallback.addQuestionFragment((ArrayList<Question>) mQuestions);
                    }
                }
            });
        }
    }

    private void setResultAmountOfRightQuestionsTextView(View rootView) {
        mResultAmountOfRightQuestionsTextView = (TextView)rootView.findViewById(R.id.result_amount_of_right_questions);
        if(mResultAmountOfRightQuestionsTextView != null){
            mAmountOfRightAnswers = Question.getAmountOfRightAnswers(mQuestions);
            int size = mQuestions.size();
            String message = mAmountOfRightAnswers + " de " + size + " preguntas correctas";
            mResultAmountOfRightQuestionsTextView.setText(message);
        }
    }

    private void setResultScoreMessageTextView(View rootView) {
        mResultScoreMessageTextView = (TextView)rootView.findViewById(R.id.result_score_message);
        if(mResultScoreMessageTextView != null){
            String message = "";
            if(mScore >= ZERO && mScore < FIFTY)
                message = getString(R.string.result_0_to_50);
            else if(mScore >= FIFTY && mScore < SEVENTY)
                message = getString(R.string.result_50_to_70);
            else if(mScore >= SEVENTY && mScore < EIGHTY_FIVE)
                message = getString(R.string.result_70_to_85);

            else if(mScore >= EIGHTY_FIVE && mScore <= ONE_HUNDRED) {
                Chapter.loadChapterPreferences(mChapters, mConfiguration.getChapters(true));
                boolean areAllChaptersSelected = Chapter.areAllChaptersSelected(mChapters);

                if(!areAllChaptersSelected)
                    message = getString(R.string.result_85_to_100_some_chapters);
                else if(mAmountOfRightAnswers < FOURTY)
                    message = getString(R.string.result_85_to_100_less_40_questions);
                else if(areAllChaptersSelected && mAmountOfRightAnswers > FOURTY)
                    message = getString(R.string.result_85_to_100_all_chapters_more_40_questions);
            }
            mResultScoreMessageTextView.setText(message);
        }
    }

    private void setResultScoreFaceImageView(View rootView) {
        mResultScoreFaceImageView = (ImageView)rootView.findViewById(R.id.result_score_face);
        if(mResultScoreFaceImageView != null){

            mImageFaceId = 0;
            if(mScore >= ZERO && mScore < FIFTY)
                mImageFaceId = R.drawable.img_result_0to50;
            else if(mScore >= FIFTY && mScore < SEVENTY)
                mImageFaceId = R.drawable.img_result_50to70;
            else if(mScore >= SEVENTY && mScore < EIGHTY_FIVE)
                mImageFaceId = R.drawable.img_result_70to85;
            else if(mScore >= EIGHTY_FIVE && mScore <= ONE_HUNDRED)
                mImageFaceId = R.drawable.img_result_85to100;

            mResultScoreFaceImageView.setImageResource(mImageFaceId);
        }
    }

    private void setResultScoreTextView(View rootView) {
        mResultScoreTextView = (TextView)rootView.findViewById(R.id.result_score);
        if (mResultScoreTextView != null) {
            mScore = Question.getScore(mQuestions);
            mResultScoreTextView.setText(String.valueOf((int)mScore));
        }
    }

    private void setResultCheckAnswerTextView(View rootView) {
        mResultCheckAnswerTextView = (TextView)rootView.findViewById(R.id.result_check_answer);
        if(mResultCheckAnswerTextView != null && mCallback != null){
            mResultCheckAnswerTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!mConfiguration.isPremium())
                        mPremiumDialog.show(getActivity().getFragmentManager(), PremiumDialog.PREMIUM_DIALOG_TAG);
                    else {
                        Score score = new Score();
                        score.setScore(mResultScoreTextView.getText().toString());
                        score.setFaceImageId(mImageFaceId);
                        score.setScoreMessage(mResultScoreMessageTextView.getText().toString());
                        score.setRightQuestions(mResultAmountOfRightQuestionsTextView.getText().toString());
                        mCallback.addAnswerFragment((ArrayList<Question>) mQuestions, mAdList, mPreviousAdIndex, score);
                    }
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

    @Override
    public void onSubscribe() {
        mBillingHelper.launchSubscriptionMonthPurchaseFlow(getActivity(), BillingHelper.nextPayload());
    }

    @Override
    public void onBenefits() {
        Intent webIntent = new Intent(getActivity(), WebActivity.class);
        webIntent.putExtra(WebActivity.URL_EXTRA_PARAM, QuizizConstants.BENEFITS_URL);
        startActivity(webIntent);
    }

    @Override
    public void onCancel() {}
}
