package com.wizturn.sdk.connect;

import android.bluetooth.BluetoothGattCharacteristic;

public abstract interface BluetoothService
{
  public abstract void update(BluetoothGattCharacteristic paramBluetoothGattCharacteristic);
}