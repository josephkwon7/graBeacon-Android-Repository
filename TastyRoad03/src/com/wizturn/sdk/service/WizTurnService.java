package com.wizturn.sdk.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.wizturn.sdk.WizTurnDelegate;
import com.wizturn.sdk.WizTurnManager;
import com.wizturn.sdk.utils.LogUtil;

public class WizTurnService extends Service
{
  private static WizTurnManager _wizturnMgr;
  private IBinder _localBinder = new LocalBinder();

  public void onCreate()
  {
    LogUtil.i("WizTurnService.onCreate()");
    super.onCreate();
    _wizturnMgr = WizTurnManager.sharedInstance(getApplicationContext());
  }

  public int onStartCommand(Intent intent, int flags, int startId)
  {
    LogUtil.i("WizTurnService.onStartCommand()");
    if (!_wizturnMgr.isStarted())
      try {
        _wizturnMgr.startController();
      } catch (Exception ie) {
        LogUtil.e("_wizturnMgr.start() Error : ", ie);
      }
    else {
      LogUtil.i("WizTurnService is already started.");
    }
    return super.onStartCommand(intent, flags, startId);
  }

  public void onDestroy()
  {
    LogUtil.i("WizTurnService.onDestroy()");
    _wizturnMgr.destroy();
    super.onDestroy();
  }

  public IBinder onBind(Intent arg0)
  {
    LogUtil.i("WizTurnService.onBind");
    return this._localBinder;
  }

  public void setWizTurnDelegate(WizTurnDelegate wizTurnDelegate) {
    LogUtil.i("WizTurnService.setWiTurnDelegate()");
    _wizturnMgr.setWizTurnDelegate(wizTurnDelegate);
  }

  public boolean isStarted() {
    return _wizturnMgr.isStarted();
  }

  public boolean start() {
    return _wizturnMgr.startController();
  }

  public void stop() {
    _wizturnMgr.stopController();
  }

  public class LocalBinder extends Binder
  {
    public LocalBinder()
    {
    }

    public WizTurnService getServerInstance()
    {
      LogUtil.i("LocalBinder.getServerInstance()");
      return WizTurnService.this;
    }
  }
}