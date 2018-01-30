package com.sanqius.loro.chatemojidemo.utils;

import com.sanqius.loro.chatemojidemo.app.EmojiApplication;
import com.sanqius.loro.chatemojidemo.entity.EmotionList;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by msdnet on 2018/1/29.
 */

public class JsonUtil {

    public static EmotionList getEmotionList() {
        String reString = getAssestsFile("EmotionCode");
        if (reString != null && !reString.equals("") && !reString.equals("null")) {
            //KakaLogUtils.e("请求的 url = " + reString );
            return new EmotionList(reString);
        }
        return null;
    }

    /**
     * @param string assests 中的文件
     * @return
     */
    public static String getAssestsFile(String string) {
        try {
            // Return an AssetManager instance for your application's package
            InputStream is = EmojiApplication.getApplication().getResources()
                    .getAssets().open(string);
            int size = is.available();

            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Convert the buffer into a string.
            String text = new String(buffer, "UTF-8");
            return text;
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
    }
}
