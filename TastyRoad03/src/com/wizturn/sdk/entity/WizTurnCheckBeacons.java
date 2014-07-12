package com.wizturn.sdk.entity;

import com.wizturn.sdk.utils.LogUtil;

public class WizTurnCheckBeacons
{
  public static final String WIZTURN_PROXIMITY_UUID = "D5756247-57A2-4344-915D-9599497940A7";

  public static boolean isOriginalWizTurnUuid(WizTurnBeacons beacon)
  {
    return "D5756247-57A2-4344-915D-9599497940A7".equalsIgnoreCase(beacon.getProximityUUID());
  }

  public static boolean isValidName(String name) {
    LogUtil.d("WizTurn 1BTName:" + name.toString());
    if (("pebBLE".equalsIgnoreCase(name)) || ("peb".equalsIgnoreCase(name))) {
      LogUtil.d("WizTurn pebBLE or peb OK");
      return true;
    }
    return false;
  }

  public static boolean isWizTurnBeacon(WizTurnBeacons beacon)
  {
    LogUtil.d("WizTurn 2BTName:" + beacon.getName().toString() + " ProximityUUID:" + beacon.getProximityUUID());
    if ((isValidName(beacon.getName())) && (isOriginalWizTurnUuid(beacon))) {
      LogUtil.d("WizTurn pebBLE OK");
      return true;
    }
    return false;
  }
}