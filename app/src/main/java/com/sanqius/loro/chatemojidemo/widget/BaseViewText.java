package com.sanqius.loro.chatemojidemo.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;

import com.sanqius.loro.chatemojidemo.app.EmojiApplication;
import com.sanqius.loro.chatemojidemo.emoji.AnimatedGifDrawable;
import com.sanqius.loro.chatemojidemo.emoji.EmoChangeUtil;
import com.sanqius.loro.chatemojidemo.entity.Emotion;
import com.sanqius.loro.chatemojidemo.entity.EmotionList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by loro on 2018/1/29.
 */

public class BaseViewText extends AppCompatTextView {


    protected boolean isInitFinished = false;
    protected Context mContext;

    public BaseViewText(Context context) {
        super(context);
    }

    public BaseViewText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public BaseViewText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();

    }

    public void initView() {

    }

    public void setImageText(CharSequence text) {
        //SpannableStringBuilder sb = getSPBuilderPNG((String) text);
        SpannableString sb = getSPBuilderPNG((String) text);
        this.setText(sb);
    }

    private static final int mMaxShowGifCount = 10;

//    }

    public void setGifText(CharSequence text, int width) {
        SpannableString sb = EmoChangeUtil.getInstance().makeGifSpannable(mContext, (String) text, new AnimatedGifDrawable.RunGifCallBack() {
            @Override
            public void run() {
                postInvalidate();
            }
        },width);
        this.setText(sb);
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private SpannableString getSPBuilderPNG(final String textContent, int width) {
        Context context = getContext();
        SpannableString value = SpannableString.valueOf(textContent);
        EmotionList mEmotionList = EmojiApplication.getInstance().getEmotionList();
        if (mEmotionList == null) {
            return value;
        } else {

        }
        //String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
        String regex = "(\\[)[\u4e00-\u9fa5a-zA-Z]{1,}(\\])";//(\\[(.)\\])";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(textContent);
        while (m.find()) {
            String tempText = m.group();
            Emotion emotion = mEmotionList.GetEmotion(tempText);
            try {
                //PNG
                String png = emotion.GetFilePath();
                Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(png));
                value.setSpan(new ImageSpan(context, zoomImage(bitmap, width, width)), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return value;
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    private SpannableString getSPBuilderPNG(final String textContent) {
        Context context = getContext();
        SpannableString value = SpannableString.valueOf(textContent);
        EmotionList mEmotionList = EmojiApplication.getInstance().getEmotionList();
        if (mEmotionList == null) {
            return value;
        } else {

        }
        //String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
        String regex = "(\\[)[\u4e00-\u9fa5a-zA-Z]{1,}(\\])";//(\\[(.)\\])";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(textContent);
        while (m.find()) {
            String tempText = m.group();
            Emotion emotion = mEmotionList.GetEmotion(tempText);
            if (emotion != null) {
                try {
                    //PNG
                    String png = emotion.GetFilePath();
                    value.setSpan(new ImageSpan(context, BitmapFactory.decodeStream(context.getAssets().open(png))), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

        }
        return value;
    }

    public void refreshView(Context context) {
    }

    protected boolean isPause = false;

    public void setPause(Context context, boolean isPause) {
        this.isPause = isPause;
    }

    public boolean getPause() {
        return this.isPause;
    }

    protected boolean isBusy = false;

    public void setBusy(Context context, boolean isBusy) {
        this.isBusy = isBusy;
    }

    public boolean getBusy() {
        return this.isBusy;
    }

    protected Object mData;

    public void setData(Object obj) {
        if (this.mData == obj) {
            return;
        }
        this.mData = obj;
        if (isInitFinished) {
            fillView();
        }
    }

    protected void fillView() {
    }

    public void refresh(Context context, Object... params) {
    }

    public void clear() {
        setText(null);
//        gifUpdateListener = null;
        //clearDrawable();
        //mContext = null;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onUserInteraction(view);
        }
    };


    protected View.OnClickListener getViewClickListener() {
        return onClickListener;
    }

    public void onUserInteraction(View view) {

    }
}
