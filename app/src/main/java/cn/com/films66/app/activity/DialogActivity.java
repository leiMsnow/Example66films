package cn.com.films66.app.activity;

import cn.com.films66.app.R;
import cn.com.films66.app.base.AppBaseActivity;
import cn.com.films66.app.fragment.RecognizeDialogFragment;

public class DialogActivity extends AppBaseActivity {

    @Override
    protected boolean includeToolbar() {
        return false;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_dialog;
    }

    @Override
    protected void initData() {
        RecognizeDialogFragment myDialogFragment = new RecognizeDialogFragment();
        myDialogFragment.setArguments(getIntent().getExtras());
        myDialogFragment.show(getSupportFragmentManager(), "myDialog");
    }
}
