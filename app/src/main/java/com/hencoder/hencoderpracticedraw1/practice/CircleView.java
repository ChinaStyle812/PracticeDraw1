package com.hencoder.hencoderpracticedraw1.practice;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.hencoder.hencoderpracticedraw1.R;

/**
 * Created by sy on 2017/8/3.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/8/3 13:56<br>
 * Revise Record:<br>
 * 2017/8/3: 创建并完成初始实现<br>
 */

public class CircleView extends View {
    public CircleView(Context context) {
        super(context);
        init(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /** 图标圆圈半径，dp */
    private static final int DEF_ICON_CIRCLE_R = 30;
    /** 默认图标个数 */
    private static final int DEF_ICON_SIZE = 5;
    private static final int DEF_TEXT_SIZE = 14;
    /** 图标文字间隔，dp */
    private static final int DEF_TEXT_IC_MARGIN = 8;
    private Context mContext;
    private Resources mRes;
    private Paint mPaint;
    /** 背景白色半透明圆圈半径 */
    private int mBgCircleRadius;
    /** 放图标的小圆半径 */
    private int mIcCircleRadius;
    private int mBgColor = 0x7FFFFFFF;
    private int mIcBgColor = Color.WHITE;
    private int mIconAngle;
    private int mTxtSize;
    private int mTxtColor = Color.WHITE;
    private int mTxtIcMargin;
    private Paint mTxtPaint;
    private ItemInner[] mItemData;
    private Point[] mPosData;
    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    private void init(Context context) {
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRes = context.getResources();
        DisplayMetrics dm = mRes.getDisplayMetrics();
        mTxtSize = (int) (DEF_TEXT_SIZE * dm.density);
        mTxtIcMargin = (int) (DEF_TEXT_IC_MARGIN * dm.density);
        mIcCircleRadius = (int) (DEF_ICON_CIRCLE_R * dm.density);
        mIconAngle = 360 / DEF_ICON_SIZE;
        mItemData = new ItemInner[DEF_ICON_SIZE];
        mPosData = new Point[DEF_ICON_SIZE];
        mTxtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTxtPaint.setTextSize(mTxtSize);
        mTxtPaint.setColor(mTxtColor);
        initTestData();
    }

    // FIXME: 2017/8/3 测试代码，调试完成后删除
    private void initTestData() {
        ItemData[] data = new ItemData[DEF_ICON_SIZE];
        String[] names = new String[] {"班圈", "作业", "荣誉", "通知", "成绩"};
        for (int i = 0; i < data.length; i++) {
            data[i] = new ItemData(R.drawable.icon, names[i]);
        }
        setData(data);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int minSize = Math.min(w, h);
        mBgCircleRadius = minSize / 2 - mIcCircleRadius - mTxtIcMargin - mTxtSize;
        calPosition(w / 2, h / 2);
    }

    /** 计算各个位置的坐标<br>
     * 数学知识扫盲：http://blog.csdn.net/can3981132/article/details/52559402
     */
    private void calPosition(int cx, int cy) {
        int startAngle = -90; //逆时针为负数，顺时针为正，以正上方为第一个基准点
        for (int i = 0; i < mItemData.length; i++) {
            Point p = new Point();
            int angle = startAngle + mIconAngle * i;
            p.x = (int) (cx + mBgCircleRadius * Math.cos(angle * Math.PI / 180));
            p.y = (int) (cy + mBgCircleRadius * Math.sin(angle * Math.PI / 180));
            mPosData[i] = p;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        // 画背景圆圈
        mPaint.setColor(mBgColor);
        canvas.drawCircle(cx, cy, mBgCircleRadius, mPaint);
        if (mItemData == null || mItemData.length != DEF_ICON_SIZE) {
            return;
        }
        drawItem(canvas);
    }

    private void drawItem(Canvas canvas) {
        mPaint.setColor(mIcBgColor);
        Paint txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(Color.BLACK);
        txtPaint.setTextSize(64);
        for (int i = 0; i < DEF_ICON_SIZE; i++) {
            Point p = mPosData[i];
            ItemInner item = mItemData[i];
            // 画图标小圆
            canvas.drawCircle(p.x, p.y, mIcCircleRadius, mPaint);
            // 设置离屏缓冲
            int saved = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);

            canvas.drawCircle(p.x, p.y, mIcCircleRadius, mPaint);
            mPaint.setXfermode(mXfermode);
            canvas.drawBitmap(item.icon, p.x - item.icon.getWidth() / 2, p.y - item.icon.getHeight() / 2, mPaint);
            mPaint.setXfermode(null);

            canvas.restoreToCount(saved);
            // 绘制文字
            boolean txtBelowIcon = isPointInBgCircle(p.x, p.y - mIcCircleRadius);
            Rect bounds = new Rect();
            mTxtPaint.getTextBounds(item.name, 0, item.name.length(), bounds);
            int txtLen = Math.abs(bounds.right - bounds.left);
            int txtH = Math.abs(bounds.bottom - bounds.top);
            if (txtBelowIcon) {
                canvas.drawText(item.name, p.x - txtLen / 2, p.y + mIcCircleRadius + mTxtIcMargin + txtH, mTxtPaint);
            } else {
                canvas.drawText(item.name, p.x - txtLen / 2, p.y - mIcCircleRadius - mTxtIcMargin * 2, mTxtPaint);
            }
        }
        mPaint.setShader(null);
    }

    /** 决定文字显示在小圆圈上方还是下方 */
    private boolean isPointInBgCircle(int x, int y) {
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        return Math.sqrt(Math.pow(cx - x, 2) + Math.pow(cy - y, 2)) <= mBgCircleRadius;
    }

    /**
     * 点击事件简单处理
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handled = isTouchInItem(event.getX(), event.getY()).touched;
                break;
            case MotionEvent.ACTION_UP:
                TouchFeedback feedback = isTouchInItem(event.getX(), event.getY());
                if (feedback.touched) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onClickItem(feedback.touchItem.iconRes, feedback.touchItem.name);
                    }
                }
                break;
        }
        return handled ? handled : super.onTouchEvent(event);
    }

    private class TouchFeedback {
        boolean touched;
        ItemInner touchItem;
    }

    private TouchFeedback isTouchInItem(float x, float y) {
        TouchFeedback feedback = new TouchFeedback();
        feedback.touched = false;
        for (int i = 0; i < mPosData.length; i++) {
            Point p = mPosData[i];
            if (Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2)) <= mIcCircleRadius) {
                feedback.touched = true;
                feedback.touchItem = mItemData[i];
                break;
            }
        }

        return feedback;
    }

    public class ItemData {
        public int iconRes;
        public String name;

        public ItemData(int iconRes, String name) {
            this.iconRes = iconRes;
            this.name = name;
        }
    }

    private class ItemInner extends ItemData{
        Bitmap icon;
        Shader shader;

        public ItemInner(ItemData item) {
            super(item.iconRes, item.name);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), item.iconRes, options);
            int size = mIcCircleRadius * 2;
            System.out.println("size: " + size);
            int widthSampleSize = options.outWidth / size;
            int heiSampleSize = options.outHeight / size;
            options.inSampleSize = widthSampleSize > heiSampleSize ? heiSampleSize : widthSampleSize;
            options.inSampleSize = options.inSampleSize < 1 ? 1 : options.inSampleSize;
            options.inJustDecodeBounds = false;
            icon = BitmapFactory.decodeResource(getResources(), item.iconRes, options);

            shader = new BitmapShader(icon, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }
    }

    public void setData(ItemData[] data) {
        if (data == null || data.length != DEF_ICON_SIZE) {
            throw new IllegalArgumentException("IllegalArgument");
        }
        for (int i = 0; i < data.length; i++) {
            ItemData item = data[i];
            mItemData[i] = new ItemInner(item);
        }
        invalidate();
    }

    private ItemClickListener mItemClickListener = new ItemClickListener() {
        @Override
        public void onClickItem(int iconRes, String name) {
            Toast.makeText(mContext, "点击了：" + name, Toast.LENGTH_SHORT).show();
        }
    };

    public interface ItemClickListener {
        void onClickItem(int iconRes, String name);
    }

    public void setItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
