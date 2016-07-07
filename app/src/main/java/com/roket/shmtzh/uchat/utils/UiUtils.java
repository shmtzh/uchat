package com.roket.shmtzh.uchat.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.roket.shmtzh.uchat.R;

/**
 * Created by shmtzh on 7/2/16.
 */
public class UiUtils {
    private static final String TAG = UiUtils.class.getSimpleName();

    public static void showErrorSnackbar(@Nullable final View view, @Nullable final String text) {
        Log.e(TAG, text);
        if (view == null || TextUtils.isEmpty(text)) {
            return;
        }
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);

        View snackView = snackbar.getView();
        snackView.setBackgroundColor(view.getContext().getResources().getColor(R.color.SnackbarErrorColor));
        snackbar.show();
    }

    public static void showPositiveSnackbar(View view, String text) {
        if (view == null) {
            return;
        }
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);

        View snackView = snackbar.getView();
        snackView.setBackgroundColor(view.getContext().getResources().getColor(R.color.SnackbarPositiveColor));
        snackbar.show();

    }


    public static void showErrorSnackbar(View view, String message, Throwable t) {
        String responseMessage = t.getLocalizedMessage();
        showErrorSnackbar(view, message + ": " + responseMessage);
    }


    @SuppressWarnings("unchecked")
    public static <T extends View> T findView(View root, int id) {
        if (root == null) return null;
        try {
            return (T) root.findViewById(id);
        } catch (ClassCastException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T findView(Activity root, int id) {
        if (root == null) return null;
        try {
            return (T) root.findViewById(id);
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Hides soft keyboard.
     *
     * @param view View which has focus
     */
    public static void hideSoftKeyboard(View view) {
        if (view == null)
            return;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(View view) {
        if (view == null)
            return;
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


}
