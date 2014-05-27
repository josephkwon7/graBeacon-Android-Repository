package com.example.wizturnbeacon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener{
	
	
	/*
	 
	 activity_main.xml과 연결돼있음.
	 onCreate() -> onClick()이 반응하면 WizTurnBeaconList.java로 이동.
	  
	 */
	
	
	
	
	//onCreate()메소드
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//res - layout -activity_main.xml의 view를 MainActivity에서 실행할 수 있게 불러옴.
 		setContentView(R.layout.activity_main);
 		
 		startActivity(new Intent(this, Start_Activity.class));
 		
 		
 		
 		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = 
			  new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
 		}
		
			
		//activity_main.xml의 main_start 버튼이 눌려지면 반응하도록 함.
		findViewById(R.id.main_start).setOnClickListener(this);

	}


	//main_start 버튼이 클릭되는 이벤트 발생 시 실행되는 메소드
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		//MainActivity의 Context정보를 WizTurnBeaconList.java로 넘김.
		Intent intent = new Intent(MainActivity.this , WizTurnBeaconList.class);
		//WizTurnBeaconList.java로 Navigation.
		startActivity(intent);
	}
}


