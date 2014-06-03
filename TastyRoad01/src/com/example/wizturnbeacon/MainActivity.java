package com.example.wizturnbeacon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener{
	
	
	/*
	 
	 activity_main.xml占쎌쥙猷욑옙占쏙옙醫롫짗占쎌눨�앾옙�덉굲占쎌쥙猷욑옙�용쐻占쎌늿�뺧옙醫묒삕
	 onCreate() -> onClick()占쎌쥙猷욑옙占쏙옙醫롫짗占쎌눨�앾옙�덉굲占쎌쥙��쭗��굲 WizTurnBeaconList.java占쎌쥙猷욑옙占쏙옙醫롫뼓占쎈챷��
	  
	 */
	/*
	add the explane
	
	dkdkfjlfdlkjlfkjldkj
	onCreate()占쎌쥙�ｏ옙��쿈占쏙옙	
	
	*/
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/*res - layout -activity_main.xml占쎌쥙猷욑옙占퐒iew占쎌쥙猷욑옙占폦ainActivity占쎌쥙猷욑옙�용쐻占쎌늿��占쎌쥙猷욑옙�용쐻占쎌늿�뺧옙醫롫짗占쏙옙占쎌쥙猷욑옙占쏙옙醫롫솂�ⓦ끉��占쎌쥙占쏙쭔袁⑹굲占쎌쥙猷욑옙占� 		
		*/
		setContentView(R.layout.activity_main);
 		/*
 		startActivity(new Intent(this, Start_Activity.class));
 		
 		*/
 		
 		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = 
			  new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
 		}
		
			
		/*
		 activity_main.xml占쎌쥙猷욑옙占퐉ain_start 占쎌쥙猷욑옙�됰뱣占쎌쥙猷욑옙占쏙옙醫롫짗占쎌눨�앾옙�덉굲占쎌쥙猷욑옙�용쐻占쎌늿��占쎌쥙猷욑옙�용쐻占쎌늿�뺧옙醫롫뼣占쎈챷�뺧옙醫롫짗占쏙옙占쎌쥙猷욑옙占�		
		 */
 		findViewById(R.id.main_start).setOnClickListener(this);

	}


	//main_start 占쎌쥙猷욑옙�됰뱣占쎌쥙猷욑옙占쏙옙�룸쐻占쎌늿�뺧옙醫롫뼄占쎈Ŋ��占쎌쥙�쏃뇡�뱀굲占쏙옙占쎌쥙�ο옙�깆굲 占쎌쥙猷욑옙占쏙옙醫롫짗占쎌눨�앾옙�덉굲占쎈떱�앭뜝�뀀쐻占썩뫁�쇽옙紐꾩굲
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		//MainActivity占쎌쥙猷욑옙占폚ontext占쎌쥙猷욑옙�용쐻占쎌늿�뺧옙醫롫짗占쏙옙WizTurnBeaconList.java占쎌쥙猷욑옙占쏙옙醫롫뼩繹먮씮��
		Intent intent = new Intent(MainActivity.this , WizTurnBeaconList.class);
		//WizTurnBeaconList.java占쎌쥙猷욑옙占폧avigation.
		startActivity(intent);
	}
}


