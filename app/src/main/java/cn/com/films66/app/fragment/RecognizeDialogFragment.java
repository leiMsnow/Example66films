package cn.com.films66.app.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.shuyu.core.BaseDialogFragment;

import cn.com.films66.app.R;
import cn.com.films66.app.utils.Constants;

/**
 * Created by zhangleilei on 10/27/16.
 */
public class RecognizeDialogFragment extends BaseDialogFragment {

    TextView tvTitle;
    TextView tvMessage;
    TextView tvLeft;
    TextView tvRight;

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_dialog;
    }

    @Override
    protected void init() {
        setCancelable(true);
        String message = getArguments().getString(Constants.KEY_RECOGNIZE_RESULT);
        tvTitle = (TextView) mView.findViewById(R.id.tv_title);
        tvMessage = (TextView) mView.findViewById(R.id.tv_message);
        tvLeft = (TextView) mView.findViewById(R.id.tv_left);
        tvRight = (TextView) mView.findViewById(R.id.tv_right);

        if (!TextUtils.isEmpty(message))
            tvMessage.setText(message);

        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
