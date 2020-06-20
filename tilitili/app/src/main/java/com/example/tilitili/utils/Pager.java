package com.example.tilitili.utils;

import android.content.Context;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.tilitili.http.BaseHttpCallback;
import com.example.tilitili.http.HttpHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pager {

    private HttpHelper httpHelper;
    private BaseHttpCallback callback;
    private OnPageListener onPageListener;
    private Context mContext;

    private MaterialRefreshLayout mRefreshLayout;

    private boolean canLoadMore;
    private String mUrl;

    private int totalPage = 1;
    private int pageIndex = 1;
    private int pageCount = 5;
    private HashMap<String, Object> params = new HashMap<>(5);

    public static final int STATE_NORMAL = 0;
    public static final int STATE_REFREH = 1;
    public static final int STATE_MORE = 2;

    private int state = STATE_NORMAL;

    public Pager(Context context, BaseHttpCallback callback, MaterialRefreshLayout refreshLayout) {
        httpHelper = HttpHelper.getInstance();
        this.callback = callback;
        this.mContext = context;
        this.mRefreshLayout = refreshLayout;
        this.initRefreshLayout();
    }

    public void request() {
        requestData();
    }

    private void initRefreshLayout() {
        this.mRefreshLayout.setLoadMore(this.canLoadMore);
        this.mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                mRefreshLayout.setLoadMore(canLoadMore);
                refresh();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (pageIndex < totalPage)
                    loadMore();
                else {
                    Toast.makeText(mContext, "无更多数据", Toast.LENGTH_LONG).show();
                    materialRefreshLayout.finishRefreshLoadMore();
                    materialRefreshLayout.setLoadMore(false);
                }
            }
        });
    }

    /**
     * 请求数据
     */
    private void requestData() {
        String url = buildUrl();
        httpHelper.get(url, callback);
    }

    /**
     * 显示数据
     */
    public <T> void showData(List<T> datas, int totalPage, int totalCount) {
        if (datas == null || datas.size() <= 0) {
            Toast.makeText(this.mContext, "加载不到数据", Toast.LENGTH_LONG).show();
            return;
        }
        if (STATE_NORMAL == state) {
            if (onPageListener != null) {
                onPageListener.load(datas, totalPage, totalCount);
            }
        } else if (STATE_REFREH == state) {
            this.mRefreshLayout.finishRefresh();
            if (onPageListener != null) {
                onPageListener.refresh(datas, totalPage, totalCount);
            }

        } else if (STATE_MORE == state) {
            this.mRefreshLayout.finishRefreshLoadMore();
            if (onPageListener != null) {
                onPageListener.loadMore(datas, totalPage, totalCount);
            }
        }
    }

    /**
     * 刷新数据
     */
    private void refresh() {
        state = STATE_REFREH;
        this.pageIndex = 1;
        requestData();
    }

    /**
     * 隐藏数据
     */
    private void loadMore() {
        state = STATE_MORE;
        this.pageIndex = ++this.pageIndex;
        requestData();
    }

    private String buildUrl() {
        return this.mUrl + "?" + buildUrlParams();
    }

    private String buildUrlParams() {
        HashMap<String, Object> map = this.params;
        map.put("page", this.pageIndex - 1);
        map.put("count", this.pageCount);

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public MaterialRefreshLayout getmRefreshLayout() {
        return this.mRefreshLayout;
    }

    public Context getmContext() {
        return this.mContext;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setPageSize(int pageCount) {
        this.pageCount = pageCount;
    }

    public void putParam(String key, Object value) {
        params.put(key, value);
    }

    public void setLoadMore(boolean loadMore) {
        this.canLoadMore = loadMore;
    }

    public void setOnPageListener(OnPageListener onPageListener) {
        this.onPageListener = onPageListener;
    }

    private void valid() {
        if (this.mContext == null)
            throw new RuntimeException("content can't be null");

        if (this.mUrl == null || "".equals(this.mUrl))
            throw new RuntimeException("url can't be  null");

        if (this.mRefreshLayout == null)
            throw new RuntimeException("MaterialRefreshLayout can't be  null");
    }

    public interface OnPageListener<T> {
        void load(List<T> datas, int totalPage, int totalCount);

        void refresh(List<T> datas, int totalPage, int totalCount);

        void loadMore(List<T> datas, int totalPage, int totalCount);
    }

    public int getState() {
        return state;
    }

}
