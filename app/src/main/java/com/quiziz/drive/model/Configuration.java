package com.quiziz.drive.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.quiziz.drive.util.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pototo on 27/03/16.
 */
public class Configuration {
    private static final String TAG = "ConfigurationModel";
    private static final String QUESTIONS_KEY = "questions";
    private static final String TIME_KEY = "time";
    private static final String DIFFICULTY_KEY = "difficulty";
    private static final String CHAPTERS_KEY = "chapters";
    private static final String LOAD_FIRST_TIME_KEY = "loadFirstTime";
    private static final String BOUND_QUESTIONS_KEY = "boundQuestions";
    private static final String QUESTIONS_COUNT_KEY = "questionsCount";
    private static final String PREMIUM_KEY = "premium";
    private static final String FIRST_TIME_KEY = "firstTime";

    private int questions;
    private int time;
    private int difficulty;
    private List<Integer> chapters;

    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;

    public Configuration(Context context) {
        mSharedPreferences = context.getSharedPreferences(Config.PREFERENCES_APP_ID, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public int getQuestions() {
        questions = mSharedPreferences.getInt(QUESTIONS_KEY, 0);
        Log.d(TAG, "Questions: " + questions);
        return questions;
    }

    public void setQuestions(int questions) {
        mEditor.putInt(QUESTIONS_KEY, questions);
        mEditor.apply();
    }

    public int getTime() {
        time = mSharedPreferences.getInt(TIME_KEY, 0);
        return time;
    }

    public void setTime(int time) {
        mEditor.putInt(TIME_KEY, time);
        mEditor.apply();
    }

    public int getDifficulty() {
        difficulty = mSharedPreferences.getInt(DIFFICULTY_KEY, 0);
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        mEditor.putInt(DIFFICULTY_KEY, difficulty);
        mEditor.apply();
    }

    public List<Integer> getChapters(boolean fromDisk) {
        if(fromDisk) {
            Set<String> chapterSet = mSharedPreferences.getStringSet(CHAPTERS_KEY, Collections.EMPTY_SET);
            chapters = parseToListOfIntegers(chapterSet);
        }
        return chapters;
    }

    public void setChapters(List<Integer> chapters, boolean saveToDisk) {
        this.chapters = chapters;
        if (saveToDisk) {
            Set<String> chapterSet = new HashSet(parseToListOfStrings(chapters));
            mEditor.putStringSet(CHAPTERS_KEY, chapterSet);
            mEditor.apply();
        }
    }

    public static List<String> parseToListOfStrings(List<Integer> chapters){
        List<String> list = new ArrayList();
        for(Integer chapter : chapters)
            list.add(String.valueOf(chapter));
        return list;
    }

    public static List<Integer> parseToListOfIntegers(Set<String> chapters){
        List<Integer> list = new ArrayList();
        for(String chapter : chapters)
            list.add(Integer.parseInt(chapter));
        return list;
    }

    public boolean isLoadFirstTime() {
        boolean loadFirstTime = mSharedPreferences.getBoolean(LOAD_FIRST_TIME_KEY, true);
        return loadFirstTime;
    }

    public void setLoadFirstTime(boolean value) {
        mEditor.putBoolean(LOAD_FIRST_TIME_KEY, value);
        mEditor.apply();
    }

    public boolean isBoundQuestion() {
        boolean isBoundQuestion = mSharedPreferences.getBoolean(BOUND_QUESTIONS_KEY, false);
        Log.d(TAG, "Bound question: " + (isBoundQuestion ? "Yes" : "No"));
        return isBoundQuestion;
    }

    public void setBoundQuestion(boolean value) {
        mEditor.putBoolean(BOUND_QUESTIONS_KEY, value);
        mEditor.apply();
    }

    public int getQuestionsCount() {
        questions = mSharedPreferences.getInt(QUESTIONS_COUNT_KEY, 0);
        Log.d(TAG, "Questions count: " + questions);
        return questions;
    }

    public void setQuestionsCount(int count) {
        mEditor.putInt(QUESTIONS_COUNT_KEY, count);
        mEditor.apply();
    }

    public boolean isPremium() {
        boolean isPremium = mSharedPreferences.getBoolean(PREMIUM_KEY, false);
        return isPremium;
    }

    public void setPremium(boolean value) {
        mEditor.putBoolean(PREMIUM_KEY, value);
        mEditor.apply();
    }

    public boolean isHelpFirstTime() {
        boolean isFirstTime = mSharedPreferences.getBoolean(FIRST_TIME_KEY, true);
        return isFirstTime;
    }

    public void setHelpFirstTIme(boolean value) {
        mEditor.putBoolean(FIRST_TIME_KEY, value);
        mEditor.apply();
    }
}
