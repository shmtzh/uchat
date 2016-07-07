package com.roket.shmtzh.uchat.fragment;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.orhanobut.hawk.Hawk;
import com.roket.shmtzh.uchat.R;
import com.roket.shmtzh.uchat.activity.CameraActivity;
import com.roket.shmtzh.uchat.adapter.FeedRecyclerViewAdapter;
import com.roket.shmtzh.uchat.api.MessageApi;
import com.roket.shmtzh.uchat.listener.FABListener;
import com.roket.shmtzh.uchat.model.Message;
import com.roket.shmtzh.uchat.utils.DateUtils;
import com.roket.shmtzh.uchat.utils.UiUtils;
import com.roket.shmtzh.uchat.view.MessagingEditText;
import com.roket.shmtzh.uchat.webapi.NetworkManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class FeedFragment extends BaseFeedFragment implements GoogleApiClient.OnConnectionFailedListener {
    private  final String TAG = getClass().getSimpleName();
    @Bind(R.id.menu_float)
    FloatingActionMenu mFloatMenu;
    @Bind(R.id.feed_location_button)
    FloatingActionButton mLocationButton;
    @Bind(R.id.feed_album_button)
    FloatingActionButton mAlbumButton;
    @Bind(R.id.feed_camera_button)
    FloatingActionButton mCameraButton;
    @Bind(R.id.feed_message_button)
    FloatingActionButton mMessageButton;
    @Bind(R.id.textInput)
    MessagingEditText mInputEditText;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    List<Message> persons;
    FeedRecyclerViewAdapter adapter;
    FABListener listener;
    boolean isRealNetwork = false;
    private static final String ACTION_GALLERY = "ACTION_GALLERY";
    private static final String ACTION_PHOTO = "ACTION_PHOTO";
    private static final int PERMISSIONS_REQUEST_FINE = 50;
    private static final int PERMISSIONS_REQUEST_COARSE = 51;

    public FeedFragment() {
    }

    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this, view);

        createPlacesConfs();


        mLocationButton.setOnClickListener(v -> {
            locationClick();
            hideMenu();
        });
        mAlbumButton.setOnClickListener(v -> {
            onLaunchCamera(ACTION_GALLERY);
            hideMenu();

        });
        mCameraButton.setOnClickListener(v -> {
            onLaunchCamera(ACTION_PHOTO);
            hideMenu();

        });
        mMessageButton.setOnClickListener(v -> {
            startMessaging(mInputEditText);
            hideMenu();
        });

        Drawable drawable = getResources().getDrawable(R.drawable.ic_send_black_24dp);
        mInputEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(llm);
        persons = new ArrayList<>();

        if (isRealNetwork) {

            getMessages();
        }

        if (Hawk.get("all") != null) {
            persons = Hawk.get("all");
        }


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy >= 0) {
                    mFloatMenu.showMenu(true);
                } else mFloatMenu.hideMenu(true);
            }
        });


        adapter = new FeedRecyclerViewAdapter(persons, getContext());
        mRecyclerView.setAdapter(adapter);

        mInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch (count) {
                    case 0:
                        animateImageView(drawable, false);
                        break;
                    case 1:
                        animateImageView(drawable, true);
                        break;
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mInputEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                endMessaging(mInputEditText);
            }

            return false;
        });

        mInputEditText.setDrawableClickListener(target -> {
            switch (target) {
                case RIGHT:
                    endMessaging(mInputEditText);
                    break;
            }
        });
        return view;
    }

    private void getMessages() {
        Subscription getMedia;
        MessageApi messageApi;
        messageApi = NetworkManager.getInstance().getMessageApi();

        getMedia = messageApi.getMessageList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    persons.addAll(responseBody);
                }, throwable -> {
                    Log.e("", "setupAudioPlayer", throwable);
                });
    }

    private void locationClick() {

        if (!isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            requestCoarsePermissions();
        }

        if (checkLocationPermissions()) {
            Message message = sendCoordinate();
            persons.add(message);
            postMessage(message);
            Hawk.put(String.valueOf(persons.size()), message);
            adapter.notifyDataSetChanged();
            mRecyclerView.smoothScrollToPosition(persons.size());
        }
    }

    private void postMessage(Message loc) {
        if (isRealNetwork) {
            Subscription getMedia;
            MessageApi messageApi;
            messageApi = NetworkManager.getInstance().getMessageApi();

            getMedia = messageApi.postMessage(loc)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseBody -> {
                        Log.e("", "success");
                    }, throwable -> {
                        Log.e("", "error", throwable);
                    });
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FABListener) {
            listener = (FABListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    public void animateImageView(final Drawable v, boolean toSent) {
        final int orange = getResources().getColor(android.R.color.holo_orange_dark);
        final int black = getResources().getColor(android.R.color.black);

        final ValueAnimator colorAnim = ObjectAnimator.ofFloat(0f, 1f);
        colorAnim.addUpdateListener(animation -> {
            float mul = (Float) animation.getAnimatedValue();
            int alpha;
            if (toSent) alpha = adjustAlpha(orange, mul);
            else alpha = adjustAlpha(black, mul);

            v.setColorFilter(alpha, PorterDuff.Mode.SRC_ATOP);
            if (mul == 0.0) {
                v.setColorFilter(null);
            }
        });

        colorAnim.setDuration(500);
        colorAnim.start();

    }


    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        switch (requestCode) {
            case CameraActivity.REQUEST_IMAGE:
                handleCrop(resultCode, result);
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        createPlacesConfs();
        super.onCreate(savedInstanceState);
    }

    public void startMessaging(View view) {
        mFloatMenu.hideMenu(false);
        mInputEditText.setText("");
        mFloatMenu.close(true);
        mInputEditText.setVisibility(View.VISIBLE);
        UiUtils.showSoftKeyboard(view);
    }

    public void endMessaging(View view) {
        UiUtils.hideSoftKeyboard(view);
        mInputEditText.setVisibility(View.GONE);
        Message message = new Message(mInputEditText.getText().toString(), DateUtils.getCurentDate(), "message");
        persons.add(message);
        postMessage(message);
        Hawk.put(String.valueOf(persons.size()), message);
        adapter.notifyDataSetChanged();
        mRecyclerView.smoothScrollToPosition(persons.size());
        mFloatMenu.showMenu(false);

    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

    }

    @Override
    public void onPause() {
        mGoogleApiClient.disconnect();
        super.onPause();
        Hawk.put("all", persons);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    @Override
    protected void saveAvatar(Uri uri, Bitmap bitmap) {
        File file = new File(uri.getPath());
        File finalDest = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        Message image = new Message(finalDest.getAbsolutePath(), DateUtils.getCurentDate(), "image");
        persons.add(image);
        postMessage(image);
        adapter.notifyDataSetChanged();
        mRecyclerView.smoothScrollToPosition(persons.size());
        mFloatMenu.showMenu(true);

        try {
            copy(file, finalDest);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onTakePhotoCancel() {
        Log.d(TAG, "onTakePhotoCancel: ");
    }

    @Override
    protected void onPictureWasNotTaken() {
        Log.d(TAG, "onPictureWasNotTaken: ");
    }

    private void hideMenu() {
        mFloatMenu.close(false);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

}
