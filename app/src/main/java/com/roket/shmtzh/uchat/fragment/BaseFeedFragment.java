package com.roket.shmtzh.uchat.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.roket.shmtzh.uchat.R;
import com.roket.shmtzh.uchat.activity.CameraActivity;
import com.roket.shmtzh.uchat.model.Message;
import com.roket.shmtzh.uchat.utils.ActivityUtils;
import com.roket.shmtzh.uchat.utils.DateUtils;
import com.roket.shmtzh.uchat.utils.ImageUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public abstract class BaseFeedFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "BaseProfileFragment";

    private static final int PERMISSIONS_REQUEST_CAMERA = 48;
    private static final int PERMISSIONS_REQUEST_WRITE = 49;
    private static final int PERMISSIONS_REQUEST_FINE = 50;
    private static final int PERMISSIONS_REQUEST_COARSE = 51;
    private static final String ACTION_GALLERY = "ACTION_GALLERY";
    private static final String ACTION_PHOTO = "ACTION_PHOTO";
    protected Uri mPhotoUri;
    public static final int REQUEST_IMAGE = 91;
    Location mLastLocation;
    protected GoogleApiClient mGoogleApiClient;

    protected abstract void saveAvatar(Uri uri, Bitmap bitmap);

    public abstract void onTakePhotoCancel();

    public void onLaunchCamera(String data) {
        if (checkPermissions()) {
            onPermissionsGranted(data);
        } else {
            if (!isPermissionGranted(Manifest.permission.CAMERA)) {
                requestCameraPermissions();
            } else if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestWritingPermissions();
            }
        }
    }

    public void createPlacesConfs() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            mPhotoUri = result.getParcelableExtra(CameraActivity.EXTRA_URI);

            try {

                Bitmap bitmap = ImageUtils.handleSamplingAndRotationBitmap(getContext(), mPhotoUri);
                bitmap.recycle();
                bitmap = ImageUtils.handleSamplingAndRotationBitmap(getContext(), mPhotoUri);

                saveAvatar(mPhotoUri, Bitmap.createBitmap(bitmap));
                bitmap.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getActivity(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            onPictureWasNotTaken();
        }
    }

    private boolean checkPermissions() {
        return isPermissionGranted(Manifest.permission.CAMERA) && isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    protected abstract void onPictureWasNotTaken();

    private View.OnClickListener onCameraPermissionSnackbarClickListener = v -> requestCameraPermissions();

    private View.OnClickListener onWritingPermissionSnackbarClickListener = v -> requestWritingPermissions();

    boolean checkLocationPermissions() {
        return isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }


    private void requestWritingPermissions() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST_WRITE);
    }

    private void requestCameraPermissions() {
        requestPermissions(new String[]{Manifest.permission.CAMERA},
                PERMISSIONS_REQUEST_CAMERA);
    }


    void requestCoarsePermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSIONS_REQUEST_COARSE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onLaunchCamera(ACTION_PHOTO);
                } else {
                    if (getView() != null) {
                        Snackbar.make(getView(), "camera " + getString(R.string.permissions_denied), Snackbar.LENGTH_LONG)
                                .setAction(R.string.ask_permissons, onCameraPermissionSnackbarClickListener).show();
                    } else {
                        Log.e(TAG, "grant permissions failed");
                    }
                }
                break;
            case PERMISSIONS_REQUEST_WRITE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onLaunchCamera(ACTION_GALLERY);
                } else {
                    if (getView() != null) {
                        Snackbar.make(getView(), "Storage: " + getString(R.string.permissions_denied), Snackbar.LENGTH_LONG)
                                .setAction(R.string.ask_permissons, onWritingPermissionSnackbarClickListener).show();
                    } else {
                        Log.e(TAG, "grant permissions failed");
                    }
                }
                break;

            case PERMISSIONS_REQUEST_COARSE:
            case PERMISSIONS_REQUEST_FINE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendCoordinate();
                } else {
                    if (getView() != null) {
                        Snackbar.make(getView(), "Storage: " + getString(R.string.permissions_denied), Snackbar.LENGTH_LONG)
                                .setAction(R.string.ask_permissons, onWritingPermissionSnackbarClickListener).show();
                    } else {
                        Log.e(TAG, "grant permissions failed");
                    }
                }
                break;
        }
    }



    public Message sendCoordinate() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            String lat = String.valueOf(mLastLocation.getLatitude());
            String lng = String.valueOf(mLastLocation.getLongitude());
            String url = "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lng + "&zoom=15&size=200x200&sensor=true";
            return new Message(url, DateUtils.getCurentDate(), "location");
        }

        return new Message("", DateUtils.getCurentDate(), "location");

    }

    private void onPermissionsGranted(String action) {
        Intent intent = new Intent(getContext(), CameraActivity.class);

        switch (action) {
            case ACTION_GALLERY:
                intent.setAction(ACTION_GALLERY);
                break;
            case ACTION_PHOTO:
                intent.setAction(ACTION_PHOTO);
                break;
        }
        ActivityUtils.startAsPopupForResult(this, intent, REQUEST_IMAGE);

    }


    boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}