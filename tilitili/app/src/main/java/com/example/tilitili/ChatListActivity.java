package com.example.tilitili;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tilitili.adapter.BaseAdapter;
import com.example.tilitili.adapter.ChatAdapter;
import com.example.tilitili.data.Contants;
import com.example.tilitili.data.Following;
import com.example.tilitili.data.Message;
import com.example.tilitili.data.MessageDatabase;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.HttpHelper;
import com.example.tilitili.http.SpotsCallBack;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Response;

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
    private Timer timer;
    TimerTask timerTask;

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
        timer = new Timer();
        timerTask = new TimerTask(){
            @Override
            public void run() {
                httpHelper.get(Contants.API.GET_USER_MESSAGES, new SpotsCallBack<String>(ChatListActivity.this) {
                    @Override
                    public void onSuccess(Response response, String string) {
                        dismissDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(string);
                            JSONArray items = jsonObject.getJSONArray("messages");
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = (JSONObject) items.get(i);
                                Message message = new Message(messageDatabase.messageDao().getSum() + 1,
                                        item.getInt("uid"),
                                        item.getString("content"),
                                        item.getInt("type"),
                                        item.getString("nickname"),
                                        item.getString("avatar"),
                                        item.getLong("messageTime"),
                                        0);
                                messageDatabase.messageDao().insert(message);
                                messages.clear();
                                messages.addAll(messageDatabase.messageDao().getAllDistinctUsers());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                        dismissDialog();
                        e.printStackTrace();
                    }
                });
            }
        };

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
    }

    void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Message message = new Message(2, 1, "123", 1, "123", "/image/campus.png");
//                Message message2 = new Message(10, 3, "123", 1, "123", "/image/campus.png");
//                Message message3 = new Message(20, 2, "123", 1, "123", "/image/campus.png");
//                messageDatabase.messageDao().deleteAll(message, message2, message3, message4);
//                messageDatabase.messageDao().insertAll(message, message2, message3);
//                messages.addAll(messageDatabase.messageDao().getAllDistinctUsers());
            }
        }).start();
    }

}
