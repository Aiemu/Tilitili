package com.example.tilitili.adapter;

import android.content.Context;
import android.net.Uri;

import com.example.tilitili.Config;
import com.example.tilitili.R;
import com.example.tilitili.data.Message;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


// 聊天列表 而非聊天消息
public class ChatAdapter extends SimpleAdapter<Message> {
    public ChatAdapter(Context context, List<Message> datas) {
        super(context, R.layout.chat_user_list_item, datas);
    }

    public ChatAdapter(Context context, int layoutResId, List<Message> datas) {
        super(context, layoutResId, datas);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, Message item) {
        SimpleDraweeView coverView = (SimpleDraweeView) viewHolder.getView(R.id.chat_user_list_item_avatar);
        coverView.setImageURI(Uri.parse(Config.getFullUrl(item.getAvatar())));

        // 用户名
        viewHolder.getTextView(R.id.chat_user_list_item_nickname).setText(item.getNickname());

        // 已关注
        viewHolder.getTextView(R.id.chat_user_list_item_message).setText("已关注");
    }
}