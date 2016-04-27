package com.quiziz.drive.views.fragments.question;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quiziz.drive.R;
import com.quiziz.drive.model.Configuration;
import com.quiziz.drive.model.Question;
import com.quiziz.drive.util.QuizizConstants;
import com.quiziz.drive.views.dialogs.exit.ExitDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by pototo on 10/03/16.
 */
public class QuestionFragment extends Fragment
    implements ExitDialog.OnExitDialogListener{

    private static final String TAG = "questionFragment";
    private static final int FIRST_ELEMENT = 0;
    private static final int ONE_ITEM = 1;
    private static final int COUNT_DOWN_INTERVAL = 1000;
    private static final int TIME_DELAY = 500;
    private static final int PAGINATE_VALUE = 5;
    private static final int TIMEOUT = 5;

    private static CountDownTimer mCountDownTimer;
    private List<Question> mQuestions;
    private int mQuestionIndex;
    private Question mQuestion;
    private ImageView mQuestionImageView, mIconResultQuestionImageView;
    private TextView mQuestionTextView;
    private TextView mOptionATextView;
    private TextView mOptionBTextView;
    private TextView mOptionCTextView;
    private TextView mCountDownTimerTextView;
    private TextView mNextQuestionTextView;
    private TextView mCancelTestTextView;
    private TextView mMessageResultQuestionTextView;
    private TextView mQuestionOfTotalQuestionsTextView;
    private TextView mPlayTextView;
    private ImageView mPlayImageView;
    private RelativeLayout mResultQuestionRelativeLayout;
    private OnQuestionFragmentListener mCallback;
    private Configuration mConfiguration;
    private boolean isNextQuestionEnable;
    private View mRootView;
    private WebView mAdWebView;
    private int mAdIndex;
    private boolean isRunning;
    private long mTime;
    private Activity mActivity;
    private ExitDialog mExitDialog;
    private boolean isNewAd;

    private List<Integer> mTextViewIds = new ArrayList<Integer>(){
        {
            add(R.id.option_a);
            add(R.id.option_b);
            add(R.id.option_c);
        }
    };

    private List<String> mAdList = new ArrayList<String>() {
        {
            add(QuizizConstants.AD_ONE);
            add(QuizizConstants.AD_TWO);
            add(QuizizConstants.AD_THREE);
            add(QuizizConstants.AD_FOURTH);
            add(QuizizConstants.AD_FIVE);
        }
    };


    public interface OnQuestionFragmentListener{
        void addResultFragment(ArrayList<Question> questions, List<String> ads, int adIndex);
        void addConfigurationFragment();
        void setGoogleAnalytics(String screenName);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mActivity = activity;
            mCallback = (OnQuestionFragmentListener)activity;
        }catch(ClassCastException exception){
            throw new ClassCastException(activity.toString() + " must implement OnQuestionFragmentListener.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelCountDownTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mTime > 0)
            startCountDownTimer(mTime);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConfiguration = new Configuration(getActivity());
        mQuestions = getArguments().getParcelableArrayList(QuizizConstants.QuestionBundle.QUESTIONS_BUNDLE_KEY);
        Log.d(TAG, "Questions list size: " + mQuestions.size());
        mExitDialog = new ExitDialog();
        mExitDialog.onAttach(this);
        if(mCallback != null)
            mCallback.setGoogleAnalytics(QuizizConstants.GA_QUESTION_TRACKER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_question, container, false);
        if(mRootView != null){
            Collections.shuffle(mAdList);
            mQuestionImageView = (ImageView)mRootView.findViewById(R.id.question_image);
            mQuestionTextView = (TextView)mRootView.findViewById(R.id.question_text);
            mResultQuestionRelativeLayout = (RelativeLayout)mRootView.findViewById(R.id.result_question);
            mIconResultQuestionImageView = (ImageView)mRootView.findViewById(R.id.icon_result_question);
            mMessageResultQuestionTextView = (TextView)mRootView.findViewById(R.id.message_result_question);
            mQuestionOfTotalQuestionsTextView = (TextView)mRootView.findViewById(R.id.question_of_total_questions);
            mAdWebView = (WebView)mRootView.findViewById(R.id.question_ad_web_view);
            mCountDownTimerTextView = (TextView)mRootView.findViewById(R.id.count_down_timer);
            setPlayTextView();
            randomOptionsOrder();
            setNextQuestionTextView();
            setCancelTestTextView();
        }
        return mRootView;
    }

    private void setPlayTextView() {
        mPlayTextView = (TextView)mRootView.findViewById(R.id.question_play);
        mPlayImageView = (ImageView)mRootView.findViewById(R.id.question_play_image);
        if (mPlayTextView != null && mPlayTextView != null) {
           mPlayTextView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if(isRunning) {
                       mPlayTextView.setText(getString(R.string.play_question));
                       mPlayImageView.setImageResource(R.drawable.btn_play);
                       cancelCountDownTimer();
                   } else {
                       mPlayTextView.setText(getString(R.string.stop_question));
                       mPlayImageView.setImageResource(R.drawable.btn_pause);
                       startCountDownTimer(mTime);
                   }
               }
           });
        }
    }

    private void randomOptionsOrder() {
        Collections.shuffle(mTextViewIds);
        setOptionATextView(mRootView, mTextViewIds.get(0));
        setOptionBTextView(mRootView, mTextViewIds.get(1));
        setOptionCTextView(mRootView, mTextViewIds.get(2));

    }

    private void setAdWebView() {
        randomAdsOrder();
        if(mAdWebView != null && isNewAd) {
            Log.d(TAG, "Ad's url to show: " +  mAdList.get(mAdIndex));
            mAdWebView.loadUrl(mAdList.get(mAdIndex));
        }
    }

    private void randomAdsOrder() {
        int index = mQuestionIndex + 1;
        Log.d(TAG, "Question index: " + index);

        if(mQuestionIndex % PAGINATE_VALUE == 0) {
            Log.d(TAG, "Show next ad");
            mAdIndex++;
            isNewAd = true;
        } else {
            isNewAd = false;
        }

        if (mAdIndex > QuizizConstants.MAX_AD_INDEX) {
            mAdIndex = 0;
            Log.d(TAG, "Random ad list");
            Collections.shuffle(mAdList);
            isNewAd = true;
        }
    }

    private void setOptionATextView(View rootView, int textViewId) {
        mOptionATextView = (TextView)rootView.findViewById(textViewId);
        if(mOptionATextView != null) {
            mOptionATextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enableNextQuestionTextView();
                    mQuestion.checkIfOptionAIsTheRightAnswer();
                    selectCurrentTextView(mOptionATextView, mOptionBTextView, mOptionCTextView);
                    mQuestion.setUserChooses(Question.UserChooses.A);
                }
            });
        }
    }

    private void setOptionBTextView(View rootView, int textViewId){
        mOptionBTextView = (TextView)rootView.findViewById(textViewId);
        if(mOptionBTextView != null) {
            mOptionBTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enableNextQuestionTextView();
                    mQuestion.checkIfOptionBIsTheRightAnswer();
                    selectCurrentTextView(mOptionBTextView, mOptionATextView, mOptionCTextView);
                    mQuestion.setUserChooses(Question.UserChooses.B);
                }
            });
        }
    }

    private void setOptionCTextView(View rootView, int textViewId){
        mOptionCTextView = (TextView)rootView.findViewById(textViewId);
        if(mOptionCTextView != null) {
            mOptionCTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enableNextQuestionTextView();
                    mQuestion.checkIfOptionCIsTheRightAnswer();
                    selectCurrentTextView(mOptionCTextView, mOptionBTextView, mOptionATextView);
                    mQuestion.setUserChooses(Question.UserChooses.C);
                }
            });
        }
    }

    private void enableNextQuestionTextView() {
        isNextQuestionEnable = true;
        if (mNextQuestionTextView != null) {
           mNextQuestionTextView.setBackgroundResource(R.drawable.btn_next_active);
        }
    }


    public void selectCurrentTextView(TextView selectTextView, TextView ... deselectTextViews){
        if(selectTextView != null) {
            selectTextView.setBackgroundResource(R.drawable.bg_option_selected);
            int color = getResources().getColor(R.color.font_option_selected);
            selectTextView.setTextColor(color);
        }
        if(deselectTextViews != null) {
            for (TextView deselectTextView : deselectTextViews) {
                deselectTextView.setBackgroundResource(R.drawable.bg_option_deselected);
                int color = getResources().getColor(R.color.font_option_deselected);
                deselectTextView.setTextColor(color);
            }
        }
    }

    private void setNextQuestionTextView() {
        mNextQuestionTextView = (TextView)mRootView.findViewById(R.id.next_question);
        if(mNextQuestionTextView != null) {
            mNextQuestionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isNextQuestionEnable)
                        showNextQuestion();
                }
            });
        }
    }

    private void setCancelTestTextView() {
        mCancelTestTextView = (TextView)mRootView.findViewById(R.id.cancel_test);
        if(mCancelTestTextView != null) {
            mCancelTestTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mExitDialog != null){
                        cancelCountDownTimer();
                        mExitDialog.show(mActivity.getFragmentManager(), ExitDialog.EXIT_DIALOG_TAG);
                    }
                }
            });
        }
    }

    private void addResultFragment() {
        if(mCallback != null) {
            cancelCountDownTimer();
            mCallback.addResultFragment((ArrayList) mQuestions, mAdList, mAdIndex);
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mQuestionIndex = FIRST_ELEMENT;
        setUpQuestion();
    }

    private void setUpQuestion() {
        if(mQuestions != null && !mQuestions.isEmpty() && mQuestionIndex < mQuestions.size()) {
            setAdWebView();
            disableNextQuestionTextView();
            mQuestion = mQuestions.get(mQuestionIndex);
            setUpQuestionImageView();
            if (mQuestionTextView != null)
                mQuestionTextView.setText(Html.fromHtml(mQuestion.getQuestion()));
            setUpQuestionOfTotalQuestions();
            setUpOption(mOptionATextView, mQuestion.getOptionA());
            setUpOption(mOptionBTextView, mQuestion.getOptionB());
            setUpOption(mOptionCTextView, mQuestion.getOptionC());
            mCountDownTimerTextView.setBackgroundResource(R.drawable.icon_timer_on);
            startCountDownTimer(mConfiguration.getTime());
            resetPlayTextView();

        }
    }

    private void resetPlayTextView() {
        if(mPlayTextView != null && mPlayImageView != null) {
            mPlayTextView.setText(getString(R.string.stop_question));
            mPlayImageView.setImageResource(R.drawable.btn_pause);
        }
    }

    private void disableNextQuestionTextView() {
        isNextQuestionEnable = false;
        if(mNextQuestionTextView != null){
            mNextQuestionTextView.setBackgroundResource(R.drawable.btn_next_unactive);
        }
    }

    private void setUpQuestionOfTotalQuestions(){
        if(mQuestionOfTotalQuestionsTextView == null)
            return;
        int totalQuestions = mQuestions.size();
        int question = mQuestionIndex + 1;
        mQuestionOfTotalQuestionsTextView.setText(question + " de " + totalQuestions);
    }

    private void setUpQuestionImageView() {
        if(mQuestionImageView != null && mQuestion.hasImage()) {
            mQuestionImageView.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                .load(mQuestion.getImageUrl())
                    .placeholder(R.drawable.icon_user_default)
                    .error(R.drawable.icon_user_default)
                .into(mQuestionImageView);
        }else
            mQuestionImageView.setVisibility(View.GONE);
    }

    private void setUpOption(TextView textView, List<String> optionList){
        if(textView != null && optionList != null && optionList.size() >= ONE_ITEM) {
            String optionText = optionList.get(FIRST_ELEMENT);
            if(optionText == null || optionText.isEmpty())
                textView.setVisibility(View.GONE);
            else {
                textView.setVisibility(View.VISIBLE);
                textView.setText(optionList.get(FIRST_ELEMENT));
            }
        }
    }

    private void startCountDownTimer(long futureTime){
        cancelCountDownTimer();
        long maxTime = (futureTime + 1) * 1000;
        mCountDownTimer = new CountDownTimer(maxTime, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                isRunning = true;
                mTime = millisUntilFinished / 1000;
                mCountDownTimerTextView.setText(Long.toString(mTime));
                if(mTime <= TIMEOUT)
                    mCountDownTimerTextView.setBackgroundResource(R.drawable.icon_timer_off);
            }

            @Override
            public void onFinish() {
                isRunning = false;
                mQuestion.setSpendTime(true);
                showSpendTimeMessage();
                showNextQuestion();
            }
        };
        mCountDownTimer.start();
    }

    private void showSpendTimeMessage(){
        if(mResultQuestionRelativeLayout != null) {
            mResultQuestionRelativeLayout.setVisibility(View.VISIBLE);
            mIconResultQuestionImageView.setImageResource(R.drawable.icon_time_large);
            mMessageResultQuestionTextView.setText(getString(R.string.spend_time));
        }
    }

    private void showNextQuestion() {
        showAnswerQuestion();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mQuestionIndex++;
                if(mResultQuestionRelativeLayout != null)
                    mResultQuestionRelativeLayout.setVisibility(View.GONE);
                if(mQuestionIndex < mQuestions.size()) {
                    resetSelectedOption(mOptionATextView, mOptionBTextView, mOptionCTextView);
                    setUpNextQuestion();
                }else
                    addResultFragment();
            }
        }, TIME_DELAY);
    }

    public void resetSelectedOption(TextView ... optionsTextViews){
        if(optionsTextViews != null) {
            for (TextView optionTextView : optionsTextViews) {
                optionTextView.setBackgroundResource(R.drawable.bg_option_deselected);
                optionTextView.setTextColor(Color.parseColor("#000000"));
            }
        }
    }

    private void showAnswerQuestion(){
        if(mResultQuestionRelativeLayout != null && isNextQuestionEnable) {
            mResultQuestionRelativeLayout.setVisibility(View.VISIBLE);
            if(mQuestion.isAnswerRight()) {
                mIconResultQuestionImageView.setImageResource(R.drawable.icon_check_large);
                mMessageResultQuestionTextView.setText(getString(R.string.answer_correct));
            }else{
                mIconResultQuestionImageView.setImageResource(R.drawable.icon_wrong_large);
                mMessageResultQuestionTextView.setText(getString(R.string.answer_incorrect));
            }
        }
    }

    private void setUpNextQuestion() {
        randomOptionsOrder();
        setUpQuestion();
    }

    private void cancelCountDownTimer(){
        if(mCountDownTimer != null){
            isRunning = false;
            mCountDownTimer.cancel();
            Log.d(TAG, "Canceling count down timer!!!");
        }
    }

    @Override
    public void onExit() {
        if(mCallback != null) {
            cancelCountDownTimer();
            mCallback.addConfigurationFragment();
        }
    }

    @Override
    public void onCancel() {
        if(mTime > 0)
            startCountDownTimer(mTime);
    }
}