package com.quiziz.drive.model;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.quiziz.drive.model.db.QuizizParseContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pototo on 03/03/16.
 */
public class AppInfo {

    private static final String TAG = "Quiziz";

    private String detail;
    private OnAppInfoListener mCallback;

    public interface OnAppInfoListener {
        void onPostExecute(AppInfo appInfo);
    }

    public void onAttach(OnAppInfoListener callback){
        mCallback = callback;
    }

    public AppInfo(){}

    public AppInfo(ParseObject row){
        detail = row.getString(QuizizParseContract.Quiziz.DETAIL);
    }

    public void getAppInfoInBackground(){
        ParseQuery query = new ParseQuery(QuizizParseContract.Quiziz.TABLE_NAME);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, ParseException e) {
                AppInfo appInfo = new AppInfo(results.get(0));
                if(isAttached())
                    mCallback.onPostExecute(appInfo);
            }
        });
    }

    private boolean isAttached() {
        return mCallback != null;
    }

    public String getDetail() {
        return detail;
    }
}
