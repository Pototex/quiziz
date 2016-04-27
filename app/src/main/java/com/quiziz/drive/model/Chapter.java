package com.quiziz.drive.model;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Chapter implements Parcelable {

    private static final String TAG = "Chapter";

    private String chapterId;
    private String name;
    private boolean selected;
    private OnChapterListener mCallback;

    public interface OnChapterListener {
        void onPostExecute(ArrayList<Chapter> chapters);
    }

    public void attach(OnChapterListener callback){
        mCallback = callback;
    }

    public static final Parcelable.Creator<Chapter> CREATOR = new Parcelable.Creator<Chapter>() {
        public Chapter createFromParcel(Parcel in) {
            return new Chapter(in);
        }

        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };

    public Chapter(){}

    public Chapter(Parcel in){
        chapterId = in.readString();
        name = in.readString();
    }

    public Chapter(ParseObject row){
        chapterId = row.getString(QuizizParseContract.Chapter.CHAPTHER_ID);
        name = row.getString(QuizizParseContract.Chapter.NAME);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(chapterId);
        out.writeString(name);
    }

    public void getChaptersInBackground(){
        ParseQuery query = new ParseQuery(QuizizParseContract.Chapter.TABLE_NAME);
        query.orderByAscending(QuizizParseContract.Chapter.CHAPTHER_ID);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, ParseException e) {
                ArrayList<Chapter> chapters = new ArrayList();
                for(ParseObject row : results)
                    chapters.add(new Chapter(row));
                if(isAttached())
                    mCallback.onPostExecute(chapters);
            }
        });
    }

    private boolean isAttached() {
        return mCallback != null;
    }

    public String getChapterId() {
        return chapterId;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public static void selectAllChapters(List<Chapter> chapters) {
        for(Chapter chapter : chapters)
            chapter.setSelected(true);
    }

    public static void deselectAllChapters(List<Chapter> chapters) {
        for(Chapter chapter : chapters)
            chapter.setSelected(false);
    }

    public static List<Integer> getSelectedChapters(List<Chapter> chapters){
        List<Integer> selectedChapters = new ArrayList();
        for(Chapter chapter : chapters)
            if(chapter.isSelected())
                selectedChapters.add(Integer.parseInt(chapter.getChapterId()));
        return selectedChapters;
    }

    public static void loadChapterPreferences(List<Chapter> chapters, List<Integer> preferences){
        for(Chapter chapter : chapters) {
            Integer chapterId = Integer.parseInt(chapter.getChapterId());
            if(preferences.contains(chapterId))
                chapter.setSelected(true);
        }
    }

    public static boolean areAllChaptersSelected(List<Chapter> chapters){
        boolean allSelected = true;
        for(Chapter chapter : chapters)
            if(!chapter.isSelected())
                allSelected = false;
        return allSelected;
    }

    public static void updateChapters(List<Chapter> chapters, Chapter update) {
        for(Chapter chapter : chapters)
            if(chapter.getChapterId().equals(update.getChapterId()))
                chapter.setSelected(update.isSelected());
    }
}
