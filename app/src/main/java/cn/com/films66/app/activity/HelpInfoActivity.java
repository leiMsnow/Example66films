package cn.com.films66.app.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.adapter.HelpInfoAdapter;
import cn.com.films66.app.api.BaseApi;
import cn.com.films66.app.api.IServiceApi;
import cn.com.films66.app.base.AppBaseActivity;
import cn.com.films66.app.model.HelpInfo;

public class HelpInfoActivity extends AppBaseActivity {

    @Bind(R.id.rv_container)
    RecyclerView rvContainer;

    private HelpInfoAdapter helpInfoAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_help;
    }

    @Override
    protected void initData() {
        helpInfoAdapter = new HelpInfoAdapter(mContext, null, R.layout.item_help);
        rvContainer.setLayoutManager(new LinearLayoutManager(mContext));
        rvContainer.setAdapter(helpInfoAdapter);

        getHelpInfo();
    }

    private void getHelpInfo() {
        BaseApi.request(BaseApi.createApi(IServiceApi.class).getHelpInfo()
                , new BaseApi.IResponseListener<List<HelpInfo>>() {
                    @Override
                    public void onSuccess(List<HelpInfo> data) {
                        helpInfoAdapter.replaceAll(data);
                    }

                    @Override
                    public void onFail() {

                    }
                });
    }
}
