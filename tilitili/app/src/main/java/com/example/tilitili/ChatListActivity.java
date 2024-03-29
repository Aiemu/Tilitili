package com.example.tilitili;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tilitili.adapter.BaseAdapter;
import com.example.tilitili.adapter.ChatAdapter;
import com.example.tilitili.data.Contants;
import com.example.tilitili.data.Message;
import com.example.tilitili.data.MessageDatabase;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.HttpHelper;
import com.example.tilitili.http.SimpleCallback;
import com.example.tilitili.utils.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

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

    final static int CHAT_USER_BACK = 324;
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

        timerTask = new TimerTask() {
            @Override
            public void run() {
                httpHelper.get(Contants.API.GET_USER_MESSAGES, new SimpleCallback<String>(ChatListActivity.this) {
                    @Override
                    public void onSuccess(Response response, String string) {
                        List<Message> messages2 = new ArrayList<>();
                        try {
                            JSONObject jsonObject = new JSONObject(string);
                            JSONArray items = jsonObject.getJSONArray("messages");
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = (JSONObject) items.get(i);
                                final Message message = new Message(item.getInt("mid"),
                                        item.getInt("uid"),
                                        UserManagerApplication.getInstance().getUser().getUserId(),
                                        item.getString("content"),
                                        item.getString("nickname"),
                                        item.getString("avatar"),
                                        item.getLong("messageTime"),
                                        0);
                                messages2.add(message);
                            }
                            if (messages2.size() > 0) {
                                new AgentAsyncTask(messages2).execute();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                        Log.d("ChatListActivity", errorMessage.getErrorMessage());
                    }
                });
            }
        };

        chatAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Message message = chatAdapter.getItem(position);
                Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
                intent.putExtra("uid", message.getUid() == UserManagerApplication.getInstance().getUser().getUserId() ? message.getReceiver() : message.getUid());
                timer.cancel();
                timer = null;
                startActivityForResult(intent, CHAT_USER_BACK);
            }
        });
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatListActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        timer.schedule(timerTask, 2000, 1000);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        List<Message> messageList = new ArrayList<>();
                        new ChatListActivity.AgentAsyncTask(messageList).execute();
                    }
                }, 100);
            }
        });
    }


    private class AgentAsyncTask extends AsyncTask<Void, Void, List<Message>> {

        //Prevent leak
        private List<Message> a_messages;

        public AgentAsyncTask(List<Message> message) {
            this.a_messages = message;
        }

        @Override
        protected List<Message> doInBackground(Void... params) {
            MessageDatabase messageDatabase = MessageDatabase.getInstance(ChatListActivity.this);
            messageDatabase.messageDao().insertAll(a_messages);
            List<Message> result = new ArrayList<>();
            List<Message> allMessages = new ArrayList<>();
            allMessages.addAll(messageDatabase.messageDao().getAllDistinctUsers(UserManagerApplication.getInstance().getUser().getUserId()));
            result.addAll(messageDatabase.messageDao().getAllDistinctUsers(UserManagerApplication.getInstance().getUser().getUserId()));
            List<Message> temp = messageDatabase.messageDao().getAllDistinctUsers2(UserManagerApplication.getInstance().getUser().getUserId());
            for (Message item : temp) {
                int flag = 0;
                for (Message i : allMessages) {
                    if (i.getUid() == item.getReceiver()) {
                        flag = 1;
                        break;
                    }
                }
                if (flag == 0) {
                    result.add(item);
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<Message> messagess) {
            messages.clear();
            messages.addAll(messagess);
            chatAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHAT_USER_BACK:
                if (resultCode == RESULT_OK) {
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            httpHelper.get(Contants.API.GET_USER_MESSAGES, new SimpleCallback<String>(ChatListActivity.this) {
                                @Override
                                public void onSuccess(Response response, String string) {
                                    List<Message> messages2 = new ArrayList<>();
                                    try {
                                        JSONObject jsonObject = new JSONObject(string);
                                        JSONArray items = jsonObject.getJSONArray("messages");
                                        for (int i = 0; i < items.length(); i++) {
                                            JSONObject item = (JSONObject) items.get(i);
                                            final Message message = new Message(item.getInt("mid"),
                                                    item.getInt("uid"),
                                                    UserManagerApplication.getInstance().getUser().getUserId(),
                                                    item.getString("content"),
                                                    item.getString("nickname"),
                                                    item.getString("avatar"),
                                                    item.getLong("messageTime"),
                                                    0);
                                            messages2.add(message);
                                        }
                                        new AgentAsyncTask(messages2).execute();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                                    ToastUtils.show(ChatListActivity.this, errorMessage.getErrorMessage());
                                }
                            });
                        }
                    };
                    timer = new Timer();
                    timer.schedule(timerTask, 2000, 1000);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            List<Message> messageList = new ArrayList<>();
                            new AgentAsyncTask(messageList).execute();
                        }
                    }, 500);
                }
        }
    }

    @Override
    public void onBackPressed() {
        timer.cancel();
        timer = null;
        finish();
    }

    @OnClick(R.id.chat_list_title_bar_back)
    public void back(View view) {
        timer.cancel();

        timer = null;
        finish();
    }
}
