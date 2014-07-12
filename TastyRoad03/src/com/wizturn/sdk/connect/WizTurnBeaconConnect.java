package com.wizturn.sdk.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import com.wizturn.sdk.entity.WizTurnBeacons;
import com.wizturn.sdk.utils.LogUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WizTurnBeaconConnect
{
  public static Set<Integer> ALLOWED_POWER_LEVELS = Collections.unmodifiableSet(new HashSet(Arrays.asList(new Integer[] { Integer.valueOf(-23), Integer.valueOf(-19), Integer.valueOf(-16), Integer.valueOf(-12), Integer.valueOf(-9), Integer.valueOf(-5), Integer.valueOf(0), 
    Integer.valueOf(4) })));
  private final Context _context;
  private final BluetoothDevice _device;
  private final BluetoothGattCallback _bluetoothGattCallback;
  private final ConnectionCallback _connectionCallback;
  private final Runnable _timeoutHandler;
  private final Handler _handler;
  private final SettingService _settingService;
  private final DeviceInfoService _deviceInfoService;
  private LinkedList<BluetoothGattCharacteristic> _toFetch;
  private BluetoothGatt _bluetoothGatt;
  private boolean _didReadCharacteristics;

  public WizTurnBeaconConnect(Context context, WizTurnBeacons beacon, ConnectionCallback connectionCallback)
  {
    this._context = context;
    this._device = deviceFromBeacon(beacon);
    this._connectionCallback = connectionCallback;
    this._bluetoothGattCallback = createBluetoothGattCallback();
    this._settingService = new SettingService();
    this._deviceInfoService = new DeviceInfoService();
    this._timeoutHandler = createTimeoutHandler();
    this._toFetch = new LinkedList();
    this._handler = new Handler();
  }

  private BluetoothDevice deviceFromBeacon(WizTurnBeacons beacon) {
    BluetoothManager bluetoothManager = (BluetoothManager)this._context.getSystemService("bluetooth");
    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
    return bluetoothAdapter.getRemoteDevice(beacon.getMacAddress());
  }

  public void connectBeacon()
  {
    LogUtil.d("Trying to connect to GATT");
    this._didReadCharacteristics = false;
    LogUtil.d("GATT BLE addr: " + this._device.getAddress().toString());
    this._bluetoothGatt = this._device.connectGatt(this._context, false, this._bluetoothGattCallback);
    this._handler.postDelayed(this._timeoutHandler, TimeUnit.SECONDS.toMillis(10L));
  }

  public void close()
  {
    if (this._bluetoothGatt != null) {
      this._bluetoothGatt.disconnect();
      this._bluetoothGatt.close();
    }
    this._handler.removeCallbacks(this._timeoutHandler);
  }

  public boolean isConnected()
  {
    BluetoothManager bluetoothManager = (BluetoothManager)this._context.getSystemService("bluetooth");
    int connectionState = bluetoothManager.getConnectionState(this._device, 7);
    LogUtil.d("Gatt BLE isConnected: " + connectionState);
    return connectionState == 2;
  }

  private BluetoothGattCallback createBluetoothGattCallback()
  {
    return new BluetoothGattCallback() {
      public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        LogUtil.d("Gatt BLE onConnectionStateChange" + newState);
        if (newState == 2) {
          LogUtil.d("Gatt BLE Connected to GATT server, discovering services: " + gatt.discoverServices());
        } else if (newState == 0) {
          LogUtil.d("Gatt BLE Disconnected from GATT server");
          WizTurnBeaconConnect.this.notifyDisconnected();
        }
      }

      public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        LogUtil.d("Gatt BLE onCharacteristicRead");
        if (status == 0) {
          WizTurnBeaconConnect.this.readCharacteristics(gatt);
        } else {
          LogUtil.d("Failed to read characteristic");
          WizTurnBeaconConnect.this._toFetch.clear();
          WizTurnBeaconConnect.this.notifyConnectionError();
        }
      }

      public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        LogUtil.d("Gatt BLE onCharacteristicWrite status:" + status);
      }

      public void onServicesDiscovered(BluetoothGatt gatt, int status)
      {
        LogUtil.d("Gatt BLE onServicesDiscovered");
        if (status == 0) {
          LogUtil.d("Gatt BLE Services discovered");
          WizTurnBeaconConnect.this.processDiscoveredServices(gatt.getServices());
//          WizTurnBeaconConnect.this.onConnectionCompleted(gatt);
        } else {
          LogUtil.d("Gatt BLE Could not discover services, status: " + status);
          WizTurnBeaconConnect.this.notifyConnectionError();
        }
      } } ;
  }

  private Runnable createTimeoutHandler() {
    return new Runnable() {
      public void run() {
        LogUtil.d("Timeout while authenticating");
        if (!WizTurnBeaconConnect.this._didReadCharacteristics) {
          if (WizTurnBeaconConnect.this._bluetoothGatt != null) {
            WizTurnBeaconConnect.this._bluetoothGatt.disconnect();
            WizTurnBeaconConnect.this._bluetoothGatt.close();
          }
          WizTurnBeaconConnect.this.notifyConnectionError();
        }
      } } ;
  }

  private void notifyConnectionError() {
    this._handler.removeCallbacks(this._timeoutHandler);
    this._connectionCallback.onConnectionError();
  }

  private void notifyDisconnected() {
    this._connectionCallback.onDisconnected();
  }

  private void processDiscoveredServices(List<BluetoothGattService> services) {
    LogUtil.d("Gatt BLE processDiscoveredServices");
    this._settingService.processGattServices(services);
    this._deviceInfoService.processGattServices(services);

    this._toFetch.clear();
    this._toFetch.addAll(this._settingService.getAvailableCharacteristics());
    this._toFetch.addAll(this._deviceInfoService.getAvailableCharacteristics());
  }

