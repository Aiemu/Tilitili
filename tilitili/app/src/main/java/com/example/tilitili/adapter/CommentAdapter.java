package com.example.tilitili.adapter;

import android.content.Context;
import android.net.Uri;

import com.example.tilitili.Config;
import com.example.tilitili.R;
import com.example.tilitili.data.Comment;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class CommentAdapter extends SimpleAdapter<Comment> {
    public CommentAdapter(Context context, List<Comment> datas) {
        super(context, R.layout.comment_item, datas);
    }

    public CommentAdapter(Context context, int layoutResId, List<Comment> datas) {
        super(context, layoutResId, datas);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, final Comment item) {
        // 封面
        SimpleDraweeView coverView = (SimpleDraweeView) viewHolder.getView(R.id.avatar_comment_item_view);
        coverView.setImageURI(Uri.parse(Config.getFullUrl(item.getAvatar())));

        // 其他
        viewHolder.getTextView(R.id.comment_item_text_username).setText(item.getNickname());
        viewHolder.getTextView(R.id.comment_item_text_time).setText(item.getCommentTime());
        viewHolder.getTextView(R.id.comment_item_text_content).setText(item.getContent());

    }

    public void resetLayout(int layoutId) {
        this.layoutResId = layoutId;
        notifyItemRangeChanged(0, getDatas().size());
    }
}