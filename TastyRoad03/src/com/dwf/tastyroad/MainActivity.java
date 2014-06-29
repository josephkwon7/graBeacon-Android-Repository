package com.dwf.tastyroad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.wizturnbeacon.R;
import com.wizturn.sdk.WizTurnManager;

public class MainActivity extends Activity implements OnClickListener{
	
	public WizTurnManager _wizturnMgr;
	public final int REQUEST_ENABLE_BT = 0000;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
 		setContentView(R.layout.activity_main);
 		
 		
 		// 블루투스 확인 및 활성화
 		_wizturnMgr = WizTurnManager.sharedInstance(this);
 		
 		if (!_wizturnMgr.isBluetoothEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
 		
 		// button 생성
 		findViewById(R.id.main_start).setOnClickListener(this);
 		findViewById(R.id.main_area).setOnClickListener(this);
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void onClick(View v) {
		switch(v.getId()) {
			
			// 맛집탁색시작 button
			case R.id.main_start:
				Intent intentStart = new Intent(MainActivity.this , WizTurnBeaconList.class);
				startActivity(intentStart);
				break;
			
			// 탐색가능지역 button	
			case R.id.main_area:
				setContentView(R.layout.area);
				WebView mWebView = (WebView) findViewById(R.id.webview);
				mWebView.getSettings().setJavaScriptEnabled(true); 
			    mWebView.loadUrl("http://tastyroad.cafe24.com/googlemap/unitedItemMapView");
			    mWebView.setWebViewClient(new WebViewClientClass()); 
		}
		
	}
	
	// 웹뷰 클래스
	private class WebViewClientClass extends WebViewClient { 
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { 
            view.loadUrl(url); 
            return true; 
        }
	}
}


