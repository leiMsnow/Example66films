package cn.com.films66.app.activity;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.Bind;
import cn.com.films66.app.R;
import cn.com.films66.app.base.AppBaseActivity;
import cn.com.films66.app.utils.Constants;

public class WebViewActivity extends AppBaseActivity {

    public static final int VIEW_VIEW_TYPE_DISCLAIMER = 1;
    public static final int VIEW_VIEW_TYPE_PLAY_VIDEO = 2;
    public static final int VIEW_VIEW_TYPE_PAY_URL = 3;

    @Bind(R.id.web_view)
    WebView webView;
    @Bind(R.id.pb_progress)
    ProgressBar mPbProgress;

    private boolean isWeChatPay = false;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void initData() {
        switch (getIntent().getIntExtra(Constants.KEY_WEB_VIEW_TYPE, -1)) {
            case VIEW_VIEW_TYPE_DISCLAIMER:
                setTitle("免责声明");
                webView.loadData(getHtmlData(), "text/html;charset=UTF-8", null);
                break;
            case VIEW_VIEW_TYPE_PAY_URL:
                toolbarHide();
                String payUrl = getIntent().getStringExtra(Constants.KEY_PAY_URL);
                webView.loadUrl(payUrl);
                break;
        }
        setView();
    }

    private void setView() {

        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(false);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("banktype=WEIXIN")) {
                    isWeChatPay = true;
                } else if (url.equals("http://b.zhiliaofu.com/")) {
                    finish();
                    return true;
                }
                view.loadUrl(url);
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

    private String getHtmlData() {
        return "<!DOCTYPE HTML>" +
                "<html>" +
                "<head>" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
                "    <title>免责声明</title>" +
                "</head>" +
                "<body style=\"margin:0 atuo;\">" +
                "<div style=\"padding-left:20px;padding-right:20px;\">" +
                "    <p>&nbsp;&nbsp;&nbsp;&nbsp;" + getString(R.string.app_name) + "提醒您：<br/>" +
                "        &nbsp;&nbsp;&nbsp;&nbsp;(以下简称APP)在使用APP前，请您务必仔细阅读并透彻理解本声明。您可以选择不使用APP，" +
                "        但如果您使用APP，您的使用行为将被视为对本声明全部内容的认可。</p>" +
                "    <p>&nbsp;&nbsp;&nbsp;&nbsp;我们将通过您的IP来收集非个人化的信息，例如您的设备的Mac地址、位置信息、" +
                "        浏览器性质、操作系统种类等。通过收集上述信息，我们进行用户流量统计，从而改进应用的管理和服务。</p>" +
                "    <p>&nbsp;&nbsp;&nbsp;&nbsp;APP中的内容均为APP以非人工检索方式、根据您键入或选择的关键字或者资源" +
                "        自动生成到第三方网页的链接，除APP注明之服务条款外，其他一切因使用APP而可能遭致的意外、疏忽、" +
                "        侵权及其造成的损失（包括因下载被搜索链接到的第三方网站内容而感染电脑病毒），APP对其概不负责，" +
                "        亦不承担任何法律责任。</p>" +
                "    <p>&nbsp;&nbsp;&nbsp;&nbsp;任何通过使用APP而搜索到的第三方网页或者任何资源均系他人制作或提供，" +
                "        您可能从该第三方网页上获得内容及享用服务，APP对其合法性概不负责，亦不承担任何法律责任。</p>" +
                "    <p>&nbsp;&nbsp;&nbsp;&nbsp;APP搜索结果根据您键入或选择的关键字或资源自动搜索获得并生成" +
                "        ，不代表APP赞成被搜索链接到的第三方网页上的内容或立场。您应该对使用搜索引擎的结果自行承担" +
                "        风险。APP不做任何形式的保证：不保证搜索结果满足您的要求，不保证搜索服务不中断，不保证搜索" +
                "        结果的安全性、正确性、及时性、合法性。因网络状况、通讯线路、第三方网站等任何原因而导致您不能正常" +
                "        使用APP，APP不承担任何法律责任。</p>" +
                "    <p>&nbsp;&nbsp;&nbsp;&nbsp;APP尊重并保护所有使用APP用户的个人隐私权，您注册的用户名、" +
                "        电子邮件地址等个人资料，非经您亲自许可或根据相关法律、法规的强制性规定，APP不会主动地泄露给" +
                "        第三方。APP提醒您：您在使用搜索引擎时输入的关键字将不被认为是您的个人隐私资料。</p>" +
                "    <p>&nbsp;&nbsp;&nbsp;&nbsp;任何单位或个人认为通过APP搜索链接到的第三方网页内容可能涉嫌侵犯" +
                "        其信息网络传播权，应该及时向APP提出书面权利通知，并提供身份证明、权属证明及详细侵权情况证明。" +
                "        APP在收到上述法律文件后，将会依法尽快断开相关链接内容。</p>" +
                "    <p>&nbsp;&nbsp;&nbsp;&nbsp;详情参见特定频道的著作权保护声明。</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
