package cn.com.films66.app.adapter;

import android.content.Context;

import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.SuperViewHolder;

import java.util.List;

import cn.com.films66.app.R;
import cn.com.films66.app.model.SettingInfo;

/**
 * Created by Azure on 2016/9/11.
 */

public class SettingAdapter extends SuperAdapter<SettingInfo> {

    public SettingAdapter(Context context, List<SettingInfo> items, int layoutResId) {
        super(context, items, layoutResId);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, SettingInfo item) {
        holder.setImageResource(R.id.iv_setting_icon, item.getResId());
        holder.setText(R.id.tv_setting_title, item.getTitle());
    }
}
