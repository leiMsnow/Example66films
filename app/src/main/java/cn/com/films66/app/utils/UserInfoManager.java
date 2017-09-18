package cn.com.films66.app.utils;

import android.content.Context;

import com.shuyu.core.uils.SPUtils;

import cn.com.films66.app.model.WeChatInfo;

/**
 * Created by Azure on 2017/9/18.
 */

public class UserInfoManager {

    public static void setUserInfo(Context mContext, WeChatInfo data) {
        SPUtils.putNoClear(mContext, Constants.IS_LOGIN, true);
        SPUtils.putNoClear(mContext, Constants.USER_ID, data.user_id);
        SPUtils.putNoClear(mContext, Constants.USER_NAME, data.name);
        SPUtils.putNoClear(mContext, Constants.USER_IMAGE, data.head_img_url);
    }

    public static void clearUserInfo(Context mContext) {
        SPUtils.putNoClear(mContext, Constants.IS_LOGIN, false);
        SPUtils.putNoClear(mContext, Constants.USER_ID, 0);
        SPUtils.putNoClear(mContext, Constants.USER_NAME, "");
        SPUtils.putNoClear(mContext, Constants.USER_IMAGE, "");
    }
}
