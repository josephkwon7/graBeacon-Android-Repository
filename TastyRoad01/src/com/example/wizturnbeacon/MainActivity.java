package com.example.wizturnbeacon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener{
	
	
	/*
	 
	 activity_main.xml�� ���������.
	 onCreate() -> onClick()�� �����ϸ� WizTurnBeaconList.java�� �̵�.
	  
	 */
	//
	//add the explane
	
	//dkdkfjlfdlkjlfkjldkj
	//onCreate()�޼ҵ�
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//res - layout -activity_main.xml�� view�� MainActivity���� ������ �� �ְ� �ҷ���.
 		setContentView(R.layout.activity_main);
 		
 		startActivity(new Intent(this, Start_Activity.class));
 		
 		
 		
 		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = 
			  new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
 		}
		
			
		//activity_main.xml�� main_start ��ư�� �������� �����ϵ��� ��.
		findViewById(R.id.main_start).setOnClickListener(this);

	}


	//main_start ��ư�� Ŭ���Ǵ� �̺�Ʈ �߻� �� ����Ǵ� �޼ҵ�
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		//MainActivity�� Context������ WizTurnBeaconList.java�� �ѱ�.
		Intent intent = new Intent(MainActivity.this , WizTurnBeaconList.class);
		//WizTurnBeaconList.java�� Navigation.
		startActivity(intent);
	}
}


