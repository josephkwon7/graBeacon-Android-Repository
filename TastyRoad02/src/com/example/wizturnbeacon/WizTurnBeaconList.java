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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wizturnbeacon.adapter.PopupHelper;
import com.example.wizturnbeacon.adapter.WizTurnBeaconListAdapter;
import com.nhn.android.mapview.NaverMapView;
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
	//상세정보화면 레이아웃에 맵 아이콘 객체 추가 - 6월 2일 2045분 김성은 수정
	private ImageButton mMap_icon;
	private ImageButton mBtn_twitter;
	private ImageButton mBtn_facebook;
	private ImageButton mBtn_kakao;
	private ImageButton mBtn_line;

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
	private Context mContext;
	private ScrollView mScrollView;
	
	/*

	 */
	
	//WizTurnBeaconList.java�좎룞���좎룞�쇿뜝�숈삕�붷뜝占쎌쿂�좎룞���좎룞�쇿뜝�쒕릺�먯삕 �좎뙣�뚮벝��	@Override
	protected void onStart() {
		super.onStart();
		Log.d("WizTurnBeacon" ,"onStart()");
		wizturnMgr_setup();

	}
	
	//onCreate()
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_ibeaconlist);
		Log.d("WizTurnBeacon" ,"onCreate()");
		
		beaconList_Init();
	}

	
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
			PopupWindow window = PopupHelper.newBasicPopupWindow(this);
			
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			View popupView = inflater.inflate(R.layout.popup, null);
			
			window.setContentView(popupView);
			int totalHeight = getWindowManager().getDefaultDisplay().getHeight();
			int[] location = new int[2];
			v.getLocationOnScreen(location);
			
			if (location[1] < (totalHeight / 2.0)) {
				// top half of screen
				window.setAnimationStyle(R.style.Animations_PopDownMenuRight);
				window.showAsDropDown(v);
			} else { // bottom half
				PopupHelper.showLikeQuickAction(window, popupView, v, getWindowManager(), 0, 0);
			}
			
			mBtn_twitter = (ImageButton) popupView.findViewById(R.id.btn_twitter);
			mBtn_twitter.setOnClickListener(this);
			
			mBtn_facebook = (ImageButton) popupView.findViewById(R.id.btn_facebook);
			mBtn_facebook.setOnClickListener(this);
			
			mBtn_kakao = (ImageButton) popupView.findViewById(R.id.btn_kakao);
			mBtn_kakao.setOnClickListener(this);
			
			mBtn_line = (ImageButton) popupView.findViewById(R.id.btn_line);
			mBtn_line.setOnClickListener(this);
			
			break;
			
		case R.id.menu_icon4:
			Log.d("WizTurnBeacon" ,"onClick menu_icon4");
			mScrollView = (ScrollView)findViewById(R.id.scrollView1);
			mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
			break;	
		
		case R.id.map_icon:
			Log.d("WizTurnBeacon", "onClick map_Btn");
			Intent intent1 = new Intent(this, NaverMapView.class);
			startActivity(intent1);
			break;
			
		case R.id.btn_twitter:
			Intent shareIntent1 = new Intent();
			shareIntent1.setAction(Intent.ACTION_SEND);
			shareIntent1.setType("image/jpeg");
			shareIntent1.setPackage("com.twitter.android");
			startActivity(shareIntent1);
			break;
		
		case R.id.btn_facebook:
			Log.e("facebook", "button event start");
			Intent shareIntent2 = new Intent();
			shareIntent2.setAction(Intent.ACTION_SEND);
			shareIntent2.setType("image/jpeg");
			shareIntent2.setPackage("com.facebook.katana");
			startActivity(shareIntent2);
			break;
			
		case R.id.btn_kakao:
			Intent shareIntent3 = new Intent();
			shareIntent3.setAction(Intent.ACTION_SEND);
			shareIntent3.setType("text/plain");
			shareIntent3.setPackage("com.kakao.talk");
			startActivity(shareIntent3);
			break;	
			
		case R.id.btn_line:
			Intent shareIntent4 = new Intent();
			shareIntent4.setAction(Intent.ACTION_SEND);
			shareIntent4.setType("image/jpeg");
			shareIntent4.putExtra(shareIntent4.EXTRA_TEXT, "");
			shareIntent4.setPackage("jp.naver.line.android");
			startActivity(shareIntent4);
			break;
			
		}
	}

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
				public void run() {
					Log.d("WizTurnBeacon" ,"GATT BLE onGetDeviceList wtDelegate");
					for (int i = 0; i < device.size(); i++) {
					if(mMode == SCANLIST && !mWizTurnBeaconListAdapter.contains(device.get(i).getMacAddress())){
							Log.d("!!!!!" ,"device " + device.get(i)._macAddr +" ADD");
							mWizTurnBeaconListAdapter.addItem(device.get(i));
							mScanList.setAdapter(mWizTurnBeaconListAdapter);
						}
						
					else if(mMode == BEACON_DETAIL && mWizTurnBeacon._macAddr.equals(device.get(i).getMacAddress())){
							Log.d("!!!!!" ,"device " + device.get(i)._macAddr +" Update");
							
							mMPower.setText(Integer.toString(device.get(i).getMeasuredPower()) + " dB");
							mRssi.setText(Float.toString(device.get(i).getRssi()) + " dB");
							mDistance.setText(Double.toString(device.get(i).getDistance()) + " m");
							mProxivity.setText(device.get(i).getProximity().toString());
						}
					}
					for (int i = 0; i < device.size(); i++) {
						for (int j = 0; j < mWizTurnBeaconListAdapter.getWizTrunBeacon_items().size(); j++) {
							Log.d("!!!!!" ,"getItem(j).getMacAddress()" + mWizTurnBeaconListAdapter.getItem(j).getMacAddress());
							Log.d("!!!!!" ,"device.get(i).getMacAddress()" + device.get(i).getMacAddress());
						
							if(mWizTurnBeaconListAdapter.getItem(j).getMacAddress().equals(device.get(i).getMacAddress())){
							Log.d("!!!!!" ,"Pre Update RSSI !!!!!"+" "+ mWizTurnBeaconListAdapter.getItem(j)._rssi +" "+device.get(i)._rssi) ;
							mWizTurnBeaconListAdapter.getItem(j)._rssi = device.get(i)._rssi;
							Log.d("!!!!!" ,"wizTrunBeacon_items.size()" + mWizTurnBeaconListAdapter.getWizTrunBeacon_items().size());
							Log.d("!!!!!" ,"Updated RSSI !!!!!" +" "+ mWizTurnBeaconListAdapter.getItem(j)._rssi +" "+device.get(i)._rssi);
						
							}
		                }
					}
					//Sort and Notify to ListAdaptor - YH
					mWizTurnBeaconListAdapter.sort();
					mWizTurnBeaconListAdapter.notifyDataSetChanged();
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
		
		mWizTurnBeaconListAdapter = new WizTurnBeaconListAdapter(this,R.layout.array_scanlist ,new ArrayList<WizTurnBeacons>());
		mScanList = (ListView) findViewById(R.id.scanList);
		mScanList.setOnItemClickListener(this);
		mBtn_refresh = (ImageButton)findViewById(R.id.btn_refresh);
		mBtn_refresh.setOnClickListener(this);
		mMode = SCANLIST;
		mContext = this;
	}

	public void menuBar_Init(String menuTitle){
		Log.d("WizTurnBeacon" ,"menuBar_Init");
		menu_text = (TextView)findViewById(R.id.titleTxt);
		mBtn_back = (ImageButton)findViewById(R.id.btn_back);
		mBtn_back.setOnClickListener(this);
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
		
		//맵버튼 객체 추가 및 온클릭 리스너 추가 - 6월 2일 2055분 김성은 수정
		mMap_icon = (ImageButton)findViewById(R.id.map_icon);
		mMap_icon.setOnClickListener(this);

		mMPower.setText(Integer.toString(item._measuredPower) + "dB");
		mRssi.setText(Float.toString(item._rssi) + "dB");
		mDistance.setText(Double.toString(item.getDistance())+ "m");
		mProxivity.setText(item._proximity.toString());

		box2 = (RelativeLayout)findViewById(R.id.box2);
		box3 = (RelativeLayout)findViewById(R.id.box3);
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

