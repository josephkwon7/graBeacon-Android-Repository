package com.dwf.tastyroad.adapter;

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

import com.dwf.tastyroad.BeaconExtended;
import com.dwf.tastyroad.MainActivity;
import com.example.wizturnbeacon.R;
import com.wizturn.sdk.entity.WizTurnBeacons;


/*
*/


public class WizTurnBeaconListAdapter extends ArrayAdapter{
	
	///Field
	private LayoutInflater inflater = null;
	public ArrayList<BeaconExtended> beaconExtended_items;
	//private HashMap<BeaconExtended, Bitmap> getImgUrlMap = new HashMap<BeaconExtended, Bitmap>();
	
	private String reqURL;
	private int count;
	//public final String serverURL = "http://192.168.200.27:8080/tastyroad/";
	public final String serverURL = "http://tastyroad.cafe24.com/";
	
	///Constructor
	public WizTurnBeaconListAdapter(Context context, int textViewResourceId,
			ArrayList<BeaconExtended> mList) {
		super(context, textViewResourceId,  mList);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		beaconExtended_items = mList;
	}
	
	///Getter added by YH
	public ArrayList<BeaconExtended> getBeaconExtended_items(){
		return this.beaconExtended_items;
	}
	
	///Method
	public boolean contains(String macAddr) {
		for (int i = 0; i < beaconExtended_items.size(); i++) {
			if (beaconExtended_items.get(i) != null && beaconExtended_items.get(i).getMacAddress().equals(macAddr)) {
				return true;
			}
		}
		return false;
	}
	
	//beacon add Item
	public void addItem(WizTurnBeacons item){
		beaconExtended_items.add(
				new BeaconExtended(item.getProximityUUID(), item.getName(), 
						item.getMacAddress(), item.getMajor(), item.getMinor(), 
						item.getMeasuredPower(), (int) item.getRssi(), 
						item.getProximity())
				);
	}
	
	//beacon get Item
	public BeaconExtended getItem(int position) {
		return beaconExtended_items.get(position);
	}
	
	//beaconList Clear
	public void clearItem() {
		beaconExtended_items.clear();
	}
	

	public View getView(int position, View v, ViewGroup parent) {
		
		Bitmap thumbImg;
		String req1 = serverURL +"beacon/";
		String req2 = beaconExtended_items.get(position).getProximityUUID()+"/";
		String req3 = beaconExtended_items.get(position).getMajor()+"/";
		String req4 = beaconExtended_items.get(position).getMinor()+"";
		reqURL = req1 + req2 + req3 + req4;
		
		
		if(v==null){
			v = inflater.inflate(R.layout.array_scanlist, null);
		}
		/*
		TextView mSSID = (TextView)v.findViewById(R.id.scanList_SSID);
		mSSID.setText("SSID: " + wizTrunBeacon_items.get(position).getName());
		*/
		//if(beaconExtended_items.get(position).getImgSmall1())
		Log.i("!!!!!", beaconExtended_items.get(position).toString());
		if(beaconExtended_items.get(position).getImgSmall1()==null){
			RequestURLThread thread = new RequestURLThread(reqURL, beaconExtended_items.get(position));
			thread.start();
			
			
				try {
					thread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
			
			this.beaconExtended_items.set(position, thread.getBeaconExtended());
		}
		
		((ImageView)v.findViewById(R.id.imageView1)).setImageBitmap(beaconExtended_items.get(position).getImgSmall1());
		
		
		
		return v;
		
	}

		
	//RSSI(전파수신강도)를 내림 차순으로 정렬. 즉 가까운 거리순으로 wiTurnBeacons 인스턴스 정렬
	public void sort() {
		Collections.sort(beaconExtended_items, new Comparator<BeaconExtended>() {
			@Override
			public int compare(BeaconExtended lhs, BeaconExtended rhs) {
				//++count;
				return (int) (rhs.getRssi() - lhs.getRssi());
			}
		});
		
		//Log.e("Collection.sort ", "compare start ..." + count);
	}
	
	public class RequestURLThread extends Thread{
		
		private BeaconExtended beaconExtended;
		private String reqURL;
		private String rawJSON;
		
		public RequestURLThread(){
			
		}
		
		public RequestURLThread(String reqURL, BeaconExtended beaconExtended) {
			super();
			this.reqURL = reqURL;
			this.beaconExtended = beaconExtended;
		}
		
		public void run(){
			Log.e(getName().toString(), "RequestURL !!!!! : "+ reqURL);
			Log.e(getName().toString(), "getJSON START!!!");

			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(reqURL);
			try{
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if(statusCode == 200){
					Log.e(getName().toString(),"getJSON OK!!!");
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(content));
					String line;
					while((line = reader.readLine()) != null){
						builder.append(line);
					}
				} else {
					Log.e(getName().toString(),"Failed to get JSON object");
				}
			}catch(ClientProtocolException e){
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
			
			rawJSON = builder.toString();
			Log.e(getName().toString(), "RawJSON !!!!! : "+ rawJSON);
			JSONParser parser = new JSONParser();
			
			try {
				Object obj = parser.parse(rawJSON);
				JSONObject jsonObject = (JSONObject) obj;

				///////////////////////////////////////////////
				String imgCommonURL = serverURL + "resources/img/";
				beaconExtended.setImgSmall1(BitmapFactory.decodeStream(new URL(imgCommonURL + (String)jsonObject.get("imgSmall1")).openConnection().getInputStream()));
				beaconExtended.setImgBig1(BitmapFactory.decodeStream(new URL(imgCommonURL + (String)jsonObject.get("imgBig1")).openConnection().getInputStream()));
				beaconExtended.setImgBig2(BitmapFactory.decodeStream(new URL(imgCommonURL + (String)jsonObject.get("imgBig2")).openConnection().getInputStream()));
				beaconExtended.setImgBig3(BitmapFactory.decodeStream(new URL(imgCommonURL + (String)jsonObject.get("imgBig3")).openConnection().getInputStream()));
				beaconExtended.setImgMenu(BitmapFactory.decodeStream(new URL(imgCommonURL + (String)jsonObject.get("imgMenu")).openConnection().getInputStream()));
				beaconExtended.setName((String)jsonObject.get("name"));
				beaconExtended.setAddr((String)jsonObject.get("addr"));
				beaconExtended.setPhone((String)jsonObject.get("phone"));
				beaconExtended.setGeoLat((Double)jsonObject.get("geoLat"));
				beaconExtended.setGeoLong((Double)jsonObject.get("geoLong"));
				beaconExtended.setCopyComment((String)jsonObject.get("copyComment"));
				
				Log.e(getName().toString(), "!!!!! " + beaconExtended.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public BeaconExtended getBeaconExtended(){
			return this.beaconExtended;
		}
	}
	
	
}
