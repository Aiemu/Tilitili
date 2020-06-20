package com.example.tilitili.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;

import com.example.tilitili.Config;
import com.example.tilitili.R;
import com.example.tilitili.UserManagerApplication;
import com.example.tilitili.data.Submission;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class HotSubmissionAdapter extends SimpleAdapter<Submission> {
    public HotSubmissionAdapter(Context context, List<Submission> datas) {
        super(context, R.layout.submission_hot_item, datas);
    }

    public HotSubmissionAdapter(Context context, int layoutResId, List<Submission> datas) {
        super(context, layoutResId, datas);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, final Submission item) {
        SimpleDraweeView draweeView = (SimpleDraweeView) viewHolder.getView(R.id.drawee_view);
        draweeView.setImageURI(Uri.parse(Config.getFullUrl(item.getCover())));
        viewHolder.getTextView(R.id.text_title).setText(item.getTitle());
        String watchTimes = item.getWatchTimes() + "已观看";
        String username = item.getUserNickname() + " 发布";
        viewHolder.getTextView(R.id.text_watch_times).setText(watchTimes);
        viewHolder.getTextView(R.id.text_username).setText(username);
        if(item.getFollowing() == 0 && item.getUid() != UserManagerApplication.getInstance().getUser().getUserId()) {
            viewHolder.getTextView(R.id.following_view).setText("未关注");
            viewHolder.getTextView(R.id.following_view).setTextColor(Color.RED);
        } else if (item.getFollowing() == 1) {
            viewHolder.getTextView(R.id.following_view).setText("已关注");
            viewHolder.getTextView(R.id.following_view).setTextColor(Color.YELLOW);
        }
    }

    public void resetLayout(int layoutId) {
        this.layoutResId = layoutId;
        notifyItemRangeChanged(0, getDatas().size());
    }
}
