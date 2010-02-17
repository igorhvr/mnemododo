/*
 * Copyright (C) 2010 Timothy Bourke
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.tbrk.mnemododo;

import java.util.Vector;

import mnemogogo.mobile.hexcsv.FindCardDirAndroid;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class Settings
    extends PreferenceActivity
{
    private class FindCardDirsTask extends
            UserTask<ListPreference, Integer, Vector<String>>
    {
        private ListPreference list_pref = null;
        private ProgressDialog progress_dialog;

        public void onPreExecute()
        {
            progress_dialog = ProgressDialog.show(Settings.this, "",
                    getString(R.string.searching_for_card_dirs), true);
        }

        public Vector<String> doInBackground(ListPreference... pref)
        {
            if (pref.length == 0) {
                list_pref = null;
                return null;
            }

            list_pref = pref[0];
            return FindCardDirAndroid.list();
        }

        public void onPostExecute(Vector<String> result)
        {
            progress_dialog.dismiss();

            if (list_pref == null) {
                return;
            }

            if (result.isEmpty()) {
                list_pref.setSummary(R.string.cannot_find_card_dirs);

            } else {
                String[] dirs = result.toArray(new String[result.size()]);
                list_pref.setEntries(dirs);
                list_pref.setEntryValues(dirs);

                list_pref.setEnabled(true);
            }
        }
    }

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        setCardDirEntries();

        setResult(RESULT_OK);
    }
    
    public void setCardDirEntries()
    {
        ListPreference pref_card_dir = (ListPreference) getPreferenceScreen()
            .findPreference("cards_path");
        CharSequence[] entries = pref_card_dir.getEntries();
        
        if (entries == null) {
            entries = (CharSequence[]) getLastNonConfigurationInstance();
        }
        
        if ((entries == null) || (entries.length == 0)) {
            pref_card_dir.setEnabled(false);
            new FindCardDirsTask().execute(pref_card_dir);

        } else {
            pref_card_dir.setEntries(entries);
            pref_card_dir.setEntryValues(entries);
            pref_card_dir.setEnabled(true);
        }
    }
    
    public Object onRetainNonConfigurationInstance()
    {
        ListPreference pref_card_dir = (ListPreference) getPreferenceScreen()
            .findPreference("cards_path");
        return pref_card_dir.getEntries();
    }

    public void onResume()
    {
        super.onResume();
        setCardDirEntries();
        setResult(RESULT_OK);
    }

    public void onContentChanged()
    {
        super.onContentChanged();
    }
}
