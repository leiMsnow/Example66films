package cn.com.films66.app.activity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.chenenyu.router.annotation.Route;
import com.shuyu.core.uils.ToastUtils;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.api.BaseApi;
import cn.com.films66.app.api.IServiceApi;
import cn.com.films66.app.base.AppBaseActivity;
import cn.com.films66.app.model.NoBodyEntity;

@Route("feedback")
public class FeedbackActivity extends AppBaseActivity {

    @Bind(R.id.et_feedback)
    EditText mEtFeedback;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.feed_back) {
            submitFeedback(mEtFeedback.getText().toString());
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitFeedback(String content) {
        BaseApi.request(BaseApi.createApi(IServiceApi.class).sendFeedback(content, 1),
                new BaseApi.IResponseListener<NoBodyEntity>() {
                    @Override
                    public void onSuccess(NoBodyEntity data) {
                        ToastUtils.getInstance().showToast("感谢您的反馈");
                    }

                    @Override
                    public void onFail() {
                        ToastUtils.getInstance().showToast("反馈失败");
                    }
                });
    }
}
