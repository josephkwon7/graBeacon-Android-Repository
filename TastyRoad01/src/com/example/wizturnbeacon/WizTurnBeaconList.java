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
import android.widget.ListView;
import android.widget.RelativeLayout;
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

	private Context mContext;

	
	/*
	 
	 layout_ibeaconlist.xml과 연결돼있음.
	 onStart() -> onCreate()
	 
	 
	 
	 */
	
	//WizTurnBeaconList.java가 실행되면 처음 시작되는 메소드
	@Override
	protected void onStart() {
		super.onStart();
		//LogCat에 뿌려줄 Debugging 정보.
		Log.d("WizTurnBeacon" ,"onStart()");
		//wizturnMgr_setup()메소드 실행.(to 블루투스 기능 설정)
		wizturnMgr_setup();

	}
	
	//onCreate()
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//layout_ibeaconlist.xml과 연결.
		setContentView(R.layout.layout_ibeaconlist);
		Log.d("WizTurnBeacon" ,"onCreate()");
		
		//beacon list 초기화
		beaconList_Init();
	}

	
	//layout_ibeaconlist.xml에서 뒤로가기 버튼을 눌렀을 때(Device의 뒤로가기 버튼) 반응하는 메소드.
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
	
	//onCreate()메서드에서 beaconList_Init()메서드 호출을 통해, 초기화가 진행.
	//beaconList_Init()메서드에서 startActivityForResult()메서드를 호출하면 onActivityResult()가 실행됨.
	//Activity.class 상속받음.(onActivityResult)
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
		Log.d("WizTurnBeacon" ,"onItemClick()");
		setContentView(R.layout.layout_ibeacondetail);
		mMode = BEACON_DETAIL;
		mWizTurnBeacon = mWizTurnBeaconListAdapter.getItem(position);
		menuBar_Init("Tasty Road");
		beaconDetail_Init(mWizTurnBeacon);

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
		}
	}

	// wizTurn Delegate(Scan시 나오는 Beacon에 대한 종합적인 정보)
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
				//Thread 내에 안드로이드 UI를 바꿀 수 있는 runOnUiThread를 작동시킴.
				public void run() {
					Log.d("WizTurnBeacon" ,"GATT BLE onGetDeviceList wtDelegate");
					for (int i = 0; i < device.size(); i++) {
						//[mMode == SCANLIST == 0] ==>검색된 beacon device가 없음을 의미.
						//[!mWizTurnBeaconListAdapter.contains(device.get(i).getMacAddress())] 
						//==>검색된 beacon device가 없으므로, mWizTurnBeaconListAdapter.contains()메소드로부터 false가 리턴됨.
						if(mMode == SCANLIST && !mWizTurnBeaconListAdapter.contains(device.get(i).getMacAddress())){
							Log.d("WizTurnBeacon" ,"device " + device.get(i)._macAddr +" ADD");
							mWizTurnBeaconListAdapter.addItem(device.get(i));
							mScanList.setAdapter(mWizTurnBeaconListAdapter);
						}
						
						//[mMode == BEACON_DETAIL ==1] ==>검색된 beacon device가 1개 있음을 의미.
						//[mWizTurnBeacon._macAddr.equals(device.get(i).getMacAddress())] 
						//==> 검색된 device가 beacon device인지 MacAddress()로 2차 확인.
						else if(mMode == BEACON_DETAIL && mWizTurnBeacon._macAddr.equals(device.get(i).getMacAddress())){
							Log.d("WizTurnBeacon" ,"device " + device.get(i)._macAddr +" Update");
							
							//beacon device에 대한 기본 정보를 layout_
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
		
		//생성자 parameter : 컨텍스트정보(WizTurnBeaconList클래스의 현재 메소드의 컨텍스트 정보), layout_scanlist.xml의 TextList연결, textList정보를 저장한 arrayList
		//array_scanlist.xml의 정보를 WizTurnBeaconListAdapter의 생성자 parameter로 전달.
		//array_scanlist.xml은 Background로 돌고 있는 layout같음.
		mWizTurnBeaconListAdapter = new WizTurnBeaconListAdapter(this,R.layout.array_scanlist ,new ArrayList<WizTurnBeacons>());
		//layout_ibeaconlist.xml의 scanlist 화면을 연결.
		mScanList = (ListView) findViewById(R.id.scanList);
		//scanlist화면에 대한 클릭 이벤트를 감지.
		mScanList.setOnItemClickListener(this);
		//layout_ibeaconlist.xml의 btn_refresh 버튼을 연결.
		mBtn_refresh = (ImageButton)findViewById(R.id.btn_refresh);
		//btn_refresh버튼에 대한 클릭 이벤트를 감지.
		mBtn_refresh.setOnClickListener(this);
		//layout_ibeaconlist.xml의 가장 위에 잇는 menu_bar를 'Test' -> 'ScanList'로 초기화
		menuBar_Init("ScanList");
		//mMode값을 0으로 초기화.
		mMode = SCANLIST;
		mContext = this;
	}

	//menu_bar초기화.
	public void menuBar_Init(String menuTitle){
		Log.d("WizTurnBeacon" ,"menuBar_Init");
		//layout_ibeaconlist.xml의 menu_text와 mBtn_back을 연결.
		menu_text = (TextView)findViewById(R.id.titleTxt);
		mBtn_back = (ImageButton)findViewById(R.id.btn_back);

		mBtn_back.setOnClickListener(this);
		//menu_text에 나타나는 문자를 모두 parameter값(menuTitle)로 변경.
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
		box3.setVisibility(View.GONE);

		mBtn_Connect = (Button) findViewById(R.id.btn_Connect);
		mBtn_Connect.setOnClickListener(this);
	}
	
	

	public void connection_Init(){
		Log.d("WizTurnBeacon" ,"connected_Init");
		box2.setVisibility(View.GONE);
		box3.setVisibility(View.VISIBLE);
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
	
	//처음 구동 됐을 때, 블루투스가 켜져있는지 확인하기 위한 메소드.
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
			//onActivityResult()메소드 호출.
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

