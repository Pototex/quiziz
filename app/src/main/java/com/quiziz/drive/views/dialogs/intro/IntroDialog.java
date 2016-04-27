package com.quiziz.drive.views.dialogs.intro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.quiziz.drive.R;

/**
 * Created by pototo on 31/03/2016.
 */
public class IntroDialog extends DialogFragment {
    public final static String INTRO_DIALOG_TAG = "IntroDialog";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_intro, null);
        builder.setView(popupView);
        Dialog dialog = builder.create();
        return dialog;
    }
}
