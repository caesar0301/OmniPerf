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

package sjtu.omnilab.omniperf.activities;

import sjtu.omnilab.omniperf.R;
import sjtu.omnilab.omniperf.core.Config;
import sjtu.omnilab.omniperf.core.UpdateIntent;
import sjtu.omnilab.omniperf.utils.AccountSelector;
import sjtu.omnilab.omniperf.utils.Logger;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Activity that handles user preferences
 */
public class OmniperfPreferenceActivity extends PreferenceActivity {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preference);
    
    Preference intervalPref = findPreference(getString(R.string.checkinIntervalPrefKey));
    Preference batteryPref = findPreference(getString(R.string.batteryMinThresPrefKey));
    
    /* This should never occur. */
    if (intervalPref == null || batteryPref == null) {
      Logger.w("Cannot find some of the preferences");
      Toast.makeText(OmniperfPreferenceActivity.this, 
        getString(R.string.menuInitializationExceptionToast), Toast.LENGTH_LONG).show();
      return;
    }
    
    OnPreferenceChangeListener prefChangeListener = new OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        String prefKey = preference.getKey();
        if (prefKey.compareTo(getString(R.string.checkinIntervalPrefKey)) == 0) {
          try {
            Integer val = Integer.parseInt((String) newValue);
            if (val <= 0 || val > 24) {
              Toast.makeText(OmniperfPreferenceActivity.this,
                  getString(R.string.invalidCheckinIntervalToast), Toast.LENGTH_LONG).show();
              return false;
            }
            return true;
          } catch (ClassCastException e) {
            Logger.e("Cannot cast checkin interval preference value to Integer");
            return false;
          } catch (NumberFormatException e) {
            Logger.e("Cannot cast checkin interval preference value to Integer");
            return false;
          }
        } else if (prefKey.compareTo(getString(R.string.batteryMinThresPrefKey)) == 0) {
          try {
            Integer val = Integer.parseInt((String) newValue);
            if (val < 0 || val > 100) {
              Toast.makeText(OmniperfPreferenceActivity.this,
                  getString(R.string.invalidBatteryToast), Toast.LENGTH_LONG).show();
              return false;
            }
            return true;
          } catch (ClassCastException e) {
            Logger.e("Cannot cast battery preference value to Integer");
            return false;
          } catch (NumberFormatException e) {
            Logger.e("Cannot cast battery preference value to Integer");
            return false;
          }
        }
        return true;
      }
    };
    
    ListPreference lp = (ListPreference)findPreference(Config.PREF_KEY_ACCOUNT);
    final CharSequence[] items = AccountSelector.getAccountList(getApplicationContext());
    lp.setEntries(items);
    lp.setEntryValues(items);
   
    // Restore current settings.
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    String selectedAccount = prefs.getString(Config.PREF_KEY_SELECTED_ACCOUNT, null);
    if (selectedAccount != null) {
      lp.setValue(selectedAccount);
    }
    
    lp.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String account = newValue.toString();
        Logger.i("account selected is: " + account);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Config.PREF_KEY_SELECTED_ACCOUNT, account);
        editor.commit();
        return true;
      }
    });
    
    intervalPref.setOnPreferenceChangeListener(prefChangeListener);
    batteryPref.setOnPreferenceChangeListener(prefChangeListener);
  }
  
  /** 
   * As we leave the settings page, changes should be reflected in various applicable components
   * */
  @Override
  protected void onDestroy() {
    super.onDestroy();
    // The scheduler has a receiver monitoring this intent to get the update.
    // TODO(Wenjie): Only broadcast update intent when there is real change in the settings.
    this.sendBroadcast(new UpdateIntent("", UpdateIntent.PREFERENCE_ACTION));
  }
}