//  private void onConnectionCompleted(BluetoothGatt gatt) {
//    this._handler.postDelayed(new Runnable(gatt) {
//      public void run() {
//        WizTurnBeaconConnect.this.readCharacteristics(this.val$gatt);
//      }
//    }
//    , 500L);
//  }

  private void readCharacteristics(BluetoothGatt gatt) {
    LogUtil.d("Gatt BLE readCharacteristics");
    if (!this._toFetch.isEmpty())
      gatt.readCharacteristic((BluetoothGattCharacteristic)this._toFetch.poll());
    else if (this._bluetoothGatt != null)
      onConnected();
  }

  private void onConnected() {
    LogUtil.d("Gatt BLE Authenticated to beacon");
    this._handler.removeCallbacks(this._timeoutHandler);
    this._didReadCharacteristics = true;
    this._connectionCallback.onConnected(new BeaconCharacteristics(this._settingService, this._deviceInfoService));
  }

  public static class BeaconCharacteristics
  {
    private final Integer _batteryPercent;
    private final Integer _txPower;
    private final String _advertisingIntervalMillis;
    private final String _firmwareVersion;
    private final String _hardwareVersion;

    public BeaconCharacteristics(SettingService settingService, DeviceInfoService deviceInfoService)
    {
      LogUtil.d("Gatt BLE BeaconCharacteristics");
      this._txPower = Integer.valueOf(settingService.getTxPower());
      this._batteryPercent = settingService.getBatteryPercent();
      this._advertisingIntervalMillis = settingService.getAdvertisingIntervalMillis();
      this._firmwareVersion = deviceInfoService.getFirmwareVersion();
      this._hardwareVersion = deviceInfoService.getHardwareVersion();

      LogUtil.d("Gatt BLE _txPower:" + this._txPower + " _batteryPercent:" + String.valueOf(this._batteryPercent) + " _advertisingIntervalMillis:" + String.valueOf(this._advertisingIntervalMillis));
      LogUtil.d("Gatt BLE _firmwareVersion:" + this._firmwareVersion + " _hardwareVersion:" + this._hardwareVersion);
    }

    public Integer getBatteryPercent()
    {
      return this._batteryPercent;
    }

    public Integer getBroadcastingPower()
    {
      return this._txPower;
    }

    public String getAdvertisingIntervalMillis()
    {
      return this._advertisingIntervalMillis;
    }

    public String getSoftwareVersion()
    {
      return this._firmwareVersion;
    }

    public String getHardwareVersion()
    {
      return this._hardwareVersion;
    }
  }

  public static abstract interface ConnectionCallback
  {
    public abstract void onConnected(WizTurnBeaconConnect.BeaconCharacteristics paramBeaconCharacteristics);

    public abstract void onConnectionError();

    public abstract void onDisconnected();
  }
}