package com.shuyu.core.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shuyu.core.R;
import com.shuyu.core.uils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

public class TabsView extends LinearLayout {

    private Context mContext;
    private List<TextView> mChildrenViews = new ArrayList<>();
    private TabsChildViewClickListener childClickListener;
    private int currentPosition = 0;

    private LinearLayout mTabParent;
    private View mIndicatorView;

    public TabsView(Context context) {
        this(context, null);
    }

    public TabsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {

        setOrientation(LinearLayout.VERTICAL);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        mTabParent = new LinearLayout(mContext);
        LayoutParams tabParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                DensityUtils.dp2px(mContext, 48));
        mTabParent.setOrientation(LinearLayout.HORIZONTAL);
        mTabParent.setLayoutParams(tabParams);

        mIndicatorView = new View(mContext);
        LayoutParams indicatorParams = new LayoutParams(0,
                DensityUtils.dp2px(mContext, 2));
        mIndicatorView.setLayoutParams(indicatorParams);
        mIndicatorView.setBackgroundColor(getResources().getColor(R.color.theme_red));

        addView(mTabParent);
        addView(mIndicatorView);

    }

    private void setIndicatorWidth(int childrenCount) {
        LayoutParams indicatorParams = (LayoutParams) mIndicatorView.getLayoutParams();
        indicatorParams.width = mContext.getResources().getDisplayMetrics().widthPixels / childrenCount;
        mIndicatorView.setLayoutParams(indicatorParams);
    }

    /**
     * 添加子项文本数组
     * 先设置setChildViewClickListener再设置此方法
     *
     * @param childrenText tabs显示的文本，数组格式
     * @param listener     tabs点击监听，返回相应的索引号，从0开始
     */
    public void setChildView(List<String> childrenText, TabsChildViewClickListener listener) {
        if (childrenText == null)
            return;
        this.childClickListener = listener;
        mChildrenViews.clear();
        mTabParent.removeAllViews();

        int count = childrenText.size();
        setIndicatorWidth(count);

        for (int i = 0; i < count; i++) {
            final TextView childView = new TextView(mContext);
            LinearLayout.LayoutParams childViewParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            childViewParams.weight = 1;
            childView.setGravity(Gravity.CENTER);
            childView.setLayoutParams(childViewParams);
            childView.setText(childrenText.get(i));
            childView.setTextColor(mContext.getResources().getColor(R.color.normal_dark_grey));
            if (i == currentPosition) {
                childView.setTextColor(mContext.getResources().getColor(R.color.theme_red));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setIndicatorPosition(currentPosition);
                    }
                }, 200);
            }
            if (childClickListener != null) {
                final int temp = i;
                childView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentPosition != temp) {
                            initChildTextColor(temp);
                            childClickListener.onTabsChildViewCLick(temp);
                            setIndicatorPosition(temp);
                            currentPosition = temp;
                        }
                    }
                });
            }
            mChildrenViews.add(childView);
            mTabParent.addView(childView);
        }
    }


    // 初始化文本颜色
    private void initChildTextColor(int position) {
        if (mChildrenViews.size() == 0)
            return;
        for (int i = 0; i < mChildrenViews.size(); i++) {
            mChildrenViews.get(i).setTextColor(mContext.getResources().getColor(R.color.normal_dark_grey));
        }
        mChildrenViews.get(position).setTextColor(mContext.getResources().getColor(R.color.theme_red));
    }

    private void setIndicatorPosition(int index) {
        float start = currentPosition * mIndicatorView.getWidth();
        float end = index * mIndicatorView.getWidth();
        ObjectAnimator indicatorAnimator = ObjectAnimator.ofFloat(mIndicatorView,
                View.TRANSLATION_X, start, end);
        indicatorAnimator.setDuration(300);
        indicatorAnimator.start();
    }

    /**
     * setChildView之前调用
     *
     * @param position
     */
    public void setPosition(int position) {
        currentPosition = position;
    }


    /**
     * 子项点击监听
     */
    public interface TabsChildViewClickListener {
        // 根据点击位置进行不同操作
        void onTabsChildViewCLick(int position);
    }
}
