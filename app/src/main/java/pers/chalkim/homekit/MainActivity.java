package pers.chalkim.homekit;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toolbar;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static WebView webView;
    private static SharedPreferences sp;
    private NsdHandler nsdHandler = new NsdHandler(this);

    private static class NsdHandler extends Handler{

         private final WeakReference<Activity> mActivity;

        private NsdHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivity.get();
            if (activity != null){
                Log.d(TAG, "Handler: " + msg.obj);

                String url = "http://" + msg.obj;
                webView.loadUrl(url);

                //存入数据
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("lastUrl", url);
                editor.apply();
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取SharedPreferences对象
        Context ctx = MainActivity.this;
        sp = ctx.getSharedPreferences("preferences", MODE_PRIVATE);

        String lastUrl = sp.getString("lastUrl", "about:blank");
        Log.d("SP", sp.getString("lastUrl", "about:blank"));

        NsdClientManager nsdClientManager = NsdClientManager.getInstance(this, nsdHandler);
        nsdClientManager.searchNsdServer("homekit_web_ui");

        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {
            //设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);  //设置WebView属性,运行执行js脚本
        webView.loadUrl(lastUrl);          //调用loadUrl方法为WebView加入链接
        setContentView(webView);                           //调用Activity提供的setContentView将webView显示出来
    }
}