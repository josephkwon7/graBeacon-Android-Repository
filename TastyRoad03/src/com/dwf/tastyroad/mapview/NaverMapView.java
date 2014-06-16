package com.dwf.tastyroad.mapview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay.OnStateChangeListener;

public class NaverMapView extends NMapActivity{
	
	public static final String API_KEY="9bed530e49c05e39d0490b637762a821";			//네이버 개발자 센터로부터 발급받은 API Key 값을 저장하는 객체
	
	/*지도의 상태를 변경하고 컨트롤하기 위한 클래스이다. 
	NMapView 클래스 생성 시 내부적으로 생성되며 NMapView 클래스의 getMapController() 메서드를 통해서 접근한다. 
	지도 중심 및 축척 레벨 변경과 지도 확대, 축소, 패닝 등 다양한 기능을 수행한다.  */
	private NMapController mMapController;
	
	/*안드로이드 Activity 클래스를 상속받은 클래스로서 NMapView 객체를 표시한다. 
	또한 Activity 라이프 사이클에 따라서 내부적으로 지도 데이터를 관리한다. NMapActivity를 상속받은 클래스는 onCreate()에서 NMapView 객체를 생성해야 한다.  */
	private NMapView mMapView;
	
	private OnStateChangeListener onPOIdataStateChangeListener;			//POI 아이템의 선택 상태 변경 시 호출되는 콜백 인터페이스를 정의한다. 
	
	/* 단말기의 현재 위치 탐색 기능을 사용하기 위한 클래스이다.
	내부적으로 시스템에서 제공하는 GPS 및 네트워크를 모두 사용하여 현재 위치를 탐색한다. */
	private NMapLocationManager mMapLocationManager;
	
	private NMapMyLocationOverlay mMyLocationOverlay;		//지도 위에 현재 위치를 표시하는 오버레이 클래스이며 NMapOverlay 클래스를 상속한다. 
	
	private NMapOverlayManager mOverlayManager;			//지도 위에 표시되는 오버레이 객체를 관리한다. 
	
	private NMapCompassManager mMapCompassManager; 			//단말기의 나침반 기능을 사용하기 위한 클래스이다. 
	
	private MapContainerView mMapContainerView;
	
	private NMapViewerResourceProvider mMapViewerResourceProvider;
	
	//지도상에 경위도 좌표를 나타내는 NGeoPoint 타입의 객체를 생성하여 초기 위치값을 저장
	private static final NGeoPoint NMAP_LOCATION_DEFAULT = new NGeoPoint(127.027736, 37.494381);
	
	private static final int NMAP_ZOOMLEVEL_DEFAULT = 18;			//맵의 초기 확대값을 저장하는 객체 (최대값 14)
	
	private static final int NMAP_VIEW_MODE_DEFAULT = NMapView.VIEW_MODE_VECTOR;			// 일반 지도 보기 모드 설정 여부값을 저장하는 객체
	
	private static final boolean NMAP_TRAFFIC_MODE_DEFAULT = false;			// 위성 지도 보기 모드 설정 여부값을 저장하는 객체
	
	private static final boolean NMAP_BICYCLE_MODE_DEFAULT = false;			// 실시간 교통정보 지도 보기 모드 설정 여부값을 저장하는 객체
	
	private static final String KEY_ZOOM_LEVEL = "NaverMapView.zoomLevel";
	private static final String KEY_CENTER_LONGITUDE = "NaverMapView.centerLongitudeE6";
	private static final String KEY_CENTER_LATITUDE = "NaverMapView.centerLatitudeE6";
	private static final String KEY_VIEW_MODE = "NaverMapView.viewMode";
	private static final String KEY_TRAFFIC_MODE = "NaverMapView.trafficMode";
	private static final String KEY_BICYCLE_MODE = "NaverMapView.bicycleMode";

	private static final String LOG_TAG = "디버깅";
	private static final boolean DEBUG = false;			// 디버그 모드 설정 여부 값을 저장하는 객체
	
	private SharedPreferences mPreferences;
	
	private double geoLat;
	private double geoLong;
	private String resName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// mMapView 객체 생성
		mMapView = new NMapView(this);
		
