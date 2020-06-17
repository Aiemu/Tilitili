package com.example.tilitili.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tilitili.EditorActivity;
import com.example.tilitili.R;
import com.example.tilitili.data.Contants;
import com.example.tilitili.data.User;
import com.example.tilitili.data.UserLocalData;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.HttpHelper;
import com.example.tilitili.http.SpotsCallBack;
import com.example.tilitili.utils.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

public class MineFragment extends BaseFragment {

    HttpHelper httpHelper;

    @ViewInject(R.id.user_profile_bio_textview)
    private TextView bioTextView;
    @ViewInject(R.id.user_profile_nickname_textview)
    private TextView nicknameTextView;
    @ViewInject(R.id.user_profile_joinat_textview)
    private TextView joinAtTextView;
    @ViewInject(R.id.user_profile_department_textview)
    private TextView departmentTextView;
    @ViewInject(R.id.user_profile_organization_textview)
    private TextView organizationTextView;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void init() {
        httpHelper = HttpHelper.getInstance();
        User user = UserLocalData.getUser(this.getContext());
        String url = Contants.API.getUrlWithID(Contants.API.GET_USER_PROFILE_URL, String.valueOf(user.getUserId()));
        httpHelper.get(url, new SpotsCallBack<String>(this.getContext()) {
            @Override
            public void onSuccess(Response response, String userString) {
                try {
                    JSONObject jsonObject = new JSONObject(userString);
                    User user = UserLocalData.getUser(this.getContext());
                    assert user != null;
                    user.setBio(jsonObject.getString("bio"));
                    user.setEmail(jsonObject.getString("email"));
                    user.setJoinAt(jsonObject.getLong("joinAt"));
                    user.setDepartment(jsonObject.getString("department"));
                    user.setOrganization(jsonObject.getString("organization"));

                    bioTextView.setText(user.getBio());
                    nicknameTextView.setText(user.getNickname());
                    joinAtTextView.setText(user.getJoinAt());
                    departmentTextView.setText(user.getDepartment());
                    organizationTextView.setText(user.getOrganization());

                    // 更新本地user
                    UserLocalData.updateUser(this.getContext(), user);
                    dismissDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                ToastUtils.show(this.getContext(), errorMessage.getErrorMessage());
                dismissDialog();
            }
        });
    }

    @OnClick(R.id.user_profile_submit)
    void goToSubmit(View view) {
        Intent intent = new Intent(this.getContext(), EditorActivity.class);
        startActivity(intent);
    }
}
