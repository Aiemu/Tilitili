package com.example.tilitili;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class ChatActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_user);
        ViewUtils.inject(this);
    }

    @OnClick(R.id.chat_user_title_bar_back)
    public void back(View v) {
        finish();
    }
}
