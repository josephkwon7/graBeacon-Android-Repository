package com.dwf.tastyroad;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.wizturnbeacon.R;
import com.wizturn.sdk.WizTurnManager;

public class MainActivity extends Activity implements OnClickListener{
	
	
	public WizTurnManager _wizturnMgr;
	public final int REQUEST_ENABLE_BT = 0000;
	
	/*
	 
	 activity_main.xml�좎럩伊숂뙴�묒삕�좎룞�숅넫濡レ쭢�좎럩�⑨옙�얠삕占쎈뜆援꿨뜝�뚯쪠�룹쉻�숋옙�⑹맶�좎럩�울옙類㏃삕�ル쵐��	 onCreate() -> onClick()�좎럩伊숂뙴�묒삕�좎룞�숅넫濡レ쭢�좎럩�⑨옙�얠삕占쎈뜆援꿨뜝�뚯쪠占쏙옙彛쀯옙占쎄뎡 WizTurnBeaconList.java�좎럩伊숂뙴�묒삕�좎룞�숅넫濡ル폆�좎럥梨뤄옙占�	  
	 */
	/*
	add the explane
	
	dkdkfjlfdlkjlfkjldkj
	onCreate()�좎럩伊숋옙節륁삕占쏙옙荑덂뜝�숈삕	
	
	*/
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/*res - layout -activity_main.xml�좎럩伊숂뙴�묒삕�좏릳iew�좎럩伊숂뙴�묒삕�좏룱ainActivity�좎럩伊숂뙴�묒삕占쎌슜�삣뜝�뚮듌占쏙옙�좎럩伊숂뙴�묒삕占쎌슜�삣뜝�뚮듌占쎈벨�숅넫濡レ쭢�좎룞�쇿뜝�뚯쪠�룹쉻�쇿뜝�숈삕�ル∥�귨옙��걠占쏙옙�좎럩伊쇿뜝�숈춸熬곣뫗援꿨뜝�뚯쪠�룹쉻�쇿뜝占�		
		*/
 		setContentView(R.layout.activity_main);
 		
		// BLE가 활성화 안되어 있으면 활성화 되게 함
 		_wizturnMgr = WizTurnManager.sharedInstance(this);
 		if (!_wizturnMgr.isBluetoothEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
 		
 		
/*
 		startActivity(new Intent(this, Start_Activity.class));
 		
 		*/
 		
// 		if (android.os.Build.VERSION.SDK_INT > 9) {
//			StrictMode.ThreadPolicy policy = 
//			  new StrictMode.ThreadPolicy.Builder().permitAll().build();
//			StrictMode.setThreadPolicy(policy);
// 		}
		
			
		/*
		 activity_main.xml�좎럩伊숂뙴�묒삕�좏릧ain_start �좎럩伊숂뙴�묒삕占쎈맧諭ｅ뜝�뚯쪠�룹쉻�쇿뜝�숈삕�ル∥吏쀥뜝�뚮닲占쎌빢�숋옙�됯뎡�좎럩伊숂뙴�묒삕占쎌슜�삣뜝�뚮듌占쏙옙�좎럩伊숂뙴�묒삕占쎌슜�삣뜝�뚮듌占쎈벨�숅넫濡ル샬�좎럥梨뤄옙類㏃삕�ル∥吏쀥뜝�숈삕�좎럩伊숂뙴�묒삕�좑옙		
		 */
 		findViewById(R.id.main_start).setOnClickListener(this);

	}


	//main_start �좎럩伊숂뙴�묒삕占쎈맧諭ｅ뜝�뚯쪠�룹쉻�쇿뜝�숈삕占쎈８�삣뜝�뚮듌占쎈벨�숅넫濡ル펲�좎럥흮占쏙옙�좎럩伊숋옙�껊눀占쎈�援꿨뜝�숈삕�좎럩伊숋옙恝�숋옙源녾뎡 �좎럩伊숂뙴�묒삕�좎룞�숅넫濡レ쭢�좎럩�⑨옙�얠삕占쎈뜆援꿨뜝�덈뼮占쎌빆�앾옙��맶�좎뜦維곻옙�쎌삕筌뤾쑴援�	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		/*MainActivity�좎럩伊숂뙴�묒삕�좏룠ontext�좎럩伊숂뙴�묒삕占쎌슜�삣뜝�뚮듌占쎈벨�숅넫濡レ쭢�좎룞�셒izTurnBeaconList.java�좎럩伊숂뙴�묒삕�좎룞�숅넫濡ル섄濚밸Ŧ��옙占�		
		 */
		 Intent intent = new Intent(MainActivity.this , WizTurnBeaconList.class);
		
		//WizTurnBeaconList.java�좎럩伊숂뙴�묒삕�좏룲avigation.
		startActivity(intent);
	}
}


