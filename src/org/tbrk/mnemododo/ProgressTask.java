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

import mnemogogo.mobile.hexcsv.Progress;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public abstract class ProgressTask<Params, Result>
    extends AsyncTask<Params, Integer, Result>
    implements Progress
{
    private ProgressDialog progress_dialog = null;
    private int progress_max = 10000;
    private int progress_level = 0;
    protected int style = ProgressDialog.STYLE_SPINNER;
    boolean task_done = false;

    protected abstract String getMessage();
    protected abstract Context getContext();

    public void onProgressUpdate(Integer... progress)
    {
        if (progress[0] == -1) {
            // do it here, and not in onPostExecute, because
            // the update requests are queued.
            if (progress_dialog != null) {
                progress_dialog.dismiss();
                progress_dialog = null;
                task_done = true;
            }

        } else {
            if (progress_dialog == null && !task_done) {
                progress_dialog = new ProgressDialog(getContext());
                progress_dialog.setProgressStyle(style);
                progress_dialog.setMessage(getMessage());
                progress_dialog.setMax(progress_max);
                progress_dialog.setCancelable(false);
                progress_dialog.show();
            }
            
            if (progress_dialog != null) {
                progress_dialog.setProgress(progress[0]);
            }
        }
    }

    public void startOperation(int length, String msg)
    {
        progress_level = 0;
        progress_max = length;
        publishProgress(0);
    }

    public void updateOperation(int delta)
    {
        progress_level += delta;
        publishProgress(progress_level);
    }

    public void stopOperation()
    {
        progress_level = 10000;
        progress_max = 10000;
        publishProgress(-1);
    }

    public void pause() {
        if (progress_dialog != null) {
            progress_dialog.dismiss();
            progress_dialog = null;
        }
    }

}
