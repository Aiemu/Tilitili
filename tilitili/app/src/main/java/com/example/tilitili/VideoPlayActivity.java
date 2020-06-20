package com.example.tilitili;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayActivity extends AppCompatActivity {
    /**
     * android.widget.VideoView：视频播放器控件
     */
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_play);
        bindViews();
    }

    private void bindViews() {
        videoView = findViewById(R.id.videoView);

        /**播放 res/raw 目录下的文件
         * android.resource:// ：前缀固定
         * com.example.administrator.helloworld：为当前类的所在的包路径，可以使用 String packageName = getPackageName(); 动态获取
         * R.raw.la_isla：最后接 res/raw 目录中的文件名
         * */
        videoView.setVideoURI(Uri.parse("http://alcdn.hls.xiaoka.tv/2017427/14b/7b3/Jzq08Sl8BbyELNTo/index.m3u8"));

        /**
         * 为 VideoView 视图设置媒体控制器，设置了之后就会自动由进度条、前进、后退等操作
         */
        videoView.setMediaController(new MediaController(this));

        /**视频准备完成时回调
         * */
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i("tag", "--------------视频准备完毕,可以进行播放.......");
            }
        });
        /**
         * 视频播放完成时回调
         */
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i("tag", "------------------视频播放完毕..........");
                /**播放完成时，再次循环播放*/
                videoView.start();
            }
        });

        /**
         * 视频播放发送错误时回调
         */
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i("tag", "---------------------视频播放失败...........");
                return false;
            }
        });

        /**开始播放视频
         * */
        videoView.start();
    }
}