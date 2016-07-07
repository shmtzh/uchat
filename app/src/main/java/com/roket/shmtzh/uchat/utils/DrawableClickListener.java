package com.roket.shmtzh.uchat.utils;

/**
 * Created by shmtzh on 7/3/16.
 */
public interface DrawableClickListener {
    public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
    public void onClick(DrawablePosition target);
}
