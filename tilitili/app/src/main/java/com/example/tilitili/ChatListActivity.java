package com.example.tilitili;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tilitili.adapter.BaseAdapter;
import com.example.tilitili.adapter.ChatAdapter;
import com.example.tilitili.data.Message;
import com.example.tilitili.data.MessageDatabase;
import com.example.tilitili.http.HttpHelper;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends Activity {

    @ViewInject(R.id.chat_user_list_recycle_view)
    RecyclerView recyclerView;
//    @ViewInject(R.id.chat_user_refresh_layout)
//    MaterialRefreshLayout materialRefreshLayout;

    MessageDatabase messageDatabase;
    Handler handler;
    List<Message> messages;
    HttpHelper httpHelper;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_user_list);
        ViewUtils.inject(this);

        messageDatabase = MessageDatabase.getInstance(this);
        handler = new Handler(Looper.getMainLooper());
        httpHelper = HttpHelper.getInstance();
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(ChatListActivity.this, messages);
        chatAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Message message = chatAdapter.getItem(position);
                Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
                intent.putExtra("uid", message.getId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(chatAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(ChatListActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        init();

    }

    void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message(2, 1, "123", 1, "123", "/image/campus.png");
                Message message2 = new Message(10, 3, "123", 1, "123", "/image/campus.png");
                Message message3 = new Message(20, 2, "123", 1, "123", "/image/campus.png");
//                messageDatabase.messageDao().deleteAll(message, message2, message3, message4);
//                messageDatabase.messageDao().insertAll(message, message2, message3);
                messages.addAll(messageDatabase.messageDao().getAllDistinctUsers());
            }
        }).start();
    }

}
