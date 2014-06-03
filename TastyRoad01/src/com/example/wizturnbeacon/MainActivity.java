package com.example.wizturnbeacon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener{
	
	
	/*
	 
	 activity_main.xml�좎룞���좎룞�쇿뜝�숈삕�좎룞�쇿뜝�숈삕�좑옙
	 onCreate() -> onClick()�좎룞���좎룞�쇿뜝�숈삕�좎떦紐뚯삕 WizTurnBeaconList.java�좎룞���좎떛�몄삕.
	  
	 */
	//
	//add the explane
	
	//dkdkfjlfdlkjlfkjldkj
	//onCreate()�좎뙣�뚮벝��	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//res - layout -activity_main.xml�좎룞��view�좎룞��MainActivity�좎룞�쇿뜝�숈삕 �좎룞�쇿뜝�숈삕�좎룞���좎룞���좎뙇怨ㅼ삕 �좎�琉꾩삕�좎룞��
 		setContentView(R.layout.activity_main);
 		/*
 		startActivity(new Intent(this, Start_Activity.class));
 		
 		*/
 		
 		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = 
			  new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
 		}
		
			
		//activity_main.xml�좎룞��main_start �좎룞�숉듉�좎룞���좎룞�쇿뜝�숈삕�좎룞�쇿뜝�숈삕 �좎룞�쇿뜝�숈삕�좎떦�몄삕�좎룞���좎룞��
		findViewById(R.id.main_start).setOnClickListener(this);

	}


	//main_start �좎룞�숉듉�좎룞���닷뜝�숈삕�좎떎�먯삕 �좎떛釉앹삕���좎뙥�쇱삕 �좎룞���좎룞�쇿뜝�숈삕�붷뜝占썲뜝�⑥냼�몄삕
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		//MainActivity�좎룞��Context�좎룞�쇿뜝�숈삕�좎룞��WizTurnBeaconList.java�좎룞���좎떬源띿삕.
		Intent intent = new Intent(MainActivity.this , WizTurnBeaconList.class);
		//WizTurnBeaconList.java�좎룞��Navigation.
		startActivity(intent);
	}
}


