package com.wizturn.sdk.entity;

import com.wizturn.sdk.WizTurnProximityState;
import com.wizturn.sdk.utils.Utils;

public class WizTurnBeacons
{
  public static float WEIGHT_CONST;
  public String _proximityUUID;
  public String _name;
  public String _macAddr;
  public int _major;
  public int _minor;
  public int _measuredPower;
  public int _rssi;
  public WizTurnProximityState _proximity;

  public WizTurnBeacons(String proximityUUID, String name, String macAddr, int major, int minor, int measuredPower, int rssi, WizTurnProximityState proximity)
  {
    this._proximityUUID = Utils.normalizeProximityUUID(proximityUUID);
    this._name = name;
    this._macAddr = macAddr;
    this._major = major;
    this._minor = minor;
    this._measuredPower = measuredPower;
    this._rssi = rssi;
    this._proximity = proximity;
  }

  public String getProximityUUID()
  {
    return this._proximityUUID;
  }

  public String getName()
  {
    return this._name;
  }

  public String getMacAddress()
  {
    return this._macAddr;
  }

  public int getMajor()
  {
    return this._major;
  }

  public int getMinor()
  {
    return this._minor;
  }

  public int getMeasuredPower()
  {
    return this._measuredPower;
  }

  public double getDistance()
  {
    return Utils.getDistance(this._rssi, this._measuredPower);
  }

  public float getRssi()
  {
    return this._rssi;
  }

  public void setRssi(int rssi) {
    this._rssi = rssi;
  }

  public WizTurnProximityState getProximity()
  {
    return this._proximity;
  }
}