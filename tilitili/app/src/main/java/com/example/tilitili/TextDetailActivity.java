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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tilitili.data.Submission;
import com.example.tilitili.utils.NewsDetailsUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;

public class TextDetailActivity extends Activity {
    private TextView title;
    private ProgressBar progressBar;
    private FrameLayout customview_layout;
    private Submission submission;
    private TextView action_comment_count;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submission_text_detail);
        ViewUtils.inject(this);
        submission = (Submission) getIntent().getSerializableExtra("submission");
        initView();
//        initWebView();
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void initWebView() {
        webView = (WebView) findViewById(R.id.wb_details);
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
            new MyAsnycTask().execute(submission.getResource(), submission.getTitle(), submission.getPlate().getTitle() + " " + submission.getSubmissionTime() + "发布");
        }
    }

    private void initView() {
        title = findViewById(R.id.title);
        progressBar = findViewById(R.id.ss_htmlprogessbar);
        action_comment_count = findViewById(R.id.action_comment_count);

        progressBar.setVisibility(View.VISIBLE);
        title.setTextSize(13);
        title.setVisibility(View.VISIBLE);
//        title.setText(submission.getResource());
        action_comment_count.setText(String.valueOf(1));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private class MyAsnycTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            String data = NewsDetailsUtil.getNewsDetails(urls[0], urls[1], urls[2]);
            return data;
        }

        @Override
        protected void onPostExecute(String data) {
            webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
        }
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
    public class JavascriptInterface {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        public void openImage(String img) {

            //
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

        @Override
        public void onPageFinished(WebView view, String url) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageFinished(view, url);
            // html加载完成之后，添加监听图片的点击js函数
            addImageClickListner();
            progressBar.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            progressBar.setVisibility(View.GONE);
            super.onReceivedError(view, errorCode, description, failingUrl);
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
