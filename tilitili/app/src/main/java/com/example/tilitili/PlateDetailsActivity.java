package com.example.tilitili;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cjj.MaterialRefreshLayout;
import com.example.tilitili.adapter.BaseAdapter;
import com.example.tilitili.adapter.HotSubmissionAdapter;
import com.example.tilitili.data.Contants;
import com.example.tilitili.data.Page;
import com.example.tilitili.data.Plate;
import com.example.tilitili.data.Submission;
import com.example.tilitili.http.ErrorMessage;
import com.example.tilitili.http.SpotsCallBack;
import com.example.tilitili.ui.BaseFragment;
import com.example.tilitili.utils.Pager;
import com.example.tilitili.utils.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class PlateDetailsActivity extends Activity implements Pager.OnPageListener<Submission> {
    @ViewInject(R.id.plate_details_title_bar_title)
    private TextView title_text;

    @ViewInject(R.id.text_plate_details_intro)
    private TextView intro_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plate_details);
        ViewUtils.inject(this);

        Intent getIntent = getIntent();
        String titleStr = getIntent.getStringExtra("title");
        String introStr = getIntent.getStringExtra("intro");

        title_text.setText(titleStr);
        intro_text.setText(introStr);
        init();
    }

    private HotSubmissionAdapter hotSubmissionAdapter;
    @ViewInject(R.id.recyclerview_plate_details)
    private RecyclerView recyclerView;
    @ViewInject(R.id.refresh_plate_details_view)
    private MaterialRefreshLayout materialRefreshLayout;

    Pager pager;
    SpotsCallBack<String> callBack;

    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ViewUtils.inject(this, view);
        return view;
    }

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
                                item.getInt("likes"),
                                item.getInt("post_time")));
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
        pager = new Pager(this, callBack, materialRefreshLayout);
        pager.setUrl(Contants.API.GET_HOT);
        pager.setLoadMore(true);
        pager.setOnPageListener(this);
        pager.setPageSize(5);
        pager.request();

    }

    @Override
    public void load(List<Submission> datas, int totalPage, int totalCount) {
        hotSubmissionAdapter = new HotSubmissionAdapter(this, datas);
        hotSubmissionAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Submission submission = hotSubmissionAdapter.getItem(position);
                Intent intent = new Intent(PlateDetailsActivity.this, TextDetailActivity.class);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(hotSubmissionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
