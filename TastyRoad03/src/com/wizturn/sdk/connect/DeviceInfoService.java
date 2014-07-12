package com.wizturn.sdk.connect;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import com.wizturn.sdk.utils.LogUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DeviceInfoService
  implements BluetoothService
{
  private final HashMap<UUID, BluetoothGattCharacteristic> characteristics = new HashMap();

  public void processGattServices(List<BluetoothGattService> services) {
    for (BluetoothGattService service : services)
      if (WizTurnUuid.DEVINFO_SERVICE.equals(service.getUuid())) {
        this.characteristics.put(WizTurnUuid.HARDWARE_VERSION_CHAR, service.getCharacteristic(WizTurnUuid.HARDWARE_VERSION_CHAR));
        this.characteristics.put(WizTurnUuid.FIRMWARE_VERSION_CHAR, service.getCharacteristic(WizTurnUuid.FIRMWARE_VERSION_CHAR));
      }
  }

  public String getFirmwareVersion() {
    LogUtil.d("getFirmwareVersion");
    return this.characteristics.containsKey(WizTurnUuid.FIRMWARE_VERSION_CHAR) ? getStringValue(((BluetoothGattCharacteristic)this.characteristics.get(WizTurnUuid.FIRMWARE_VERSION_CHAR)).getValue()) : null;
  }

  public String getHardwareVersion() {
    LogUtil.d("getHardwareVersion");
    return this.characteristics.containsKey(WizTurnUuid.HARDWARE_VERSION_CHAR) ? getStringValue(((BluetoothGattCharacteristic)this.characteristics.get(WizTurnUuid.HARDWARE_VERSION_CHAR)).getValue()) : null;
  }

  public void update(BluetoothGattCharacteristic characteristic) {
    this.characteristics.put(characteristic.getUuid(), characteristic);
  }

  public Collection<BluetoothGattCharacteristic> getAvailableCharacteristics() {
    List chars = new ArrayList(this.characteristics.values());
    chars.removeAll(Collections.singleton(null));
    return chars;
  }

  private static String getStringValue(byte[] bytes)
  {
    return new String(bytes);
  }
}