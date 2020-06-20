package com.example.tilitili;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.tilitili.data.Contants;
import com.example.tilitili.data.User;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.HttpHelper;
import com.example.tilitili.http.SpotsCallBack;
import com.example.tilitili.utils.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Response;

public class LoginActivity extends Activity {
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
        if (!UserManagerApplication.getInstance().getSessionId().equals(""))
            isLogin();
    }

    private void isLogin() {
        httpHelper.get(Contants.API.CHECK_LOGIN_URL, new SpotsCallBack<String>(this) {

            @Override
            public void onSuccess(Response response, String s) {
                Intent register_intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(register_intent);
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {

            }
        });
    }

    @OnClick(R.id.btn_login)
    public void login(View view) {
        // to test video player
//        Intent register_intent = new Intent(LoginActivity.this, VideoPlayActivity.class);
//        startActivity(register_intent);

        final String username = edit_username.getText().toString();
        final String password = edit_password.getText().toString();

        Map<String, String> map = new HashMap<>(2);
        map.put("username", username);
        map.put("password", password);

        SpotsCallBack<String> stringSpotsCallBack = new SpotsCallBack<String>(this) {
            @Override
            public void onSuccess(Response response, String userString) {
                User user = null;
                UserManagerApplication application = UserManagerApplication.getInstance();
                try {
                    JSONObject jsonObject = new JSONObject(userString);
                    user = new User(jsonObject.getInt("uid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissDialog();
                    ToastUtils.show(LoginActivity.this, "登录失败");
                    return;
                }

                Headers headers = response.headers();
                List<String> cookies = headers.values("Set-Cookie");
                try {
                    String session = (String) cookies.get(0);
                    String sessionid = session.substring(0, session.indexOf(";"));
                    application.putSessionId(sessionid);
                    application.putUser(user);
                    Intent register_intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(register_intent);
                } catch (IndexOutOfBoundsException e) {
                    dismissDialog();
                    e.printStackTrace();
                    ToastUtils.show(LoginActivity.this, "登录失败");
                }
                dismissDialog();
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                dismissDialog();
                ToastUtils.show(LoginActivity.this, errorMessage.getErrorMessage());
            }
        };
        stringSpotsCallBack.setMessage(R.string.logining);
        httpHelper.post(Contants.API.LOGIN_URL, map, stringSpotsCallBack);
    }

    @OnClick(R.id.register_button)
    public void register(View view) {
        Intent register_intent = new Intent(this, RegisterActivity.class);
        startActivity(register_intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.text_reset)
    public void reset(View view) {
        Intent reset_intent = new Intent(this, ResetPWActivity.class);
        startActivity(reset_intent);
    }
}