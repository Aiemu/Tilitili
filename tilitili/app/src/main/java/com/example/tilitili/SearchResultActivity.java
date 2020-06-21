package com.example.tilitili;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.tilitili.adapter.BaseAdapter;
import com.example.tilitili.adapter.HotSubmissionAdapter;
import com.example.tilitili.adapter.SearchUserAdapter;
import com.example.tilitili.data.Contants;
import com.example.tilitili.data.Following;
import com.example.tilitili.data.Submission;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.HttpHelper;
import com.example.tilitili.http.SpotsCallBack;
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

import okhttp3.Response;

public class SearchResultActivity extends Activity {

    @ViewInject(R.id.recyclerview_search_result)
    private RecyclerView recyclerView;
    @ViewInject(R.id.refresh_search_result_view)
    private MaterialRefreshLayout materialRefreshLayout;
    @ViewInject(R.id.search_user_text_view)
    private TextView user_text_view;
    @ViewInject(R.id.search_submission_text_view)
    private TextView submission_text_view;

    private String searchContent;
    private HttpHelper httpHelper;

    private HotSubmissionAdapter hotSubmissionAdapter;
    private SearchUserAdapter searchUserAdapter;

    private List<Submission> submissions;
    private List<Following> followings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        ViewUtils.inject(this);
        searchContent = getIntent().getStringExtra("search_content");
        httpHelper = HttpHelper.getInstance();
        materialRefreshLayout.setLoadMore(false);
        submission_text_view.setTextColor(Color.parseColor("#82318E"));
        user_text_view.setTextColor(Color.BLACK);
        submissions = new ArrayList<>();
        followings = new ArrayList<>();
        searchUserAdapter = new SearchUserAdapter(this, followings);
        hotSubmissionAdapter = new HotSubmissionAdapter(this, submissions);
        init();
    }

    public void init() {
        searchSubmission();
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                materialRefreshLayout.setLoadMore(false);
                searchSubmission();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                Toast.makeText(SearchResultActivity.this, "无更多数据", Toast.LENGTH_LONG).show();
                materialRefreshLayout.finishRefreshLoadMore();
                materialRefreshLayout.setLoadMore(false);
            }
        });
    }

    public void searchSubmission() {
        httpHelper.post(Contants.API.SUBMISSION_SEARCH_URL, new HashMap<String, String>(2) {
            {
                put("title", searchContent);
                put("maxCount", String.valueOf(10));
            }
        }, new SpotsCallBack<String>(SearchResultActivity.this) {
            @Override
            public void onSuccess(Response response, String s) {
                dismissDialog();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray items = jsonObject.getJSONArray("submission");
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
                                item.getInt("following")));
                    }
                    hotSubmissionAdapter.notifyDataSetChanged();
                    hotSubmissionAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Submission submission = hotSubmissionAdapter.getItem(position);
                            Intent intent = new Intent(SearchResultActivity.this, TextDetailActivity.class);
                            intent.putExtra("submission", (Serializable) submission);
                            startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(hotSubmissionAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultActivity.this));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                Toast.makeText(this.getContext(), "加载数据失败", Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    public void searchUser() {
        httpHelper.post(Contants.API.USER_SEARCH_URL, new HashMap<String, String>(2) {
            {
                put("username", searchContent);
                put("maxCount", String.valueOf(10));
            }
        }, new SpotsCallBack<String>(SearchResultActivity.this) {
            @Override
            public void onSuccess(Response response, String s) {
                dismissDialog();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray items = jsonObject.getJSONArray("users");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = (JSONObject) items.get(i);
                        Following following = new Following(
                                item.getInt("uid"),
                                item.getString("nickname"),
                                item.getString("avatar")
                        );
                        following.setIsFollowing(item.getInt("isFollowing"));
                        followings.add(following);
                    }
                    searchUserAdapter.notifyDataSetChanged();
                    searchUserAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Following following = searchUserAdapter.getItem(position);
                            Intent intent = new Intent(SearchResultActivity.this, UserInfoActivity.class);
                            intent.putExtra("uid", following.getUserId());
                            intent.putExtra("isFollowing", following.getIsFollowing());
                            startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(searchUserAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultActivity.this));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                Toast.makeText(this.getContext(), "加载数据失败", Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }

    @OnClick(R.id.search_submission_text_view)
    public void searchSubmissionClicked(View v) {
        submissions.clear();
        hotSubmissionAdapter.notifyDataSetChanged();
        followings.clear();
        searchUserAdapter.notifyDataSetChanged();
        searchSubmission();
        submission_text_view.setTextColor(Color.parseColor("#82318E"));
        user_text_view.setTextColor(Color.BLACK);
    }

    @OnClick(R.id.search_user_text_view)
    public void searchUserClicked(View v) {
        submissions.clear();
        hotSubmissionAdapter.notifyDataSetChanged();
        followings.clear();
        searchUserAdapter.notifyDataSetChanged();
        searchUser();
        submission_text_view.setTextColor(Color.BLACK);
        user_text_view.setTextColor(Color.parseColor("#82318E"));

    }

    @OnClick(R.id.search_result_title_bar_back)
    public void back(View v) {
        finish();
    }
}
