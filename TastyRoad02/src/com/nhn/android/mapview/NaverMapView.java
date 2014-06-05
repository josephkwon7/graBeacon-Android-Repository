package com.nhn.android.mapview;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapLocationManager.OnLocationChangeListener;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.NMapView.OnMapStateChangeListener;
import com.nhn.android.maps.NMapView.OnMapViewTouchEventListener;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager.OnCalloutOverlayListener;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay.OnStateChangeListener;

public class NaverMapView extends NMapActivity implements OnMapStateChangeListener, OnMapViewTouchEventListener, OnCalloutOverlayListener{

	public static final String API_KEY="9bed530e49c05e39d0490b637762a821";
	NMapController mMapController;
	NMapView mMapView;
	OnStateChangeListener onPOIdataStateChangeListener;
	OnLocationChangeListener onMyLocationChangeListener;
//	MapContainerView mMapContainer;
	
	//NMapOverlayManager : 지도 위에 표시되는 오버레이 객체들을 관리 (객체 생성)
	//NMapPOIdataOverlay : 여러개의 오버레이 아이템들을 하나의 오버레이 객체에서 관리하기 위함 (객체 생성)

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// create map view
		NMapView mMapView = new NMapView(this);
		
		// create parent view to rotate map view
//	    mMapContainerView = new MapContainerView(this);
//	    mMapContainerView.addView(mMapView);


		// set a registered API key for Open MapViewer Library
		mMapView.setApiKey(API_KEY);

		// set the activity content to the map view
		setContentView(mMapView);

		// initialize map view
		mMapView.setClickable(true);

		// register listener for map state changes
		mMapView.setOnMapStateChangeListener(this);
		mMapView.setOnMapViewTouchEventListener(this);

		// use built in zoom controls
		mMapView.setBuiltInZoomControls(true, null);

		// use map controller to zoom in/out, pan and set map center, zoom level etc.
		mMapController = mMapView.getMapController();
		
		///////////////////////////////////오버레이 기능 추가 부분////////////////////////////////////////////////
		
		NMapViewerResourceProvider mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

		NMapOverlayManager mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
		
		int markerId = NMapPOIflagType.PIN;

		// set POI data
		NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
		poiData.beginPOIdata(2);
		
		//마커가 찍힐 위도, 경도 정의, 마커를 클릭하였을 때 위에 문구창 텍스트 정의 부분
		poiData.addPOIitem(127.027717, 37.494406, "춘하추동 : 02-3474-9297", markerId, 0);
//		poiData.addPOIitem(127.061, 37.51, "Pizza 123-456", markerId, 0);
		poiData.endPOIdata();

		// create POI data overlay
		NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
		
		poiDataOverlay.showAllPOIdata(0);
		
		poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);
		
		mOverlayManager.setOnCalloutOverlayListener((OnCalloutOverlayListener)this);
		
//		// location manager
//		NMapLocationManager mMapLocationManager = new NMapLocationManager(this);
//		mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
//		
//		// compass manager
//		NMapCompassManager mMapCompassManager = new NMapCompassManager(this);
//		
//		// create my location overlay
//		NMapMyLocationOverlay mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
	} // end of onCreate Method
	
	public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
		// [[TEMP]] handle a click event of the callout
		Toast.makeText(NaverMapView.this, "onCalloutClick: " + item.getTitle(), Toast.LENGTH_LONG).show();
	}

	public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
		if (item != null) {
			Log.i("NMAP", "onFocusChanged: " + item.toString());
		} else {
			Log.i("NMAP", "onFocusChanged: ");
		}
	}

	public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay itemOverlay, NMapOverlayItem overlayItem, Rect itemBounds) {
		// set your callout overlay
		return new NMapCalloutBasicOverlay(itemOverlay, overlayItem, itemBounds);
	}

	@Override
	public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {
		if (errorInfo == null) { // success
			mMapController.setMapCenter(new NGeoPoint(127.027736, 37.494381));
		} else { // fail
			Log.e("NMAP", "onMapInitHandler: error=" + errorInfo.toString());
		}
	} // end of onMapInitHandler

	@Override
	public void onLongPress(NMapView arg0, MotionEvent arg1) {}
	@Override
	public void onLongPressCanceled(NMapView arg0) {}
	@Override
	public void onScroll(NMapView arg0, MotionEvent arg1, MotionEvent arg2) {}
	@Override
	public void onSingleTapUp(NMapView arg0, MotionEvent arg1) {}
	@Override
	public void onTouchDown(NMapView arg0, MotionEvent arg1) {}
	@Override
	public void onTouchUp(NMapView arg0, MotionEvent arg1) {}
	@Override
	public void onAnimationStateChange(NMapView arg0, int arg1, int arg2) {}
	@Override
	public void onMapCenterChange(NMapView arg0, NGeoPoint arg1) {}
	@Override
	public void onMapCenterChangeFine(NMapView arg0) {}
	@Override
	public void onZoomLevelChange(NMapView arg0, int arg1) {}
	
} // end of class
