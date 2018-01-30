package com.sanqius.loro.chatemojidemo.emoji;

import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by loro on 2018/1/12.
 * gif 运行类
 */


public class GifRunnable implements Runnable {
    private Map<String, List<AnimatedGifDrawable>> mGifDrawableMap = new HashMap<>();// 用来存储每个 Activity 显示的Drawable
    private Map<String, List<AnimatedGifDrawable.RunGifCallBack>> listenersMap = new HashMap<>();
    private Handler mHandler;
    private boolean isRunning = false;
    private String currentActivity = null;
    private long frameDuration = 300;

    public GifRunnable(AnimatedGifDrawable gifDrawable, Handler handler) {
        addGifDrawable(gifDrawable);
        mHandler = handler;
    }



    @Override
    public void run() {
        isRunning = true;
        if (currentActivity != null) {
            List<AnimatedGifDrawable> runningDrawables = mGifDrawableMap.get(currentActivity);
            if (runningDrawables != null) {
                for (AnimatedGifDrawable gifDrawable : runningDrawables) {
                    List<AnimatedGifDrawable.RunGifCallBack> mCallBackList = gifDrawable.getUpdateListener();
                    List<AnimatedGifDrawable.RunGifCallBack> runningListener = listenersMap.get(currentActivity);
                    if (runningListener != null) {
                        for (int i = 0; i < mCallBackList.size(); i++) {
                            if (!runningListener.contains(mCallBackList.get(i))) {
                                runningListener.add(mCallBackList.get(i));
                            }
                        }
                    } else {
                        // 为空时肯定不存在直接添加
                        runningListener = new ArrayList<>();
                        runningListener.addAll(mCallBackList);
                        listenersMap.put(currentActivity, runningListener);
                    }
                    gifDrawable.nextFrame();

                }
                for (AnimatedGifDrawable.RunGifCallBack callBack : listenersMap.get(currentActivity)) {
                    if (callBack != null) {

                        callBack.run();
                    }
                }
                frameDuration = 80;//固定Emoji表情帧数为80
//                Log.e("frameDuration", "frameDuration="+frameDuration + "");
            }
        }
        mHandler.postDelayed(this, frameDuration);
    }


    public void addGifDrawable(AnimatedGifDrawable gifDrawable) {
        List<AnimatedGifDrawable> runningDrawables = mGifDrawableMap.get(gifDrawable.getContainerTag());

        if (runningDrawables != null) {
            for (int i = 0; i < runningDrawables.size(); i++) {
//                Log.e("addGifDrawable", runningDrawables.get(i) + "");
            }

        }

        if (runningDrawables != null) {
            if (!runningDrawables.contains(gifDrawable)) {
//                Log.e("addGifDrawable1", gifDrawable + "线程：" + Thread.currentThread().getName() + "runningDrawables" + runningDrawables);
                runningDrawables.add(gifDrawable);
//                Log.e("runningDrawables", runningDrawables.size() + "");

                for (int i = 0; i < runningDrawables.size(); i++) {
//                    Log.e("runningDrawables有", runningDrawables.get(i) + "");
                }

            } else {
                List<AnimatedGifDrawable.RunGifCallBack> runningListener = listenersMap.get(currentActivity);
                if (runningListener != null) {
                    List<AnimatedGifDrawable.RunGifCallBack> mCallBackList = gifDrawable.getUpdateListener();
                    for (int i = 0; i < mCallBackList.size(); i++) {
                        if (!runningListener.contains(mCallBackList.get(i))) {
                            runningListener.add(mCallBackList.get(i));
                        }
                    }
//                    if (!runningListener.contains(gifDrawable.getUpdateListener()) ) {
//                        runningListener.add(gifDrawable.getUpdateListener());
//                    }
                } else {
                    runningListener = new ArrayList<>();
                    runningListener.addAll(gifDrawable.getUpdateListener());
                    listenersMap.put(currentActivity, runningListener);
                }
            }
        } else {
            // 为空时肯定不存在直接添加
//            Log.e("addGifDrawable", "为空");
            runningDrawables = new ArrayList<>();
            runningDrawables.add(gifDrawable);
            mGifDrawableMap.put(gifDrawable.getContainerTag(), runningDrawables);
        }
    }

    /**
     * 使用了表情转换的界面退出时调用，停止动态图handler
     */
    public void pauseHandler() {
        //暂停时空执行
        currentActivity = null;
    }

    /**
     * 使用了表情转换的界面退出时调用，停止动态图handler
     */
    public void clearHandler(String activityName) {
        if (activityName.equals(currentActivity)) {
            currentActivity = null;
        }
//        currentActivity = null;
        //清除当前页的数据
        List<AnimatedGifDrawable> ml = mGifDrawableMap.remove(activityName);
        if(null != ml){
            if(ml.size()>0){
                for (int i = 0; i < ml.size(); i++) {
                    List<AnimatedGifDrawable.RunGifCallBack> cb =   ml.get(i).getUpdateListener();
                    cb.clear();
                }
            }
        }
        // 当退出当前Activity后没表情显示时停止 Runable 清除所有动态表情数据
        listenersMap.remove(activityName);
        if (mGifDrawableMap.size() == 0) {
            clearAll();
        }
    }

    private void clearAll() {
        mHandler.removeCallbacks(this);
        mHandler.removeCallbacksAndMessages(null);
        mGifDrawableMap.clear();
        isRunning = false;
    }

    /**
     * 启动运行
     */
    public void resumeHandler(String activityName) {
        currentActivity = activityName;
        if (mGifDrawableMap != null && mGifDrawableMap.size() > 0 && !isRunning) {
            run();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}
