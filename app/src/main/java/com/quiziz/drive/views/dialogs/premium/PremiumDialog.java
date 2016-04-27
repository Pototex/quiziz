package com.quiziz.drive.views.dialogs.premium;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.quiziz.drive.R;

/**
 * Created by pototo on 07/04/16.
 */
public class PremiumDialog extends DialogFragment {
    public final static String PREMIUM_DIALOG_TAG = "PremiumDialog";
    private TextView mSubscribeTextView;
    private TextView mBenefitsTextView;
    private TextView mCancelTextView;
    private OnPremiumDialogListener mCallback;

    public interface OnPremiumDialogListener {
        void onSubscribe();
        void onBenefits();
        void onCancel();
    }

    public void onAttach(OnPremiumDialogListener callback){
        mCallback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_premium, null);

        setSubscribeTextView(popupView);
        setBenefitsTextView(popupView);
        setCancelTextView(popupView);

        builder.setView(popupView);
        Dialog dialog = builder.create();

        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(isAttached())
            mCallback.onCancel();
    }

    private void setBenefitsTextView(View popupView) {
        mBenefitsTextView = (TextView)popupView.findViewById(R.id.premium_benefits);
        if(mBenefitsTextView != null) {
            mBenefitsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isAttached())
                        mCallback.onBenefits();
                    dismiss();
                }
            });
        }
    }

    private void setSubscribeTextView(View popupView) {
        mSubscribeTextView = (TextView)popupView.findViewById(R.id.premium_subscribe);
        if(mSubscribeTextView != null) {
            mSubscribeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isAttached())
                        mCallback.onSubscribe();
                    dismiss();
                }
            });
        }
    }

    private void setCancelTextView(View popupView) {
        mCancelTextView = (TextView)popupView.findViewById(R.id.premium_not_thanks);
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
