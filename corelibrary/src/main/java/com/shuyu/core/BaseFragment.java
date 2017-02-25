package com.shuyu.core;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shuyu.core.uils.KeyBoardUtils;

import butterknife.ButterKnife;

/**
 * 通用模板类
 * initData 数据统一处理
 * Created by zhangleilei on 15/7/7 18:54.
 */
public abstract class BaseFragment extends Fragment {

    protected View mView;
    protected Context mContext;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onPause() {
        super.onPause();
        KeyBoardUtils.hideSoftKeyboard(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutRes(), container, false);
        ButterKnife.bind(this, mView);
        initData();
        return mView;
    }

    /**
     * 获取Fragment的布局文件
     *
     * @return Layout
     */
    protected abstract int getLayoutRes();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
