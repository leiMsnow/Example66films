package cn.com.films66.app.activity;

import android.widget.EditText;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.base.AppBaseActivity;

public class FeedbackActivity extends AppBaseActivity {

    @Bind(R.id.et_feedback)
    EditText mEtFeedback;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void initData() {
//        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(mEtFeedback.getText().toString().trim())) {
//                    ToastUtils.getInstance().showToast("请输入您的意见");
//                    return;
//                }
//                submitFeedback(mEtFeedback.getText().toString(), mEtTel.getText().toString());
//            }
//        });
    }

    private void submitFeedback(String content, String contact) {

    }
}
