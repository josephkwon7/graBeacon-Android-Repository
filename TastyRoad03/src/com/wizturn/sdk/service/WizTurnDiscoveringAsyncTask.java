package com.wizturn.sdk.service;

import android.os.AsyncTask;
import com.wizturn.sdk.baseclass.IBluetoothManager;

class WizTurnDiscoveringAsyncTask extends AsyncTask<Void, Void, Void>
{
  private final float GATHERING_INTERVAL = 1000.0F;
  private IBluetoothManager _btManager;

  public WizTurnDiscoveringAsyncTask(IBluetoothManager btManager)
  {
    this._btManager = btManager;
  }

  protected void onPreExecute()
  {
    super.onPreExecute();
  }

  protected Void doInBackground(Void[] voids)
  {
    while (!isCancelled()) {
      this._btManager.start();
      try
      {
        Thread.sleep(1000L);

        publishProgress(new Void[0]);

        if (!isCancelled());
      }
      catch (InterruptedException localInterruptedException) {
        this._btManager.stop();
      }
    }
    return null;
  }

  protected void onProgressUpdate(Void[] voids)
  {
    super.onProgressUpdate(voids);
    this._btManager.scanCompleted();
  }

  protected void onPostExecute(Void nothing)
  {
    super.onPostExecute(nothing);
  }

  protected void onCancelled()
  {
    super.onCancelled();
    this._btManager.stop();
  }
}