package com.roket.shmtzh.uchat.listener;

import java.io.File;
import java.io.IOException;

/**
 * Created by shmtzh on 7/1/16.
 */
public interface OnTakePhotoListener {

    void onTakePicture(File file) throws IOException;
}
