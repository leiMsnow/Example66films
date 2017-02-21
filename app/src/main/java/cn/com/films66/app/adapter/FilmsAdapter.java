package cn.com.films66.app.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.shuyu.core.uils.ImageShowUtils;

import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.SuperViewHolder;

import java.util.List;

import cn.com.films66.app.R;
import cn.com.films66.app.model.FilmEntity;

/**
 * Created by zhangleilei on 21/02/2017.
 */

public class FilmsAdapter extends SuperAdapter<FilmEntity> {

    public FilmsAdapter(Context context, List<FilmEntity> items, int layoutResId) {
        super(context, items, layoutResId);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, FilmEntity item) {
        holder.setText(R.id.tv_video_title, item.name)
                .setText(R.id.tv_video_time, item.getRuntime());

        ImageShowUtils.showImage(mContext, item.cover_url,
                (ImageView) holder.findViewById(R.id.iv_video_bg));
    }
}
