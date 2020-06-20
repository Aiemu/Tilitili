package com.example.tilitili;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.tilitili.data.Contants;
import com.example.tilitili.data.User;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.HttpHelper;
import com.example.tilitili.http.SpotsCallBack;
import com.example.tilitili.http.UploadHttpHelper;
import com.example.tilitili.ui.MineFragment;
import com.example.tilitili.utils.ToastUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

public class UserProfileUpdateActivity extends Activity {

    @ViewInject(R.id.edit_choose_avatar_image_view)
    private SimpleDraweeView userAvatarView;
    @ViewInject(R.id.update_info_bio)
    private TextView updateBioTextView;
    @ViewInject(R.id.update_info_nickname)
    private TextView updateNicknameTextView;
    @ViewInject(R.id.update_info_department)
    private TextView updateDepartmentTextView;
    @ViewInject(R.id.update_info_progressbar)
    ProgressBar progressBar;

    private String avatarUri;
    private UploadHttpHelper uploadHttpHelper;
    private HttpHelper httpHelper;

    private final static int SELECT_AVATAR_CODE = 459;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_update);
        ViewUtils.inject(this);
        uploadHttpHelper = new UploadHttpHelper(progressBar);
        httpHelper = HttpHelper.getInstance();
        avatarUri = "";
    }

    @OnClick(R.id.update_info_button)
    public void updateInfo(View view) {
        String nickname = updateNicknameTextView.getText().toString();
        String department = updateDepartmentTextView.getText().toString();
        String bio = updateBioTextView.getText().toString();

        User user = UserManagerApplication.getInstance().getUser();
        bio = bio.equals("") ? user.getBio() : bio;
        department = department.equals("") ? user.getDepartment() : department;
        nickname = nickname.equals("") ? user.getNickname() : nickname;
        avatarUri = avatarUri.equals("") ? user.getAvatar() : avatarUri;

        Map<String, String> map = new HashMap<>(4);
        map.put("bio", bio);
        map.put("department", department);
        map.put("nickname", nickname);
        map.put("avatar", avatarUri);

        final String finalNickname = nickname;
        final String finalDepartment = department;
        final String finalBio = bio;
        SpotsCallBack<String> stringSpotsCallBack = new SpotsCallBack<String>(this) {
            @Override
            public void onSuccess(Response response, String userString) {
                UserManagerApplication application = UserManagerApplication.getInstance();
                User user1 = application.getUser();
                user1.setNickname(finalNickname);
                user1.setDepartment(finalDepartment);
                user1.setAvatar(avatarUri);
                user1.setBio(finalBio);

                application.clearUser();
                application.putUser(user1);

                dismissDialog();
                ToastUtils.show(UserProfileUpdateActivity.this, "修改成功");
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                dismissDialog();
                ToastUtils.show(UserProfileUpdateActivity.this, errorMessage.getErrorMessage());
            }
        };
        stringSpotsCallBack.setMessage(R.string.logining);
        httpHelper.post(Contants.API.LOGIN_URL, map, stringSpotsCallBack);
    }

    @OnClick(R.id.edit_choose_avatar_image_view)
    public void uploadAvatar(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, SELECT_AVATAR_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_AVATAR_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    uploadImage(uri, SELECT_AVATAR_CODE);
                }
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void uploadImage(Uri filepath, final int code) {
        final File imageFile = new File(getRealPathFromURI(filepath));
        uploadHttpHelper.upload(uploadHttpHelper.buildRequest(imageFile, Contants.API.UploadType.IMAGE), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String mMessage = Objects.requireNonNull(response.body()).string();
                UserProfileUpdateActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(mMessage);
                            if (code == SELECT_AVATAR_CODE) {
                                avatarUri = (String) jsonObject.get("uri");
                                userAvatarView.setImageURI(Config.getFullUrl(avatarUri));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
