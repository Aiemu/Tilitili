package com.example.tilitili.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tilitili.R;
import com.example.tilitili.UserManagerApplication;
import com.example.tilitili.data.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MsgViewHolder> {
    private List<Message> list;

    public MessageAdapter(List<Message> list) {
        this.list = list;
    }

    static class MsgViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llLeft, llRight;
        TextView tvLeft, tvRight;

        public MsgViewHolder(@NonNull View itemView) {
            super(itemView);
            llLeft = itemView.findViewById(R.id.ll_left);
            llRight = itemView.findViewById(R.id.ll_right);
            tvLeft = itemView.findViewById(R.id.tv_left);
            tvRight = itemView.findViewById(R.id.tv_right);
        }
    }

    @NonNull
    @Override
    public MsgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user_item, parent, false);
        return new MsgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgViewHolder holder, int position) {
        Message msg = list.get(position);
        // 这里根据消息的类型来选择不同的布局
        if (msg.getUid() != UserManagerApplication.getInstance().getUser().getUserId()) {
            holder.llLeft.setVisibility(View.VISIBLE);
            holder.llRight.setVisibility(View.GONE);
            holder.tvLeft.setText(msg.getContent());
        } else {
            holder.llRight.setVisibility(View.VISIBLE);
            holder.llLeft.setVisibility(View.GONE);
            holder.tvRight.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}