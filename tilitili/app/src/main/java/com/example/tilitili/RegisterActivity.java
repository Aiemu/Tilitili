package com.example.tilitili;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.tilitili.data.Contants;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.HttpHelper;
import com.example.tilitili.http.SpotsCallBack;
import com.example.tilitili.utils.CaptchaUtil;
import com.example.tilitili.utils.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

public class RegisterActivity extends Activity {

    @ViewInject(R.id.register_nickname_edit_text)
    private EditText edit_nickname;
    @ViewInject(R.id.register_username_edit_text)
    private EditText edit_username;
    @ViewInject(R.id.register_password_edit_text)
    private EditText edit_password;
    @ViewInject(R.id.register_duplicate_password_edit_text)
    private EditText edit_password_con;
    @ViewInject(R.id.register_email_edit_text)
    private EditText edit_email;
    @ViewInject(R.id.register_captcha_edit_text)
    private EditText edit_captcha;
    @ViewInject(R.id.register_captcha_image_view)
    private ImageView captcha_view;

    CaptchaUtil captchaUtil;
    HttpHelper httpHelper;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        ViewUtils.inject(this);
        captchaUtil = CaptchaUtil.getInstance();
        httpHelper = HttpHelper.getInstance();
        captcha_view.setImageBitmap(captchaUtil.createBitmap());
    }

    @OnClick(R.id.register_button)
    public void register(View view) {
        String username = edit_username.getText().toString();
        String password = edit_password.getText().toString();
        String password_con = edit_password_con.getText().toString();
        String nickname = edit_nickname.getText().toString();
        String email = edit_email.getText().toString();
        String captcha = edit_captcha.getText().toString();
        if (!captchaUtil.verifyCaptcha(captcha)) {
            ToastUtils.show(this, "验证码不正确，请重新输入。");
            captcha_view.setImageBitmap(captchaUtil.createBitmap());
        } else if (!password.equals(password_con)) {
            ToastUtils.show(this, "两次密码不一致，请重新输入。");
        } else if (!email.endsWith("@mails.tsinghua.edu.cn") && !email.endsWith("@mail.tsinghua.edu.cn")) {
            ToastUtils.show(this, "请输入正确的清华邮箱");
        } else {
            Map<String, String> map = new HashMap<>(4);
            map.put("username", username);
            map.put("password", password);
            map.put("email", email);
            map.put("nickname", nickname);
            SpotsCallBack<String> stringSpotsCallBack = new SpotsCallBack<String>(this) {
                @Override
                public void onSuccess(Response response, String blank) {
                    Intent register_intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    ToastUtils.show(RegisterActivity.this, R.string.register_success);
                    startActivity(register_intent);
                }

                @Override
                public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                    dismissDialog();
                    ToastUtils.show(RegisterActivity.this, errorMessage.getErrorMessage());
                }
            };
            stringSpotsCallBack.setMessage(R.string.registering);
            httpHelper.post(Contants.API.REGISTER_URL, map, stringSpotsCallBack);
        }
    }

    @OnClick(R.id.register_return_login_text)
    public void returnLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.register_captcha_image_view)
    public void updateCaptcha(View view) {
        captcha_view.setImageBitmap(captchaUtil.createBitmap());
    }
}