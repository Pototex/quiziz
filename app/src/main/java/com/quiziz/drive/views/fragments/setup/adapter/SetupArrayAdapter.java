package com.quiziz.drive.views.fragments.setup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.quiziz.drive.R;
import com.quiziz.drive.model.Setup;
import com.quiziz.drive.util.QuizizConstants;

import java.util.List;

/**
 * Created by pototo 28/03/16.
 */
public class SetupArrayAdapter extends ArrayAdapter<Setup>{
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int LINK = 0;
    private static final int LOGO = 1;
    private LayoutInflater mInflater;
    private OnSetupArrayAdapterListener mCallback;

    public interface OnSetupArrayAdapterListener {
        void mailToIntent();
        void playStoreIntent();
        void webIntent(String url);
    }

    public static class ViewHolder {
        TextView mNameTextView;
    }

    public SetupArrayAdapter(Context context, List<Setup> setups){
        super(context, 0, setups);
        mInflater = LayoutInflater.from(context);
    }

    public void attach(OnSetupArrayAdapterListener callback){
        mCallback = callback;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        int type = getItemViewType(position);

        if(convertView == null){
            viewHolder = new ViewHolder();

            switch(type) {
                case LINK:
                    convertView = mInflater.inflate(R.layout.array_adapter_setup, parent, false);
                    viewHolder.mNameTextView = (TextView) convertView.findViewById(R.id.array_adapter_setup_name);
                    break;
                case LOGO:
                    convertView = mInflater.inflate(R.layout.array_adapter_setup_logo, parent, false);
                    break;
            }
            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder)convertView.getTag();

        Setup setup = getItem(position);


        if(!setup.isLogo())
            setLinkValues(viewHolder, setup);

        return convertView;
    }

    private void setLinkValues(ViewHolder viewHolder, Setup setup) {
        viewHolder.mNameTextView.setText(setup.getName());
        if (setup.getUrl().equals(QuizizConstants.MAIL_TO))
            mailToOnClick(viewHolder);
        else if(setup.getUrl().equals(QuizizConstants.PLAY_STORE_URL))
            playStoreOnClick(viewHolder);
        else
            webOnClick(viewHolder, setup.getUrl());
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        Setup setup = (Setup)getItem(position);
        return setup.isLogo() ? LOGO : LINK;
    }

    private void mailToOnClick(ViewHolder viewHolder){
        if(viewHolder != null && viewHolder.mNameTextView != null) {
            viewHolder.mNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCallback != null)
                        mCallback.mailToIntent();
                }
            });
        }
    }

    private void playStoreOnClick(ViewHolder viewHolder){
        if(viewHolder != null && viewHolder.mNameTextView != null) {
            viewHolder.mNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCallback != null)
                        mCallback.playStoreIntent();
                }
            });
        }
    }

    private void webOnClick(ViewHolder viewHolder, final String url){
        if(viewHolder != null && viewHolder.mNameTextView != null) {
            viewHolder.mNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCallback != null)
                        mCallback.webIntent(url);
                }
            });
        }
    }
}
