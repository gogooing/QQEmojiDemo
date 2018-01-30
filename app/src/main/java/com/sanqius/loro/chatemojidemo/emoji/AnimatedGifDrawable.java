package com.sanqius.loro.chatemojidemo.emoji;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by loro on 2018/1/12.
 * GifDrawable 类
 */

public class AnimatedGifDrawable extends AnimationDrawable {

    private String containerTag; // 显示此表情的界面的 Tag
    private int mCurrentIndex = 0;
    private RunGifCallBack mGifCallBack;

    public AnimatedGifDrawable(InputStream source, int bounds) {
        GifDecoder decoder = new GifDecoder();
        decoder.read(source);
        // Iterate through the gif frames, add each as animation frame
        for (int i = 0; i < decoder.getFrameCount(); i++) {
            Bitmap bitmap = decoder.getFrame(i);
            BitmapDrawable drawable = new BitmapDrawable(bitmap);
            // Explicitly set the bounds in order for the frames to display
            drawable.setBounds(0, 0, bounds, bounds);
            addFrame(drawable, decoder.getDelay(i));
            if (i == 0) {
                // Also set the bounds for this container drawable
                setBounds(0, 0, bounds, bounds);
            }
        }
    }

    /**
     * Naive method to proceed to next frame. Also notifies listener.
     */
    public void nextFrame() {
        mCurrentIndex = (mCurrentIndex + 1) % getNumberOfFrames();
    }

    /**
     * Return display duration for current frame
     */
    public int getFrameDuration() {
        return getDuration(mCurrentIndex);
    }

    /**
     * Return drawable for current frame
     */
    public Drawable getDrawable() {
        return getFrame(mCurrentIndex);
    }

    /**
     * Interface to notify listener to update/redraw
     * Can't figure out how to invalidate the drawable (or span in which it sits) itself to force redraw
     */
    public interface RunGifCallBack {
        void run();
    }

    public List<RunGifCallBack> getUpdateListener() {
        return mRunGifCallBacks;
    }

    private List<RunGifCallBack> mRunGifCallBacks = new ArrayList<>();

    public void setRunCallBack(RunGifCallBack gifCallBack) {
        if (!mRunGifCallBacks.contains(gifCallBack)) {
            mRunGifCallBacks.add(gifCallBack);
        }
//		mGifCallBack = gifCallBack;
    }

    public String getContainerTag() {
        return containerTag;
    }

    public void setContainerTag(String containerTag) {
        this.containerTag = containerTag;
    }
}
