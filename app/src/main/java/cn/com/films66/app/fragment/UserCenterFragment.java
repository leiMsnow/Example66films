package cn.com.films66.app.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.shuyu.core.BaseFragment;
import com.shuyu.core.uils.ToastUtils;

import org.byteam.superadapter.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.activity.AboutActivity;
import cn.com.films66.app.activity.FeedbackActivity;
import cn.com.films66.app.activity.HelpInfoActivity;
import cn.com.films66.app.activity.WebViewActivity;
import cn.com.films66.app.adapter.SettingAdapter;
import cn.com.films66.app.model.SettingInfo;
import cn.com.films66.app.utils.Constants;

public class UserCenterFragment extends BaseFragment {

    @Bind(R.id.rv_container)
    RecyclerView mRvContainer;
    private SettingAdapter mSettingAdapter;

    public static UserCenterFragment newInstance() {
        UserCenterFragment fragment = new UserCenterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_user_center;
    }

    @Override
    protected void initData() {
        mSettingAdapter = new SettingAdapter(mContext, initSettingData(), R.layout.item_setting);
        mRvContainer.setLayoutManager(new LinearLayoutManager(mContext));
        mRvContainer.setAdapter(mSettingAdapter);

        mSettingAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int viewType, int position) {
                Intent intent;
                switch (mSettingAdapter.getItem(position).getSetId()) {
                    case SettingInfo.RECOMMEND:
                        break;
                    case SettingInfo.CLEAR:
                        ToastUtils.getInstance().showToast("清除完成");
                        break;
                    case SettingInfo.FEEDBACK:
                        intent = new Intent(mContext, FeedbackActivity.class);
                        startActivity(intent);
//                        Router.build("feedback").go(mContext);
                        break;
                    case SettingInfo.UPDATE:
                        ToastUtils.getInstance().showToast("已经是最新版本");
                        break;
                    case SettingInfo.DISCLAIMER:
                        intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra(Constants.KEY_WEB_VIEW_TYPE, 1);
                        startActivity(intent);
                        break;
                    case SettingInfo.SERVICE:
                        intent = new Intent(mContext, HelpInfoActivity.class);
                        startActivity(intent);
                        break;
                    case SettingInfo.ABOUT:
                        intent = new Intent(mContext, AboutActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }


    private List<SettingInfo> initSettingData() {
        List<SettingInfo> settings = new ArrayList<>();
//        settings.add(new SettingsInfo(SettingsInfo.RECOMMEND, R.mipmap.ic_protocol, getString(R.string.recommend)));
//        settings.add(new SettingsInfo(SettingsInfo.SERVICE, R.mipmap.ic_service, getString(R.string.download)));
        settings.add(new SettingInfo(SettingInfo.SERVICE, R.mipmap.ic_help, getString(R.string.help)));
        settings.add(new SettingInfo(SettingInfo.FEEDBACK, R.mipmap.ic_feedback, getString(R.string.feedback)));
        settings.add(new SettingInfo(SettingInfo.CLEAR, R.mipmap.ic_clean_cache, getString(R.string.clean_cache)));
        settings.add(new SettingInfo(SettingInfo.ABOUT, R.mipmap.ic_about, getString(R.string.about)));
//        settings.add(new SettingsInfo(SettingsInfo.UPDATE, R.mipmap.ic_update, getString(R.string.update)));
//        settings.add(new SettingsInfo(SettingsInfo.DISCLAIMER, R.mipmap.ic_disclaimer, getString(R.string.disclaimer)));

        return settings;
    }
}
