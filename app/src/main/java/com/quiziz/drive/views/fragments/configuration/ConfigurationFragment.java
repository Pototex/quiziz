package com.quiziz.drive.views.fragments.configuration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.quiziz.drive.QuizizApplication;
import com.quiziz.drive.R;
import com.quiziz.drive.model.Chapter;
import com.quiziz.drive.model.Configuration;
import com.quiziz.drive.model.Question;
import com.quiziz.drive.util.BillingHelper;
import com.quiziz.drive.util.InternetConnectionManager;
import com.quiziz.drive.util.QuizizConstants;
import com.quiziz.drive.views.activities.web.WebActivity;
import com.quiziz.drive.views.dialogs.generate.GenerateDialog;
import com.quiziz.drive.views.dialogs.premium.PremiumDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pototo on 23/03/16.
 */
public class ConfigurationFragment extends Fragment
    implements
        Question.OnQuestionListener,
        PremiumDialog.OnPremiumDialogListener {

    private static final String BEGINNER = "Principiante";
    private static final String INTERMEDIATE = "Intermedio";
    private static final int INTERMEDIATE_CODE = 1;
    private static final String ADVANCED = "Avanzado";
    private static final int ADVANCED_CODE = 2;
    private static final String SECONDS_LABEL = " segundos";
    private static final int DIFFICULTY_DEFAULT_VALUE = 1;
    private static final int QUESTION_DEFAULT_VALUE = 20;
    private static final int TIME_DEFAULT_VALUE = 30;
    public static final int QUESTION_INTERVAL = 5;
    private static final int TIME_INTERVAL = 5;

    private Configuration mConfiguration;
    private OnConfigurationFragmentListener mCallback;
    private SeekBar mQuestionSeekBar;
    private SeekBar mDifficultySeekBar;
    private SeekBar mTimeSeekBar;
    private TextView mQuestionSelectedTextView;
    private TextView mDifficultySelectedTextView;
    private TextView mTimeSelectedTextView;
    private TextView mGenerateTextView;
    private TextView mEditChapters;
    private ImageView mSetupImageView;
    private ImageView mWelcomeImageView;
    private List<Question> mQuestions;
    private List<Chapter> mChapters;
    private Question mQuestion;
    private GenerateDialog mGenerateDialog;
    private PremiumDialog mPremiumDiualog;
    private Activity mActivity;
    private BillingHelper mBillingHelper;
    private View mRootView;

    public interface OnConfigurationFragmentListener{
        void addQuestionFragment(ArrayList<Question> questions);
        void addChapterFragment();
        void addSetupFragment();
        void setGoogleAnalytics(String screenName);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mActivity = activity;
            mCallback = (OnConfigurationFragmentListener)activity;
        }catch(ClassCastException exception){
            throw new ClassCastException(activity.toString() + " must implement OnConfigurationFragmentListener.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBillingHelper = BillingHelper.getInstance(getActivity());
        mChapters = getArguments().getParcelableArrayList(QuizizConstants.ChapterBundle.CHAPTERS_BUNDLE_KEY);
        mGenerateDialog = new GenerateDialog();
        mQuestion = new Question();
        mQuestion.attach(this);
        mPremiumDiualog = new PremiumDialog();
        mPremiumDiualog.onAttach(this);
        mConfiguration = new Configuration(getActivity());
        setConfigurationDefaultValues();
        if(mCallback != null)
            mCallback.setGoogleAnalytics(QuizizConstants.GA_CONFIGURATION_TRACKER);
    }

    private void setConfigurationDefaultValues() {
        if (mConfiguration.isLoadFirstTime()) {
            mConfiguration.setQuestions(QUESTION_DEFAULT_VALUE);
            mConfiguration.setTime(TIME_DEFAULT_VALUE);
            mConfiguration.setDifficulty(DIFFICULTY_DEFAULT_VALUE);
            Chapter.selectAllChapters(mChapters);
            saveChapterPreferences();
            mConfiguration.setLoadFirstTime(false);
        }
    }

    private void saveChapterPreferences() {
        List<Integer> selectedChapters = Chapter.getSelectedChapters(mChapters);
        mConfiguration.setChapters(selectedChapters, true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_configuration, container, false);

        setSetupImageView();
        setWelcomeImageView();
        setQuestionSelectedTextView();
        setDifficultySelectedTextView();
        setTimeSelectedTextView();
        setEditChaptersTextView();
        setGenerateTextView();
        setQuestionSeekBar();
        setDifficultySeekBar();
        setTimeSeekBar();

        return mRootView;
    }

    private void setWelcomeImageView() {
        mWelcomeImageView = (ImageView) mRootView.findViewById(R.id.configuration_welcome);
        if (mWelcomeImageView != null) {
            mWelcomeImageView.setVisibility(mConfiguration.isHelpFirstTime() ? View.VISIBLE : View.GONE);
            if(mConfiguration.isHelpFirstTime())
                mConfiguration.setHelpFirstTIme(false);
            mWelcomeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mWelcomeImageView.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setSetupImageView() {
        mSetupImageView = (ImageView)mRootView.findViewById(R.id.configuration_setup_image_view);
        if(mSetupImageView != null) {
            mSetupImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isAttached())
                        mCallback.addSetupFragment();
                }
            });
        }
    }

    private void setEditChaptersTextView() {
        mEditChapters = (TextView)mRootView.findViewById(R.id.configuration_edit_chapters);
        if (mEditChapters != null) {

            final List<Integer> chapters = mConfiguration.getChapters(true);
            if(mChapters.size() == chapters.size())
                mEditChapters.setText(getString(R.string.configuration_edit_chapters));
            else
                mEditChapters.setText(getString(R.string.configuration_custom_chapters));

            mEditChapters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isAttached()) {
                        mCallback.addChapterFragment();
                    }
                }
            });
        }
    }

    private boolean isAttached() {
        return mCallback != null;
    }

    private void setTimeSelectedTextView() {
        mTimeSelectedTextView = (TextView)mRootView.findViewById(R.id.configuration_time_selected);
        if(mTimeSelectedTextView != null)
            mTimeSelectedTextView.setText(String.valueOf(mConfiguration.getTime() + SECONDS_LABEL));
    }

    private void setDifficultySelectedTextView() {
        mDifficultySelectedTextView = (TextView)mRootView.findViewById(R.id.configuration_difficulty_selected);
        if (mDifficultySelectedTextView != null)
            mDifficultySelectedTextView.setText(getDifficultyMessage(mConfiguration.getDifficulty()));
    }

    private void setQuestionSelectedTextView() {
        mQuestionSelectedTextView = (TextView)mRootView.findViewById(R.id.configuration_question_selected);
        if(mQuestionSelectedTextView != null)
            mQuestionSelectedTextView.setText(String.valueOf(mConfiguration.getQuestions()));
    }

    private void setTimeSeekBar() {
        mTimeSeekBar = (SeekBar)mRootView.findViewById(R.id.configuration_time_seek_bar);
        if(mTimeSeekBar != null){
            mTimeSeekBar.setProgress(getIndexTimeProgress());
            mTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    progress = getTimeProgressValue(progress);
                    if(mTimeSelectedTextView != null)
                        mTimeSelectedTextView.setText(String.valueOf(progress) + SECONDS_LABEL);
                    mConfiguration.setTime(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }

    private int getTimeProgressValue(int progress) {
        progress = TIME_INTERVAL * (progress + 1);
        return progress;
    }

    private int getIndexTimeProgress() {
        return (mConfiguration.getTime() / TIME_INTERVAL) -1;
    }

    private void setDifficultySeekBar() {
        mDifficultySeekBar = (SeekBar)mRootView.findViewById(R.id.configuration_difficulty_seek_bar);
        if(mDifficultySeekBar != null){
            mDifficultySeekBar.setProgress(getIndexDifficultyProgress());
            mDifficultySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    if(progress == ADVANCED_CODE && !mConfiguration.isPremium()) {
                        mPremiumDiualog.show(getActivity().getFragmentManager(), PremiumDialog.PREMIUM_DIALOG_TAG);
                    } else {
                        int value = getDifficultyProgressValue(progress);
                        String message = getDifficultyMessage(value);
                        if(mDifficultySelectedTextView != null)
                            mDifficultySelectedTextView.setText(message);
                        mConfiguration.setDifficulty(value);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
    }

    private int getDifficultyProgressValue(int progress) {
        return progress + 1;
    }

    private int getIndexDifficultyProgress() {
        int difficulty = mConfiguration.getDifficulty();
        if(difficulty > 0)
            difficulty--;
        return difficulty;
    }

    private String getDifficultyMessage(int value) {
        String message = "";
        if(value == 1)
            message = BEGINNER;
        else if(value == 2)
            message = INTERMEDIATE;
        else if(value == 3)
            message = ADVANCED;
        return message;
    }

    private void setGenerateTextView() {
        mGenerateTextView = (TextView)mRootView.findViewById(R.id.configuration_generate);
        if(mGenerateTextView != null){
            mGenerateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGenerateTextView.setEnabled(false);
                    if(InternetConnectionManager.isOnline(getActivity())) {
                        List<Integer> chapters = mConfiguration.getChapters(true);
                        if(chapters == null || chapters.isEmpty())
                            Toast.makeText(getActivity(), getString(R.string.no_selected_chapters), Toast.LENGTH_SHORT).show();
                        else {
                            if (mGenerateDialog != null)
                                mGenerateDialog.show(mActivity.getFragmentManager(), GenerateDialog.GENERATE_DIALOG_TAG);
                            mQuestion.getQuestionsInBackground(mConfiguration);
                        }
                    }else {
                        mGenerateTextView.setEnabled(true);
                        Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onPostExecute(ArrayList<Question> questions) {
        if(mGenerateDialog != null)
            mGenerateDialog.dismiss();
        mQuestions = questions;
        if(isAttached())
            mCallback.addQuestionFragment((ArrayList<Question>)mQuestions);
    }

    private void setQuestionSeekBar() {
        mQuestionSeekBar = (SeekBar)mRootView.findViewById(R.id.configuration_question_seek_bar);
        if(mQuestionSeekBar != null){
            mQuestionSeekBar.setProgress(getIndexQuestionProgress());
            mQuestionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                    if(mConfiguration.isBoundQuestion() && progress > getIndexQuestionCountProgress()) {
                        progress = getIndexQuestionCountProgress();
                        mQuestionSeekBar.setProgress(progress);
                    }

                    int value = getQuestionProgressValue(progress);
                    if(mQuestionSelectedTextView != null)
                        mQuestionSelectedTextView.setText(String.valueOf(value));
                    mConfiguration.setQuestions(value);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }

    private int getQuestionProgressValue(int progress) {
        progress = QUESTION_INTERVAL * (progress + 1);
        return progress;
    }

    private int getIndexQuestionProgress() {
        return (mConfiguration.getQuestions() / QUESTION_INTERVAL) - 1;
    }

    private int getIndexQuestionCountProgress() {
        return (mConfiguration.getQuestionsCount() / QUESTION_INTERVAL) - 1;
    }

    @Override
    public void onSubscribe() {
        if(mDifficultySeekBar != null)
            mDifficultySeekBar.setProgress(INTERMEDIATE_CODE);
        mBillingHelper.launchSubscriptionMonthPurchaseFlow(getActivity(), BillingHelper.nextPayload());
    }

    @Override
    public void onBenefits() {
        if(mDifficultySeekBar != null)
            mDifficultySeekBar.setProgress(INTERMEDIATE_CODE);
        Intent webIntent = new Intent(getActivity(), WebActivity.class);
        webIntent.putExtra(WebActivity.URL_EXTRA_PARAM, QuizizConstants.BENEFITS_URL);
        startActivity(webIntent);
    }

    @Override
    public void onCancel() {
        if(mDifficultySeekBar != null)
            mDifficultySeekBar.setProgress(INTERMEDIATE_CODE);
    }
}