/* Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sjtu.omnilab.omniperf.core;

import sjtu.omnilab.omniperf.R;
import sjtu.omnilab.omniperf.utils.Logger;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * A broadcast receiver that starts the Speedometer service upon the BOOT_COMPLETED event.
 */
public class WatchdogBootReceiver extends BroadcastReceiver {

  @Override
  public final void onReceive(Context context, Intent intent) {
    Logger.i("Boot intent received.");
    Intent serviceIntent = new Intent(context, MeasurementSchedulerService.class);
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    
    if (prefs.getBoolean(context.getString(R.string.startOnBootPrefKey),
        Config.DEFAULT_START_ON_BOOT)) {
      Logger.i("Starting MeasurementScheduler from watchdog");
      context.startService(serviceIntent);
    }
  }
}
