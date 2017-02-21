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
import cn.com.films66.app.activity.ServiceActivity;
import cn.com.films66.app.activity.WebViewActivity;
import cn.com.films66.app.adapter.SettingAdapter;
import cn.com.films66.app.model.SettingEntity;
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
        mSettingAdapter = new SettingAdapter(mContext, initSettingData(), R.layout.item_settings);
        mRvContainer.setLayoutManager(new LinearLayoutManager(mContext));
        mRvContainer.setAdapter(mSettingAdapter);

        mSettingAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int viewType, int position) {
                Intent intent;
                switch (mSettingAdapter.getItem(position).getSetId()) {
                    case SettingEntity.RECOMMEND:
                        break;
                    case SettingEntity.CLEAR:
                        ToastUtils.getInstance().showToast("清除完成");
                        break;
                    case SettingEntity.FEEDBACK:
                        intent = new Intent(mContext, FeedbackActivity.class);
                        startActivity(intent);
                        break;
                    case SettingEntity.UPDATE:
                        ToastUtils.getInstance().showToast("已经是最新版本");
                        break;
                    case SettingEntity.DISCLAIMER:
                        intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra(Constants.KEY_WEB_VIEW_TYPE, 1);
                        startActivity(intent);
                        break;
                    case SettingEntity.SERVICE:
                        intent = new Intent(mContext, ServiceActivity.class);
                        startActivity(intent);
                        break;
                    case SettingEntity.ABOUT:
                        intent = new Intent(mContext, AboutActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }


    private List<SettingEntity> initSettingData() {
        List<SettingEntity> settings = new ArrayList<>();
//        settings.add(new SettingsInfo(SettingsInfo.RECOMMEND, R.mipmap.ic_protocol, getString(R.string.recommend)));
//        settings.add(new SettingsInfo(SettingsInfo.SERVICE, R.mipmap.ic_service, getString(R.string.download)));
        settings.add(new SettingEntity(SettingEntity.SERVICE, R.mipmap.ic_help, getString(R.string.help)));
        settings.add(new SettingEntity(SettingEntity.FEEDBACK, R.mipmap.ic_feedback, getString(R.string.feedback)));
        settings.add(new SettingEntity(SettingEntity.CLEAR, R.mipmap.ic_clean_cache, getString(R.string.clean_cache)));
        settings.add(new SettingEntity(SettingEntity.ABOUT, R.mipmap.ic_about, getString(R.string.about)));
//        settings.add(new SettingsInfo(SettingsInfo.UPDATE, R.mipmap.ic_update, getString(R.string.update)));
//        settings.add(new SettingsInfo(SettingsInfo.DISCLAIMER, R.mipmap.ic_disclaimer, getString(R.string.disclaimer)));

        return settings;
    }
}
