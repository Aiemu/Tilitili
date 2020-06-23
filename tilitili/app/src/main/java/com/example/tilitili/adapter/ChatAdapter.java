package com.example.tilitili.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.example.tilitili.Config;
import com.example.tilitili.R;
import com.example.tilitili.data.Message;
import com.example.tilitili.data.MessageDatabase;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


// 聊天列表 而非聊天消息
public class ChatAdapter extends SimpleAdapter<Message> {
    public ChatAdapter(Context context, List<Message> datas) {
        super(context, R.layout.chat_user_list_item, datas);
    }

    @Override
    protected void convert(final BaseViewHolder viewHolder, final Message item) {
        SimpleDraweeView coverView = (SimpleDraweeView) viewHolder.getView(R.id.chat_user_list_item_avatar);
        coverView.setImageURI(Uri.parse(Config.getFullUrl(item.getAvatar())));

        // 用户名
        viewHolder.getTextView(R.id.chat_user_list_item_nickname).setText(String.valueOf(item.getNickname()));

        final MessageDatabase messageDatabase = MessageDatabase.getInstance(this.context);
        // 已关注
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int count = messageDatabase.messageDao().getUnread(item.uid);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String message = "未读消息数:" + count;
                        viewHolder.getTextView(R.id.chat_user_list_item_message).setText(message);
                    }
                });
            }
        }).start();
    }
}