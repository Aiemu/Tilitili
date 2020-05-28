package com.example.tilitili.adapter;

import android.content.Context;
import android.net.Uri;

import com.example.tilitili.R;
import com.example.tilitili.data.Submission;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class HotSubmissionAdapter extends SimpleAdapter<Submission> {
    public HotSubmissionAdapter(Context context, List<Submission> datas) {
        super(context, R.layout.submission_hot_item, datas);
    }

    public HotSubmissionAdapter(Context context, int layoutResId, List<Submission> datas) {
        super(context, layoutResId, datas);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, final Submission item) {
        SimpleDraweeView draweeView = (SimpleDraweeView) viewHolder.getView(R.id.drawee_view);
        draweeView.setImageURI(Uri.parse("http://t7.baidu.com/it/u=2436905109,3905541917&fm=79&app=86&f=JPEG?w=1000&h=1500"));

        viewHolder.getTextView(R.id.text_title).setText(item.getTitle());
        String watchTimes = item.getWatchTimes() + "已观看";
        String plate = item.getPlateId() + "发布";
        viewHolder.getTextView(R.id.text_watch_times).setText(watchTimes);
        viewHolder.getTextView(R.id.text_plate).setText(plate);
    }

    public void resetLayout(int layoutId){
        this.layoutResId  = layoutId;
        notifyItemRangeChanged(0,getDatas().size());
    }
}
