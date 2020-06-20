package com.example.tilitili;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cjj.MaterialRefreshLayout;
import com.example.tilitili.adapter.FollowingAdapter;
import com.example.tilitili.data.Contants;
import com.example.tilitili.data.Following;
import com.example.tilitili.data.Page;
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

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class FollowListActivity extends Activity implements Pager.OnPageListener<Following> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_follow_list);
        ViewUtils.inject(this);

        init();
    }

    private FollowingAdapter followingAdapter;
    @ViewInject(R.id.recyclerview_user_follow_list)
    private RecyclerView recyclerView;
    @ViewInject(R.id.refresh_user_follow_list_view)
    private MaterialRefreshLayout materialRefreshLayout;

    Pager pager;
    SpotsCallBack<String> callBack;

    public void init() {
        callBack = new SpotsCallBack<String>(this) {
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
                Page<Following> followingPage = null;
                List<Following> followings = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(page);
                    JSONArray items = jsonObject.getJSONArray("list");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = (JSONObject) items.get(i);
                        followings.add(new Following(item.getInt("uid"),
                                item.getString("nickname"),
                                item.getString("avatar")));
                    }
                    followingPage = new Page<>(jsonObject.getInt("currentPage"),
                            jsonObject.getInt("pageSize"),
                            jsonObject.getInt("totalPage"),
                            jsonObject.getInt("totalCount"),
                            followings);
                    pager.setPageIndex(followingPage.getCurrentPage());
                    pager.setPageCount(followingPage.getPageSize());
                    pager.setTotalPage(followingPage.getTotalPage());
                    pager.showData(followingPage.getList(), followingPage.getTotalPage(), followingPage.getTotalCount());
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
        pager = new Pager(this, callBack, materialRefreshLayout);
        pager.setUrl(Contants.API.GET_USER_SUBMISSION);
        pager.setLoadMore(true);
        pager.setOnPageListener(this);
        pager.setPageSize(7);
        pager.request();

    }

    @Override
    public void load(List<Following> datas, int totalPage, int totalCount) {
        followingAdapter = new FollowingAdapter(this, datas);
//        followingAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Following following = followingAdapter.getItem(position);
//                Intent intent = new Intent(FollowListActivity.this, TextDetailActivity.class);
//                startActivity(intent);
//            }
//        });

        recyclerView.setAdapter(followingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void refresh(List<Following> datas, int totalPage, int totalCount) {
        followingAdapter.refreshData(datas);
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void loadMore(List<Following> datas, int totalPage, int totalCount) {
        followingAdapter.loadMoreData(datas);
        recyclerView.scrollToPosition(followingAdapter.getDatas().size());
    }

    @OnClick(R.id.user_follow_list_title_bar_back)
    public void back(View v) {
        finish();
    }
}

