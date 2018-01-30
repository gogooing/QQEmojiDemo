package com.sanqius.loro.chatemojidemo.emoji;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import com.sanqius.loro.chatemojidemo.utils.KeyboardUtils;

import java.util.HashSet;
import java.util.regex.Matcher;

/**
 * Created by loro on 2018/1/12.
 * description ：表情文字转表情图的转换类
 */

public class EmoChangeUtil {

    private static EmoChangeUtil mEmoChangeUtil;
    private int MAX_PER_VIEW = 50;//控制一个view超过多少个表情的时候就显示静态图
    private GifRunnable mGifRunnable;
    private Handler mHandler = new Handler();
    /* faceInfo 用于存放表情的起始位置和对应文字
     * 每一个表情在字符串中的起始位置不同因此用位置数组作为 key 避免相同表情时覆盖信息
     */
    private HashSet<int[]> faceInfo = new HashSet<>();

    public static EmoChangeUtil getInstance() {
        if (mEmoChangeUtil == null) {
            synchronized (EmoChangeUtil.class) {
                if (mEmoChangeUtil == null) {
                    mEmoChangeUtil = new EmoChangeUtil();
                }
            }
        }
        mEmoChangeUtil.MAX_PER_VIEW = EmoManager.getInstance().getMaxGifPerView();
        return mEmoChangeUtil;
    }

    /**
     * 设置单个 TextView 最多显示的动态图个数
     * 超过这个个数则所有表情显示为静态图
     *
     * @param maxGifPerView 阈值
     */
    public EmoChangeUtil setMaxGifPerView(int maxGifPerView) {
        MAX_PER_VIEW = maxGifPerView;
        return this;
    }

