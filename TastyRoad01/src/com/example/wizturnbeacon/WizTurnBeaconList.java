package com.example.wizturnbeacon;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wizturnbeacon.adapter.WizTurnBeaconListAdapter;
import com.wizturn.sdk.WizTurnDelegate;
import com.wizturn.sdk.WizTurnManager;
import com.wizturn.sdk.WizTurnProximityState;
import com.wizturn.sdk.baseclass.IWizTurnController;
import com.wizturn.sdk.connect.WizTurnBeaconConnect;
import com.wizturn.sdk.connect.WizTurnBeaconConnect.BeaconCharacteristics;
import com.wizturn.sdk.entity.WizTurnBeacons;


public class WizTurnBeaconList extends Activity implements OnClickListener , OnItemClickListener{

	public WizTurnBeaconListAdapter mWizTurnBeaconListAdapter;
	public ListView mScanList;

	private ProgressDialog _loadingDialog;

	public WizTurnBeacons mWizTurnBeacon;
	public WizTurnManager _wizturnMgr;
	public WizTurnBeaconConnect _connect;

	public final int REQUEST_ENABLE_BT = 0000;
	public int mMode;
	public final int SCANLIST = 0;
	public final int BEACON_DETAIL = 1;

	private ImageButton mBtn_refresh;
	private ImageButton mBtn_back;
	private Button mBtn_Connect;
	private ImageButton sharelogo5;
	private ImageButton menu_icon4;

	private TextView mSSID;
	private TextView mMacAddr;
	private TextView mUUID;
	private TextView mMajor;
	private TextView mMinor;

	private TextView mMPower;
	private TextView mRssi;
	private TextView mDistance;
	private TextView mProxivity;

	private TextView mConnected;
	private TextView mTXPower;
	private TextView mInterval;
	private TextView mBattery;
	private TextView mHardware;
	private TextView mFirmware;

	private TextView menu_text;

	private RelativeLayout  box2 ,box3;
	private LinearLayout box4;
	//占쎈뜆�좑옙袁⑹뜍占쏙옙detail,2,3占쏙옙�⑥쥙�숃쳸占퐇inearlayout占쏙옙占쎄쑴�좑옙占폹ox4占쏙옙占쎄쑬諭�	private LinearLayout box4;
	private Context mContext;
	private ScrollView mScrollView;
	
	/*
	 
	 layout_ibeaconlist.xml�좎룞���좎룞�쇿뜝�숈삕�좎룞�쇿뜝�숈삕�좑옙
	 onStart() -> onCreate()
	 
	 
	 
	 */
	
	//WizTurnBeaconList.java�좎룞���좎룞�쇿뜝�숈삕�붷뜝占쎌쿂�좎룞���좎룞�쇿뜝�쒕릺�먯삕 �좎뙣�뚮벝��	@Override
	protected void onStart() {
		super.onStart();
		//LogCat�좎룞���좎떬琉꾩삕�좎룞��Debugging �좎룞�쇿뜝�숈삕.
		Log.d("WizTurnBeacon" ,"onStart()");
		//wizturnMgr_setup()�좎뙣�뚮벝���좎룞�쇿뜝�숈삕.(to �좎룞�쇿뜝�숈삕�좎룞�쇿뜝占썲뜝�숈삕�좑옙�좎룞�쇿뜝�숈삕)
		wizturnMgr_setup();

	}
	
	//onCreate()
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//layout_ibeaconlist.xml�좎룞���좎룞�쇿뜝�숈삕.
		setContentView(R.layout.layout_ibeaconlist);
		Log.d("WizTurnBeacon" ,"onCreate()");
		
