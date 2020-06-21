package com.example.tilitili.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;

import com.example.tilitili.Config;
import com.example.tilitili.R;
import com.example.tilitili.UserManagerApplication;
import com.example.tilitili.data.Following;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class SearchUserAdapter extends SimpleAdapter<Following> {

    public SearchUserAdapter(Context context, List<Following> datas) {
        super(context, R.layout.user_follow_list_item, datas);
    }

    public SearchUserAdapter(Context context, int layoutResId, List<Following> datas) {
        super(context, layoutResId, datas);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, final Following item) {
        // 头像
        SimpleDraweeView coverView = (SimpleDraweeView) viewHolder.getView(R.id.avatar_follow_list_item_view);
        coverView.setImageURI(Uri.parse(Config.getFullUrl(item.getAvatar())));

        // 用户名
        viewHolder.getTextView(R.id.follow_item_text_username).setText(item.getNickname());

        // 已关注
        if (item.getIsFollowing() == 0 && item.getUserId() != UserManagerApplication.getInstance().getUser().getUserId()) {
            viewHolder.getTextView(R.id.follow_item_text_follow).setText("未关注");
            viewHolder.getTextView(R.id.follow_item_text_follow).setTextColor(Color.parseColor("#888888"));
        } else if (item.getIsFollowing() == 1) {
            viewHolder.getTextView(R.id.follow_item_text_follow).setText("已关注");
            viewHolder.getTextView(R.id.follow_item_text_follow).setTextColor(Color.parseColor("#82318E"));
        }
    }

    public void resetLayout(int layoutId) {
        this.layoutResId = layoutId;
        notifyItemRangeChanged(0, getDatas().size());
    }
}
