package cn.com.films66.app.wxapi;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.uils.SPUtils;
import com.shuyu.core.uils.ToastUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;

import cn.com.films66.app.R;
import cn.com.films66.app.api.BaseApi;
import cn.com.films66.app.api.IServiceApi;
import cn.com.films66.app.model.WeChatInfo;
import cn.com.films66.app.utils.Constants;
import cn.com.films66.app.utils.UserInfoManager;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private IWXAPI mApi;
    String keys = "1a43f40a5ed4ddae5d909bfbb2464f3f";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        mContext = this;
        mApi = WXAPIFactory.createWXAPI(this, Constants.WECHAT_KEY, true);
        mApi.handleIntent(this.getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtils.d(this.getClass().getName(), "onReq: " + baseReq.getType());
    }

    @Override
    public void onResp(BaseResp resp) {
        int result;
        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
            result = R.string.errcode_success;
            getWeChatUserInfo(resp);
        } else {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    result = R.string.errcode_cancel;
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    result = R.string.errcode_deny;
                    break;
                case BaseResp.ErrCode.ERR_UNSUPPORT:
                    result = R.string.errcode_unsupported;
                    break;
                default:
                    result = R.string.errcode_unknown;
                    break;
            }
            ToastUtils.getInstance().showToast(result);
        }
        LogUtils.d(this.getClass().getName(), "onResp.openId: " + resp.openId);
    }

    private void getWeChatUserInfo(BaseResp resp) {
        BaseApi.request(BaseApi.createApi(IServiceApi.class).
                        getWeChatUserInfo(resp.openId),
                new BaseApi.IResponseListener<WeChatInfo>() {
                    @Override
                    public void onSuccess(WeChatInfo data) {
                        ToastUtils.getInstance().showToast("授权成功");
                        UserInfoManager.setUserInfo(mContext, data);
                        finish();
                    }

                    @Override
                    public void onFail() {
                        ToastUtils.getInstance().showToast("授权用户信息失败");
                       UserInfoManager.clearUserInfo(mContext);
                        finish();
                    }
                });
    }
}
