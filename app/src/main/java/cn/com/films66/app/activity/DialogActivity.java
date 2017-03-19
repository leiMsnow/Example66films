package cn.com.films66.app.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import cn.com.films66.app.R;
import cn.com.films66.app.base.AppBaseActivity;
import cn.com.films66.app.service.DownloadService;

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

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvRight = (TextView) findViewById(R.id.tv_right);

        tvTitle.setText("识别结果");
        tvMessage.setText("识别到精彩剧集啦，下载观看吗？");

        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DownloadService.class);
                intent.putExtras(getIntent());
                startService(intent);
                finish();
            }
        });
    }
}
