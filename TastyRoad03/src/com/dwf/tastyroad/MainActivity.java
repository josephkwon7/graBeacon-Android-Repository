package com.dwf.tastyroad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.wizturnbeacon.R;
import com.wizturn.sdk.WizTurnManager;

public class MainActivity extends Activity implements OnClickListener{
	
	public WizTurnManager _wizturnMgr;
	public final int REQUEST_ENABLE_BT = 0000;
	
	public int mMode;
	public final int AREA = 2;
	public final int MAIN = 3;
	
	private ImageButton mBtn_back;
	WebView mWebView;
	ProgressBar progress;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
 		setContentView(R.layout.activity_main);
 		mMode = MAIN;
 		
 		// 블루투스 확인 및 활성화
 		_wizturnMgr = WizTurnManager.sharedInstance(this);
 		
 		if (!_wizturnMgr.isBluetoothEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
 		
 		// 버튼 생성
 		findViewById(R.id.main_start).setOnClickListener(this);
 		findViewById(R.id.main_area).setOnClickListener(this);
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void onClick(View v) {
		switch(v.getId()) {
			
			// 맛집탐색시작 버튼
			case R.id.main_start:
				Intent intentStart = new Intent(MainActivity.this , WizTurnBeaconList.class);
				startActivity(intentStart);
				break;
			
			// 탐색가능지역 버튼	
			case R.id.main_area:
				setContentView(R.layout.area);
				mMode = AREA;

				findViewById(R.id.btn_back).setOnClickListener(this);

				progress = (ProgressBar) this.findViewById(R.id.progress);
				mWebView = (WebView) findViewById(R.id.webview);

				mWebView.getSettings().setJavaScriptEnabled(true);

				mWebView.loadUrl("http://tastyroad.cafe24.com/googlemap/unitedItemMapView");

				mWebView.setWebViewClient(new WebViewClientClass());
				mWebView.setWebChromeClient(new WebChromeClient() {

					@Override
					public void onProgressChanged(WebView view, int newProgress) {
						progress.setProgress(newProgress);
						if (newProgress == 100) {
							progress.setVisibility(View.GONE);
						} else {
							progress.setVisibility(View.VISIBLE);
						}
						super.onProgressChanged(view, newProgress);
					}
				});
				break;

			// 웹뷰 메뉴바 Back 버튼
			case R.id.btn_back:
				finish();
				Intent intentMain = new Intent(this, MainActivity.class);
				startActivity(intentMain);
				break; 
		}
		
	}
	
	// 디바이스 Back 버튼
	@Override
	public void onBackPressed() {
		if (mMode == AREA) {
			Intent intentMain = new Intent(this, MainActivity.class);
			startActivity(intentMain);
		} else if (mMode == MAIN) {
			finish();
			moveTaskToBack(true);
			android.os.Process.killProcess(android.os.Process.myPid());
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


