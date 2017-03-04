package cn.com.films66.app.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.shuyu.core.uils.ImageShowUtils;

import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.SuperViewHolder;

import java.util.List;

import cn.com.films66.app.R;
import cn.com.films66.app.model.HelpInfo;

/**
 * Created by zhangleilei on 2017/3/3.
 */

public class HelpInfoAdapter extends SuperAdapter<HelpInfo> {

    public HelpInfoAdapter(Context context, List<HelpInfo> items, int layoutResId) {
        super(context, items, layoutResId);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, HelpInfo item) {
        holder.setText(R.id.tv_help, item.content);
        ImageShowUtils.showImage(mContext, item.image_url
                , (ImageView) holder.findViewById(R.id.iv_help));
    }
}
