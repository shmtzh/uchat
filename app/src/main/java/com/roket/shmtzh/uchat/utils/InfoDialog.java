package com.roket.shmtzh.uchat.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by shmtzh on 7/1/16.
 */
public class InfoDialog {


    public static void showDialog(android.app.FragmentManager manager, String title, String text) {
        _InfoDialog infoDialog = new _InfoDialog();
        infoDialog.setText(text);
        infoDialog.setTitle(title);
        infoDialog.show(manager, "Info dialog");
    }

    public static void showDialog(android.app.FragmentManager manager, String title, String text, InfoDialog.OnCloseListener closeListener) {
        _InfoDialog infoDialog = new _InfoDialog();
        infoDialog.setText(text);
        infoDialog.setCallback(closeListener);
        infoDialog.setTitle(title);
        infoDialog.show(manager, "Info dialog");
    }

    public static void showDialog(android.support.v4.app.FragmentManager manager, String title, String text) {
        _InfoSupportDialog infoDialog = new _InfoSupportDialog();
        infoDialog.setText(text);
        infoDialog.setTitle(title);
        infoDialog.show(manager, "Info dialog");
    }

    public static class _InfoDialog extends android.app.DialogFragment {

        private String mTitle;
        private String mText;
        private OnCloseListener callback;

        public void setTitle(String mTitle) {
            this.mTitle = mTitle;
        }

        public void setText(String mText) {
            this.mText = mText;
        }

        @Override
        public void onDismiss(final DialogInterface dialog) {
            super.onDismiss(dialog);
            final Activity activity = getActivity();
            if (activity instanceof DialogInterface.OnDismissListener) {
                ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
            }
        }


        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog);
            builder.setTitle(mTitle);
            builder.setMessage(mText)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (callback != null) {
                                callback.onClose();
                            }
                            dismiss();
                        }
                    });
            return builder.create();
        }

        public void setCallback(OnCloseListener callback) {
            this.callback = callback;
        }
    }

    public static class _InfoSupportDialog extends android.support.v4.app.DialogFragment {

        private String mTitle;
        private String mText;

        private OnCloseListener callback;

        public void setTitle(String mTitle) {
            this.mTitle = mTitle;
        }

        public void setText(String mText) {
            this.mText = mText;
        }

        @NonNull
        @Override
        public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog);
            builder.setTitle(mTitle);
            builder.setMessage(mText)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (callback != null) {
                                callback.onClose();
                            }
                            dismiss();
                        }
                    });
            return builder.create();
        }


        @Override
        public void onDismiss(final DialogInterface dialog) {
            super.onDismiss(dialog);
            final Activity activity = getActivity();
            if (activity instanceof DialogInterface.OnDismissListener) {
                ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
            }
        }
    }

    public interface OnCloseListener {
        void onClose();
    }

}
