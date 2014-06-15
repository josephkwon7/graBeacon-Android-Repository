package com.dwf.tastyroad;

import android.graphics.Bitmap;

import com.wizturn.sdk.WizTurnProximityState;
import com.wizturn.sdk.entity.WizTurnBeacons;

public class BeaconExtended extends WizTurnBeacons {

	///Field
	private int resId;
	private String name;
	private String addr;
	private String phone;
	private String licenseNo;
	private double geoLat;
	private double geoLong;
	private String copyComment;
	private Bitmap imgSmall1;
	private Bitmap imgMapMarker;
	private Bitmap imgBig1;
	private Bitmap imgBig2;
	private Bitmap imgBig3;
	private Bitmap imgBig4;
	private Bitmap imgMenu;

	///Constructor
	public BeaconExtended(String proximityUUID, String name, String macAddr,
			int major, int minor, int measuredPower, int rssi,
			WizTurnProximityState proximity) {
		super(proximityUUID, name, macAddr, major, minor, measuredPower, rssi,
				proximity);
	}

	///Getter/Setter
	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	public double getGeoLat() {
		return geoLat;
	}

	public void setGeoLat(double geoLat) {
		this.geoLat = geoLat;
	}

	public double getGeoLong() {
		return geoLong;
	}

	public void setGeoLong(double geoLong) {
		this.geoLong = geoLong;
	}

	public String getCopyComment() {
		return copyComment;
	}

	public void setCopyComment(String copyComment) {
		this.copyComment = copyComment;
	}

	public Bitmap getImgSmall1() {
		return imgSmall1;
	}

	public void setImgSmall1(Bitmap imgSmall1) {
		this.imgSmall1 = imgSmall1;
	}

	public Bitmap getImgMapMarker() {
		return imgMapMarker;
	}

	public void setImgMapMarker(Bitmap imgMapMarker) {
		this.imgMapMarker = imgMapMarker;
	}

	public Bitmap getImgBig1() {
		return imgBig1;
	}

	public void setImgBig1(Bitmap imgBig1) {
		this.imgBig1 = imgBig1;
	}

	public Bitmap getImgBig2() {
		return imgBig2;
	}

	public void setImgBig2(Bitmap imgBig2) {
		this.imgBig2 = imgBig2;
	}

	public Bitmap getImgBig3() {
		return imgBig3;
	}

	public void setImgBig3(Bitmap imgBig3) {
		this.imgBig3 = imgBig3;
	}

	public Bitmap getImgBig4() {
		return imgBig4;
	}

	public void setImgBig4(Bitmap imgBig4) {
		this.imgBig4 = imgBig4;
	}

	public Bitmap getImgMenu() {
		return imgMenu;
	}

	public void setImgMenu(Bitmap imgMenu) {
		this.imgMenu = imgMenu;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("BeaconExtended [resId=");
		builder.append(resId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", addr=");
		builder.append(addr);
		builder.append(", phone=");
		builder.append(phone);
		builder.append(", licenseNo=");
		builder.append(licenseNo);
		builder.append(", geoLat=");
		builder.append(geoLat);
		builder.append(", geoLong=");
		builder.append(geoLong);
		builder.append(", copyComment=");
		builder.append(copyComment);
		builder.append(", imgSmall1=");
		builder.append(imgSmall1);
		builder.append(", imgMapMarker=");
		builder.append(imgMapMarker);
		builder.append(", imgBig1=");
		builder.append(imgBig1);
		builder.append(", imgBig2=");
		builder.append(imgBig2);
		builder.append(", imgBig3=");
		builder.append(imgBig3);
		builder.append(", imgBig4=");
		builder.append(imgBig4);
		builder.append(", imgMenu=");
		builder.append(imgMenu);
		builder.append(", proximityUUID=");
		builder.append(super._proximityUUID);
		
		builder.append("]");
		
		
		return builder.toString();
	}
	
}
