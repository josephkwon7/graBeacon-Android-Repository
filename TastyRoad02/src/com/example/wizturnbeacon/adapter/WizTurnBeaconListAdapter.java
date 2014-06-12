package com.example.wizturnbeacon.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.wizturnbeacon.MainActivity;
import com.example.wizturnbeacon.R;
import com.wizturn.sdk.entity.WizTurnBeacons;


/*
*/


public class WizTurnBeaconListAdapter extends ArrayAdapter{
	
	///Field	
	private LayoutInflater inflater = null;
	private ArrayList<WizTurnBeacons> wizTrunBeacon_items;
	HashMap<WizTurnBeacons, Bitmap> getImgUrlMap = new HashMap<WizTurnBeacons, Bitmap>();
	private String reqURL;
	int count;
	
	///Constructor
	public WizTurnBeaconListAdapter(Context context, int textViewResourceId,
			ArrayList<WizTurnBeacons> mList) {
		super(context, textViewResourceId,  mList);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		wizTrunBeacon_items = mList;
	}
	
	///Getter added by YH
	public ArrayList<WizTurnBeacons> getWizTrunBeacon_items(){
		return this.wizTrunBeacon_items;
	}
	
	///Method
	public boolean contains(String macAddr) {
		for (int i = 0; i < wizTrunBeacon_items.size(); i++) {
			if (wizTrunBeacon_items.get(i) != null && wizTrunBeacon_items.get(i).getMacAddress().equals(macAddr)) {
				return true;
			}
		}
		return false;
	}
	
	//beacon add Item
	public void addItem(WizTurnBeacons item){
		wizTrunBeacon_items.add(item);
	}
	
	//beacon get Item
	public WizTurnBeacons getItem(int position) {
		return wizTrunBeacon_items.get(position);
	}
	
	//beaconList Clear
	public void clearItem() {
		wizTrunBeacon_items.clear();
	}
	

	public View getView(int position, View v, ViewGroup parent) {
		
		Bitmap thumbImg;
		String req1 = "http://192.168.200.27:8080/tastyroad/beacon/Thumbnail/";
		String req2 = wizTrunBeacon_items.get(position).getProximityUUID()+"/";
		String req3 = wizTrunBeacon_items.get(position).getMajor()+"/";
		String req4 = wizTrunBeacon_items.get(position).getMinor()+"";
		reqURL = req1 + req2 + req3 + req4;
		
		if(v==null){
			v = inflater.inflate(R.layout.array_scanlist, null);
		}
		/*
		TextView mSSID = (TextView)v.findViewById(R.id.scanList_SSID);
		mSSID.setText("SSID: " + wizTrunBeacon_items.get(position).getName());
		*/
		if(!getImgUrlMap.containsKey(wizTrunBeacon_items.get(position))){
			RequestURLThread thread = new RequestURLThread(reqURL);
			thread.start();
			
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
				thumbImg = thread.getBitmap();
				getImgUrlMap.put(wizTrunBeacon_items.get(position), thumbImg);
		}
		else{
			thumbImg=getImgUrlMap.get(wizTrunBeacon_items.get(position));
		}	
		
			ImageView thumbView = (ImageView)v.findViewById(R.id.imageView1);
			thumbView.setImageBitmap(thumbImg);
		
			return v;
		
	}

		
	//RSSI(전파수신강도)를 내림 차순으로 정렬. 즉 가까운 거리순으로 wiTurnBeacons 인스턴스 정렬
	public void sort() {
		Collections.sort(wizTrunBeacon_items, new Comparator<WizTurnBeacons>() {
			@Override
			public int compare(WizTurnBeacons lhs, WizTurnBeacons rhs) {
				++count;
				return (int) (rhs.getRssi() - lhs.getRssi());
			}
		});
		
		Log.e("Collection.sort ", "compare start ..." + count);
	}
	
	public class RequestURLThread extends Thread{
		
		String reqURL;
		String rawJSON;
		String thumbURL;
		Bitmap thumbImg;
		
		public RequestURLThread(){
			
		}
		
		public RequestURLThread(String reqURL) {
			super();
			this.reqURL = reqURL;
		}
		
		public void run(){
			Log.e(MainActivity.class.toString(), "RequestURL !!!!! : "+ reqURL);
			Log.e(MainActivity.class.toString(),"getJSON START!!!");

			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(reqURL);
			try{
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if(statusCode == 200){
					Log.e(MainActivity.class.toString(),"getJSON OK!!!");
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(content));
					String line;
					while((line = reader.readLine()) != null){
						builder.append(line);
					}
				} else {
					Log.e(MainActivity.class.toString(),"Failed to get JSON object");
				}
			}catch(ClientProtocolException e){
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
			///////////////////////////////////////////////
			
			rawJSON = builder.toString();
			Log.e(MainActivity.class.toString(), "RawJSON !!!!! : "+ rawJSON);
			JSONParser parser = new JSONParser();
			
			try {
				Object obj = parser.parse(rawJSON);
				JSONObject jsonObject = (JSONObject) obj;
			 	thumbURL = (String) jsonObject.get("imgSmall1");
			 	Log.e(MainActivity.class.toString(), "URL !!!!! : "+thumbURL);
			 	
				try {
					URL url = new URL(thumbURL);
					thumbImg = BitmapFactory.decodeStream(url.openConnection().getInputStream());
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		public Bitmap getBitmap(){
			return thumbImg;
		}
	}
	
	
}
