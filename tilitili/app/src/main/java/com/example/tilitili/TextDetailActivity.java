package com.example.tilitili;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tilitili.data.Contants;
import com.example.tilitili.data.Submission;
import com.example.tilitili.http.DownloadHttpHelper;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.HttpHelper;
import com.example.tilitili.http.SpotsCallBack;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
    @ViewInject(R.id.wb_details)
    WebView webView;
    private FrameLayout customview_layout;
    private Submission submission;
    private DownloadHttpHelper downloadHttpHelper;
    private File outputDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submission_text_detail);
        ViewUtils.inject(this);
        submission = (Submission) getIntent().getSerializableExtra("submission");
        downloadHttpHelper = DownloadHttpHelper.getInstance();
        outputDir = this.getCacheDir();
        initView();
        initWebView();
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void initWebView() {
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
        }
    }

    private void initView() {
        progressBar.setVisibility(View.VISIBLE);
        title.setTextSize(13);
        title.setVisibility(View.VISIBLE);
        title.setText(submission.getResource());
        action_comment_count.setText(String.valueOf(submission.getCommentsCount()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void setContent() {
        downloadHttpHelper.download(submission.getResource(), Contants.API.UploadType.HTML, new Callback() {
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
                String data = "<body>" +
                        "<center><h2 style='font-size:16px;'>" + submission.getTitle() + "</h2></center>";
                data = data + "<p align='left' style='margin-left:10px'>"
                        + "<span style='font-size:10px;'>"
                        + submission.getUserNickname()
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
                webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
                Log.d("msg", "readSaveFile: \n" + sb.toString());
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
}
