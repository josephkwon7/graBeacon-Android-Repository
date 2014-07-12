package com.wizturn.sdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import com.wizturn.sdk.baseclass.IWizTurnController;
import com.wizturn.sdk.entity.WizTurnBeacons;
import com.wizturn.sdk.service.WizTurnController;
import com.wizturn.sdk.service.WizTurnService;
import com.wizturn.sdk.utils.LogUtil;
import java.util.List;

public class WizTurnManager
{
  private static volatile WizTurnManager _instance;
  private Context _context;
  private IWizTurnController _wtController;
  private WizTurnDelegate _wtDelegate;
  private boolean _isStarted;

  public static WizTurnManager sharedInstance(Context context)
  {
    if (_instance == null) {
      synchronized (WizTurnManager.class) {
        if (_instance == null) {
          _instance = new WizTurnManager(context);
        }
      }
    }
    return _instance;
  }

  private WizTurnManager(Context context) {
    LogUtil.i("WizTurnManager init()");
    this._context = context;
  }

  public void setInitController()
  {
    this._wtController = new WizTurnController(this._context);
    this._wtController.init();
  }

  public void setWizTurnDelegate(WizTurnDelegate wtDelegate)
  {
    this._wtDelegate = wtDelegate;
    if (this._wtController == null) {
      return;
    }
    this._wtController.setWizTurnControllerDelegate(new WizTurnControllerDelegate()
    {
      public void onGetRSSI(IWizTurnController sender, List<String> lzSSID, List<Integer> lzRSSI)
      {
        WizTurnManager.this._wtDelegate.onGetRSSI(sender, lzSSID, lzRSSI);
      }

      public void onGetDeviceList(IWizTurnController sender, List<WizTurnBeacons> discoveredDevice)
      {
        WizTurnManager.this._wtDelegate.onGetDeviceList(sender, discoveredDevice);
      }

      public void onGetProximity(IWizTurnController sender, WizTurnProximityState proximity)
      {
        WizTurnManager.this._wtDelegate.onGetProximity(sender, proximity);
      }
    });
  }

  public boolean startController()
  {
    if (this._isStarted)
      return true;
    try
    {
      if (this._wtController != null)
        this._wtController.start();
    }
    catch (Exception ioe) {
      LogUtil.e("Error on starting controller.", ioe);
      return false;
    }

    this._isStarted = true;
    return this._isStarted;
  }

  public void stopController()
  {
    if (this._wtController != null) {
      this._wtController.stop();
    }
    this._isStarted = false;
  }

  public boolean isStarted()
  {
    return this._isStarted;
  }

  public void setUseBeaconMac(String beacon)
  {
    this._wtController.setUseBeaconMac(beacon);
  }

  public void destroy()
  {
    try
    {
      if (this._wtController != null) {
        this._wtController.stop();
        this._wtController.destroy();
      }
    } catch (Exception e) {
      LogUtil.e("WizTurnManager.destroy", e);
    }
  }

  public boolean hasBluetooth()
  {
    return this._context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
  }

  public boolean isBluetoothEnabled()
  {
    if (!checkPermissionsAndService()) {
      LogUtil.d("AndroidManifest.xml does not contain android.permission.BLUETOOTH or android.permission.BLUETOOTH_ADMIN permissions. BeaconService may be also not declared in AndroidManifest.xml.");
      return false;
    }
    BluetoothManager bluetoothManager = (BluetoothManager)this._context.getSystemService("bluetooth");
    BluetoothAdapter adapter = bluetoothManager.getAdapter();
    return (adapter != null) && (adapter.isEnabled());
  }

  private boolean checkPermissionsAndService() {
    PackageManager pm = this._context.getPackageManager();
    int bluetoothPermission = pm.checkPermission("android.permission.BLUETOOTH", this._context.getPackageName());
    int bluetoothAdminPermission = pm.checkPermission("android.permission.BLUETOOTH_ADMIN", this._context.getPackageName());

    Intent intent = new Intent(this._context, WizTurnService.class);
    List resolveInfo = pm.queryIntentServices(intent, 65536);

    return (bluetoothPermission == 0) && (bluetoothAdminPermission == 0) && (resolveInfo.size() > 0);
  }
}