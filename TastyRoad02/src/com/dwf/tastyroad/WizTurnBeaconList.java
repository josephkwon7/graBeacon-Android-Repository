package com.dwf.tastyroad;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dwf.tastyroad.adapter.WizTurnBeaconListAdapter;
import com.dwf.tastyroad.mapview.NaverMapView;
import com.example.wizturnbeacon.R;
import com.wizturn.sdk.WizTurnDelegate;
import com.wizturn.sdk.WizTurnManager;
import com.wizturn.sdk.WizTurnProximityState;
import com.wizturn.sdk.baseclass.IWizTurnController;
import com.wizturn.sdk.connect.WizTurnBeaconConnect;
import com.wizturn.sdk.entity.WizTurnBeacons;


public class WizTurnBeaconList extends Activity implements OnClickListener , OnItemClickListener{

	///Field
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
	//private Button mBtn_Connect;
	private ImageButton sharelogo5;
	private ImageButton menu_icon4;
	private ImageButton mMap_icon;
	private ImageButton mBtn_twitter;
	private ImageButton mBtn_facebook;
	private ImageButton mBtn_kakao;
	private ImageButton mBtn_line;

	private TextView mDistance;
	private TextView menu_text;

	private RelativeLayout  box2,box3;
	private LinearLayout box4;
	private Context mContext;
	private ScrollView mScrollView;
	
	///Method
	
