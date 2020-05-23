package com.example.tilitili;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tilitili.data.Contants;
import com.example.tilitili.data.User;
import com.example.tilitili.http.HttpHelper;
import com.example.tilitili.http.SpotsCallBack;
import com.example.tilitili.utils.ToastUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

public class LoginActivity extends Activity {
    private Gson mGson = new Gson();
    private HttpHelper httpHelper = HttpHelper.getInstance();

    @ViewInject(R.id.edit_username)
    private EditText edit_username;
    @ViewInject(R.id.edit_password)
    private EditText edit_password;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ViewUtils.inject(this);
    }


    @OnClick(R.id.btn_login)
    public void login(View view) {
        final String username = edit_username.getText().toString();
        final String password = edit_password.getText().toString();

        Map<String, String> map = new HashMap<>(2);
        map.put("username", username);
        map.put("password", password);
        SpotsCallBack<User> stringSpotsCallBack = new SpotsCallBack<User>(this) {
            @Override
            public void onSuccess(Response response, User user) {
                UserManagerApplication application = UserManagerApplication.getInstance();
                application.putUser(user);
                Intent register_intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(register_intent);
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                dismissDialog();
                ToastUtils.show(LoginActivity.this, R.string.login_error);
            }
        };
        stringSpotsCallBack.setMessage(R.string.logining);
        httpHelper.post(Contants.API.LOGIN_URL, map, stringSpotsCallBack);
    }

    @OnClick(R.id.btn_register)
    public void register(View view) {
        Intent register_intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(register_intent);
    }

    @OnClick(R.id.text_reset)
    public void reset(View view) {
        Intent reset_intent = new Intent(LoginActivity.this, ResetPWActivity.class);
        startActivity(reset_intent);
    }
}