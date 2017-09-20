package cn.com.films66.app.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.shuyu.core.uils.DataCleanUtils;
import com.shuyu.core.uils.ImageShowUtils;
import com.shuyu.core.uils.SPUtils;
import com.shuyu.core.uils.ToastUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.byteam.superadapter.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.adapter.SettingAdapter;
import cn.com.films66.app.base.AppBaseActivity;
import cn.com.films66.app.model.SettingInfo;
import cn.com.films66.app.utils.Constants;
import cn.com.films66.app.utils.UserInfoManager;
import cn.com.films66.app.utils.VideoUtils;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppBaseActivity {

    @Bind(R.id.rv_container)
    RecyclerView mRvContainer;

    @Bind(R.id.tv_user_name)
    TextView mTextView;
    @Bind(R.id.tv_logout)
    TextView mLogout;

    @Bind(R.id.iv_user)
    CircleImageView mImageVie;

    private SettingAdapter mSettingAdapter;
    private IWXAPI mApi;
    private boolean isRecognize;



    @Override
    protected int getLayoutRes() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initData() {
        isRecognize = getIntent().getExtras().getBoolean("isRecognize",false);
        mApi = WXAPIFactory.createWXAPI(this, Constants.WECHAT_KEY, true);
        mApi.registerApp(Constants.WECHAT_KEY);
        initAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_recognize){
            if (isRecognize){

            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUserInfo() {

        if ((Boolean) SPUtils.getNoClear(mContext, Constants.IS_LOGIN, false)) {
            mLogout.setVisibility(View.VISIBLE);
            mTextView.setText(SPUtils.getNoClear(mContext, Constants.USER_NAME, "登录").toString());
            ImageShowUtils.showImage(mContext,
                    SPUtils.getNoClear(mContext, Constants.USER_IMAGE, "").toString(), mImageVie);
            mLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfoManager.clearUserInfo(mContext);
                    setUserInfo();
                }
            });
        } else {
            mLogout.setVisibility(View.GONE);
            mTextView.setText("登录");
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    mApi.sendReq(req);
                }
            });
        }


    }

    private void initAdapter() {
        mSettingAdapter = new SettingAdapter(mContext, initSettingData(), R.layout.item_setting);
        mRvContainer.setLayoutManager(new LinearLayoutManager(mContext));
        mRvContainer.setAdapter(mSettingAdapter);

        mSettingAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int viewType, int position) {
                Intent intent;
                switch (mSettingAdapter.getItem(position).getSetId()) {
                    case SettingInfo.CLEAR:
                        cleanCache();
                        break;
                    case SettingInfo.FEEDBACK:
                        intent = new Intent(mContext, FeedbackActivity.class);
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
        settings.add(new SettingInfo(SettingInfo.SERVICE, R.mipmap.ic_help, getString(R.string.help)));
        settings.add(new SettingInfo(SettingInfo.FEEDBACK, R.mipmap.ic_feedback, getString(R.string.feedback)));
        settings.add(new SettingInfo(SettingInfo.CLEAR, R.mipmap.ic_clean_cache, getString(R.string.clean_cache)
                + getCacheSize()));
        settings.add(new SettingInfo(SettingInfo.ABOUT, R.mipmap.ic_about, getString(R.string.about)));
        return settings;
    }

    @NonNull
    private String getCacheSize() {
        String cache = "";
        try {
            cache = DataCleanUtils.getCacheSize(VideoUtils.getVideoCacheDir());
            cache = "(" + cache + ")";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache;
    }

    private void cleanCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataCleanUtils.cleanCustomCache(VideoUtils.getVideoCacheDir()
                                .getAbsolutePath(),
                        new DataCleanUtils.CleanCacheListener() {
                            @Override
                            public void cleanComplete() {
                                if (mContext != null) {
                                    UserInfoActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mSettingAdapter.getItem(2).setTitle(getString(R.string.clean_cache) + getCacheSize());
                                            mSettingAdapter.notifyDataSetChanged();
                                            ToastUtils.getInstance().showToast("清除完成");
                                        }
                                    });
                                }
                            }
                        });
            }
        }).start();
    }
}
