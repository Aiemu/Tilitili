package com.example.tilitili;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tilitili.adapter.MessageAdapter;
import com.example.tilitili.data.Contants;
import com.example.tilitili.data.Message;
import com.example.tilitili.data.MessageDatabase;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.HttpHelper;
import com.example.tilitili.http.SimpleCallback;
import com.example.tilitili.http.SpotsCallBack;
import com.example.tilitili.utils.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Response;

public class ChatActivity extends Activity {
    @ViewInject(R.id.chat_user_recycle_view)
    private RecyclerView recyclerView;
    @ViewInject(R.id.edit_message)
    private EditText editText;
    @ViewInject(R.id.chat_user_title_bar_title)
    private TextView title;

    private int uid;
    private HttpHelper httpHelper;
    private MessageDatabase messageDatabase;
    private MessageAdapter messageAdapter;
    private List<Message> messages = new ArrayList<>();
    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_user);
        ViewUtils.inject(this);
        uid = getIntent().getIntExtra("uid", 0);
        httpHelper = HttpHelper.getInstance();
        messageDatabase = MessageDatabase.getInstance(this);
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messageAdapter);
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                httpHelper.get(Contants.API.GET_USER_MESSAGES, new SimpleCallback<String>(ChatActivity.this) {
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
                                        item.getInt("uid") == uid ? 1 : 0);
                                messages2.add(message);
                            }
                            if (messages2.size() > 0) {
                                new ChatActivity.AgentAsyncTask(messages2).execute();
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
        timer.schedule(timerTask, 1000, 1000);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        List<Message> messageList = new ArrayList<>();
                        new ChatActivity.AgentAsyncTask(messageList).execute();
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
            messageDatabase.messageDao().insertAll(a_messages);
            messageDatabase.messageDao().setRead(uid);
            return messageDatabase.messageDao().getOneUserMessage(uid, UserManagerApplication.getInstance().getUser().getUserId());
        }

        @Override
        protected void onPostExecute(List<Message> messagess) {
            messages.clear();
            messages.addAll(messagess);
            messageAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messagess.size() - 1);
            if(messagess.size() > 0)
                title.setText(messagess.get(0).getNickname());
        }
    }

    @Override
    public void onBackPressed() {
        timer.cancel();
        setResult(RESULT_OK);
        finish();
    }

    @OnClick(R.id.chat_user_title_bar_back)
    public void back(View v) {
        timer.cancel();
        setResult(RESULT_OK);
        finish();
    }

    @OnClick(R.id.btn_post_message)
    public void post(View view) {
        final String message2 = editText.getText().toString();
        httpHelper.post(Contants.API.SEND_USER_MESSAGE, new HashMap<String, String>() {
            {
                put("uid", String.valueOf(uid));
                put("content", message2);
            }
        }, new SpotsCallBack<String>(ChatActivity.this) {
            @Override
            public void onSuccess(Response response, String string) {
                dismissDialog();
                ToastUtils.show(ChatActivity.this, "发送成功");
                editText.setText("");
                Message message1 = null;
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    message1 = new Message(jsonObject.getInt("mid"),
                            UserManagerApplication.getInstance().getUser().getUserId(),
                            uid,
                            message2,
                            UserManagerApplication.getInstance().getUser().getNickname(),
                            UserManagerApplication.getInstance().getUser().getAvatar(),
                            jsonObject.getLong("messageTime"),
                            1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (message1 != null) {
                    List<Message> temp = new ArrayList<>();
                    temp.add(message1);
                    new ChatActivity.AgentAsyncTask(temp).execute();
                }
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                dismissDialog();
            }
        });
    }

}
