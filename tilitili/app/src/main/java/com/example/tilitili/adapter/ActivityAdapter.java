package com.example.tilitili.adapter;

import android.content.Context;
import android.net.Uri;

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
//        avatarView.setImageURI(Uri.parse(Contants.API.BASE_URL + item.ge));

        // 封面
        SimpleDraweeView coverView = (SimpleDraweeView) viewHolder.getView(R.id.cover_activity_item_view);
        coverView.setImageURI(Uri.parse("https://bkimg.cdn.bcebos.com/pic/a5c27d1ed21b0ef4d7a9a534ddc451da81cb3e12?x-bce-process=image/watermark,g_7,image_d2F0ZXIvYmFpa2U4MA==,xp_5,yp_5"));

        // 其他数据
        viewHolder.getTextView(R.id.text_activity_item_title).setText(item.getTitle());
        viewHolder.getTextView((R.id.text_activity_item_post_time)).setText(item.getSubmissionTime());
        viewHolder.getTextView(R.id.text_activity_item_username).setText(item.getTitle());
        viewHolder.getTextView(R.id.activity_item_star_num).setText(String.valueOf(item.getLikesCount()));
    }

    public void resetLayout(int layoutId) {
        this.layoutResId = layoutId;
        notifyItemRangeChanged(0, getDatas().size());
    }
}
