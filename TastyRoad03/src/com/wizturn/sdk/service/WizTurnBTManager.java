package com.wizturn.sdk.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import com.wizturn.sdk.WizTurnProximityState;
import com.wizturn.sdk.baseclass.IBluetoothManager;
import com.wizturn.sdk.baseclass.IBluetoothManager.ScanCompletedCallback;
import com.wizturn.sdk.entity.WizTurnBeacons;
import com.wizturn.sdk.entity.WizTurnCheckBeacons;
import com.wizturn.sdk.utils.LogUtil;
import com.wizturn.sdk.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class WizTurnBTManager
  implements IBluetoothManager
{
  private ConcurrentHashMap<String, WizTurnBeacons> _discoveredDevice = new ConcurrentHashMap();
  private BluetoothAdapter _btAdapter;
  private static WizTurnDiscoveringAsyncTask _task;
  private Context _context;
  private IBluetoothManager.ScanCompletedCallback _scanCompletedCallback;
  private boolean _isForceBTon;
  private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
  {
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
    {
      WizTurnBeacons beacon = WizTurnBTManager.this.beaconFromLeScan(device, rssi, scanRecord);

      if ((beacon == null) || (!WizTurnCheckBeacons.isWizTurnBeacon(beacon))) {
        LogUtil.i("Device " + device + " is not an pebBLE beacon");
        return;
      }
      LogUtil.d("Device " + device + " is pebBLE beacon");
      WizTurnBTManager.this._discoveredDevice.put(device.getName(), beacon);
    }
  };

  public WizTurnBTManager(Context context)
  {
    this._context = context;
  }

  private boolean turnOnBT() {
    this._btAdapter = BluetoothAdapter.getDefaultAdapter();

    if (this._btAdapter != null) {
      if (!this._btAdapter.isEnabled()) {
        this._isForceBTon = true;
        return this._btAdapter.enable();
      }
      return true;
    }
    return false;
  }

  public WizTurnBeacons beaconFromLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
  {
    String scanRecordAsHex = Utils.toString(scanRecord);
    LogUtil.d("WizTurn scanRecordAsHex " + scanRecordAsHex + " addr:" + device.getAddress());
    for (int i = 0; i < scanRecord.length; i++) {
      int payloadLength = Utils.unsignedByteToInt(scanRecord[i]);
      if ((payloadLength == 0) || (i + 1 >= scanRecord.length))
      {
        break;
      }
      if (Utils.unsignedByteToInt(scanRecord[(i + 1)]) != 255) {
        i += payloadLength;
      } else {
        if (payloadLength == 26) {
          if ((Utils.unsignedByteToInt(scanRecord[(i + 2)]) == 76) && (Utils.unsignedByteToInt(scanRecord[(i + 3)]) == 0) && (Utils.unsignedByteToInt(scanRecord[(i + 4)]) == 2) && (Utils.unsignedByteToInt(scanRecord[(i + 5)]) == 21)) {
            String proximityUUID = String.format("%s-%s-%s-%s-%s", new Object[] { scanRecordAsHex.substring(18, 26), scanRecordAsHex.substring(26, 30), scanRecordAsHex.substring(30, 34), scanRecordAsHex.substring(34, 38), scanRecordAsHex.substring(38, 50) });

            int major = Utils.unsignedByteToInt(scanRecord[(i + 22)]) + Utils.unsignedByteToInt(scanRecord[(i + 23)]) * 256;
            int minor = Utils.unsignedByteToInt(scanRecord[(i + 24)]) + Utils.unsignedByteToInt(scanRecord[(i + 25)]) * 256;
            int measuredPower = scanRecord[(i + 26)];
            double distance = Utils.getDistance(rssi, measuredPower);
            WizTurnProximityState proximity;
           
            if (distance < 0.0D) {
              proximity = WizTurnProximityState.Unknown;
            }
            else
            {
             
              if ((distance >= 0.0D) && (distance <= 0.5D)) {
                proximity = WizTurnProximityState.Immediate;
              }
              else
              {
              
                if ((distance > 0.5D) && (distance <= 3.0D))
                  proximity = WizTurnProximityState.Near;
                else
                  proximity = WizTurnProximityState.Far; 
              }
            }
            LogUtil.i("WizTurn proximityUUID:" + proximityUUID + " major:" + major + " minor:" + minor + " measuredPower:" + measuredPower + " proximity:" + proximity);
            return new WizTurnBeacons(proximityUUID, device.getName(), device.getAddress(), major, minor, measuredPower, rssi, proximity);
          }
          return null;
        }

        return null;
      }
    }

    return null;
  }

  public Context getContext()
  {
    return this._context;
  }

  public void startDiscovery(IBluetoothManager.ScanCompletedCallback scanCompletedCallback)
  {
    turnOnBT();
    this._scanCompletedCallback = scanCompletedCallback;
    this._discoveredDevice.clear();
    if (_task != null) {
      _task.cancel(true);
    }
    _task = new WizTurnDiscoveringAsyncTask(this);
    _task.execute(new Void[0]);
  }

  public void scanCompleted()
  {
    LogUtil.d("BluetoothManager.scanCompleted()" + this._discoveredDevice.size());

    if (this._discoveredDevice.size() > 0) {
      List discoveredDevice = new ArrayList(this._discoveredDevice.values());
      this._scanCompletedCallback.onScanCompleted(discoveredDevice);
      this._discoveredDevice.clear();
    }
  }

  public void start()
  {
    LogUtil.d("BluetoothManager.start()");
    stop();
    if (this._btAdapter != null) {
      this._btAdapter.startLeScan(this.mLeScanCallback);
      LogUtil.d("_btAdapter.startLeScan()");
    }
  }

  public boolean isDiscovering()
  {
    return (this._btAdapter != null) && (this._btAdapter.isDiscovering());
  }

  public void stop()
  {
    LogUtil.d("BluetoothManager.stop()");

    if ((isDiscovering()) && 
      (this._btAdapter != null)) {
      this._btAdapter.cancelDiscovery();
    }

    if (this._btAdapter != null) {
      LogUtil.d("_btAdapter.stopLeScan()");
      this._btAdapter.stopLeScan(this.mLeScanCallback);
    }
  }

  public void destroy()
  {
    stopDiscovery();
    LogUtil.d("BluetoothManager.destroy()");

    if (this._btAdapter != null) {
      LogUtil.d("_btAdapter.stopLeScan()");
      this._btAdapter.stopLeScan(this.mLeScanCallback);
      this._btAdapter = null;
    }
  }

  public void stopDiscovery()
  {
    if (this._isForceBTon) {
      this._btAdapter.disable();
    }
    this._isForceBTon = false;
    LogUtil.d("BluetoothManager.stopDiscovery()");
    if ((_task != null) && (!_task.isCancelled())) {
      _task.cancel(true);
      _task = null;
    }
  }
}