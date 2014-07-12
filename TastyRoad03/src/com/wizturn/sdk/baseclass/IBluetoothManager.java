package com.wizturn.sdk.baseclass;

import android.content.Context;
import com.wizturn.sdk.entity.WizTurnBeacons;
import java.util.List;

public abstract interface IBluetoothManager
{
  public abstract void startDiscovery(ScanCompletedCallback paramScanCompletedCallback);

  public abstract void stopDiscovery();

  public abstract void scanCompleted();

  public abstract void destroy();

  public abstract boolean isDiscovering();

  public abstract void start();

  public abstract void stop();

  public abstract Context getContext();

  public static abstract interface ScanCompletedCallback
  {
    public abstract void onScanCompleted(List<WizTurnBeacons> paramList);
  }
}