		//beacon list �좎떗源띿삕��		beaconList_Init();
	}

	
	//layout_ibeaconlist.xml�좎룞�쇿뜝�숈삕 �좎뙓濡쒓낀�쇿뜝�숈삕 �좎룞�숉듉�좎룞���좎룞�쇿뜝�숈삕�좎룞���좎룞��Device�좎룞���좎뙓濡쒓낀�쇿뜝�숈삕 �좎룞�숉듉) �좎룞�쇿뜝�숈삕�좎떦�먯삕 �좎뙣�뚮벝��
	public void onBackPressed() {
		Log.d("WizTurnBeacon" ,"onBackPressed()");
		back();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("WizTurnBeacon" ,"onDestroy()");
		if (_wizturnMgr.isStarted()) {
			// WizTurnMgr Destroy
			_wizturnMgr.destroy();
		}
	}
	
	//onCreate()�좎뙣�쎌삕�좎뜴�먨뜝�숈삕 beaconList_Init()�좎뙣�쎌삕�좎룞���멨뜝�숈삕�좎룞���좎룞�쇿뜝�숈삕, �좎떗源띿삕�붷뜝�숈삕 �좎룞�쇿뜝�숈삕.
	//beaconList_Init()�좎뙣�쎌삕�좎뜴�먨뜝�숈삕 startActivityForResult()�좎뙣�쎌삕�좎뜴瑜��멨뜝�숈삕�좎떦紐뚯삕 onActivityResult()�좎룞���좎룞�쇿뜝�숈삕�좑옙
	//Activity.class �좎룞�숅걫�좎룞�쇿뜝占�onActivityResult)
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("WizTurnBeacon" ,"onActivityResult()");
		//BLE available
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				//Wizturn Scan Start
				_wizturnMgr.setInitController();
				_wizturnMgr.setWizTurnDelegate(_wtDelegate);
				_wizturnMgr.startController();
			} else {
				//BLE Not available 
				Toast.makeText(this, "Bluetooth Low Energy not enabled", Toast.LENGTH_LONG).show();
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	
	
	
	@Override
	public void onItemClick(AdapterView<?> adpaterView, View view, int position, long l_position) {
		//beaconList Click
		switch(mWizTurnBeaconListAdapter.getItem(position).getMajor()) {
		case 22:
			Log.d("WizTurnBeacon" ,"onItemClick()");
			setContentView(R.layout.layout_ibeacondetail);
			mMode = BEACON_DETAIL;
			mWizTurnBeacon = mWizTurnBeaconListAdapter.getItem(position);
			menuBar_Init("Tasty Road");
			beaconDetail_Init(mWizTurnBeacon);
			break;
		
		case 33:
			Log.d("WizTurnBeacon" ,"onItemClick()");
			setContentView(R.layout.layout_ibeacondetail2);
			mMode = BEACON_DETAIL;
			mWizTurnBeacon = mWizTurnBeaconListAdapter.getItem(position);
			menuBar_Init("Tasty Road");
			beaconDetail_Init(mWizTurnBeacon);
			break;
		
		case 11:
			Log.d("WizTurnBeacon" ,"onItemClick()");
			setContentView(R.layout.layout_ibeacondetail3);
			mMode = BEACON_DETAIL;
			mWizTurnBeacon = mWizTurnBeaconListAdapter.getItem(position);
			menuBar_Init("Tasty Road");
			beaconDetail_Init(mWizTurnBeacon);
			break;
		}
		return;
	}

	@Override
	public void onClick(View v) {
		//Button Click
		switch(v.getId()){
		case R.id.btn_refresh:
			Log.d("WizTurnBeacon" ,"onClick btn_refresh");
			if(_wizturnMgr.isStarted()){
				mWizTurnBeaconListAdapter.clearItem();
				mScanList.setAdapter(mWizTurnBeaconListAdapter);
			}
			break;

		case R.id.btn_back:
			Log.d("WizTurnBeacon" ,"onClick btn_back");
			back();
			break;

		case R.id.btn_Connect:
			Log.d("WizTurnBeacon" ,"onClick btn_Connect");
			if (mWizTurnBeacon != null) {
				_connect = new WizTurnBeaconConnect(this, mWizTurnBeacon, createConnectionCallback());
				Log.d("WizTurnBeacon" ,"Gatt BLE isConnected: " + _connect.isConnected());
				//not Connected
				if (_connect.isConnected() == false){
					_wizturnMgr.stopController();
					showDialog("Connecting...");
					_connect.connectBeacon();
					//Already Connected
				}else if (_connect.isConnected() == true){
					Toast.makeText(mContext, "Status:Already Connected", Toast.LENGTH_SHORT).show();
				}
			}
			break;
			
		case R.id.sharelogo5:
			Log.d("WizTurnBeacon" ,"onClick sharelogo5");
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("image/jpeg");
			startActivity(intent.createChooser(intent, "공유하기"));
			break;
			
		case R.id.menu_icon4:
			Log.d("WizTurnBeacon" ,"onClick menu_icon4");
			mScrollView = (ScrollView)findViewById(R.id.scrollView1);
			mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
			break;	
		}
	}

	// wizTurn Delegate(Scan�좎룞���좎룞�쇿뜝�숈삕�좎룞��Beacon�좎룞���좎룞�쇿뜝�숈삕 �좎룞�쇿뜝�숈삕�좎룞�쇿뜝�숈삕 �좎룞�쇿뜝�숈삕)
	private WizTurnDelegate _wtDelegate = new WizTurnDelegate() {
		//GetRssi Event
		@Override
		public void onGetRSSI(IWizTurnController iWizTurnController, List<String> Data, List<Integer> RSSI) {
			Log.d("WizTurnBeacon" ,"GATT BLE onGetRSSI wtDelegate");

		}
		//Get beacon device Event
		@Override
		public void onGetDeviceList(IWizTurnController iWizTurnController, final List<WizTurnBeacons> device) {
			runOnUiThread(new Runnable() {
				/*	
				 *  getMacAddr = Mac Address of the beacon
				 * 	getMajor = Major version of the beacon
				 * 	getMinor  = Minor version of the beacon
				 * 	getName = SSID of the beacon
				 * 	getProximity = Defines distance relationship between the device and the beacon
				 *  getProximityUUID = Proximity UUID of the beacon
				 *  getRssi = Rssi at the moment of scanning
				 *  getMeasuredPower = Measured Power of the beacon
				 *  getDistance = Distance between the device and the beacon
				 *    
				 */
				//Thread �좎룞�쇿뜝�숈삕 �좎떕�몄삕�좎룞�숂돌�좑옙UI�좎룞���좎뙐�먯삕 �좎룞���좎뙇�먯삕 runOnUiThread�좎룞���좎뙗�몄삕�좎룞�숉궡.
				public void run() {
					Log.d("WizTurnBeacon" ,"GATT BLE onGetDeviceList wtDelegate");
					for (int i = 0; i < device.size(); i++) {
						//[mMode == SCANLIST == 0] ==>�좎떙�쇱삕�좎룞��beacon device�좎룞���좎룞�쇿뜝�숈삕�좎룞���좎떎諭꾩삕.
						//[!mWizTurnBeaconListAdapter.contains(device.get(i).getMacAddress())] 
						//==>�좎떙�쇱삕�좎룞��beacon device�좎룞���좎룞�쇿뜝�숈삕�좎떎琉꾩삕, mWizTurnBeaconListAdapter.contains()�좎뙣�뚮벝�숃��좎룞�쇿뜝占퐀alse�좎룞���좎룞�쇿뜝�밸벝��
						if(mMode == SCANLIST && !mWizTurnBeaconListAdapter.contains(device.get(i).getMacAddress())){
							Log.d("WizTurnBeacon" ,"device " + device.get(i)._macAddr +" ADD");
							mWizTurnBeaconListAdapter.addItem(device.get(i));
							mScanList.setAdapter(mWizTurnBeaconListAdapter);
						}
						
						//[mMode == BEACON_DETAIL ==1] ==>�좎떙�쇱삕�좎룞��beacon device�좎룞��1�좎룞���좎룞�쇿뜝�숈삕�좎룞���좎떎諭꾩삕.
						//[mWizTurnBeacon._macAddr.equals(device.get(i).getMacAddress())] 
						//==> �좎떙�쇱삕�좎룞��device�좎룞��beacon device�좎룞�쇿뜝�숈삕 MacAddress()�좎룞��2�좎룞���뺝뜝�숈삕.
						else if(mMode == BEACON_DETAIL && mWizTurnBeacon._macAddr.equals(device.get(i).getMacAddress())){
							Log.d("WizTurnBeacon" ,"device " + device.get(i)._macAddr +" Update");
							
							//beacon device�좎룞���좎룞�쇿뜝�숈삕 �좎뜦蹂��좎룞�쇿뜝�숈삕�좎룞��layout_
							mMPower.setText(Integer.toString(device.get(i).getMeasuredPower()) + " dB");
							mRssi.setText(Float.toString(device.get(i).getRssi()) + " dB");
							mDistance.setText(Double.toString(device.get(i).getDistance()) + " m");
							mProxivity.setText(device.get(i).getProximity().toString());
						}
					}
				}
			});
		}
		//Proximity Event
		@Override
		public void onGetProximity(IWizTurnController iWizTurnController, WizTurnProximityState proximity) {
			Log.d("WizTurnBeacon" ,"GATT BLE onGetProximity wtDelegate");

		}
	};




	//Connection Callback
	private WizTurnBeaconConnect.ConnectionCallback createConnectionCallback() {
		return new WizTurnBeaconConnect.ConnectionCallback() {
			//connected
			@Override
			public void onConnected(final BeaconCharacteristics beaconChars) {

				/*	 
				 *  getAdvertisingIntervalMillis = Advertising interval in milliseconds
				 * 	getBatteryPercent = Battery level in percent
				 * 	getBroadcastingPower  = Broadcasting power
				 * 	getHardwareVersion = Revision of hardware
				 *  getSoftwareVersion = Version of operating system
				 *  
				 */

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.d("WizTurnBeacon" ,"GATT BLE onConnected ConnectionCallback");
						mTransactionListener.onConnectedComplete(beaconChars);
					}
				});
			}

			//connection error
			@Override
			public void onConnectionError() {
				dismiss();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.d("WizTurnBeacon" ,"GATT BLE onConnectionError ConnectionCallback");
						Toast.makeText(mContext, "Status:ConnectionError", Toast.LENGTH_SHORT).show();
					}
				});
			}
			//disconnected
			@Override
			public void onDisconnected() {
				dismiss();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.d("WizTurnBeacon" ,"GATT BLE onDisconnected ConnectionCallback");
						_wizturnMgr.startController();
						Toast.makeText(mContext, "Status: Disconnected from beacon", Toast.LENGTH_SHORT).show();
					}
				});
			}
		};
	}

	private TransactionListener mTransactionListener = new TransactionListener() {

		//connection complete Listener
		@Override
		public void onConnectedComplete(BeaconCharacteristics beaconChars) {
			Log.d("WizTurnBeacon" ,"onConnectedComplete()");
			dismiss();
			connection_Init();
			mConnected.setText("" +"YES");
			mTXPower.setText("" + beaconChars.getBroadcastingPower() + " dB");
			mInterval.setText("" +beaconChars.getAdvertisingIntervalMillis() + " Hz");
			mBattery.setText("" +beaconChars.getBatteryPercent() + " %");
			mHardware.setText("" +beaconChars.getHardwareVersion().toString() );
			mFirmware.setText("" +beaconChars.getSoftwareVersion().toString());
			Toast.makeText(mContext, "Connected", Toast.LENGTH_LONG).show();
		}

	};

	public interface TransactionListener{
		public void onConnectedComplete(BeaconCharacteristics beaconChars);
	}


	public void beaconList_Init(){
		Log.d("WizTurnBeacon" ,"beaconList_Init");
		setContentView(R.layout.layout_ibeaconlist);
		
		//�좎룞�쇿뜝�숈삕 parameter : �좎룞�쇿뜝�붿룞�숉듃�좎룞�쇿뜝�숈삕(WizTurnBeaconList�닷뜝�숈삕�좎룞�쇿뜝�숈삕 �좎룞�쇿뜝�숈삕 �좎뙣�뚮벝�쇿뜝�숈삕 �좎룞�쇿뜝�붿룞�숉듃 �좎룞�쇿뜝�숈삕), layout_scanlist.xml�좎룞��TextList�좎룞�쇿뜝�숈삕, textList�좎룞�쇿뜝�숈삕�좎룞���좎룞�쇿뜝�숈삕�좎룞��arrayList
		//array_scanlist.xml�좎룞���좎룞�쇿뜝�숈삕�좎룞��WizTurnBeaconListAdapter�좎룞���좎룞�쇿뜝�숈삕 parameter�좎룞���좎룞�쇿뜝占�		//array_scanlist.xml�좎룞��Background�좎룞���좎룞�쇿뜝�숈삕 �좎뙇�먯삕 layout�좎룞�쇿뜝�숈삕.
		mWizTurnBeaconListAdapter = new WizTurnBeaconListAdapter(this,R.layout.array_scanlist ,new ArrayList<WizTurnBeacons>());
		//layout_ibeaconlist.xml�좎룞��scanlist �붷뜝�숈삕�좎룞���좎룞�쇿뜝�숈삕.
		mScanList = (ListView) findViewById(R.id.scanList);
		//scanlist�붷뜝�쎌뿉 �좎룞�쇿뜝�숈삕 �닷뜝�숈삕 �좎떛釉앹삕�멨뜝�숈삕 �좎룞�쇿뜝�숈삕.
		mScanList.setOnItemClickListener(this);
		//layout_ibeaconlist.xml�좎룞��btn_refresh �좎룞�숉듉�좎룞���좎룞�쇿뜝�숈삕.
		mBtn_refresh = (ImageButton)findViewById(R.id.btn_refresh);
		//btn_refresh�좎룞�숉듉�좎룞���좎룞�쇿뜝�숈삕 �닷뜝�숈삕 �좎떛釉앹삕�멨뜝�숈삕 �좎룞�쇿뜝�숈삕.
		mBtn_refresh.setOnClickListener(this);
		//layout_ibeaconlist.xml�좎룞���좎룞�쇿뜝�숈삕 �좎룞�쇿뜝�숈삕 �좎뙆�먯삕 menu_bar�좎룞��'Test' -> 'ScanList'�좎룞���좎떗源띿삕��		menuBar_Init("ScanList");
		//mMode�좎룞�쇿뜝�숈삕 0�좎룞�쇿뜝�숈삕 �좎떗源띿삕��
		mMode = SCANLIST;
		mContext = this;
	}

	//menu_bar�좎떗源띿삕��
	public void menuBar_Init(String menuTitle){
		Log.d("WizTurnBeacon" ,"menuBar_Init");
		//layout_ibeaconlist.xml�좎룞��menu_text�좎룞��mBtn_back�좎룞���좎룞�쇿뜝�숈삕.
		menu_text = (TextView)findViewById(R.id.titleTxt);
		mBtn_back = (ImageButton)findViewById(R.id.btn_back);

		mBtn_back.setOnClickListener(this);
		//menu_text�좎룞���좎룞�숉��좎룞�쇿뜝�숈삕 �좎룞�쇿뜝�섎챿���좎룞�쇿뜝占퐌arameter�좎룞��menuTitle)�좎룞���좎룞�쇿뜝�숈삕.
		menu_text.setText(menuTitle);
	}


	public void beaconDetail_Init(WizTurnBeacons item){
		Log.d("WizTurnBeacon" ,"beaconDetail_Init");
		mSSID = (TextView)findViewById(R.id.ibeacondetail_SSID);
		mMacAddr = (TextView)findViewById(R.id.ibeacondetail_MacAddr);
		mUUID = (TextView)findViewById(R.id.ibeacondetail_UUID);
		mMajor = (TextView)findViewById(R.id.ibeacondetail_Major);
		mMinor = (TextView)findViewById(R.id.ibeacondetail_Minor);

		mMPower = ((TextView)findViewById(R.id.ibeacondetail_MPower));
		mRssi = ((TextView)findViewById(R.id.ibeacondetail_Rssi));
		mDistance = ((TextView)findViewById(R.id.ibeacondetail_Distance));
		mProxivity = ((TextView)findViewById(R.id.ibeacondetail_Proximity));
		mConnected = ((TextView)findViewById(R.id.ibeacondetail_connected));
		mTXPower = ((TextView)findViewById(R.id.ibeacondetail_TX));
		mInterval = ((TextView)findViewById(R.id.ibeacondetail_Interval));
		mBattery = ((TextView)findViewById(R.id.ibeacondetail_Battery));
		mHardware = ((TextView)findViewById(R.id.ibeacondetail_Hardware));
		mFirmware = ((TextView)findViewById(R.id.ibeacondetail_Firmware));

		mSSID.setText(item._name);
		mMacAddr.setText(item._macAddr);
		mUUID.setText(item._proximityUUID);
		mMajor.setText(Integer.toString(item._major));
		mMinor.setText(Integer.toString(item._minor));

		mMPower.setText(Integer.toString(item._measuredPower) + "dB");
		mRssi.setText(Float.toString(item._rssi) + "dB");
		mDistance.setText(Double.toString(item.getDistance())+ "m");
		mProxivity.setText(item._proximity.toString());

		box2 = (RelativeLayout)findViewById(R.id.box2);
		box3 = (RelativeLayout)findViewById(R.id.box3);
		//占쎈뜆�좑옙袁⑹뜍占쏙옙detail,2,3占쏙옙�⑥쥙�숃쳸占퐇inearlayout占쏙옙占쎄쑴�좑옙占폹ox4占쎈뜆肉�占쎄쑴��占쎈베�ョ몴占쏙쭗�ㅻあ box4占쎈뜆肉�占쏙퐢�쀯옙占�		box4 = (LinearLayout)findViewById(R.id.box4);
		box3.setVisibility(View.GONE);

		mBtn_Connect = (Button) findViewById(R.id.btn_Connect);
		mBtn_Connect.setOnClickListener(this);
		
		sharelogo5 = (ImageButton) findViewById(R.id.sharelogo5);
		sharelogo5.setOnClickListener(this);
		
		menu_icon4 = (ImageButton) findViewById(R.id.menu_icon4);
		menu_icon4.setOnClickListener(this);
	}
	
	

	public void connection_Init(){
		Log.d("WizTurnBeacon" ,"connected_Init");
		box2.setVisibility(View.GONE);
		box3.setVisibility(View.VISIBLE);
		//占쎈뜆�좑옙袁⑹뜍占쏙옙detail,2,3占쏙옙�⑥쥙�숃쳸占퐇inearlayout占쏙옙占쎄쑴�좑옙占폹ox4�쒙옙癰귣똻�좑옙袁⑥쨯 占썬끉��visible)
		box4.setVisibility(View.VISIBLE);
		mBtn_Connect.setVisibility(View.GONE);
	}


	public void back(){
		Log.d("WizTurnBeacon" ,"back()");
		if(mMode == SCANLIST){
			if (_wizturnMgr.isStarted()) {
				// WizTurn Scan Stop
				_wizturnMgr.stopController();
			}
			finish();
		}else if(mMode == BEACON_DETAIL){
			if(_connect != null && _connect.isConnected() == true){
				//disconnect
				_connect.close();
			}
			beaconList_Init();
			_wizturnMgr.startController();			

		}else{
			_wizturnMgr.destroy();
		}
	}
	
	//泥섇뜝�숈삕 �좎룞�쇿뜝�숈삕 �좎룞�쇿뜝�숈삕 �좎룞�� �좎룞�쇿뜝�숈삕�좎룞�쇿뜝�숈삕�좑옙�좎룞�쇿뜝�숈삕�좎뙇�먯삕�좎룞���뺝뜝�숈삕�좎떦源띿삕 �좎룞�쇿뜝�숈삕 �좎뙣�뚮벝��
	public void wizturnMgr_setup(){
		Log.d("WizTurnBeacon" ,"wizturnMgr_setup()");
		_wizturnMgr = WizTurnManager.sharedInstance(this);

		// Check if device supports BLE.
		if (!_wizturnMgr.hasBluetooth()) {
			Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
			return;
		}
		// If BLE is not enabled, let user enable it.
		if (!_wizturnMgr.isBluetoothEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			//onActivityResult()�좎뙣�뚮벝���멨뜝�숈삕.
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			//Wizturn Scan Start
			_wizturnMgr.setInitController();
			_wizturnMgr.setWizTurnDelegate(_wtDelegate);
			_wizturnMgr.startController();
		}

	}

	private void showDialog(String Title){
		Log.d("WizTurnBeacon" ,"showDialog()");
		_loadingDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
		_loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		_loadingDialog.setTitle(R.string.app_name);
		_loadingDialog.setMessage(Title);
		_loadingDialog.setCancelable(false);
		_loadingDialog.setIndeterminate(true);
		_loadingDialog.show();
	}

	private void dismiss(){
		Log.d("WizTurnBeacon" ,"dismiss()");
		_loadingDialog.dismiss();
	}
}

