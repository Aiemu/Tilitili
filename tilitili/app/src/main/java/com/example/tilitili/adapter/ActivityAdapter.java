package com.example.tilitili.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tilitili.MainActivity;
import com.example.tilitili.R;
import com.example.tilitili.data.Submission;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lidroid.xutils.view.annotation.event.OnClick;

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
<<<<<<< HEAD
        avatarView.setImageURI(Uri.parse(Contants.API.BASE_URL + item.getUserAvatar()));
=======
//        avatarView.setImageURI(Uri.parse(Contants.API.BASE_URL + item.ge));
>>>>>>> 5e624afe6ceacbe3f9535f0a6605ecc2c058bb16

        // 封面
        SimpleDraweeView coverView = (SimpleDraweeView) viewHolder.getView(R.id.cover_activity_item_view);
        coverView.setImageURI(Uri.parse(Contants.API.BASE_URL + item.getCover()));

        // 其他数据
        viewHolder.getTextView(R.id.text_activity_item_username).setText(item.getUserNickname());
        viewHolder.getTextView((R.id.text_activity_item_post_time)).setText("已关注");

        viewHolder.getTextView(R.id.text_activity_item_title).setText(item.getTitle());
        viewHolder.getTextView(R.id.activity_item_star_num).setText(String.valueOf(item.getLikesCount()));
        viewHolder.getTextView(R.id.activity_item_comment_num).setText(String.valueOf(item.getCommentsCount()));

        if (item.getIsLike() == 1) {
                viewHolder.getImageView(R.id.star_icon).setColorFilter(Color.parseColor("#9C27B0"));
        }
        else if (item.getIsLike() == 0) {
            viewHolder.getImageView(R.id.star_icon).setColorFilter(Color.parseColor("#888888"));
        }

        viewHolder.getImageView(R.id.star_icon).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (item.getIsLike() == 1) {
                    // todo: 取消点赞
                    ImageView icon = (ImageView)v.findViewById(R.id.star_icon);
                    icon.setColorFilter(Color.parseColor("#888888"));
                    TextView num = (TextView)v.findViewById(R.id.activity_item_star_num);
                    num.setText(Integer.parseInt(num.getText().toString()) - 1);
                }
                else if (item.getIsLike() == 0) {
                    // todo: 确认点赞
                    ImageView icon = (ImageView)v.findViewById(R.id.star_icon);
                    icon.setColorFilter(Color.parseColor("#9C27B0"));
                    TextView num = (TextView)v.findViewById(R.id.activity_item_star_num);
                    num.setText(Integer.parseInt(num.getText().toString()) + 1);
                }
            }
        });
    }

    public void resetLayout(int layoutId) {
        this.layoutResId = layoutId;
        notifyItemRangeChanged(0, getDatas().size());
    }
}
