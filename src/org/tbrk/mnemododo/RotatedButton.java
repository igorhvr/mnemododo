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
