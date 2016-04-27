package com.quiziz.drive.views.dialogs.exit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.quiziz.drive.R;

/**
 * Created by pototo 07/04/16.
 */
public class ExitDialog extends DialogFragment {
    public final static String EXIT_DIALOG_TAG = "ExitDialog";
    private TextView mOkTextView;
    private TextView mCancelTextView;
    private OnExitDialogListener mCallback;

    public interface OnExitDialogListener {
        void onExit();
        void onCancel();
    }

    public void onAttach(OnExitDialogListener callback){
        mCallback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_exit, null);
        setOkTextView(popupView);
        setCancelTextView(popupView);
        builder.setView(popupView);
        return builder.create();
    }

    private void setOkTextView(View popupView) {
        mOkTextView = (TextView)popupView.findViewById(R.id.popup_exit_ok);
        if(mOkTextView != null) {
            mOkTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isAttached())
                        mCallback.onExit();
                    dismiss();
                }
            });
        }
    }

    private void setCancelTextView(View popupView) {
        mCancelTextView = (TextView)popupView.findViewById(R.id.popup_exit_cancel);
        if(mCancelTextView != null) {
            mCancelTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isAttached())
                        mCallback.onCancel();
                    dismiss();
                }
            });
        }
    }

    private boolean isAttached(){
        return mCallback != null;
    }
}
