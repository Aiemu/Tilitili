package com.example.tilitili.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cjj.MaterialRefreshLayout;
import com.example.tilitili.R;
import com.example.tilitili.TextDetailActivity;
import com.example.tilitili.adapter.BaseAdapter;
import com.example.tilitili.adapter.HotSubmissionAdapter;
import com.example.tilitili.data.Contants;
import com.example.tilitili.data.Page;
import com.example.tilitili.data.Submission;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.SpotsCallBack;
import com.example.tilitili.utils.Pager;
import com.example.tilitili.utils.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends BaseFragment implements Pager.OnPageListener<Submission> {
    private HotSubmissionAdapter hotSubmissionAdapter;
    @ViewInject(R.id.text_home_hot_order)
    private TextView hotText;
    @ViewInject(R.id.text_home_new_order)
    private TextView newText;
    @ViewInject(R.id.home_recyclerview)
    private RecyclerView recyclerView;
    @ViewInject(R.id.home_refresh_view)
    private MaterialRefreshLayout materialRefreshLayout;

    Pager pager;
    SpotsCallBack<String> callBack;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void init() {
        callBack = new SpotsCallBack<String>(this.getActivity()) {
            @Override
            public void onFailure(Request request, Exception e) {
                dismissDialog();
                ToastUtils.show(this.getContext(), "请求出错：" + e.getMessage(), Toast.LENGTH_LONG);
                if (Pager.STATE_REFREH == pager.getState()) {
                    pager.getmRefreshLayout().finishRefresh();
                } else if (Pager.STATE_MORE == pager.getState()) {
                    pager.getmRefreshLayout().finishRefreshLoadMore();
                }
            }

            @Override
            public void onSuccess(Response response, String page) {
                dismissDialog();
                Page<Submission> submissionPage = null;
                List<Submission> submissions = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(page);
                    JSONArray items = jsonObject.getJSONArray("list");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = (JSONObject) items.get(i);
                        submissions.add(new Submission(item.getInt("sid"),
                                item.getInt("type"),
                                item.getString("plateTitle"),
                                item.getString("title"),
                                item.getString("cover"),
                                item.getString("introduction"),
                                item.getString("resource"),
                                item.getInt("submissionTime"),
                                item.getInt("watchTimes"),
                                item.getInt("likesCount"),
                                item.getInt("isLike"),
                                item.getInt("commentsCount"),
                                item.getInt("uid"),
                                item.getString("userNickname"),
                                item.getInt("following")));
                    }
                    submissionPage = new Page<>(jsonObject.getInt("currentPage"),
                            jsonObject.getInt("pageSize"),
                            jsonObject.getInt("totalPage"),
                            jsonObject.getInt("totalCount"),
                            submissions);
                    pager.setPageIndex(submissionPage.getCurrentPage());
                    pager.setPageCount(submissionPage.getPageSize());
                    pager.setTotalPage(submissionPage.getTotalPage());
                    pager.showData(submissionPage.getList(), submissionPage.getTotalPage(), submissionPage.getTotalCount());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                Toast.makeText(this.getContext(), "加载数据失败", Toast.LENGTH_LONG).show();
                dismissDialog();

                if (Pager.STATE_REFREH == pager.getState()) {
                    pager.getmRefreshLayout().finishRefresh();
                } else if (Pager.STATE_MORE == pager.getState()) {
                    pager.getmRefreshLayout().finishRefreshLoadMore();
                }
            }
        };
        pager = new Pager(this.getActivity(), callBack, materialRefreshLayout);
        pager.setUrl(Contants.API.GET_HOT);
        pager.setLoadMore(true);
        pager.setOnPageListener(this);
        pager.setPageSize(5);
        pager.request();

    }

    @Override
    public void load(List<Submission> datas, int totalPage, int totalCount) {
        hotSubmissionAdapter = new HotSubmissionAdapter(this.getContext(), datas);
        hotSubmissionAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Submission submission = hotSubmissionAdapter.getItem(position);
                Intent intent = new Intent(getContext(), TextDetailActivity.class);
                intent.putExtra("submission", (Serializable) submission);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(hotSubmissionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void refresh(List<Submission> datas, int totalPage, int totalCount) {
        hotSubmissionAdapter.refreshData(datas);
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void loadMore(List<Submission> datas, int totalPage, int totalCount) {
        hotSubmissionAdapter.loadMoreData(datas);
        recyclerView.scrollToPosition(hotSubmissionAdapter.getDatas().size());
    }

    @OnClick(R.id.linearLayoutCompatOrderHot)
    public void setHotOrder(View view) {
        this.hotText.setTextColor(Color.parseColor("#82318E"));
        this.hotText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


        this.newText.setTextColor(Color.parseColor("#888888"));
        this.newText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
    }

    @OnClick(R.id.linearLayoutCompatOrderNew)
    public void setNewOrder(View view) {
        this.newText.setTextColor(Color.parseColor("#82318E"));
        this.newText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


        this.hotText.setTextColor(Color.parseColor("#888888"));
        this.hotText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
    }
}
