package com.wizturn.sdk.service;

import android.content.Context;
import com.wizturn.sdk.WizTurnControllerDelegate;
import com.wizturn.sdk.WizTurnProximityState;
import com.wizturn.sdk.baseclass.IBluetoothManager;
import com.wizturn.sdk.baseclass.IBluetoothManager.ScanCompletedCallback;
import com.wizturn.sdk.baseclass.IWizTurnController;
import com.wizturn.sdk.entity.WizTurnBeacons;
import com.wizturn.sdk.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class WizTurnController
  implements IWizTurnController
{
  private WizTurnControllerDelegate _wtControllerDelegate;
  private Context _context;
  private IBluetoothManager _btManager;
  private String _useBeacon = "";

  public WizTurnController(Context context) {
    this._context = context;
  }

  public void setWizTurnControllerDelegate(WizTurnControllerDelegate sgControllerDelegate)
  {
    this._wtControllerDelegate = sgControllerDelegate;
  }

  public void init()
  {
    this._btManager = new WizTurnBTManager(this._context);
  }

  public void start()
  {
    this._btManager.startDiscovery(new IBluetoothManager.ScanCompletedCallback()
    {
      public void onScanCompleted(List<WizTurnBeacons> discoveredDevice) {
        if (discoveredDevice == null) {
          return;
        }
        if (WizTurnController.this._wtControllerDelegate != null) {
          List lzSSNAME = new ArrayList();
          List lzRSSI = new ArrayList();

          for (int i = 0; i < discoveredDevice.size(); i++) {
            lzSSNAME.add(((WizTurnBeacons)discoveredDevice.get(i)).getName());
            lzRSSI.add(Integer.valueOf(((WizTurnBeacons)discoveredDevice.get(i))._rssi));
            Double distance = Double.valueOf(Utils.getDistance(((WizTurnBeacons)discoveredDevice.get(i)).getRssi(), ((WizTurnBeacons)discoveredDevice.get(i)).getMeasuredPower()));

            if (WizTurnController.this._useBeacon.equals(((WizTurnBeacons)discoveredDevice.get(i))._macAddr)) {
              if (distance.doubleValue() < 0.0D)
                WizTurnController.this._wtControllerDelegate.onGetProximity(WizTurnController.this, WizTurnProximityState.Unknown);
              else if ((distance.doubleValue() > 0.0D) && (distance.doubleValue() < 0.5D))
                WizTurnController.this._wtControllerDelegate.onGetProximity(WizTurnController.this, WizTurnProximityState.Immediate);
              else if ((distance.doubleValue() > 0.5D) && (distance.doubleValue() < 3.0D))
                WizTurnController.this._wtControllerDelegate.onGetProximity(WizTurnController.this, WizTurnProximityState.Near);
              else {
                WizTurnController.this._wtControllerDelegate.onGetProximity(WizTurnController.this, WizTurnProximityState.Far);
              }
            }
          }

          WizTurnController.this._wtControllerDelegate.onGetRSSI(WizTurnController.this, lzSSNAME, lzRSSI);
          WizTurnController.this._wtControllerDelegate.onGetDeviceList(WizTurnController.this, discoveredDevice);
        }

        if (WizTurnController.this._wtControllerDelegate == null)
          return;
      }
    });
  }

  public void stop()
  {
    this._btManager.stopDiscovery();
  }

  public void destroy()
  {
    this._btManager.destroy();
  }

  public void setUseBeaconMac(String beacon)
  {
    this._useBeacon = beacon;
  }
}