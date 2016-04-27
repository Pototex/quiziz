package com.quiziz.drive.views.dialogs.generate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.quiziz.drive.R;

/**
 * Created by pototo on 31/03/2016.
 */
public class GenerateDialog extends DialogFragment {
    public final static String GENERATE_DIALOG_TAG = "GenerateDialog";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_generate, null);
        builder.setView(popupView);
        Dialog dialog = builder.create();
        return dialog;
    }
}
