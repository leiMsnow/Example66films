package cn.com.films66.app.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.shuyu.core.widget.ChangeColorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.base.AppBaseActivity;
import cn.com.films66.app.fragment.MainFragment;
import cn.com.films66.app.fragment.UserCenterFragment;

public class MainActivity extends AppBaseActivity {

    @Bind(R.id.ccv_main)
    ChangeColorView ccvMain;
    @Bind(R.id.ccv_me)
    ChangeColorView ccvMe;

    private List<Fragment> mFragments = null;
    private List<ChangeColorView> mChangeColorViews = null;
    private int[] mTitles = {R.string.nav_main, R.string.nav_me};

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        hideToolbarBack();
        initBottomMenu();
        initDefaultFragment();
    }

    private void initDefaultFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(MainFragment.newInstance());
        mFragments.add(UserCenterFragment.newInstance());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            ft.add(R.id.fl_container, mFragments.get(i));
            if (i > 0) ft.hide(mFragments.get(i));
        }
        ft.commit();
    }

    private void initBottomMenu() {
        mChangeColorViews = new ArrayList<>();
        mChangeColorViews.add(ccvMain);
        mChangeColorViews.add(ccvMe);
        for (int i = 0; i < mChangeColorViews.size(); i++) {
            mChangeColorViews.get(i).setOnClickListener(new OnButtonMenuClickListener(i));
        }
        mChangeColorViews.get(0).setIconAlpha(1.0f);
    }

    class OnButtonMenuClickListener implements View.OnClickListener {
        int position;

        OnButtonMenuClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            setBottomMenu(position);
        }
    }

    private void setBottomMenu(int position) {
        if (mChangeColorViews != null) {
            for (int i = 0; i < mChangeColorViews.size(); i++) {
                mChangeColorViews.get(i).setIconAlpha(0);
            }
            setTitle(mTitles[position]);
            mChangeColorViews.get(position).setIconAlpha(1.0f);
            switchContent(position);
        }
    }

    public void switchContent(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            ft.hide(mFragments.get(i));
        }
        ft.show(mFragments.get(position));
        ft.commit();
    }
}
