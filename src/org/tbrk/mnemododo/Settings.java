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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.KeyEvent;
import android.widget.TextView;

public class Settings
    extends PreferenceActivity
{
    protected int key_assign_mode = 0;
    protected Dialog key_assign_dialog = null;
    
    protected static final int key_text_ids[] = {
        R.id.key_show, R.id.key_grade0, R.id.key_grade1,
        R.id.key_grade2, R.id.key_grade3, R.id.key_grade4,
        R.id.key_grade5
    };
    protected static final String key_pref_names[] = {
        "key_show_answer", "key_grade0", "key_grade1",
        "key_grade2", "key_grade3", "key_grade4",
        "key_grade5"
    };

    protected int[] keys_assigned = new int[key_text_ids.length];
    
    protected static final int DIALOG_KEY_ASSIGN = 0;
    
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

        Preference prefKeys = (Preference) findPreference("prefKeys");
        prefKeys.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                startAssignKeys();
                return false;
            }
        });
        
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

    protected Dialog onCreateDialog(int id)
    {
        Dialog dialog = null;

        // Context mContext = getApplicationContext();
        Context mContext = Settings.this;

        switch (id) {
        case DIALOG_KEY_ASSIGN:
            dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.mapkeys);
            dialog.setTitle(getString(R.string.setting_keys));
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                {
                    if (keyCode == KeyEvent.KEYCODE_MENU
                            || keyCode == KeyEvent.KEYCODE_HOME
                            || keyCode == KeyEvent.KEYCODE_BACK) {
                        return false;
                    }
                    
                    for (int i = 0; i < key_assign_mode; ++i) {
                        if (keys_assigned[i] == keyCode) {
                            return false;
                        }
                    }

                    keys_assigned[key_assign_mode++] = keyCode;
                    if (key_assign_mode < keys_assigned.length) {
                        highlightCurrentKey();
                    } else {
                        finishAssignKeys();
                        dialog.dismiss();
                    }

                    return true;
                }
            });
            key_assign_dialog = dialog;
            break;
        }

        return dialog;
    }

    public void highlightCurrentKey()
    {
        if (key_assign_dialog == null) {
            return;
        }

        for (int i = 0; i < key_text_ids.length; ++i) {
            TextView t = (TextView) key_assign_dialog.findViewById(key_text_ids[i]);
            if (t != null) {
                if (i == key_assign_mode) {
                    t.setEnabled(true);
                    t.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                } else {
                    t.setEnabled(false);
                    t.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }
            }
        }
    }
    
    public void startAssignKeys()
    {
        key_assign_mode = 0;
        showDialog(DIALOG_KEY_ASSIGN);
        highlightCurrentKey();
    }

    public void finishAssignKeys()
    {
        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Editor prefsEdit = prefs.edit();
        
        for (int i = 0; i < key_pref_names.length; ++i) {
            prefsEdit.putInt(key_pref_names[i], keys_assigned[i]);
        }
        prefsEdit.commit();
    }

}
