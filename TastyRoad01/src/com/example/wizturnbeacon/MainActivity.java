package com.example.wizturnbeacon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener{
	
	
	/*
	 
	 activity_main.xml占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙占�
	 onCreate() -> onClick()占쏙옙 占쏙옙占쏙옙占싹몌옙 WizTurnBeaconList.java占쏙옙 占싱듸옙.
	  
	 */
	//
	//add the explane
	
	//dkdkfjlfdlkjlfkjldkj
	//onCreate()占쌨소듸옙
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//res - layout -activity_main.xml占쏙옙 view占쏙옙 MainActivity占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙 占쌍곤옙 占쌀뤄옙占쏙옙.
 		setContentView(R.layout.activity_main);
 		/*
 		startActivity(new Intent(this, Start_Activity.class));
 		
 		*/
 		
 		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = 
			  new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
 		}
		
			
		//activity_main.xml占쏙옙 main_start 占쏙옙튼占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占싹듸옙占쏙옙 占쏙옙.
		findViewById(R.id.main_start).setOnClickListener(this);

	}


	//main_start 占쏙옙튼占쏙옙 클占쏙옙占실댐옙 占싱븝옙트 占쌩삼옙 占쏙옙 占쏙옙占쏙옙풔占�占쌨소듸옙
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		//MainActivity占쏙옙 Context占쏙옙占쏙옙占쏙옙 WizTurnBeaconList.java占쏙옙 占싼깍옙.
		Intent intent = new Intent(MainActivity.this , WizTurnBeaconList.class);
		//WizTurnBeaconList.java占쏙옙 Navigation.
		startActivity(intent);
	}
}