    /**
     * 图文混排工具方法
     *
     * @param //       classTag 当前显示界面的 Tag（一般是Activity），
     *                 用于在退出界面时将界面内的动图对象从 Runable 中移除
     * @param value    要转换的文字
     * @param callBack 动图刷新回调
     * @return 图文混排后显示的内容
     */
    public SpannableString makeGifSpannable(Context context, String value,
                                            AnimatedGifDrawable.RunGifCallBack callBack, int width) {
        if (TextUtils.isEmpty(value)) {
            value = "";
        }
        String classTag = ((Activity) context).getLocalClassName();
        faceInfo.clear();
        int start, end;
        SpannableString spannableString = new SpannableString(value);
        Matcher matcher = EmoManager.getInstance().getPattern().matcher(value);
        /*
         单个 TextView 中显示动态图太多刷新绘制比较消耗内存，
         因此做类似QQ动态表情的限制，
         超过 MAX_PER_VIEW 个就显示静态表情
        */
        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            faceInfo.add(new int[]{start, end});
        }
        int faces = faceInfo.size();
        for (int[] faceIndex : faceInfo) {
            if (faces > MAX_PER_VIEW) {
                Drawable drawable = getEmotDrawable(EmoManager.getInstance().getContext(), value.substring(faceIndex[0], faceIndex[1]), 0);
                if (drawable != null) {
                    ImageSpan span = new ImageSpan(drawable);
                    spannableString.setSpan(span, faceIndex[0], faceIndex[1], Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
//                return getSPBuilderPNG( context,value, width);
            } else {
                AnimatedGifDrawable gifDrawable = EmoticonManager.getInstance().getDrawableGif(EmoManager.getInstance().getContext(),
                        value.substring(faceIndex[0], faceIndex[1]));
//                Log.e("ej","gifDrawable = "+gifDrawable);
                if (gifDrawable != null) {
                    gifDrawable.setRunCallBack(callBack);
                    gifDrawable.setContainerTag(classTag);
                    // 如果动态图执行类不存在，则创建一个新的执行类。存在则将此次转化的表情加入执行类中
                    if (mGifRunnable == null) {
                        mGifRunnable = new GifRunnable(gifDrawable, mHandler);
//                        Log.e("ej","创建了一次");
                    } else {
                        mGifRunnable.addGifDrawable(gifDrawable);
//                        Log.e("ej","添加了"+gifDrawable);
                    }
                    AnimatedImageSpan span = new AnimatedImageSpan(gifDrawable, mGifRunnable);
                    spannableString.setSpan(span, faceIndex[0], faceIndex[1], Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                } else { //不存在动态图资源时用静态图代替
                    Drawable drawable = getEmotDrawable(EmoManager.getInstance().getContext(), value.substring(faceIndex[0], faceIndex[1]), 0);
                    if (drawable != null) {
                        ImageSpan span = new ImageSpan(drawable);
                        spannableString.setSpan(span, faceIndex[0], faceIndex[1], Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }
//                    return getSPBuilderPNG( context,value, width);
                }
            }
        }
        return spannableString;
    }


    /**
     * 将带表情的文本转换成图文混排的文本（动态图也转换为静态图）
     *
     * @param value 待转换文本
     * @return 转换结果
     */

    public SpannableString makeEmojiSpannable(String value, int width) {
        if (TextUtils.isEmpty(value)) {
            value = "";
        }
        int start;
        int end;
        SpannableString mSpannableString = new SpannableString(value);
        Matcher matcher = EmoManager.getInstance().getPattern().matcher(value);
        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            String emot = value.substring(start, end);
            Drawable drawable = getEmotDrawable(EmoManager.getInstance().getContext(), emot, width);
            if (drawable != null) {
                ImageSpan span = new ImageSpan(drawable);
                mSpannableString.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        return mSpannableString;
    }

    /**
     * 将 EditText 文本替换为静态表情图
     */
    public void replaceEmoticons(Editable editable, int start, int count) {
        if (count <= 0 || editable.length() < start + count)
            return;
        CharSequence s = editable.subSequence(start, start + count);
        Matcher matcher = EmoManager.getInstance().getPattern().matcher(s);
        while (matcher.find()) {
            int from = start + matcher.start();
            int to = start + matcher.end();
            String emot = editable.subSequence(from, to).toString();
            Drawable d = getEmotDrawable(EmoManager.getInstance().getContext(), emot, 0);
            if (d != null) {
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
                editable.setSpan(span, from, to, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    /**
     * 获取静态表情 Drawable 对象
     *
     * @param context 上下文
     * @param text    表情对应的文本信息
     * @return 静态表情 Drawable对象
     */
    public Drawable getEmotDrawable(Context context, String text, int width) {
        Drawable drawable = EmoticonManager.getInstance().getDrawable(context, text);
        int size;
        if (width > 0) {
            size = width;
        } else {
            size = KeyboardUtils.dip2px(context, EmoManager.getInstance().getDefaultEmoBoundsDp());
        }
        if (drawable != null) {
            drawable.setBounds(0, 0, size, size);
        }
        return drawable;
    }

    /**
     * 开始执行某一个界面的动态表情显示
     *
     * @param activityTag 停止界面 activity 的 Tag
     */
    public void resumeGif(String activityTag) {
        if (mGifRunnable != null) {
            mGifRunnable.resumeHandler(activityTag);
        }
    }

    /**
     * 暂停当前界面的动态表情执行
     */
    public void pauseGif() {
        if (mGifRunnable != null) {
            mGifRunnable.pauseHandler();
        }
    }

    /**
     * 停止某个界面的动态表情执行,将任务移除
     *
     * @param activityTag 停止界面 activity 的 Tag
     */
    public void clearGif(String activityTag) {
        if (mGifRunnable != null) {
            mGifRunnable.clearHandler(activityTag);
        }
    }
//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    private SpannableString getSPBuilderPNG(Context context,final String textContent, int width) {
//        SpannableString value = SpannableString.valueOf(textContent);
//        EmotionList mEmotionList = IMApp.getInstance().getEmotionList();
//        if (mEmotionList == null) {
//            return value;
//        } else {
//
//        }
//        //String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
//        String regex = "(\\[)[\u4e00-\u9fa5a-zA-Z]{1,}(\\])";//(\\[(.)\\])";
//        Pattern p = Pattern.compile(regex);
//        Matcher m = p.matcher(textContent);
//        while (m.find()) {
//            String tempText = m.group();
//            Emotion emotion = mEmotionList.GetEmotion(tempText);
//            try {
//                //PNG
//                String png = emotion.GetFilePath();
//                Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(png));
//                value.setSpan(new ImageSpan(context, zoomImage(bitmap, width, width)), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            } catch (Exception e) {
//
//                e.printStackTrace();
//            }
//        }
//        return value;
//    }
//    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
//                                   double newHeight) {
//        // 获取这个图片的宽和高
//        float width = bgimage.getWidth();
//        float height = bgimage.getHeight();
//        // 创建操作图片用的matrix对象
//        Matrix matrix = new Matrix();
//        // 计算宽高缩放率
//        float scaleWidth = ((float) newWidth) / width;
//        float scaleHeight = ((float) newHeight) / height;
//        // 缩放图片动作
//        matrix.postScale(scaleWidth, scaleHeight);
//        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
//                (int) height, matrix, true);
//        return bitmap;
//    }
}