		 // 맵 뷰 회전 기능을 사용하기 위한 부모 클래스 객체 생성
	    mMapContainerView = new MapContainerView(this);
	    mMapContainerView.addView(mMapView);

		// 맵뷰 라이브러리에 발급받은 API Key 설정
		mMapView.setApiKey(API_KEY);

		// set the activity content to the map view
		setContentView(mMapContainerView);

		// 맵 뷰 요소 초기화
		mMapView.setClickable(true);
		mMapView.setEnabled(true);
		mMapView.setFocusable(true);
		mMapView.setFocusableInTouchMode(true);
		mMapView.requestFocus();

		// register listener for map state changes
		mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
		mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
		mMapView.setOnMapViewDelegate(onMapViewTouchDelegate);

		// use map controller to zoom in/out, pan and set map center, zoom level etc.
		mMapController = mMapView.getMapController();
		
		// use built in zoom controls
		mMapView.setBuiltInZoomControls(true, null);
		
		///////////////////////////////////오버레이 기능 추가 부분////////////////////////////////////////////////
		
		//Intent 이용 WizTrunBeaconList에서 위도, 경도, 이름 받아옴
		Intent intent = getIntent();
		//Bitmap imgMarker = (Bitmap) intent.getExtras().get("imgMarker");
		geoLat = intent.getExtras().getDouble("geoLat");
		geoLong = intent.getExtras().getDouble("geoLong");
		resName = intent.getExtras().getString("name");
		
		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);//, imgMarker);
		
		mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
		// register callout overlay listener to customize it.
		mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);
		
		// location manager
		mMapLocationManager = new NMapLocationManager(this);
		mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);		
		
		// compass manager
		mMapCompassManager = new NMapCompassManager(this);
		
		// create my location overlay
		mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);   
		
		mMapLocationManager.enableMyLocation(true);
		
		addMarker();
		
	} // end of onCreate Method
	
	@Override
	protected void onStart() {
		startMyLocation();
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		stopMyLocation();
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {

		//액티비티 종료(뒤로가기를 한다거나..)시 맵의 중심 위치 및 확대/축소 레벨을 저장함
		saveInstanceState();
		stopMyLocation();
		super.onDestroy();
	}
	
	private void addMarker() {

		int markerId = NMapPOIflagType.PIN;

		// set POI data
		NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
		poiData.beginPOIdata(1);
		
		Log.e(getClass().getName(), "!!!!! " +resName);

		
		//마커가 찍힐 위/경도 정의, 마커를 클릭하였을 때 위에 문구창 텍스트 정의 부분
		poiData.addPOIitem(geoLong, geoLat, resName, markerId, 0);
//		poiData.addPOIitem(127.027717, 37.494406, null, markerId, 0);
//		poiData.addPOIitem(127.061, 37.51, "Pizza 123-456", markerId, 0);
		poiData.endPOIdata();

		// create POI data overlay
		NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
		poiDataOverlay.showAllPOIdata(0);
		poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);

	}
	
	private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {
		
		/**
		 * 현재 위치 변경 시 호출된다. myLocation객체에 변경된 좌표가 전달된다. 현재 위치를 계속 탐색하려면 true를 반환한다.
		 */
		@Override
		public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {
			
			if (mMapController != null) {
				mMapController.animateTo(myLocation);
			}

			return true;
		}
		
		/**
		 * 정해진 시간 내에 현재 위치 탐색 실패 시 호출된다.
		 */
		@Override
		public void onLocationUpdateTimeout(NMapLocationManager locationManager) {
			
			// stop location updating
						//			Runnable runnable = new Runnable() {
						//				public void run() {										
						//					stopMyLocation();
						//				}
						//			};
						//			runnable.run();	

						Toast.makeText(NaverMapView.this, "현재 위치 검색이 지연되고 있습니다", Toast.LENGTH_LONG).show();			
						//Your current location is temporarily unavailable. --> 현재 위치 검색이 지연되고 있습니다
		}
		
		
		/**
		 * 현재 위치가 지도 상에 표시할 수 있는 범위를 벗어나는 경우에 호출된다.
		 */
		@Override
		public void onLocationUnavailableArea(NMapLocationManager locationManager,	NGeoPoint myLocation) {
			
			Toast.makeText(NaverMapView.this, "현재 지역 정보 검색이 지연되고 있습니다", Toast.LENGTH_LONG).show();
			//Your current location is unavailable area. --> 현재 지역 정보 검색이 지연되고 있습니다
			
			stopMyLocation();
		}
	}; //end of onMyLocationChangeListener
	
	/**
	 * startMyLocation을 호출하여 콤파스 기능을 구동시 미세한 끊김 현상 발생
	 * 원인 분석 후 수정요망
	 */
	private void startMyLocation() {
		
		if (mMyLocationOverlay != null) {
			if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
				mOverlayManager.addOverlay(mMyLocationOverlay);
			}
			
			//isMyLocationEnabled -> 현재 위치 탐색중인지 여부를 반환한다
			if (mMapLocationManager.isMyLocationEnabled()) { // 이전에 MyLocationEnable을 활성화 시켜야 true 값을 반환하고 아래의 if문이 구동된다.
				//isAutoRotateEnabled : 지도 회전 기능 활성화 상태를 가져온다
				if (!mMapView.isAutoRotateEnabled()) {
					
					//setCompassHeadingVisible : 나침반 각도 표시 여부를 설정한다
					mMyLocationOverlay.setCompassHeadingVisible(true);

					//enableCompass : 시스템에서 나침반 기능을 제공하지 않으면 false를 반환한다.
					mMapCompassManager.enableCompass() ;

					/* setAutoRotateEnabled : 지도 회전 기능 활성화 여부를 설정한다.
					enabled : 지도 회전 활성화 여부
					suspend : 지도 회전 해제 시 애니메이션 여부 */
					mMapView.setAutoRotateEnabled(true, false);

					mMapContainerView.requestLayout();
					
				} else {
					stopMyLocation();
				}

				mMapView.postInvalidate();
				
			} else {
				boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
				// 내위치 탐색이 이루어지지 않는 상태라면 시스템 설정 액티비티로 전환시킴
				if (!isMyLocationEnabled) {
					Toast.makeText(NaverMapView.this, "시스템 설정에서 My Location 설정을 활성화시켜주세요.",Toast.LENGTH_LONG).show();
					Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(goToSettings);
					return;
				}
			}
		}
	} // end of startMyLocation method

	private void stopMyLocation() {
		if (mMyLocationOverlay != null) {
			mMapLocationManager.disableMyLocation();

			if (mMapView.isAutoRotateEnabled()) {
				mMyLocationOverlay.setCompassHeadingVisible(false);

				//disableCompass : 나침반 모니터링을 종료한다
				mMapCompassManager.disableCompass();

				mMapView.setAutoRotateEnabled(false, false);

				mMapContainerView.requestLayout();
			}
		}
	} // end of stopMyLocation method
	
	
	
	/**
	 * POI 아이템의 말풍선이 선택되면 호출된다
	 */
	public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
		// [[TEMP]] handle a click event of the callout
		Toast.makeText(NaverMapView.this, "onCalloutClick: " + item.getTitle(), Toast.LENGTH_LONG).show();
	}
	
	
	/**
	 * POI 아이템의 선택 상태가 변경되면 호출된다. 
	 * 이전에 선택된 아이템이 선택 해제되면 poiItem으로 null이 전달된다.
	 */
	public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
		if (item != null) {
			Log.i(LOG_TAG, "onFocusChanged: " + item.toString());
		} else {
			Log.i(LOG_TAG, "onFocusChanged: ");
		}
	}
	
	
	private final NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener = new NMapOverlayManager.OnCalloutOverlayListener() {
		/**
		 * POI 아이템 선택 시 말풍선 오버레이 객체를 생성하기 전에 호출된다. 겹친 아이템에 대해 처리하려면 null을 반환한다.
		 */
		public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay itemOverlay, NMapOverlayItem overlayItem, Rect itemBounds) {
			// handle overlapped items
			if (itemOverlay instanceof NMapPOIdataOverlay) {
				NMapPOIdataOverlay poiDataOverlay = (NMapPOIdataOverlay)itemOverlay;

				// check if it is selected by touch event
				if (!poiDataOverlay.isFocusedBySelectItem()) {
					int countOfOverlappedItems = 1;

					NMapPOIdata poiData = poiDataOverlay.getPOIdata();
					for (int i = 0; i < poiData.count(); i++) {
						NMapPOIitem poiItem = poiData.getPOIitem(i);

						// skip selected item
						if (poiItem == overlayItem) {
							continue;
						}

						// check if overlapped or not
						if (Rect.intersects(poiItem.getBoundsInScreen(), overlayItem.getBoundsInScreen())) {
							countOfOverlappedItems++;
						}
					}

					if (countOfOverlappedItems > 1) {
						String text = countOfOverlappedItems + " overlapped items for " + overlayItem.getTitle();
						Toast.makeText(NaverMapView.this, text, Toast.LENGTH_LONG).show();
						return null;
					}
				}
			}

			// use custom old callout overlay
			if (overlayItem instanceof NMapPOIitem) {
				NMapPOIitem poiItem = (NMapPOIitem)overlayItem;

				if (poiItem.showRightButton()) {
					return new NMapCalloutCustomOldOverlay(itemOverlay, overlayItem, itemBounds,
							mMapViewerResourceProvider);
				}
			}

			// use custom callout overlay
			return new NMapCalloutCustomOverlay(itemOverlay, overlayItem, itemBounds, mMapViewerResourceProvider);

			// set basic callout overlay
			//return new NMapCalloutBasicOverlay(itemOverlay, overlayItem, itemBounds);			
		}
	}; // end of OnCalloutOverlayListener method
	
	
	
	
	
	
	/* MapView State Change Listener*/
	private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {
		/**
		 * 지도가 초기화된 후 호출된다. 정상적으로 초기화되면 errorInfo 객체는 null이 전달되며, 초기화 실패 시 errorInfo객체에 에러 원인이 전달된다
		 */
		@Override
		public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {
			if (errorInfo == null) { // success
				//			mMapController.setMapCenter(new NGeoPoint(127.027736, 37.494381));
				restoreInstanceState();
			} else { // fail
				Log.e(LOG_TAG, "onMapInitHandler: error=" + errorInfo.toString());
			}
		} // end of onMapInitHandler

		/*  지도 애니메이션 상태 변경 시 호출된다.
		animType : ANIMATION_TYPE_PAN or ANIMATION_TYPE_ZOOM
		animState : ANIMATION_STATE_STARTED or ANIMATION_STATE_FINISHED
		 */
		@Override
		public void onAnimationStateChange(NMapView mapView, int animType, int animState) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onAnimationStateChange: animType=" + animType + ", animState=" + animState);
			}
		}

		// API에 내용 없음
		@Override
		public void onMapCenterChangeFine(NMapView mapView) {}

		//지도 중심 변경 시 호출되며 변경된 중심 좌표가 파라미터로 전달된다.
		@Override
		public void onMapCenterChange(NMapView mapView, NGeoPoint center) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onMapCenterChange: center=" + center.toString());
			}
		}

		//지도 레벨 변경 시 호출되며 변경된 지도 레벨이 파라미터로 전달된다.
		@Override
		public void onZoomLevelChange(NMapView mapView, int level) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onZoomLevelChange: level=" + level);
			}
		}
	};

	
	// 객체 상태 복구 정보
	private void restoreInstanceState() {
		mPreferences = getPreferences(MODE_PRIVATE);

		int longitudeE6 = mPreferences.getInt(KEY_CENTER_LONGITUDE, NMAP_LOCATION_DEFAULT.getLongitudeE6());
		int latitudeE6 = mPreferences.getInt(KEY_CENTER_LATITUDE, NMAP_LOCATION_DEFAULT.getLatitudeE6());
		int level = mPreferences.getInt(KEY_ZOOM_LEVEL, NMAP_ZOOMLEVEL_DEFAULT);
		int viewMode = mPreferences.getInt(KEY_VIEW_MODE, NMAP_VIEW_MODE_DEFAULT);
		boolean trafficMode = mPreferences.getBoolean(KEY_TRAFFIC_MODE, NMAP_TRAFFIC_MODE_DEFAULT);
		boolean bicycleMode = mPreferences.getBoolean(KEY_BICYCLE_MODE, NMAP_BICYCLE_MODE_DEFAULT);

		mMapController.setMapViewMode(viewMode);
		mMapController.setMapViewTrafficMode(trafficMode);
		mMapController.setMapViewBicycleMode(bicycleMode);
		mMapController.setMapCenter(new NGeoPoint(longitudeE6, latitudeE6), level);
	}
	
	// 객체 상태 저장 정보
	private void saveInstanceState() {
		if (mPreferences == null) {
			return;
		}

		NGeoPoint center = mMapController.getMapCenter();
		int level = mMapController.getZoomLevel();
		int viewMode = mMapController.getMapViewMode();
		boolean trafficMode = mMapController.getMapViewTrafficMode();
		boolean bicycleMode = mMapController.getMapViewBicycleMode();

		SharedPreferences.Editor edit = mPreferences.edit();

		edit.putInt(KEY_CENTER_LONGITUDE, center.getLongitudeE6());
		edit.putInt(KEY_CENTER_LATITUDE, center.getLatitudeE6());
		edit.putInt(KEY_ZOOM_LEVEL, level);
		edit.putInt(KEY_VIEW_MODE, viewMode);
		edit.putBoolean(KEY_TRAFFIC_MODE, trafficMode);
		edit.putBoolean(KEY_BICYCLE_MODE, bicycleMode);

		edit.commit();

	}
	
	private final NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener() {
		//지도 위에서 터치 후 일정 시간이 경과하면 호출된다.
		@Override
		public void onLongPress(NMapView mapView, MotionEvent ev) {}

		//API에 내용 없음
		@Override
		public void onLongPressCanceled(NMapView mapView) {}

		//지도 위에서 스크롤 이벤트 발생 시 호출된다.
		@Override
		public void onScroll(NMapView mapView, MotionEvent e1, MotionEvent e2) {}

		//지도 위에서 탭 이벤트 발생 시 호출된다.
		@Override
		public void onSingleTapUp(NMapView mapView, MotionEvent ev) {}

		//지도 터치 다운 이벤트 발생 시 호출된다.
		@Override
		public void onTouchDown(NMapView mapView, MotionEvent ev) {}

		//지도 터치 업 이벤트 발생 시 호출된다.
		@Override
		public void onTouchUp(NMapView mapView, MotionEvent ev) {}
	};

	/** 
	 * 맵 회전을 위한 컨테이너 뷰 클래스
	 */
	private class MapContainerView extends ViewGroup {

		public MapContainerView(Context context) {
			super(context);
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
			final int width = getWidth();
			final int height = getHeight();
			final int count = getChildCount();
			for (int i = 0; i < count; i++) {
				final View view = getChildAt(i);
				final int childWidth = view.getMeasuredWidth();
				final int childHeight = view.getMeasuredHeight();
				final int childLeft = (width - childWidth) / 2;
				final int childTop = (height - childHeight) / 2;
				view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
			}

			if (changed) {
				mOverlayManager.onSizeChanged(width, height);
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
			int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
			int sizeSpecWidth = widthMeasureSpec;
			int sizeSpecHeight = heightMeasureSpec;

			final int count = getChildCount();
			for (int i = 0; i < count; i++) {
				final View view = getChildAt(i);

				if (view instanceof NMapView) {
					if (mMapView.isAutoRotateEnabled()) {
						int diag = (((int)(Math.sqrt(w * w + h * h)) + 1) / 2 * 2);
						sizeSpecWidth = MeasureSpec.makeMeasureSpec(diag, MeasureSpec.EXACTLY);
						sizeSpecHeight = sizeSpecWidth;
					}
				}

				view.measure(sizeSpecWidth, sizeSpecHeight);
			}
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}	
	
	private final NMapView.OnMapViewDelegate onMapViewTouchDelegate = new NMapView.OnMapViewDelegate() {

		@Override
		public boolean isLocationTracking() {
			if (mMapLocationManager != null) {
				if (mMapLocationManager.isMyLocationEnabled()) {
					return mMapLocationManager.isMyLocationFixed();
				}
			}
			return false;
		}

	};
} // end of class
