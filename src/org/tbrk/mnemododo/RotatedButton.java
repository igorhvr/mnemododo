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

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.Button;

public class RotatedButton
    extends Button
{
    public int angle = 0;

    public RotatedButton(Context context)
    {
        super(context);
    }
    
    public RotatedButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    
    public RotatedButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    
    protected void onDraw(Canvas canvas) {
        if (angle == 0) {
            super.onDraw(canvas);
        } else {
            TextPaint paint = getPaint();
            int width = getWidth();
            int height = getHeight();
            
            float text_width = paint.measureText((String) getText());

            canvas.save();
            canvas.rotate(angle, width / 2, height / 2);

            //super.onDraw(canvas);
            canvas.drawText((String) getText(),
                             (width - text_width) / 2.0f,
                             height / 2.0f, paint);
            
            canvas.restore();
        }
    }
}
