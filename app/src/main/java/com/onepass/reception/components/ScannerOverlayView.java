package com.onepass.reception.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ScannerOverlayView extends View {

    private Paint backgroundPaint;
    private Paint borderPaint;
    private RectF frameRect;

    public ScannerOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(0x99000000); // semi-transparent black

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(6f);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int size = (int) (Math.min(w, h) * 0.65f);
        int left = (w - size) / 2;
        int top = (h - size) / 2;
        frameRect = new RectF(left, top, left + size, top + size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Darken outside
        canvas.drawRect(0, 0, getWidth(), frameRect.top, backgroundPaint);
        canvas.drawRect(0, frameRect.bottom, getWidth(), getHeight(), backgroundPaint);
        canvas.drawRect(0, frameRect.top, frameRect.left, frameRect.bottom, backgroundPaint);
        canvas.drawRect(frameRect.right, frameRect.top, getWidth(), frameRect.bottom, backgroundPaint);

        // Draw square border
        canvas.drawRect(frameRect, borderPaint);
    }
}

