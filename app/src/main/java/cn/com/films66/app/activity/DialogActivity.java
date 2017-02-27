package cn.com.films66.app.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import cn.com.films66.app.R;
import cn.com.films66.app.base.AppBaseActivity;
import cn.com.films66.app.model.CustomFileEntity;
import cn.com.films66.app.utils.Constants;

@Deprecated
public class DialogActivity extends AppBaseActivity {

    private TextView tvTitle;
    private TextView tvMessage;
    private TextView tvLeft;
    private TextView tvRight;

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
        CustomFileEntity customFile = getIntent().getParcelableExtra(Constants.KEY_RECOGNIZE_RESULT);
        if (customFile == null) {
            finish();
            return;
        }
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvRight = (TextView) findViewById(R.id.tv_right);

        tvTitle.setText("识别结果");
        if (!TextUtils.isEmpty(customFile.title))
            tvMessage.setText(customFile.title);
        else
            tvMessage.setText(customFile.audio_id);

        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RecognizeResultActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
                finish();
            }
        });
    }
}
