package com.mobile.scavenger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Activity_Open_Photos extends Activity {
	WebView webview;
	ProgressDialog dialog;
	String string_hunt_url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_show);
		string_hunt_url = getIntent().getStringExtra("hunt_url");

		dialog = new ProgressDialog(Activity_Open_Photos.this);

		webview = (WebView) findViewById(R.id.webview_photo);
		dialog.show();
		webview.loadUrl(string_hunt_url);
		webview.setWebViewClient(new myWebClient());
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
	}

	public class myWebClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			dialog.show();

		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			dialog.dismiss();
		}

	}

}
