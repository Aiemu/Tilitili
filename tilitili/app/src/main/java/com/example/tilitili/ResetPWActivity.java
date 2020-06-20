package com.example.tilitili;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.tilitili.data.Contants;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.HttpHelper;
import com.example.tilitili.http.SpotsCallBack;
import com.example.tilitili.utils.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

public class ResetPWActivity extends Activity {

    @ViewInject(R.id.reset_username)
    private EditText usernameEditView;
    @ViewInject(R.id.reset_password)
    private EditText passwordEditView;
    @ViewInject(R.id.reset_password_dup)
    private EditText passwordEditViewDup;

    HttpHelper httpHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        ViewUtils.inject(this);
        httpHelper = HttpHelper.getInstance();
    }

    @OnClick(R.id.reset_button)
    public void reset(View v) {
        final String username = usernameEditView.getText().toString();
        final String password = passwordEditView.getText().toString();
        final String password_dup = passwordEditViewDup.getText().toString();

        if (!password.equals(password_dup)) {
            ToastUtils.show(this, "两次密码不一致，请重新输入");
            return;
        }

        Map<String, String> params = new HashMap<>(2);
        params.put("username", username);
        params.put("password", password);

        httpHelper.post(Contants.API.FORGET_PASSWORD, params, new SpotsCallBack<String>(this) {
            @Override
            public void onSuccess(Response response, String s) {
                dismissDialog();
                ToastUtils.show(this.getContext(), "邮件已发送成功，请前往邮箱查看。");
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                dismissDialog();
                ToastUtils.show(this.getContext(), "重置失败，请重新尝试。");
            }
        });
    }
}