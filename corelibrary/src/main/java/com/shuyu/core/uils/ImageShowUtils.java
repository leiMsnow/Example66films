package com.shuyu.core.uils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shuyu.core.R;

/**
 * Created by Azure on 2016/9/13.
 */

public class ImageShowUtils {

    public static void showImage(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).placeholder(R.mipmap.ic_default_image).into(imageView);
    }

    public static void showImage(Context context, int url, ImageView imageView) {
        Glide.with(context).load(url).placeholder(R.color.transparent).into(imageView);
    }
}
