package com.example.ezclassapp.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ezclassapp.Activities.Constants;
import com.example.ezclassapp.Helpers.StringImageConverter;
import com.example.ezclassapp.Models.Comment;
import com.example.ezclassapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ezclassapp.Activities.Constants.PREFS_NAME;
import static com.example.ezclassapp.Activities.Constants.USER_NAME;

/**
 * n
 * Created by tychan on 9/4/17.
 */

public class CreateCommentDialogFragment extends DialogFragment {

    private static final String REVIEW_UID = "REVIEW_UID";

    // Builder method for CreateCommentDialogFragment
    public static CreateCommentDialogFragment newInstance(String reviewUID) {
        // Create a bundle to store reviewUID
        Bundle bundle = new Bundle();
        bundle.putString(REVIEW_UID, reviewUID);
        CreateCommentDialogFragment fragment = new CreateCommentDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the reviewUID from bundle
        // TODO: Remove the comment below and remove the reviewUID
        // final String reviewUID = getArguments().getString(REVIEW_UID);
        final String reviewUID = "Kt9Hi-Q8_u6lqsoAt_4";
        // Use the alert builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get instance of sharedpreferences
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate the alert dialog view and initialize the name and image
        View view = inflater.inflate(R.layout.alert_dialog_create_comment, null);
        // Set user name and image from Shared Preferences
        TextView userData = (TextView) view.findViewById(R.id.alert_user_name);
        final String username = preferences.getString(USER_NAME, null);
        userData.setText(username);
        CircleImageView userImage = (CircleImageView) view.findViewById(R.id.alert_user_pic);

        Bitmap bitmap = StringImageConverter.decodeBase64AndSetImage(preferences.getString(Constants.USER_PIC, null), userImage);
        if (bitmap == null) {
            // Set user image to color primary if there is no profile image set yet, change null to theme if any
            userImage.setImageResource(R.color.colorPrimary);
            Log.d("detailed", "background color set");
        } else {
            userImage.setImageBitmap(bitmap);
            Log.d("detailed", "bitmap set");
        }

        // Inflate and set the layout for the dialog
        // Pass null for parent view because it's going in the dialog layout
        builder.setView(view)
                .setTitle("Write a comment")
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
                            SharedPreferences preferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
                            String userUID = preferences.getString(Constants.USER_UID, null);
                            // Create a database reference
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                            // Create a string UID to store the comment
                            String commentUID = database.child(Constants.COMMENT).child(reviewUID).push().getKey();
                            // Create a comment object
                            Comment userComment = new Comment(userUID, input, commentUID);
                            // Set the value in UID to the Comment object
                            database.child(Constants.COMMENT).child(reviewUID).child(commentUID).setValue(userComment);
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
