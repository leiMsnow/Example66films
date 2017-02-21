package com.shuyu.core.widget;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shuyu.core.R;

import org.byteam.superadapter.SuperAdapter;

/**
 * 加载更多RecyclerView
 * 使用方法：
 * 1 设置setOnLoadMoreListener回调
 * 2 [可选]设置pageSize(pageSize)默认为10条
 * 3 数据调用成功后调用setResultStatus(resultSize)
 * Created by zhangleilei on 6/14/16.
 */
public class LoadMoreRecyclerView extends RecyclerView {


    private View mFooter;
    private TextView mTextView;
    private ProgressBar mProgressBar;

    // 判断是否正在加载
    private boolean isLoading;
    // 判断是否全部加载完
    private boolean isLoadFull;
    //每页返回的数量
    private int pageSize = 10;

    private OnLoadMoreListener onLoadListener;

    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }


    // 初始化组件
    private void initView() {

        if (isInEditMode())
            return;

        mFooter = LayoutInflater.from(getContext()).inflate(R.layout.load_more_item, this);

        mTextView = (TextView) mFooter.findViewById(R.id.tv_more_data);
        mProgressBar = (ProgressBar) mFooter.findViewById(R.id.pb_progress);

        addOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean isLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                int totalItemCount = manager.getItemCount();
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem == (totalItemCount - 1)
                        && isLast
                        && !isLoading
                        && !isLoadFull) {
                    if (onLoadListener != null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onLoadListener.onLoadMore();
                            }
                        }, 500);

                        setLoadStatus();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isLast = dy > 0;
            }
        });
    }

    private void setLoadStatus() {
        if (getAdapter() instanceof SuperAdapter) {
            ((SuperAdapter) getAdapter()).addFooterView(mFooter);
            isLoading = true;
            mTextView.setText("正在加载...");
            mProgressBar.setVisibility(VISIBLE);
            mFooter.setVisibility(VISIBLE);
        }
    }

    /**
     * 这个方法是根据结果的大小来决定footer显示的。
     * 这里假定每次请求的条数为10。如果请求到了10条。则认为还有数据。
     * 如过结果不足10条，则认为数据已经全部加载，这时footer显示已经全部加载
     *
     * @param resultSize
     */
    public void setResultStatus(int resultSize) {
        isLoading = false;
        if (resultSize == 0 || resultSize > 0 && resultSize < pageSize) {
            isLoadFull = true;
            mFooter.setVisibility(GONE);
        } else if (resultSize == pageSize) {
            isLoadFull = false;
            mFooter.setVisibility(View.GONE);
        }
    }

    // 加载更多监听
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


    public interface OnLoadMoreListener {
        void onLoadMore();
    }

}
