package com.hencoder.hencoderpracticedraw1.practice;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class Practice10HistogramView extends View {

    public Practice10HistogramView(Context context) {
        super(context);
        init(context);
    }

    public Practice10HistogramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Practice10HistogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public static final int BAR_COLOR = 0xFF16B972;
    public static final int BAR_SIZE = 7;
    private Paint mPaint;
    private Path mLine;
    private int mLineWidth;
    private int mBarWidth;
    private int mHorizontalMargin;
    private int mBarMargin;

    private void init(Context context) {
        Resources res = context.getResources();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLine = new Path();
        DisplayMetrics dm = res.getDisplayMetrics();
        mLineWidth = (int) (dm.density * 1);
        mBarWidth = (int) (dm.density * 20);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHorizontalMargin = (int) (w * 0.15);
        mBarMargin = (int) (w * 0.7 / BAR_SIZE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        综合练习
//        练习内容：使用各种 Canvas.drawXXX() 方法画直方图
        mLine.reset();
        mLine.moveTo(mHorizontalMargin, 40);
        mLine.lineTo(mHorizontalMargin, 40 + 300);
        mLine.lineTo(mHorizontalMargin + 400, 40 + 300);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mLine, mPaint);
    }
}
