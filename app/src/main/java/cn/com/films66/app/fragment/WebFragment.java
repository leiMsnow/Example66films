package cn.com.films66.app.fragment;

import android.webkit.WebView;

import com.shuyu.core.BaseFragment;

import butterknife.Bind;
import cn.com.films66.app.R;

/**
 * Created by zhangleilei on 2017/2/25.
 */

public class WebFragment extends BaseFragment {

    @Bind(R.id.web_view)
    WebView webView;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_web;
    }

    @Override
    protected void initData() {

    }
}
