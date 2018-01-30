package com.sanqius.loro.chatemojidemo.app;

import android.app.Application;
import android.content.Context;

import com.sanqius.loro.chatemojidemo.emoji.EmoManager;
import com.sanqius.loro.chatemojidemo.entity.EmotionList;
import com.sanqius.loro.chatemojidemo.utils.JsonUtil;

/**
 * Created by loro on 2018/1/29.
 */

public class EmojiApplication extends Application {
    private static EmojiApplication mInstance = null;

    private static EmotionList mEmotionList = null;

    public static EmojiApplication getApplication() {
        return mInstance;
    }

    public static Context getContext() {
        return mInstance;
    }

    public static EmojiApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mEmotionList = JsonUtil.getEmotionList();

        configEmoView();
    }

    private void configEmoView() {
        new EmoManager.Builder()
                .with(getApplicationContext()) // 传递 Context
                .maxGifPerView(24)
                .emoticonDir("face") // asset 下存放表情的目录路径（asset——> configFileName 之间的路径,结尾不带斜杠）
                .sourceDir("images") // 存放 emoji 表情资源文件夹路径（emoticonDir 图片资源之间的路径,结尾不带斜杠）
                .build(); //构建 EmoManager 单列
    }

    public static EmotionList getEmotionList() {
        return mEmotionList;
    }
}
