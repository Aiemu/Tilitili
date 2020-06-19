package com.example.tilitili.adapter;

import android.content.Context;
import android.net.Uri;

import com.example.tilitili.R;
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
        coverView.setImageURI(Uri.parse("https://bkimg.cdn.bcebos.com/pic/a5c27d1ed21b0ef4d7a9a534ddc451da81cb3e12?x-bce-process=image/watermark,g_7,image_d2F0ZXIvYmFpa2U4MA==,xp_5,yp_5"));

        // 板块名
        viewHolder.getTextView(R.id.text_plate_item_title).setText(item.getTitle());
    }

    public void resetLayout(int layoutId) {
        this.layoutResId = layoutId;
        notifyItemRangeChanged(0, getDatas().size());
    }
}