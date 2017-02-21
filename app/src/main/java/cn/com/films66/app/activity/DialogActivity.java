package cn.com.films66.app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.com.films66.app.R;
import cn.com.films66.app.fragment.RecognizeDialogFragment;

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        RecognizeDialogFragment myDialogFragment = new RecognizeDialogFragment();
        myDialogFragment.setArguments(getIntent().getExtras());
        myDialogFragment.show(getSupportFragmentManager(), "myDialog");
    }
}
