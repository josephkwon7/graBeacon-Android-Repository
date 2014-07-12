package com.wizturn.sdk.utils;

import android.util.Log;
import com.wizturn.sdk.WizTurnConfig;

public class LogUtil
{
  public static void d(String message)
  {
    if (WizTurnConfig.bDebug)
      Log.d("WizTurn", message);
  }

  public static void e(String message, Exception e) {
    if (WizTurnConfig.bDebug)
      Log.e("WizTurn", message, e);
  }

  public static void i(String message) {
    if (WizTurnConfig.bDebug)
      Log.i("WizTurn", message);
  }

  public static void v(String message) {
    if (WizTurnConfig.bDebug)
      Log.v("WizTurn", message);
  }
}