package cn.com.films66.app.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import cn.com.films66.app.R;

public class WebEventActivity extends AbsEventActivity {

    @Bind(R.id.web_view)
    WebView webView;
    @Bind(R.id.pb_progress)
    ProgressBar mPbProgress;
    @Bind(R.id.tv_complete)
    TextView tvComplete;
    @Bind(R.id.rl_top)
    View topView;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_web_event;
    }

    @Override
    protected void initData() {
        setTitle("");
        toolbarHide();
        topView.setVisibility(View.GONE);
        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mEvents != null) {
            webView.loadUrl(mEvents.resources_url);
        }
        setView();
    }


    private void setView() {
        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        WebSettings webSettings = webView.getSettings();
        if (webSettings == null)
            return;
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String request) {
                webView.loadUrl(request);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mPbProgress.setVisibility(View.GONE);
                } else {
                    mPbProgress.setVisibility(View.VISIBLE);
                    mPbProgress.setProgress(newProgress);
                }
            }
        });
    }
}
