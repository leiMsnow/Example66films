package cn.com.films66.app.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.shuyu.core.BaseDialogFragment;

import cn.com.films66.app.R;
import cn.com.films66.app.activity.RecognizeResultActivity;
import cn.com.films66.app.model.CustomFile;
import cn.com.films66.app.utils.Constants;

/**
 * Created by zhangleilei on 10/27/16.
 */
@Deprecated
public class RecognizeDialogFragment extends BaseDialogFragment {

    private TextView tvTitle;
    private TextView tvMessage;
    private TextView tvLeft;
    private TextView tvRight;

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_dialog;
    }

    @Override
    protected void init() {
        setCancelable(false);
        CustomFile customFile = getArguments().getParcelable(Constants.KEY_RECOGNIZE_RESULT);
        if (customFile == null) {
            dismiss();
            return;
        }
        tvTitle = (TextView) mView.findViewById(R.id.tv_title);
        tvMessage = (TextView) mView.findViewById(R.id.tv_message);
        tvLeft = (TextView) mView.findViewById(R.id.tv_left);
        tvRight = (TextView) mView.findViewById(R.id.tv_right);

        tvTitle.setText("识别结果");
        if (!TextUtils.isEmpty(customFile.title))
            tvMessage.setText(customFile.title);
        else
            tvMessage.setText(customFile.audio_id);

        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RecognizeResultActivity.class);
                intent.putExtras(getArguments());
                startActivity(intent);
                dismiss();
            }
        });
    }
}
