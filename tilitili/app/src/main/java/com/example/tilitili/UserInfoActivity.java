package com.example.tilitili;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tilitili.data.Contants;
import com.example.tilitili.data.User;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.HttpHelper;
import com.example.tilitili.http.SpotsCallBack;
import com.example.tilitili.utils.ToastUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Response;

public class UserInfoActivity extends Activity {

    @ViewInject(R.id.user_info_avatar)
    private SimpleDraweeView avatarImageView;
    @ViewInject(R.id.user_info_bio_textview)
    private TextView bioTextView;
    @ViewInject(R.id.user_info_nickname_textview)
    private TextView nicknameTextView;
    @ViewInject(R.id.user_info_joinat_textview)
    private TextView joinAtTextView;
    @ViewInject(R.id.user_info_department_textview)
    private TextView departmentTextView;
    @ViewInject(R.id.user_info_username_textview)
    private TextView usernameTextView;
    @ViewInject(R.id.user_info_is_following)
    private Button button;

    private HttpHelper httpHelper = HttpHelper.getInstance();
    private int uid = -1;
    private int isFollowing = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail_info);
        uid = getIntent().getIntExtra("uid", -1);
        isFollowing = getIntent().getIntExtra("isFollowing", 0);
        ViewUtils.inject(this);
        getUserInfo();
    }

    private void getUserInfo() {
        httpHelper.get(Contants.API.GET_USER_PROFILE_URL + uid, new SpotsCallBack<String>(this) {
            @Override
            public void onSuccess(Response response, String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    User user = new User(uid);
                    user.setUsername(jsonObject.getString("username"));
                    user.setEmail(jsonObject.getString("email"));
                    user.setNickname(jsonObject.getString("nickname"));
                    user.setDepartment(jsonObject.getString("department"));
                    user.setJoinAt(jsonObject.getLong("joinAt"));
                    user.setBio(jsonObject.getString("bio"));
                    user.setAvatar(jsonObject.getString("avatar"));

                    bioTextView.setText(user.getBio());
                    nicknameTextView.setText(user.getNickname());
                    joinAtTextView.setText(user.getJoinAt());
                    departmentTextView.setText(user.getDepartment());
                    usernameTextView.setText(user.getUsername());

                    if (!user.getAvatar().equals(""))
                        avatarImageView.setImageURI(Uri.parse(Config.getFullUrl(user.getAvatar())));
                    else
                        avatarImageView.setImageURI(Uri.parse("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3927644377,808105775&fm=26&gp=0.jpg"));

                    if (isFollowing == 1) {
                        button.setText("已关注");
                    } else {
                        button.setText("未关注");
                    }
                    dismissDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                e.printStackTrace();
            }
        });
    }

    @OnClick(R.id.user_info_is_following)
    void follow(View v) {
        if (isFollowing == 1) {
            httpHelper.post(Contants.API.UNFOLLOW_USER_URL, new HashMap<String, String>() {
                {
                    put("uid", String.valueOf(uid));
                }
            }, new SpotsCallBack<String>(UserInfoActivity.this) {
                @Override
                public void onSuccess(Response response, String s) {
                    dismissDialog();
                    isFollowing = 0;
                    button.setText("未关注");
                }

                @Override
                public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                    dismissDialog();
                    e.printStackTrace();
                    ToastUtils.show(UserInfoActivity.this, errorMessage.getErrorMessage());
                }
            });
        } else {
            httpHelper.post(Contants.API.FOLLOW_USER_URL, new HashMap<String, String>() {
                {
                    put("uid", String.valueOf(uid));
                }
            }, new SpotsCallBack<String>(UserInfoActivity.this) {
                @Override
                public void onSuccess(Response response, String s) {
                    dismissDialog();
                    isFollowing = 1;
                    button.setText("已关注");
                }

                @Override
                public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                    dismissDialog();
                    e.printStackTrace();
                    ToastUtils.show(UserInfoActivity.this, errorMessage.getErrorMessage());
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
