package cn.com.films66.app.fragment;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.Bind;
import cn.com.films66.app.R;

/**
 * Created by zhangleilei on 2017/2/25.
 */

public class WebFragment extends EventFragment {

    @Bind(R.id.web_view)
    WebView webView;
    @Bind(R.id.pb_progress)
    ProgressBar mPbProgress;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_web;
    }

    @Override
    protected void initData() {
        setView();
        if (mEvents != null) {
            webView.loadUrl(mEvents.resources_url);
        }
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
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
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
