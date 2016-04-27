package com.quiziz.drive.views.fragments.chapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.quiziz.drive.R;
import com.quiziz.drive.model.Chapter;
import com.quiziz.drive.model.Configuration;
import com.quiziz.drive.model.Question;
import com.quiziz.drive.util.QuizizConstants;
import com.quiziz.drive.views.fragments.chapter.adapter.ChapterArrayAdapter;
import com.quiziz.drive.views.fragments.configuration.ConfigurationFragment;

import java.util.List;

/**
 * Created by pototo on 27/03/16.
 */
public class ChapterFragment extends Fragment
        implements ChapterArrayAdapter.OnChapterArrayAdapterListener{
    private static final String TAG = "ChapterFragment";
    private List<Chapter> mChapters;
    private ListView mChaptersListView;
    private ChapterArrayAdapter mChapterArrayAdapter;
    private CheckBox mChapterAllCheckBox;
    private Configuration mConfiguration;
    private ImageView mExitImageView;
    private View mRootView;
    private boolean isUpdateChapterFromArrayAdapter;
    private OnChapterFragmentListener mCallback;

    public interface OnChapterFragmentListener {
        void exit();
        void setGoogleAnalytics(String screenName);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mCallback = (OnChapterFragmentListener)activity;
        }catch(ClassCastException exception){
            throw new ClassCastException(activity.toString() + " must implement OnChapterFragmentListener.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConfiguration = new Configuration(getActivity());
        mChapters = getArguments().getParcelableArrayList(QuizizConstants.ChapterBundle.CHAPTERS_BUNDLE_KEY);
        Chapter.loadChapterPreferences(mChapters, mConfiguration.getChapters(true));
        Log.d(TAG, "Chapters list size: " + mChapters.size());
        if(mCallback != null)
            mCallback.setGoogleAnalytics(QuizizConstants.GA_CHAPTER_TRACKER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_chapter, container, false);
        setChaptersListView();
        setChapterAllCheckBox();
        setExitImageView();
        setOnBackKeyListener();
        return mRootView;
    }

    private void setOnBackKeyListener() {
        mRootView.setFocusableInTouchMode(true);
        mRootView.requestFocus();
        mRootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                boolean result = false;
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.d(TAG, "Chapter back pressed ...");
                    returnToConfigurationFragment();
                    result = true;
                }
                return result;
            }
        });
    }


    private void setExitImageView() {
        mExitImageView = (ImageView)mRootView.findViewById(R.id.chapter_exit);
        if(mExitImageView != null){
            mExitImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnToConfigurationFragment();
                }
            });
        }

    }

    private synchronized void returnToConfigurationFragment() {
        saveChapterPreferences(true);
        setQuestionBound();
        if(mCallback != null)
            mCallback.exit();
    }

    private void setQuestionBound() {

        int questionByChapterCount = Question.getQuestionsCount(mConfiguration);
        Log.d(TAG, "Questions available by chapters selected: " + questionByChapterCount);

        int questionRequestedCount = mConfiguration.getQuestions();
        Log.d(TAG, "Questions requested: " + questionRequestedCount);

        boolean isBound = mConfiguration.isBoundQuestion();
        Log.d(TAG, "Question bound: " + (isBound ? "Yes" : "No"));

        if((questionByChapterCount < questionRequestedCount) ||
           (questionByChapterCount > questionRequestedCount && isBound)
          ) {
            mConfiguration.setBoundQuestion(true);
            int value = getBoundQuestionValue(questionByChapterCount);
            mConfiguration.setQuestions(value);
            mConfiguration.setQuestionsCount(value);
        } else
            mConfiguration.setBoundQuestion(false);
    }

    public static int getBoundQuestionValue(int value) {
        int result = value / ConfigurationFragment.QUESTION_INTERVAL;
        return result * ConfigurationFragment.QUESTION_INTERVAL;
    }

    @Override
    public void updateChapters(Chapter chapter) {
        Chapter.updateChapters(mChapters, chapter);
        if(mChapterAllCheckBox != null) {
            isUpdateChapterFromArrayAdapter = true;
            mChapterAllCheckBox.setChecked(Chapter.areAllChaptersSelected(mChapters));
        }
        updateChaptersListView();
        saveChapterPreferences(false);
    }

    private void setChapterAllCheckBox() {
        mChapterAllCheckBox = (CheckBox)mRootView.findViewById(R.id.chapter_all_check_box);
        if(mChapterAllCheckBox != null){

            mChapterAllCheckBox.setChecked(Chapter.areAllChaptersSelected(mChapters));
            mChapterAllCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isUpdateChapterFromArrayAdapter = false;
                }
            });
            mChapterAllCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked)
                        Chapter.selectAllChapters(mChapters);
                    else if(!isChecked && !isUpdateChapterFromArrayAdapter)
                        Chapter.deselectAllChapters(mChapters);
                    updateChaptersListView();
                    saveChapterPreferences(false);
                }
            });
        }
    }

    private void saveChapterPreferences(boolean saveToDisk) {
        List<Integer> selectedChapters = Chapter.getSelectedChapters(mChapters);
        mConfiguration.setChapters(selectedChapters, saveToDisk);
    }

    private void updateChaptersListView() {
        if(mChapterArrayAdapter != null)
            mChapterArrayAdapter.notifyDataSetChanged();
    }

    private void setChaptersListView() {
        mChaptersListView = (ListView)mRootView.findViewById(R.id.chapter_list_view);
        if(mChaptersListView != null && mChapters != null && !mChapters.isEmpty()){
            mChapterArrayAdapter = new ChapterArrayAdapter(getActivity(), mChapters);
            mChapterArrayAdapter.attach(this);
            mChaptersListView.setAdapter(mChapterArrayAdapter);
        }
    }
}
