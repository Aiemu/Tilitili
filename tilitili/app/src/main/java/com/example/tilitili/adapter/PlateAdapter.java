package com.example.tilitili.adapter;

import android.content.Context;
import android.net.Uri;

import com.example.tilitili.R;
import com.example.tilitili.data.Contants;
import com.example.tilitili.data.Plate;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class PlateAdapter extends SimpleAdapter<Plate> {
    public PlateAdapter(Context context, List<Plate> datas) {
        super(context, R.layout.plate_item, datas);
    }

    public PlateAdapter(Context context, int layoutResId, List<Plate> datas) {
        super(context, layoutResId, datas);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, final Plate item) {
        // 封面
        SimpleDraweeView coverView = (SimpleDraweeView) viewHolder.getView(R.id.cover_plate_item_view);
        coverView.setImageURI(Uri.parse(Contants.API.BASE_URL + item.getCover()));

        // 板块名
        viewHolder.getTextView(R.id.text_plate_item_title).setText(item.getTitle());
    }

    public void resetLayout(int layoutId) {
        this.layoutResId = layoutId;
        notifyItemRangeChanged(0, getDatas().size());
    }
}