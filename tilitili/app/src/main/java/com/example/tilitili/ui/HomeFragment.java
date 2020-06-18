package com.example.tilitili.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.tilitili.data.Plate;
import com.example.tilitili.data.Submission;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.SpotsCallBack;
import com.example.tilitili.utils.Pager;
import com.example.tilitili.utils.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends BaseFragment implements Pager.OnPageListener<Submission> {
    private HotSubmissionAdapter hotSubmissionAdapter;
    @ViewInject(R.id.recyclerview)
    private RecyclerView recyclerView;
    @ViewInject(R.id.refresh_view)
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
                    Pager.getBuilder().getmRefreshLayout().finishRefresh();
                } else if (Pager.STATE_MORE == pager.getState()) {
                    Pager.getBuilder().getmRefreshLayout().finishRefreshLoadMore();
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
                        JSONObject plateString = item.getJSONObject("plate");
                        Plate plate = new Plate(plateString.getInt("id"),
                                plateString.getString("title"),
                                plateString.getInt("owner"),
                                plateString.getLong("startTime"),
                                plateString.getString("description"));
                        submissions.add(new Submission(item.getInt("id"),
                                plate,
                                item.getInt("type"),
                                item.getString("resource"),
                                item.getString("title"),
                                item.getString("introduction"),
                                item.getInt("submissionTime"),
                                item.getInt("watchTimes"),
                                item.getInt("likes")));
                    }
                    submissionPage = new Page<>(jsonObject.getInt("currentPage"),
                            jsonObject.getInt("pageSize"),
                            jsonObject.getInt("totalPage"),
                            jsonObject.getInt("totalCount"),
                            submissions);
                    Pager.getBuilder().setPageIndex(submissionPage.getCurrentPage());
                    Pager.getBuilder().setPageCount(submissionPage.getPageSize());
                    Pager.getBuilder().setTotalPage(submissionPage.getTotalPage());
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
                    Pager.getBuilder().getmRefreshLayout().finishRefresh();
                } else if (Pager.STATE_MORE == pager.getState()) {
                    Pager.getBuilder().getmRefreshLayout().finishRefreshLoadMore();
                }
            }
        };

        pager = Pager.newBuilder()
                .setUrl(Contants.API.GET_HOT)
                .setLoadMore(true)
                .setOnPageListener(this)
                .setPageSize(5)
                .setRefreshLayout(materialRefreshLayout)
                .build(this.getActivity(), callBack);
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
}
