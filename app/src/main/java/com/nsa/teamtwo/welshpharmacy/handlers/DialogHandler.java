package com.nsa.teamtwo.welshpharmacy.handlers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.nsa.teamtwo.welshpharmacy.util.UtilCallback;

public class DialogHandler {

    public static void showConfirmationDialog(final Activity activity, int message, int positiveButton, int negativeButton, final UtilCallback callback) {
        showConfirmationDialog(activity, activity.getString(message), activity.getString(positiveButton), activity.getString(negativeButton), callback);
    }

    public static void showConfirmationDialog(final Activity activity, String message, String positiveButton, String negativeButton, final UtilCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callback.onSuccess();
            }
        });
        builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callback.onFail();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
