package com.example.wizturnbeacon.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wizturnbeacon.MainActivity;
import com.example.wizturnbeacon.R;
import com.wizturn.sdk.entity.WizTurnBeacons;


/*
	�о�� BeaconList�� �����ִ� ArrayAdapter.
*/


public class WizTurnBeaconListAdapter extends ArrayAdapter{
	//layoutInflater : ���̾ƿ��� �°� �����͸� ä���ִ� ����.
	LayoutInflater inflater = null;
	ArrayList<WizTurnBeacons> wizTrunBeacon_items;
	//���� parameter : ���ؽ�Ʈ����, layout_scanlist.xml�� TextList����, textList������ ������ arrayList
	public WizTurnBeaconListAdapter(Context context, int textViewResourceId,
			ArrayList<WizTurnBeacons> mList) {
		super(context, textViewResourceId,  mList);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		wizTrunBeacon_items = mList;
	}
	
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
	
	//���̾ƿ��� �°� �����͸� ä���ִ� �޼ҵ�
	
	//���⿡ �츮 �̹����� �����ϸ� ��.
	public View getView(int position, View v, ViewGroup parent) {
		if(v==null){
			v = inflater.inflate(R.layout.array_scanlist, null);
		}
		/*
		TextView mSSID = (TextView)v.findViewById(R.id.scanList_SSID);
		TextView mUUID = (TextView)v.findViewById(R.id.scanList_UUID);
		TextView mMacAddr= (TextView)v.findViewById(R.id.scanList_MacAddr);
		TextView mMajor = (TextView)v.findViewById(R.id.scanList_Major);
		TextView mMinor= (TextView)v.findViewById(R.id.scanList_Minor);
		
		mSSID.setText("SSID: " + wizTrunBeacon_items.get(position).getName());
		mMacAddr.setText("MacAddr: " + wizTrunBeacon_items.get(position).getMacAddress());
		mUUID.setText("UUID: " + wizTrunBeacon_items.get(position).getProximityUUID());
		mMajor.setText("Major: " + Integer.toString(wizTrunBeacon_items.get(position).getMajor()));
		mMinor.setText("Minor: " + Integer.toString(wizTrunBeacon_items.get(position).getMinor()));
		*/
		
		
		//String reqURL = "http://192.168.200.63:8080/SpringMVC2/do/beacon/";
		String req1 = "http://192.168.200.27:8080/tastyroad/beacon/Thumbnail/";
		String req2 = wizTrunBeacon_items.get(position).getProximityUUID()+"/";
		String req3 = wizTrunBeacon_items.get(position).getMajor()+"/";
		String req4 = wizTrunBeacon_items.get(position).getMinor()+"";
		String reqURL = req1 + req2 + req3 + req4;
		Log.e(MainActivity.class.toString(), "RequestURL !!!!! : "+ reqURL);
		
		String rawJSON = getJSON(reqURL);
		
		Log.e(MainActivity.class.toString(), "RawJSON !!!!! : "+ rawJSON);

		JSONParser parser = new JSONParser();
		
		
		try {
			Object obj = parser.parse(rawJSON);
			JSONObject jsonObject = (JSONObject) obj;
		 	String thumbURL = (String) jsonObject.get("imgSmall1");
			Log.e(MainActivity.class.toString(), "URL !!!!! : "+thumbURL);
			
			URL url = new URL(thumbURL);
			
			Bitmap thumbImg = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			//TextView mSSID = (TextView)v.findViewById(R.id.scanList_SSID);
			//mSSID.setText("SSID: " + thumbURL);
			ImageView thumbView = (ImageView)v.findViewById(R.id.imageView1);
			thumbView.setImageBitmap(thumbImg);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 	
		
		return v;
		
	}

	public String getJSON(String address){
		Log.e(MainActivity.class.toString(),"getJSON START!!!");

		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(address);
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
		return builder.toString();
	}
		
	
	
	
}
