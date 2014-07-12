package com.wizturn.sdk.baseclass;

import com.wizturn.sdk.WizTurnControllerDelegate;

public abstract interface IWizTurnController
{
  public abstract void setWizTurnControllerDelegate(WizTurnControllerDelegate paramWizTurnControllerDelegate);

  public abstract void init();

  public abstract void start();

  public abstract void stop();

  public abstract void destroy();

  public abstract void setUseBeaconMac(String paramString);
}