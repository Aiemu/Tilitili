package com.example.tilitili;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.lidroid.xutils.ViewUtils;

public class HistoryActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_update);
        ViewUtils.inject(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.departments, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }
}