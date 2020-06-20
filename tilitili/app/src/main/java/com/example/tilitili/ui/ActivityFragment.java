package com.example.tilitili.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cjj.MaterialRefreshLayout;
import com.example.tilitili.CommentActivity;
import com.example.tilitili.LoginActivity;
import com.example.tilitili.MainActivity;
import com.example.tilitili.R;
import com.example.tilitili.TextDetailActivity;
import com.example.tilitili.UserManagerApplication;
import com.example.tilitili.adapter.ActivityAdapter;
import com.example.tilitili.adapter.BaseAdapter;
import com.example.tilitili.data.Contants;
import com.example.tilitili.data.Page;
import com.example.tilitili.data.Submission;
import com.example.tilitili.data.User;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.HttpHelper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

public class ActivityFragment extends BaseFragment implements Pager.OnPageListener<Submission> {
    private HttpHelper httpHelper = HttpHelper.getInstance();

    private ActivityAdapter activityAdapter;
    @ViewInject(R.id.activity_recyclerview)
    private RecyclerView recyclerView;
    @ViewInject(R.id.activity_refresh_view)
    private MaterialRefreshLayout materialRefreshLayout;

    Pager pager;
    SpotsCallBack<String> callBack;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void init() {
        callBack = new SpotsCallBack<String>(this.getContext()) {
            @Override
            public void onFailure(Request request, Exception e) {
                ToastUtils.show(this.getContext(), "请求出错：" + e.getMessage());
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
                                item.getLong("submissionTime"),
                                item.getInt("watchTimes"),
                                item.getInt("likesCount"),
                                item.getInt("isLike"),
                                item.getInt("commentsCount"),
                                item.getInt("uid"),
                                item.getString("userNickname"),
                                item.getInt("following"),
                                item.getString("userAvatar")));
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
        pager.setUrl(Contants.API.GET_USER_ACTIVITY);
        pager.setLoadMore(true);
        pager.setOnPageListener(this);
        pager.setPageSize(5);
        pager.request();

    }

    @Override
    public void load(List<Submission> datas, int totalPage, int totalCount) {
        activityAdapter = new ActivityAdapter(this.getContext(), datas);
        activityAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Submission submission = activityAdapter.getItem(position);
                Intent intent = new Intent(getContext(), TextDetailActivity.class);
                intent.putExtra("submission", (Serializable) submission);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(activityAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void refresh(List<Submission> datas, int totalPage, int totalCount) {
        activityAdapter.refreshData(datas);
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void loadMore(List<Submission> datas, int totalPage, int totalCount) {
        activityAdapter.loadMoreData(datas);
        recyclerView.scrollToPosition(activityAdapter.getDatas().size());
    }

    // todo: check correctness
    @OnClick(R.id.comment_icon)
    public void loadComment() {
        activityAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Submission submission = activityAdapter.getItem(position);
                Intent intent = new Intent(getContext(), CommentActivity.class);
                intent.putExtra("sid", submission.getSid());
                startActivity(intent);
            }
        });
    }

    // todo: check correctness
    @OnClick(R.id.star_icon)
    public void changeLike() {
        activityAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Submission submission = activityAdapter.getItem(position);
                final View v = view;
                if (submission.getIsLike() == 1) {
                    // 取消点赞
                    Map<String, String> map = new HashMap<>(1);
                    map.put("username", "0");

                    SpotsCallBack<String> stringSpotsCallBack = new SpotsCallBack<String>(ActivityFragment.this.getActivity()) { // todo
                        @Override
                        public void onSuccess(Response response, String userString) {
                            ImageView icon = (ImageView)v.findViewById(R.id.star_icon);
                            icon.setColorFilter(Color.parseColor("#888888"));
                            TextView num = (TextView)v.findViewById(R.id.activity_item_star_num);
                            num.setText(Integer.parseInt(num.getText().toString()) - 1);

                            dismissDialog();
                        }

                        @Override
                        public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                            dismissDialog();
                        }
                    };
                    httpHelper.post(Contants.API.SUBMISSION_LIKE + submission.getSid(), map, stringSpotsCallBack);

                }
                else if (submission.getIsLike() == 0) {
                    // 点赞
                    Map<String, String> map = new HashMap<>(1);
                    map.put("username", "1");

                    SpotsCallBack<String> stringSpotsCallBack = new SpotsCallBack<String>(ActivityFragment.this.getActivity()) { // todo
                        @Override
                        public void onSuccess(Response response, String userString) {
                            ImageView icon = (ImageView) v.findViewById(R.id.star_icon);
                            icon.setColorFilter(Color.parseColor("#9C27B0"));
                            TextView num = (TextView) v.findViewById(R.id.activity_item_star_num);
                            num.setText(Integer.parseInt(num.getText().toString()) + 1);

                            dismissDialog();
                        }

                        @Override
                        public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                            dismissDialog();
                        }
                    };
                    httpHelper.post(Contants.API.SUBMISSION_LIKE + submission.getSid(), map, stringSpotsCallBack);
                }
            }
        });
    }
}
