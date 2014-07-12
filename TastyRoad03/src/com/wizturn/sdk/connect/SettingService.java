package com.wizturn.sdk.connect;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import com.wizturn.sdk.utils.LogUtil;
import com.wizturn.sdk.utils.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SettingService
  implements BluetoothService
{
  private final HashMap<UUID, BluetoothGattCharacteristic> characteristics = new HashMap();

  private String[] advertizing = { "0.2", "0.3", "0.5", "1", "2", "5", "10", "15", "20" };

  public void processGattServices(List<BluetoothGattService> services) {
    for (BluetoothGattService service : services)
      if (WizTurnUuid.SETTING_SERVICE.equals(service.getUuid())) {
        this.characteristics.put(WizTurnUuid.UUID_FIRST_CHAR, service.getCharacteristic(WizTurnUuid.UUID_FIRST_CHAR));
        this.characteristics.put(WizTurnUuid.UUID_SECOND_CHAR, service.getCharacteristic(WizTurnUuid.UUID_SECOND_CHAR));
        this.characteristics.put(WizTurnUuid.MAJOR_CHAR, service.getCharacteristic(WizTurnUuid.MAJOR_CHAR));
        this.characteristics.put(WizTurnUuid.MINOR_CHAR, service.getCharacteristic(WizTurnUuid.MINOR_CHAR));
        this.characteristics.put(WizTurnUuid.BATTERY_CHAR, service.getCharacteristic(WizTurnUuid.BATTERY_CHAR));
        this.characteristics.put(WizTurnUuid.TXPOWER_CHAR, service.getCharacteristic(WizTurnUuid.TXPOWER_CHAR));
        this.characteristics.put(WizTurnUuid.MEASURED_POWER_CHAR, service.getCharacteristic(WizTurnUuid.MEASURED_POWER_CHAR));
        this.characteristics.put(WizTurnUuid.ADVERTISING_INTERVAL_CHAR, service.getCharacteristic(WizTurnUuid.ADVERTISING_INTERVAL_CHAR));
      }
  }

  public boolean hasCharacteristic(UUID uuid) {
    return this.characteristics.containsKey(uuid);
  }

  public Integer getBatteryPercent()
  {
    String batt = Utils.toString(((BluetoothGattCharacteristic)this.characteristics.get(WizTurnUuid.BATTERY_CHAR)).getValue());
    LogUtil.d("batt:" + batt);
    return this.characteristics.containsKey(WizTurnUuid.BATTERY_CHAR) ? Integer.valueOf(getUnsignedByte(((BluetoothGattCharacteristic)this.characteristics.get(WizTurnUuid.BATTERY_CHAR)).getValue())) : null;
  }

  public Byte getMeasuredPower() {
    return this.characteristics.containsKey(WizTurnUuid.MEASURED_POWER_CHAR) ? Byte.valueOf(((BluetoothGattCharacteristic)this.characteristics.get(WizTurnUuid.MEASURED_POWER_CHAR)).getValue()[0]) : null;
  }

  public int getTxPower() {
    int power = 0;
    if (this.characteristics.containsKey(WizTurnUuid.TXPOWER_CHAR)) {
      power = Integer.valueOf(getUnsignedByte(((BluetoothGattCharacteristic)this.characteristics.get(WizTurnUuid.TXPOWER_CHAR)).getValue())).intValue();
      return convertPower(power);
    }
    return 0;
  }

  public String getAdvertisingIntervalMillis()
  {
    int interval = 0;
    if (this.characteristics.containsKey(WizTurnUuid.ADVERTISING_INTERVAL_CHAR)) {
      interval = Integer.valueOf(getUnsignedByte(((BluetoothGattCharacteristic)this.characteristics.get(WizTurnUuid.ADVERTISING_INTERVAL_CHAR)).getValue())).intValue();
      return this.advertizing[interval];
    }
    return "";
  }

  public void update(BluetoothGattCharacteristic characteristic)
  {
    this.characteristics.put(characteristic.getUuid(), characteristic);
  }

  public Collection<BluetoothGattCharacteristic> getAvailableCharacteristics() {
    List chars = new ArrayList(this.characteristics.values());
    chars.removeAll(Collections.singleton(null));
    return chars;
  }

  private static int getUnsignedByte(byte[] bytes) {
    return unsignedByteToInt(bytes[0]);
  }

  private static int getUnsignedInt16(byte[] bytes) {
    return unsignedByteToInt(bytes[0]) + (unsignedByteToInt(bytes[1]) << 8);
  }

  private static int unsignedByteToInt(byte value)
  {
    return value & 0xFF;
  }

  private int convertPower(int power) {
    int cPower = 0;
    switch (power) {
    case 1:
      cPower = -23;
      break;
    case 2:
    case 3:
      cPower = -19;
      break;
    case 4:
    case 5:
      cPower = -16;
      break;
    case 6:
    case 7:
      cPower = -12;
      break;
    case 8:
    case 9:
      cPower = -9;
      break;
    case 10:
    case 11:
      cPower = -5;
      break;
    case 12:
    case 13:
    case 14:
      cPower = 0;
      break;
    case 15:
    case 16:
      cPower = 4;
    }

    return cPower;
  }
}