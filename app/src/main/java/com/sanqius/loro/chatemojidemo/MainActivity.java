package com.sanqius.loro.chatemojidemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sanqius.loro.chatemojidemo.widget.BaseViewText;

public class MainActivity extends AppCompatActivity {

    protected BaseViewText mTvEmoji;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvEmoji = (BaseViewText) findViewById(R.id.main_tv_emoji);
        mTvEmoji.setGifText("[撇嘴]Hello World![撇嘴]\n[色]Hello World![色]\n[發呆]Hello World![發呆]\n[得意]Hello World![得意]\n[害羞]Hello World![害羞]\n[閉嘴]Hello World![閉嘴]\n[睡]Hello World![睡]\n[尷尬]Hello World![尷尬]\n[發怒]Hello World![發怒]\n[調皮]Hello World![調皮]\n[呲牙]Hello World![呲牙]\n[難過]Hello World![難過]",getResources().getDimensionPixelSize(R.dimen.gif_width));
    }
}
