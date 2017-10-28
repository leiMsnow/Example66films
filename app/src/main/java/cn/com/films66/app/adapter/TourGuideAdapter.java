package cn.com.films66.app.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.com.films66.app.R;

/**
 * 自定义类实现PagerAdapter，填充显示数据
 */
public class TourGuideAdapter extends PagerAdapter {

    private int[] images = {
            R.mipmap.tour_guide_1,
            R.mipmap.tour_guide_2,
            R.mipmap.tour_guide_3,
            R.mipmap.bg_launcher,
    };

    // 显示多少个页面
    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // 初始化显示的条目对象
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // return super.instantiateItem(container, position);
        // 准备显示的数据，一个简单的TextView
        ImageView tv = new ImageView(container.getContext());
        tv.setBackgroundResource(images[position]);
        // 添加到ViewPager容器
        container.addView(tv);
        // 返回填充的View对象
        return tv;
    }

    // 销毁条目对象
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // super.destroyItem(container, position, object);
        container.removeView((View) object);
    }
}
