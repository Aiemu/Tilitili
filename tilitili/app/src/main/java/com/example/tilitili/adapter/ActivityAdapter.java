package com.example.tilitili.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;

import com.example.tilitili.Config;
import com.example.tilitili.R;
import com.example.tilitili.data.Submission;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class ActivityAdapter extends SimpleAdapter<Submission> {
    public ActivityAdapter(Context context, List<Submission> datas) {
        super(context, R.layout.activity_item, datas);
    }

    public ActivityAdapter(Context context, int layoutResId, List<Submission> datas) {
        super(context, layoutResId, datas);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, final Submission item) {
        // 头像
        SimpleDraweeView avatarView = (SimpleDraweeView) viewHolder.getView(R.id.avatar_activity_item_view);
        avatarView.setImageURI(Uri.parse(Config.getFullUrl(item.getUserAvatar())));

        // 封面
        SimpleDraweeView coverView = (SimpleDraweeView) viewHolder.getView(R.id.cover_activity_item_view);
        coverView.setImageURI(Uri.parse(Config.getFullUrl(item.getCover())));

        // 其他数据
        viewHolder.getTextView(R.id.text_activity_item_username).setText(item.getUserNickname());
        viewHolder.getTextView((R.id.text_activity_item_post_time)).setText("已关注");

        viewHolder.getTextView(R.id.text_activity_item_title).setText(item.getTitle());
        viewHolder.getTextView(R.id.text_activity_item_plate).setText(item.getPlateTitle());
        viewHolder.getTextView(R.id.activity_item_star_num).setText(String.valueOf(item.getLikesCount()));
        viewHolder.getTextView(R.id.activity_item_comment_num).setText(String.valueOf(item.getCommentsCount()));

        if (item.getIsLike() == 1) {
            viewHolder.getImageView(R.id.star_icon).setColorFilter(Color.parseColor("#9C27B0"));
        } else if (item.getIsLike() == 0) {
            viewHolder.getImageView(R.id.star_icon).setColorFilter(Color.parseColor("#888888"));
        }
    }

    public void resetLayout(int layoutId) {
        this.layoutResId = layoutId;
        notifyItemRangeChanged(0, getDatas().size());
    }
}
