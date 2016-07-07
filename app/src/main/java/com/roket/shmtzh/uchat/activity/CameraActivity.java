package com.roket.shmtzh.uchat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;
import com.roket.shmtzh.uchat.R;
import com.roket.shmtzh.uchat.fragment.BaseFeedFragment;
import com.roket.shmtzh.uchat.fragment.CameraFragment;
import com.roket.shmtzh.uchat.listener.OnTakePhotoListener;
import com.roket.shmtzh.uchat.utils.ActivityUtils;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CameraActivity extends Activity implements OnTakePhotoListener {

    public static final int REQUEST_IMAGE = 91;
    public static final String EXTRA_URI = "EXTRA_URI";
    public static final String EXTRA_URL = "EXTRA_URL";

    private static final String TAG = "CameraActivity";
    private final static int REQUEST_CAMERA = 0;
    private final static int SELECT_FILE = 1;

    private static final String ACTION_PHOTO = "ACTION_PHOTO";
    private static final String ACTION_GALLERY = "ACTION_GALLERY";
    String destinationUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Intent intent;
        switch (getIntent().getAction()) {
            case ACTION_PHOTO:
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, CameraFragment.newInstance(this))
                        .commit();
                break;
            case ACTION_GALLERY:
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                break;
        }
    }

    private static void selectImage(final android.support.v4.app.Fragment fragment) {
        final CharSequence[] items = {"Take Photo", "Choose From Library", "Cancel"};
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
        builder.setCancelable(false);
        builder.setTitle("Pick Image");
        builder.setItems(items, (dialog1, item) -> {
            Intent intent = new Intent(fragment.getContext(), CameraActivity.class);
            switch (item) {
                case 0:
                    intent.setAction(ACTION_PHOTO);
                    ActivityUtils.startAsPopupForResult(fragment, intent, REQUEST_IMAGE);
                    break;
                case 1:
                    intent.setAction(ACTION_GALLERY);
                    ActivityUtils.startAsPopupForResult(fragment, intent, REQUEST_IMAGE);
                    break;
                case 2:
                    dialog1.dismiss();
                    ((BaseFeedFragment) fragment).onTakePhotoCancel();
                    break;
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_FILE:
                    onSelectFromGalleryResult(result);
                    break;
                case REQUEST_CAMERA:
                    Bitmap bitmap = (Bitmap) result.getExtras().get("data");
                    onCaptureImageResult(bitmap);
                    break;
                case Crop.REQUEST_PICK:
                    beginCrop(result.getData());
                    break;
                case Crop.REQUEST_CROP:
                    handleCrop(resultCode, result);
                    break;
            }
        } else {
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {

            Uri uri = Crop.getOutput(result);
            Intent intent = new Intent();
            intent.putExtra(EXTRA_URI, uri);
            setResult(RESULT_OK, intent);
            finish();
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Crop failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void onCaptureImageResult(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        destinationUrl = destination.getAbsolutePath();
        Hawk.put("url", destinationUrl);

        FileOutputStream fo = null;
        try {
            if (destination.createNewFile()) {
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
            } else {
                Toast.makeText(getApplicationContext(), "not possible to create the file.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fo != null) {
                try {
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        beginCrop(destination);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        if (selectedImageUri.toString().startsWith("content://")) {
            onCaptureImageResult(getImage(getApplicationContext(), selectedImageUri));
        } else {
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = getContentResolver().query(selectedImageUri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);
                cursor.close();

                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                onCaptureImageResult(bm);
            } else {
                Log.e(TAG, "onSelectFromGalleryResult: Cursor == null");
            }
        }

    }

    private void beginCrop(Uri source) {
        File file = new File(source.getPath());
        if (file.exists()) {
            Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
            Crop.of(source, destination).asSquare().start(this, Crop.REQUEST_CROP);
        } else {
            Log.e(TAG, "beginCrop: file not exist");
        }
    }

    private void beginCrop(File file) {
        if (file.exists()) {
            Uri source = Uri.fromFile(file);
            Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
            Crop.of(source, destination).asSquare().start(this, Crop.REQUEST_CROP);
        } else {
            Log.e(TAG, "beginCrop: file not exist");
        }
    }


    private void beginCrop(File file, String dest) throws IOException {
        if (file.exists()) {
            Uri source = Uri.fromFile(file);
            Hawk.put("url", destinationUrl);

            Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
            Crop.of(source, destination).asSquare().start(this, Crop.REQUEST_CROP);
        } else {
            Log.e(TAG, "beginCrop: file not exist");
        }
    }


    @Override
    public void onTakePicture(File file) throws IOException {
        beginCrop(file, file.getAbsolutePath());
    }

    public static Bitmap getImage(Context context, Uri uri) {
        Bitmap bmp = null;
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                bmp = BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bmp;
    }
}
