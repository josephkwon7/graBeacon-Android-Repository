package com.wizturn.sdk;

import com.wizturn.sdk.baseclass.IWizTurnController;
import com.wizturn.sdk.entity.WizTurnBeacons;
import java.util.List;

public abstract interface WizTurnDelegate
{
  public abstract void onGetRSSI(IWizTurnController paramIWizTurnController, List<String> paramList, List<Integer> paramList1);

  public abstract void onGetDeviceList(IWizTurnController paramIWizTurnController, List<WizTurnBeacons> paramList);

  public abstract void onGetProximity(IWizTurnController paramIWizTurnController, WizTurnProximityState paramWizTurnProximityState);
}