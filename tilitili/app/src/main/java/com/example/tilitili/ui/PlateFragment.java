package com.example.tilitili.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cjj.MaterialRefreshLayout;
import com.example.tilitili.R;
import com.example.tilitili.TextDetailActivity;
import com.example.tilitili.adapter.ActivityAdapter;
import com.example.tilitili.adapter.BaseAdapter;
import com.example.tilitili.adapter.PlateAdapter;
import com.example.tilitili.data.Contants;
import com.example.tilitili.data.Page;
import com.example.tilitili.data.Plate;
import com.example.tilitili.data.Submission;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.SpotsCallBack;
import com.example.tilitili.utils.Pager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class PlateFragment extends BaseFragment implements Pager.OnPageListener<Plate> {
    private PlateAdapter plateAdapter;
    @ViewInject(R.id.recyclerview)
    private RecyclerView recyclerView;
    @ViewInject(R.id.refresh_view)
    private MaterialRefreshLayout materialRefreshLayout;

    Pager pager;
    SpotsCallBack<String> callBack;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plate, container, false);
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void init() {
        callBack = new SpotsCallBack<String>(this.getActivity()) {
            @Override
            public void onFailure(Request request, Exception e) {
                dismissDialog();
                Toast.makeText(this.getContext(), "请求出错：" + e.getMessage(), Toast.LENGTH_LONG).show();
                if (Pager.STATE_REFREH == pager.getState()) {
                    Pager.getBuilder().getmRefreshLayout().finishRefresh();
                } else if (Pager.STATE_MORE == pager.getState()) {
                    Pager.getBuilder().getmRefreshLayout().finishRefreshLoadMore();
                }
            }

            @Override
            public void onSuccess(Response response, String page) {
                dismissDialog();
                Page<Plate> platePage = null;
                List<Plate> plates = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(page);
                    JSONArray items = jsonObject.getJSONArray("list");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = (JSONObject) items.get(i);
                        plates.add(new Plate(item.getInt("id"),
                                item.getString("title"),
                                item.getInt("owner"),
                                item.getLong("startTime"),
                                item.getString("description")));
                    }
                    platePage = new Page<>(jsonObject.getInt("currentPage"),
                            jsonObject.getInt("pageSize"),
                            jsonObject.getInt("totalPage"),
                            jsonObject.getInt("totalCount"),
                            plates);
                    Pager.getBuilder().setPageIndex(platePage.getCurrentPage());
                    Pager.getBuilder().setPageCount(platePage.getPageSize());
                    Pager.getBuilder().setTotalPage(platePage.getTotalPage());
                    pager.showData(platePage.getList(), platePage.getTotalPage(), platePage.getTotalCount());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                Toast.makeText(this.getContext(), "加载数据失败", Toast.LENGTH_LONG).show();
                dismissDialog();

                if (Pager.STATE_REFREH == pager.getState()) {
                    Pager.getBuilder().getmRefreshLayout().finishRefresh();
                } else if (Pager.STATE_MORE == pager.getState()) {
                    Pager.getBuilder().getmRefreshLayout().finishRefreshLoadMore();
                }
            }
        };

        pager = Pager.newBuilder()
                .setUrl(Contants.API.GET_PLATE)
                .setLoadMore(true)
                .setOnPageListener(this)
                .setPageSize(8)
                .setRefreshLayout(materialRefreshLayout)
                .build(this.getActivity(), callBack);
        pager.request();

    }

    @Override
    public void load(List<Plate> datas, int totalPage, int totalCount) {
        plateAdapter = new PlateAdapter(this.getContext(), datas);
        plateAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Plate plate = plateAdapter.getItem(position);
                Intent intent = new Intent(getContext(), TextDetailActivity.class);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(plateAdapter);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void refresh(List<Plate> datas, int totalPage, int totalCount) {
        plateAdapter.refreshData(datas);
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void loadMore(List<Plate> datas, int totalPage, int totalCount) {
        plateAdapter.loadMoreData(datas);
        recyclerView.scrollToPosition(plateAdapter.getDatas().size());
    }
}
