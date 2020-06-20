package com.example.tilitili;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cjj.MaterialRefreshLayout;
import com.example.tilitili.adapter.CommentAdapter;
import com.example.tilitili.data.Comment;
import com.example.tilitili.data.Contants;
import com.example.tilitili.data.Page;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

public class CommentActivity extends Activity implements Pager.OnPageListener<Comment> {

    private CommentAdapter commentAdapter;
    @ViewInject(R.id.recyclerview_comment)
    private RecyclerView recyclerView;
    @ViewInject(R.id.refresh_comment_view)
    private MaterialRefreshLayout materialRefreshLayout;
    @ViewInject(R.id.edit_comment)
    private EditText commentEditText;


    Pager pager;
    private HttpHelper httpHelper;

    private int sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);
        ViewUtils.inject(this);
        httpHelper = HttpHelper.getInstance();
        Intent getIntent = getIntent();
        sid = getIntent.getIntExtra("sid", -1);

        init();
    }

    public void init() {
        SpotsCallBack<String> callBack = new SpotsCallBack<String>(this) {
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
                Page<Comment> commentPage = null;
                List<Comment> comments = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(page);
                    JSONArray items = jsonObject.getJSONArray("list");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = (JSONObject) items.get(i);
                        comments.add(new Comment(item.getString("content"),
                                item.getString("nickname"),
                                item.getString("avatar"),
                                item.getLong("commentTime")));
                    }
                    commentPage = new Page<>(jsonObject.getInt("currentPage"),
                            jsonObject.getInt("pageSize"),
                            jsonObject.getInt("totalPage"),
                            jsonObject.getInt("totalCount"),
                            comments);
                    pager.setPageIndex(commentPage.getCurrentPage());
                    pager.setPageCount(commentPage.getPageSize());
                    pager.setTotalPage(commentPage.getTotalPage());
                    pager.showData(commentPage.getList(), commentPage.getTotalPage(), commentPage.getTotalCount());
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
        pager.setUrl(Contants.API.GET_SUBMISSION_COMMENT + sid);
        pager.setLoadMore(true);
        pager.setOnPageListener(this);
        pager.setPageSize(5);
        pager.request();

    }

    @Override
    public void load(List<Comment> datas, int totalPage, int totalCount) {
        commentAdapter = new CommentAdapter(this, datas);
//        commentAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Comment comment = commentAdapter.getItem(position);
//                Intent intent = new Intent(PlateDetailActivity.this, TextDetailActivity.class);
//                startActivity(intent);
//            }
//        });

        recyclerView.setAdapter(commentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void refresh(List<Comment> datas, int totalPage, int totalCount) {
        commentAdapter.refreshData(datas);
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void loadMore(List<Comment> datas, int totalPage, int totalCount) {
        commentAdapter.loadMoreData(datas);
        recyclerView.scrollToPosition(commentAdapter.getDatas().size());
    }

    @OnClick(R.id.comment_title_bar_back)
    public void back(View view) {
        finish();
    }

    @OnClick(R.id.btn_post_comment)
    public void postComment(View view) {
        Map<String, String> map = new HashMap<>(1);
        map.put("content", commentEditText.getText().toString());
        httpHelper.post(Contants.API.POST_COMMENT_URL + sid, map, new SpotsCallBack<String>(CommentActivity.this) {
            @Override
            public void onSuccess(Response response, String s) {
                ToastUtils.show(this.getContext(), "发送成功");
                dismissDialog();
                init();
            }

            @Override
            public void onError(Response response, ErrorMessage errorMessage, Exception e) {
                ToastUtils.show(this.getContext(), "发送失败");
                dismissDialog();
            }
        });
    }
}
