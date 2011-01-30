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

import java.io.IOException;
import android.util.Log;
import android.content.Context;
import mnemogogo.mobile.hexcsv.HexCsvAndroid;
import mnemogogo.mobile.hexcsv.Progress;

class CardStore
{
    public HexCsvAndroid cards = null;
    public String cards_path = null;

    protected long cards_timestamp = 0;
    protected int cards_to_load = 50;

    protected LoadStatsTask stats_task = null;

    public CardStore()
    {
    }

    public CardStore(String path, int cards_to_load,
                     TaskListener<String> callback)
    {
        this.cards_to_load = cards_to_load;

        stats_task = new LoadStatsTask(callback);
        stats_task.execute(path);
    }

    public boolean active()
    {
        return ((cards_path != null) && (cards != null));
    }

    public void updateCallback(TaskListener<String> callback)
    {
        if (stats_task != null) {
            stats_task.updateCallback(callback);
        }
    }

    private class LoadStatsTask
            extends ProgressTask<String, String>
    {
        protected HexCsvAndroid loaddb;
        protected String error_msg;
        protected String load_path;

        LoadStatsTask(TaskListener<String> callback)
        {
            super(callback, R.string.loading_card_dir);
        }

        public String doInBackground(String... path)
        {
            load_path = path[0];
            try {
                loaddb = new HexCsvAndroid(load_path, LoadStatsTask.this);
                loaddb.cards_to_load = cards_to_load;

            } catch (Exception e) {
                stopOperation();
                return getString(R.string.corrupt_card_dir)
                    + "\n\n(" + e.toString() + ")";

            } catch (OutOfMemoryError e) {
                stopOperation();
                return getString(R.string.not_enough_memory_to_load);
            }

            stopOperation();
            return null;
        }

        public void onPostExecute(String error_msg)
        {
            stats_task = null;

            if (error_msg == null) {
                cards = loaddb;
                cards_path = load_path;
                cards_timestamp = loaddb.nowInDays();

                try {
                    cards.backupCards(new StringBuffer(load_path), null);
                } catch (IOException e) { }

                callback.onFinished(null);

            } else {
                cards = null;
                callback.onFinished(error_msg);
            }
        }
    }

    boolean needsReload()
    {
        return (cards != null && cards_timestamp != cards.nowInDays());
    }

    void resume()
    {
        if (cards != null) {
            cards.reopen(cards_path);
        }
    }

    void close()
    {
        if (cards != null) {
            cards.close();
        }
    }
    
    boolean loadingCards()
    {
        return (stats_task != null);
    }

    boolean needLoadCards(String settings_cards_path)
    {
        return (cards_path == null && settings_cards_path != null)
               || (cards_path != null
                   && settings_cards_path != null
                   && !cards_path.equals(settings_cards_path));
    }

    void saveCards()
        throws IOException
    {
        if ((cards != null) && (cards_path != null)) {
            cards.writeCards(new StringBuffer(cards_path), null);
        }
    }

    void writeCategorySkips()
    {
        cards.writeCategorySkips(new StringBuffer(cards_path));
    }

    void onPause()
    {
        if (stats_task != null) {
            stats_task.pause();
        }
    }

    int numScheduled() {
        if (cards == null) {
            return 0;
        } else {
            return cards.numScheduled();
        }
    }

    boolean canLearnAhead() {
        if (cards == null) {
            return false;
        } else {
            return cards.canLearnAhead();
        }
    }

    public void setProgress(Progress progress)
    {
        if (cards != null) {
            cards.setProgress(progress);
        }
    }
}

