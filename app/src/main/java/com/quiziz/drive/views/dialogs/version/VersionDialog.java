package com.quiziz.drive.views.dialogs.version;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.quiziz.drive.R;

/**
 * Created by pototo 07/04/16.
 */
public class VersionDialog extends DialogFragment {
    public final static String VERSION_DIALOG_TAG = "IntroDialog";
    private TextView mOkTextView;
    private TextView mMessageTextView;
    private String mAppDetail;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_version, null);
        setMessageTextView(popupView);
        setOkTextView(popupView);
        builder.setView(popupView);
        return builder.create();
    }

    private void setMessageTextView(View popupView) {
        mMessageTextView = (TextView)popupView.findViewById(R.id.popup_version_message);
        if (mMessageTextView != null && mAppDetail != null && !mAppDetail.isEmpty())
            mMessageTextView.setText(mAppDetail);
    }

    private void setOkTextView(View popupView) {
        mOkTextView = (TextView)popupView.findViewById(R.id.popup_version_ok);
        if(mOkTextView != null) {
            mOkTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }

    public void setAppDetail(String appDetail) {
        mAppDetail = appDetail;
    }
}
