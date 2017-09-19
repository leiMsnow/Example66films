package cn.com.films66.app.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.shuyu.core.uils.ImageShowUtils;

import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.SuperViewHolder;

import java.util.List;

import cn.com.films66.app.R;
import cn.com.films66.app.model.Film;
import cn.com.films66.app.model.MyDanmaku;

/**
 * Created by zhangleilei on 21/02/2017.
 */
public class DanmakuAdapter extends SuperAdapter<MyDanmaku> {

    public DanmakuAdapter(Context context, List<MyDanmaku> items, int layoutResId) {
        super(context, items, layoutResId);
    }

    public static DanmakuAdapter createAdapter(Context context){
        return new DanmakuAdapter(context,null,R.layout.item_danmaku);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, MyDanmaku item) {
        holder.setText(R.id.tv_danmaku, item.content);
    }
}