	//최초 자동 실행
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_ibeaconlist);
		Log.d("WizTurnBeacon" ,"onCreate()");
		
		beaconList_Init();
	}
	
	//onCreate() 다음에 자동실행
	@Override
	protected void onStart() {
		super.onStart();
		Log.d("WizTurnBeacon" ,"onStart()");
		wizturnMgr_setup();
	}
	
	//back 버튼 클릭시 자동 호출되는 callback method
	@Override
	public void onBackPressed() {
		Log.d("WizTurnBeacon" ,"onBackPressed()");
		back();
	}

	//App종료시 자동 호출되는 callback method
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("WizTurnBeacon" ,"onDestroy()");
		if (_wizturnMgr.isStarted()) {
			_wizturnMgr.destroy();
		}
	}
	
	//BLE(Bluetooth Low Energy) 기능을 켜는 요청 처리
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

	//detail page 추가 작업 필요
	@Override
	public void onItemClick(AdapterView<?> adpaterView, View view, int position, long l_position) {
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

	//onClick Callback method - item click시 자동 호출됨
	@Override
	public void onClick(View v) {
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

		//(removed case for connect)
		
		//@detail view : SNS share button click시 공유 popup 보여주기
		case R.id.sharelogo5:
			Log.d("WizTurnBeacon" ,"onClick sharelogo5");
			
			PopupWindow window = PopupHelper.newBasicPopupWindow(this);
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View popupView = inflater.inflate(R.layout.popup, null);
			int totalHeight = getWindowManager().getDefaultDisplay().getHeight();
			int[] location = new int[2];
			
			window.setContentView(popupView);

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
		
		//@detail view : menu button click시 page 하단 이동 	
		case R.id.menu_icon4:
			Log.d("WizTurnBeacon" ,"onClick menu_icon4");
			mScrollView = (ScrollView)findViewById(R.id.scrollView1);
			mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
			break;	
		
		//@detail view : map button click시 지도 보기 화면 이동
		case R.id.map_icon:
			Log.d("WizTurnBeacon", "onClick map_Btn");
			Intent intent1 = new Intent(this, NaverMapView.class);
			startActivity(intent1);
			break;
		
		//@SNS share popup : twitter 시작
		case R.id.btn_twitter:
			Intent shareIntent1 = new Intent();
			shareIntent1.setAction(Intent.ACTION_SEND);
			shareIntent1.setType("image/jpeg");
			shareIntent1.setPackage("com.twitter.android");
			startActivity(shareIntent1);
			break;
		
		//@SNS share popup : facebook 시작
		case R.id.btn_facebook:
			Log.e("facebook", "button event start");
			Intent shareIntent2 = new Intent();
			shareIntent2.setAction(Intent.ACTION_SEND);
			shareIntent2.setType("image/jpeg");
			shareIntent2.setPackage("com.facebook.katana");
			startActivity(shareIntent2);
			break;
		
		//@SNS share popup : kakao talk 시작
		case R.id.btn_kakao:
			Intent shareIntent3 = new Intent();
			shareIntent3.setAction(Intent.ACTION_SEND);
			shareIntent3.setType("text/plain");
			shareIntent3.setPackage("com.kakao.talk");
			startActivity(shareIntent3);
			break;	
		
		//@SNS share popup : kakao talk 시작
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

	//WizturnDelegate inner class - beacon 검색(scan) 결과를 받아서 처리하는 부분
	private WizTurnDelegate _wtDelegate = new WizTurnDelegate() {

		//onGetRSSI 호출시 log만 찍는 역할
		@Override
		public void onGetRSSI(IWizTurnController iWizTurnController, List<String> Data, List<Integer> RSSI) {
			Log.d("WizTurnBeacon" ,"GATT BLE onGetRSSI wtDelegate");

		}
		
		//DeviceList(beacon list)를 탐지(scan) 후 받아올 경우 호출 되는 메서드로 보임
		@Override
		public void onGetDeviceList(IWizTurnController iWizTurnController, final List<WizTurnBeacons> device) {
			
			//runOnUiThread! : 특정 action을 UI 쓰레드에서 실행함. 현재 실행중인 쓰레드가 UI쓰레드(main) 이면 즉시 수행됨
			//현재 실행중인 쓰레드가 UI쓰레드(main)가 아니면 UI쓰레드(main)에 queue(대기열)에 들어가 순번이 되면 수행됨 - YH
			runOnUiThread(new Runnable() {
				
				//run() method - Thread로 수행될 body 부분  
				public void run() {
					Log.d("WizTurnBeacon" ,"GATT BLE onGetDeviceList wtDelegate");
		
					//List<WizTurnBeacons> device : 탐지된 beacon device를 말함
					//탐지된 beacon 갯수만큼 반복 수행
					for (int i = 0; i < device.size(); i++) {
						
						//현재 SCANLIST 모드에 있고, mWizTurnBeaconListAdapter.wizTrunBeacon_items list에 포함되어 있지 않으면
						//즉, 표시하지 않음 새로운 beacon device가 탐지 되었다면.
						if(mMode == SCANLIST && !mWizTurnBeaconListAdapter.contains(device.get(i).getMacAddress())){
							Log.i("!!!!!" ,"device " + device.get(i)._macAddr +" ADD");
							//mWizTurnBeaconListAdapter.wizTrunBeacon_items list에 추가 (한 후 ListView에 표시)
							mWizTurnBeaconListAdapter.addItem(device.get(i));
							//mWizTurnBeaconListAdapter를 mScanList(ListView)에 붙여줌. 즉 화면에 표시함.
							mScanList.setAdapter(mWizTurnBeaconListAdapter);
						}
					
						else if(mMode == BEACON_DETAIL && mWizTurnBeaconListAdapter.contains(device.get(i).getMacAddress())){
							//@detail view : 실시간 거리 표시
							mDistance.setText(Double.toString(device.get(i).getDistance()) + " m");
						}
					
					}
											
					//탐지된 beacon 객체가 들어 있는 mWizTurnBeaconListAdapter.wizTrunBeacon_items list를 대상으로,
					//새로 측정된 RSSI(수신전파 강도. 거리 계산에 사용되는 인자값).를 Update 함 (새로운 거리값 계산을 위함) 
					for (int i = 0; i < device.size(); i++) {
						for (int j = 0; j < mWizTurnBeaconListAdapter.getWizTrunBeacon_items().size(); j++) {
							Log.i("!!!!!" ,"getItem(j).getMacAddress()" + mWizTurnBeaconListAdapter.getItem(j).getMacAddress());
							Log.i("!!!!!" ,"device.get(i).getMacAddress()" + device.get(i).getMacAddress());
						
							//탐지된 device가 list에 저장된 device와 같으면.(mac주소로 비교)
							if(mWizTurnBeaconListAdapter.getItem(j).getMacAddress().equals(device.get(i).getMacAddress())){
								Log.i("!!!!!" ,"Pre Update RSSI !!!!!"+" "+ mWizTurnBeaconListAdapter.getItem(j)._rssi +" "+device.get(i)._rssi) ;
	
								//RSSI(수신전파 강도. 거리 계산에 사용되는 인자값) update 부분
								mWizTurnBeaconListAdapter.getItem(j)._rssi = device.get(i)._rssi;
								
								Log.i("!!!!!" ,"wizTrunBeacon_items.size()" + mWizTurnBeaconListAdapter.getWizTrunBeacon_items().size());
								Log.i("!!!!!" ,"Updated RSSI !!!!!" +" "+ mWizTurnBeaconListAdapter.getItem(j)._rssi +" "+device.get(i)._rssi);
							}
		                }
					}
					//RSSI기준으로 내림차순 정렬 수행 
					mWizTurnBeaconListAdapter.sort();
					//정렬 후에 Adaptor에 표시할 list변동을 알려서 정렬된 list로 다시 표시하게 함
					mWizTurnBeaconListAdapter.notifyDataSetChanged();
				}
				
			});
		}
		
		//거리 가까운 정도 표시, Immediate, Near, Far, .... (사용안함)
		@Override
		public void onGetProximity(IWizTurnController iWizTurnController, WizTurnProximityState proximity) {
			Log.d("WizTurnBeacon" ,"GATT BLE onGetProximity wtDelegate");

		}
	};

	//@main view : view 초기화
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

	//@ 공통 : menuBar 초기화
	public void menuBar_Init(String menuTitle){
		Log.d("WizTurnBeacon" ,"menuBar_Init");
		menu_text = (TextView)findViewById(R.id.titleTxt);
		mBtn_back = (ImageButton)findViewById(R.id.btn_back);
		mBtn_back.setOnClickListener(this);
		menu_text.setText(menuTitle);
	}

	//@detail view : view 초기화
	public void beaconDetail_Init(WizTurnBeacons item){
		Log.d("WizTurnBeacon" ,"beaconDetail_Init");

		//맛집 상세 정보 image 부분 (메뉴까지 포함)
		box2 = (RelativeLayout)findViewById(R.id.box2);
		
		//맛집 정보 table 부분
		box3 = (RelativeLayout)findViewById(R.id.box3);
		box3.setVisibility(View.GONE);
				
		//맛집까지의 거리를 실시간 표시
		mDistance = ((TextView)findViewById(R.id.ibeacondetail_Distance));
		mDistance.setText(Double.toString(item.getDistance())+ "m");
		
		//메뉴 보기 버튼 (detail view 제일 아래로 이동)
		menu_icon4 = (ImageButton) findViewById(R.id.menu_icon4);
		menu_icon4.setOnClickListener(this);
		
		//맵 보기 버튼 (맵보기 view로 이동)
		mMap_icon = (ImageButton)findViewById(R.id.map_icon);
		mMap_icon.setOnClickListener(this);
		
		//SNS 공유 버튼 (공유 popup 띄움)
		sharelogo5 = (ImageButton) findViewById(R.id.sharelogo5);
		sharelogo5.setOnClickListener(this);
	}

	//back 버트 클릭시 수행
	public void back(){
		Log.d("WizTurnBeacon" ,"back()");
		//main view에서 back 했으면.
		if(mMode == SCANLIST){
			//beacon 탐지 종료
			if (_wizturnMgr.isStarted()) {
				// WizTurn Scan Stop
				_wizturnMgr.stopController();
			}
			//activity 종료
			finish();
		//@detail view에서 back했을 경우
		}else if(mMode == BEACON_DETAIL){
			//main view 초기화
			beaconList_Init();
			//beacon 탐지 다시 시작
			_wizturnMgr.startController();			
		//기타 다른 화면에서 back했을 경우 App 종료
		}else{
			_wizturnMgr.destroy();
		}
	}
	
	//BlueTooth 기능 보유 여부 확인 및 활성화(Enabled) 확인. 비활성시 활성화. Beacon탐지 시작.
	public void wizturnMgr_setup(){
		Log.d("WizTurnBeacon" ,"wizturnMgr_setup()");
		_wizturnMgr = WizTurnManager.sharedInstance(this);

		// BLE(bluetooth low energy)를 지원 하는지 확인.
		if (!_wizturnMgr.hasBluetooth()) {
			Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
			return;
		}
		
		// BLE가 활성화 안되어 있으면 활성화 되게 함 (추가로 wizturn manager가 제공하는 서비스 정상 동작 확인)
		if (!_wizturnMgr.isBluetoothEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			//Beacon탐지 시작
			_wizturnMgr.setInitController();
			_wizturnMgr.setWizTurnDelegate(_wtDelegate);
			_wizturnMgr.startController();
		}
	}
}

