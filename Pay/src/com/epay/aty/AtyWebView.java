package com.epay.aty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.tt.toolsUtil.Config;

public class AtyWebView extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_webview);
		WebView wv = (WebView) this.findViewById(R.id.wv_webView);
		WebSettings webSettings = wv.getSettings();
		webSettings.setBlockNetworkImage(false);
		//webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		//webSettings.setUseWideViewPort(true);
		//webSettings.setLoadWithOverviewMode(true);
		Intent intent = this.getIntent();
		String coupon_detail_url = intent.getStringExtra(Config.WEB_URL);
//lllllllllllllllll_jml@@@@@@@@@@@@
		//111111111111_ml!!!!!!!!!!!!!!!

//wv.loadDataWithBaseURL(coupon_detail_url, "", "text/html", "utf-8", null);
		wv.loadUrl(coupon_detail_url);
		
	}

}
