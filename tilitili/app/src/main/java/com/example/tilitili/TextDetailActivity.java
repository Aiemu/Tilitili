package com.example.tilitili;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.tilitili.data.Contants;
import com.example.tilitili.data.Submission;
import com.example.tilitili.http.DownloadHttpHelper;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.HttpHelper;
import com.example.tilitili.http.SpotsCallBack;
import com.example.tilitili.utils.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TextDetailActivity extends Activity {
    @ViewInject(R.id.detail_title)
    private TextView title;
    @ViewInject(R.id.ss_htmlprogessbar)
    private ProgressBar progressBar;
    @ViewInject(R.id.action_comment_count)
    private TextView action_comment_count;
    @ViewInject(R.id.action_like_count)
    private TextView action_like_count;
    @ViewInject(R.id.action_favorite_count)
    private TextView action_favorite_count;
    @ViewInject(R.id.wb_details)
    private WebView webView;
    @ViewInject(R.id.videoView)
    private VideoView videoView;

    public final static int COMMENT_CODE = 193;

    private Submission submission;
    private DownloadHttpHelper downloadHttpHelper;
    private HttpHelper httpHelper;
    private File outputDir;
    private String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submission_text_detail);
        ViewUtils.inject(this);
        submission = (Submission) getIntent().getSerializableExtra("submission");
        downloadHttpHelper = DownloadHttpHelper.getInstance();
        httpHelper = HttpHelper.getInstance();
        outputDir = this.getCacheDir();
        addWatchTime();
        initView();
        if (submission.getType() == Contants.API.SubmissionType.TEXT) {
            try {
                initWebView();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            initVideoView();
        }
    }

    private void initVideoView() {
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(Uri.parse(Config.getFullUrl(submission.getResource())));

        videoView.setMediaController(new MediaController(this));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i("tag", "--------------视频准备完毕,可以进行播放.......");
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i("tag", "------------------视频播放完毕..........");
                videoView.start();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i("tag", "---------------------视频播放失败...........");
                return false;
            }
        });
        videoView.start();
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void initWebView() throws InterruptedException {
        progressBar.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        if (!TextUtils.isEmpty(submission.getResource())) {
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setSupportZoom(false);
            settings.setBuiltInZoomControls(false);
            webView.setBackgroundResource(R.color.transparent);
            webView.addJavascriptInterface(new JavascriptInterface(getApplicationContext()), "imagelistner");
            webView.setWebChromeClient(new MyWebChromeClient());
            webView.setWebViewClient(new MyWebViewClient());
            setContent();
            while (!data.endsWith("</body>"))
                Thread.sleep(50);
            webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
        }
    }

    private void initView() {
        title.setTextSize(13);
        title.setVisibility(View.VISIBLE);
        title.setText(Config.getFullUrl(submission.getResource()));
        action_comment_count.setText(String.valueOf(submission.getCommentsCount()));
        action_like_count.setText(String.valueOf(submission.getLikesCount()));
        action_favorite_count.setText(String.valueOf(submission.getFavoriteCount()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case COMMENT_CODE:
                if (resultCode == RESULT_OK) {
                    int comment = data.getIntExtra("comments", 0);
                    action_comment_count.setText(String.valueOf(Integer.parseInt(action_comment_count.getText().toString()) + comment));
                }
        }
    }

    public void setContent() {
        downloadHttpHelper.download(Config.getFullUrl(submission.getResource()), Contants.API.UploadType.HTML, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to download file: " + response);
                }
                File outputFile = File.createTempFile("prefix", ".html", outputDir);
                FileOutputStream fos = new FileOutputStream(outputFile);
                fos.write(Objects.requireNonNull(response.body()).bytes());
                fos.close();
                data = "<body>" +
                        "<center><h2 style='font-size:16px;'>" + submission.getTitle() + "</h2></center>";
                data = data + "<p align='left' style='margin-left:10px'>"
                        + "<span style='font-size:10px;'>"
                        + "本文章由 " + submission.getUserNickname() + " 于 " + submission.getSubmissionTime() + " 发布"
                        + "</span>"
                        + "</p>";
                data = data + "<hr size='1' />";
                FileInputStream fis = new FileInputStream(outputFile);
                byte[] temp = new byte[1024];
                StringBuilder sb = new StringBuilder("");
                int len = 0;
                while ((len = fis.read(temp)) > 0) {
                    sb.append(new String(temp, 0, len));
                }
                fis.close();
                data = data + sb.toString() + "</body>";
            }
        });
    }

    public void addWatchTime() {
        httpHelper.post(Contants.API.ADD_WATCH_TIME_URL + submission.getSid(), new HashMap<String, String>(0), new SpotsCallBack<String>(TextDetailActivity.this) {
            @Override
            public void onSuccess(Response response, String s) {
                Log.d("观看次数", "加一");
                dismissDialog();
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                e.printStackTrace();
                dismissDialog();
            }
        });
    }

    // 注入js函数监听
    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，在还是执行的时候调用本地接口传递url过去
        webView.loadUrl("javascript:(function(){"
                + "var objs = document.getElementsByTagName(\"img\");"
                + "var imgurl=''; " + "for(var i=0;i<objs.length;i++)  " + "{"
                + "imgurl+=objs[i].src+',';"
                + "    objs[i].onclick=function()  " + "    {  "
                + "        window.imagelistner.openImage(imgurl);  "
                + "    }  " + "}" + "})()");
    }

    // js通信接口
    public static class JavascriptInterface {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        public void openImage(String img) {
            String[] imgs = img.split(",");
            ArrayList<String> imgsUrl = new ArrayList<String>();
            for (String s : imgs) {
                imgsUrl.add(s);
                Log.i("图片的URL>>>>>>>>>>>>>>>>>>>>>>>", s);
            }
            Intent intent = new Intent();
            intent.putStringArrayListExtra("infos", imgsUrl);
//            intent.setClass(context, ImageShowActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    // 监听
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public void onPageFinished(WebView view, String url) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageFinished(view, url);
            // html加载完成之后，添加监听图片的点击js函数
            addImageClickListner();
            progressBar.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            progressBar.setVisibility(View.GONE);
            super.onReceivedError(view, request, error);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            // TODO Auto-generated method stub
            if (newProgress != 100) {
                progressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    @OnClick(R.id.detail_back)
    public void goBack(View view) {
        finish();
    }

    @OnClick(R.id.action_view_comment)
    public void viewComments(View view) {
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra("sid", submission.getSid());
        startActivityForResult(intent, COMMENT_CODE);
    }

    @OnClick(R.id.action_favor)
    public void like(View view) {
        httpHelper.post(Contants.API.SUBMISSION_LIKE + submission.getSid(), new HashMap<String, String>() {
            {
                put("like", String.valueOf(submission.getIsLike() == 0 ? 1 : 0));
            }
        }, new SpotsCallBack<String>(TextDetailActivity.this) {
            @Override
            public void onSuccess(Response response, String s) {
                dismissDialog();
                if (submission.getIsLike() == 0) {
                    ToastUtils.show(TextDetailActivity.this, "点赞成功");
                    submission.setIsLike(1);
                    submission.setLikesCount(submission.getLikesCount() + 1);
                    action_like_count.setText(String.valueOf(submission.getLikesCount()));
                } else {
                    ToastUtils.show(TextDetailActivity.this, "取消点赞成功");
                    submission.setIsLike(0);
                    submission.setLikesCount(submission.getLikesCount() - 1);
                    action_like_count.setText(String.valueOf(submission.getLikesCount()));
                }
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                dismissDialog();
                ToastUtils.show(TextDetailActivity.this, errorMessage.getErrorMessage());
                e.printStackTrace();
            }
        });
    }

    @OnClick(R.id.action_favorite)
    public void favorite(View view) {
        httpHelper.post(Contants.API.SUBMISSION_FAVORITE + submission.getSid(), new HashMap<String, String>() {
            {
                put("favorite", String.valueOf(submission.getIsFavorite() == 0 ? 1 : 0));
            }
        }, new SpotsCallBack<String>(TextDetailActivity.this) {
            @Override
            public void onSuccess(Response response, String s) {
                dismissDialog();
                if (submission.getIsFavorite() == 0) {
                    ToastUtils.show(TextDetailActivity.this, "收藏成功");
                    submission.setIsFavorite(1);
                    submission.setFavoriteCount(submission.getFavoriteCount() + 1);
                } else {
                    ToastUtils.show(TextDetailActivity.this, "取消收藏成功");
                    submission.setIsFavorite(0);
                    submission.setFavoriteCount(submission.getFavoriteCount() - 1);
                }
                action_favorite_count.setText(String.valueOf(submission.getFavoriteCount()));
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                dismissDialog();
                ToastUtils.show(TextDetailActivity.this, errorMessage.getErrorMessage());
                e.printStackTrace();
            }
        });


    }

    @OnClick(R.id.action_share)
    public void share(View view) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        share.putExtra(Intent.EXTRA_SUBJECT, "新闻网址");
        share.putExtra(Intent.EXTRA_TEXT, Config.getFullUrl(submission.getResource()));

        startActivity(Intent.createChooser(share, "分享该链接"));
    }
}
