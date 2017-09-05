package com.example.ezclassapp.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.example.ezclassapp.R;

/**n
 * Created by tychan on 9/4/17.
 */

public class CreateCommentDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must implement this
       this interface in order to receive event callbacks. Each method passes the DialogFragment
       in case the host needs to query it.
     */
    public interface CreateCommentListener {
        public void onDialogPositiveClick(DialogFragment fragment, String comment);
    }

    // Use this instance of the interface to deliver action events
    CreateCommentListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host implements the callback interface
        try {
            mListener = (CreateCommentListener) context;
        } catch (ClassCastException e) {
            // The Activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement CreateCommentListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the alert builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null for parent view because it's going in the dialog layout
        builder.setView(inflater.inflate(R.layout.alert_dialog_create_comment, null))
        // Add action buttons
            .setPositiveButton(R.string.alert_box_submit_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText comment = (EditText) getDialog().findViewById(R.id.alert_comment);
                    String input = comment.getText().toString().trim();
                    Log.d("create_comment", input);
                    // If input string is empty, then close the dialog box
                    if (checkInput(input)) {
                        dismiss();
                    } else {
                        // TODO: Write comment to Firebase
                        // TODO: Send comment string back to detailed review page
                        mListener.onDialogPositiveClick(CreateCommentDialogFragment.this, input);
                    }
                }
            }).setNeutralButton(R.string.alert_box_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });


        return builder.create();
    }

    public Boolean checkInput(String input) {
        return TextUtils.isEmpty(input);
    }
}
