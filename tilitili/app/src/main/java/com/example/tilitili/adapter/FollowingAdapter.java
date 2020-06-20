package com.example.tilitili.adapter;

import android.content.Context;
import android.net.Uri;

import com.example.tilitili.R;
import com.example.tilitili.data.Following;
import com.example.tilitili.data.Plate;
import com.example.tilitili.data.User;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class FollowingAdapter extends SimpleAdapter<Following> {
    public FollowingAdapter(Context context, List<Following> datas) {
        super(context, R.layout.user_follow_list_item, datas);
    }

    public FollowingAdapter(Context context, int layoutResId, List<Following> datas) {
        super(context, layoutResId, datas);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, final Following item) {
        // 头像
        SimpleDraweeView coverView = (SimpleDraweeView) viewHolder.getView(R.id.avatar_follow_list_item_view);
        coverView.setImageURI(Uri.parse("https://bkimg.cdn.bcebos.com/pic/a5c27d1ed21b0ef4d7a9a534ddc451da81cb3e12?x-bce-process=image/watermark,g_7,image_d2F0ZXIvYmFpa2U4MA==,xp_5,yp_5"));

        // 用户名
        viewHolder.getTextView(R.id.follow_item_text_username).setText(item.getNickname());

        // 已关注
        viewHolder.getTextView(R.id.follow_item_text_follow).setText("已关注");
    }

    public void resetLayout(int layoutId) {
        this.layoutResId = layoutId;
        notifyItemRangeChanged(0, getDatas().size());
    }
}
