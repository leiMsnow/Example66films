package cn.com.films66.app.fragment;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.shuyu.core.BaseFragment;

import butterknife.Bind;
import cn.com.films66.app.R;

public class MainFragment extends BaseFragment {

    @Bind(R.id.rv_container)
    RecyclerView rvContainer;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initData() {

    }
}
