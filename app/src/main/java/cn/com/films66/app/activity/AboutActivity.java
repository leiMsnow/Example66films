package cn.com.films66.app.activity;

import android.widget.TextView;

import com.shuyu.core.uils.AppUtils;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.base.AppBaseActivity;

public class AboutActivity extends AppBaseActivity {

    @Bind(R.id.tv_version)
    TextView mTvVersion;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_about;
    }

    @Override
    protected void initData() {
        String version = "Ver " + AppUtils.getVersionName();
        mTvVersion.setText(version);
    }

}
