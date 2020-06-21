package com.example.tilitili;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.example.tilitili.data.Contants;
import com.example.tilitili.data.Plate;
import com.example.tilitili.data.User;
import com.example.tilitili.data.UserLocalData;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.HttpHelper;
import com.example.tilitili.http.SpotsCallBack;
import com.example.tilitili.http.UploadHttpHelper;
import com.example.tilitili.utils.ToastUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nanchen.compresshelper.CompressHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class EditorActivity extends Activity {
    @ViewInject(R.id.editor)
    private RichEditor mEditor;
    @ViewInject(R.id.progressBar)
    private ProgressBar progressBar;
    @ViewInject(R.id.edit_choose_cover_image_view)
    private ImageView cover_image_view;
    @ViewInject(R.id.text_title_edit_text)
    private EditText title_edit_text;
    @ViewInject(R.id.text_introduction_edit_text)
    private EditText introduction_edit_text;
    @ViewInject(R.id.edit_choose_plate_spinner)
    private Spinner plate_spinner;
    @ViewInject(R.id.submission_type_choose)
    private TextSwitcher submission_choose_text_switcher;
    @ViewInject(R.id.text_editor_layout)
    private LinearLayout text_editor_layout;
    @ViewInject(R.id.video_editor_layout)
    private LinearLayout video_editor_layout;
    @ViewInject(R.id.edit_choose_video_text_view)
    private TextView video_name_text_view;

    private static final int SELECT_PHOTO_CODE = 879;
    private static final int SELECT_COVER_CODE = 880;
    private static final int SELECT_VIDEO_CODE = 881;

    private HttpHelper httpHelper;
    private UploadHttpHelper uploadHttpHelper;
    private User user;

    private String html_text = "";
    private String cover_uri = "";
    private String html_uri = "";
    private String video_uri = "";
    private Uri video_upload_uri;
    private int platePosition = 0;
    private int choose_count = 0;
    private List<Plate> plates = new ArrayList<>();
    private List<String> plateTitles = new ArrayList<>();
    DisplayMetrics metrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.text_editor);
        ViewUtils.inject(this);

        httpHelper = HttpHelper.getInstance();
        uploadHttpHelper = new UploadHttpHelper(progressBar);
        user = UserLocalData.getUser(this);
        setPlateSpinner();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */}

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                    }
                }).check();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */}

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                    }
                }).check();

        submission_choose_text_switcher.setText("上传投稿");
        Animation textAnimationIn = AnimationUtils.
                loadAnimation(this, android.R.anim.fade_in);
        textAnimationIn.setDuration(200);

        Animation textAnimationOut = AnimationUtils.
                loadAnimation(this, android.R.anim.fade_out);
        textAnimationOut.setDuration(200);

        submission_choose_text_switcher.setInAnimation(textAnimationIn);
        submission_choose_text_switcher.setOutAnimation(textAnimationOut);

        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(22);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("Insert text here...");

        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                html_text = text;
            }
        });

        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSubscript();
            }
        });

        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSuperscript();
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBullets();
            }
        });

        findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setNumbers();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImg();
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
            }
        });
        findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertTodo();
            }
        });
    }

    public void selectImg() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, SELECT_PHOTO_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PHOTO_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    progressBar.setVisibility(View.VISIBLE);
                    uploadImage(uri, SELECT_PHOTO_CODE);
                }
                break;
            case SELECT_COVER_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(uri, filePathColumns, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePathColumns[0]);
                    String imagePath = c.getString(columnIndex);
                    Bitmap bm = BitmapFactory.decodeFile(imagePath);
                    cover_image_view.setImageBitmap(bm);
                    c.close();
                    uploadImage(uri, SELECT_COVER_CODE);
                }
                break;
            case SELECT_VIDEO_CODE:
                if (resultCode == RESULT_OK) {
                    video_upload_uri = data.getData();
                    video_name_text_view.setText(getRealPathFromURI(video_upload_uri).substring(getRealPathFromURI(video_upload_uri).lastIndexOf('/') + 1));
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
        File imageFile = new File(getRealPathFromURI(filepath));
        if (SELECT_PHOTO_CODE == code)
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
        imageFile = new CompressHelper.Builder(this)
                .setMaxWidth(300)
                .setMaxHeight(240)
                .setQuality(80)
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .build()
                .compressToFile(imageFile);
        uploadHttpHelper.upload(uploadHttpHelper.buildRequest(imageFile, Contants.API.UploadType.IMAGE), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String mMessage = response.body().string();
                EditorActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(mMessage);
                            if (code == SELECT_PHOTO_CODE)
                                mEditor.insertImage(Config.getFullUrl((String) jsonObject.get("uri")), "dachshund");
                            else if (code == SELECT_COVER_CODE) {
                                cover_uri = (String) jsonObject.get("uri");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @OnClick(R.id.text_edit_confirm_btn)
    public void submissionPublish(View view) throws InterruptedException {
        if (cover_uri.equals("")) {
            ToastUtils.show(this, "未选择封面");
            return;
        } else if ((html_text.equals("") && choose_count % 2 == 0) || (video_uri == null && choose_count % 2 == 1)) {
            ToastUtils.show(this, "未填写内容");
            return;
        } else if (title_edit_text.getText().toString().equals("")) {
            ToastUtils.show(this, "未填写标题");
            return;
        } else if (introduction_edit_text.getText().toString().equals("")) {
            ToastUtils.show(this, "未填写简介");
            return;
        }

        if (choose_count % 2 == 0) {
            // 网页上传
            File outputFile = null;
            try {
                outputFile = File.createTempFile(uploadHttpHelper.generateString(12), ".html", this.getCacheDir());
                FileWriter fileWriter = new FileWriter(outputFile);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.print(html_text);
                printWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert outputFile != null;

            Request request = uploadHttpHelper.buildRequest(outputFile, Contants.API.UploadType.HTML);
            if (request == null) {
                ToastUtils.show(this, "上传失败，请重新尝试");
                return;
            }
            uploadHttpHelper.upload(request, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String mMessage = response.body().string();

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(mMessage);
                        html_uri = (String) jsonObject.get("uri");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            while (html_uri.equals("")) {
                Thread.sleep(50);
            }
        } else {
            //视频上传
            File imageFile = new File(getRealPathFromURI(video_upload_uri));
            Request request = uploadHttpHelper.buildRequest(imageFile, Contants.API.UploadType.VIDEO);

            if (request == null) {
                ToastUtils.show(this, "上传失败，请重新尝试");
                return;
            }

            uploadHttpHelper.upload(request, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String mMessage = response.body().string();

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(mMessage);
                        video_uri = (String) jsonObject.get("uri");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            while (video_uri.equals("")) {
                Thread.sleep(50);
            }
        }

        Map<String, String> map = new HashMap<>(5);
        map.put("title", title_edit_text.getText().toString());
        map.put("introduction", introduction_edit_text.getText().toString());
        map.put("resource", choose_count % 2 == 0 ? html_uri : video_uri);
        map.put("cover", cover_uri);
        map.put("type", choose_count % 2 == 0 ? String.valueOf(Contants.API.SubmissionType.TEXT) : String.valueOf(Contants.API.SubmissionType.VIDEO));
        map.put("pid", String.valueOf(plates.get(platePosition).getPid()));

        SpotsCallBack<String> stringSpotsCallBack = new SpotsCallBack<String>(this) {
            @Override
            public void onSuccess(Response response, String user) {
                finish();
                dismissDialog();
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                dismissDialog();
                ToastUtils.show(EditorActivity.this, errorMessage.getErrorMessage());
            }
        };
        stringSpotsCallBack.setMessage(R.string.submitting);
        httpHelper.post(Contants.API.SUBMISSION_UPLOAD_URL, map, stringSpotsCallBack);
    }

    @OnClick(R.id.edit_choose_cover_image_view)
    public void chooseCover(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, SELECT_COVER_CODE);
    }

    @OnClick(R.id.edit_choose_video_text_view)
    public void uploadVideo(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*");
        startActivityForResult(intent, SELECT_VIDEO_CODE);
    }

    @OnClick(R.id.submission_type_choose)
    public void chooseType(View view) {
        choose_count += 1;
        if (choose_count % 2 == 0) {
            submission_choose_text_switcher.setText("上传投稿");
            text_editor_layout.setVisibility(View.VISIBLE);
            video_editor_layout.setVisibility(View.GONE);
        } else {
            submission_choose_text_switcher.setText("上传视频");
            text_editor_layout.setVisibility(View.GONE);
            video_editor_layout.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.text_edit_exit_btn)
    public void back(View view) {
        finish();
    }

    private void setPlateSpinner() {
        httpHelper.get(Contants.API.GET_PRIVILEGE_PLATES, new SpotsCallBack<String>(this) {

            @Override
            public void onSuccess(Response response, String s) {
                dismissDialog();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray items = jsonObject.getJSONArray("plates");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = (JSONObject) items.get(i);
                        plates.add(new Plate(
                                item.getInt("pid"),
                                item.getString("title"),
                                item.getString("description"),
                                item.getString("cover")
                        ));
                        plateTitles.add(item.getString("title"));
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditorActivity.this, R.layout.support_simple_spinner_dropdown_item, plateTitles);
                        plate_spinner.setAdapter(adapter);

                        plate_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                platePosition = position;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                platePosition = 1;
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {

            }
        });
    }

}
