package com.quiziz.drive.views.fragments.chapter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.quiziz.drive.R;
import com.quiziz.drive.model.Chapter;

import java.util.List;

/**
 * Created by pototo on 27/03/16.
 */
public class ChapterArrayAdapter extends ArrayAdapter<Chapter>{
    private LayoutInflater mInflater;
    private OnChapterArrayAdapterListener mCallback;

    public interface OnChapterArrayAdapterListener {
        void updateChapters(Chapter chapter);
    }

    public static class ViewHolder {
        TextView mNameTextView;
        CheckBox mCheckBox;
    }

    public ChapterArrayAdapter(Context context, List<Chapter> chapters){
        super(context, 0, chapters);
        mInflater = LayoutInflater.from(context);
    }

    public void attach(OnChapterArrayAdapterListener callback){
        mCallback = callback;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        viewHolder = new ViewHolder();
        convertView = mInflater.inflate(R.layout.array_adapter_chapter, parent, false);
        viewHolder.mNameTextView = (TextView)convertView.findViewById(R.id.array_adapter_chapter_name);
        viewHolder.mCheckBox = (CheckBox)convertView.findViewById(R.id.array_adapter_chapter_check_box);

        final Chapter chapter = getItem(position);
        viewHolder.mNameTextView.setText(chapter.getName());
        viewHolder.mCheckBox.setChecked(chapter.isSelected());
        viewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSelected) {
                if(mCallback != null) {
                    chapter.setSelected(isSelected);
                    mCallback.updateChapters(chapter);
                }
            }
        });
        return convertView;
    }
}
