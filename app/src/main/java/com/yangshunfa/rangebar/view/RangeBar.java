package com.yangshunfa.rangebar.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.yangshunfa.rangebar.R;

/**
 * Created by yangshunfa on 2017/8/10.
 * tips: 左右两端可选的 seekBar ；仿制美团；
 */

public class RangeBar extends View {

    private Paint mPaint;
    /** 固定背景的 top */
    private int   mSolidTop;
    /** 固定背景的 bottom */
    private int   mSolidBottom;
    /** 固定背景的 left  */
    private int   mSolidLeft;
    /** 固定背景的 right */
    private int   mSolidRight;
    private Slider mLeftSlider;
    private Slider mRightSlider;
//    private Slider mTouchingSlider;
    private float mRadius;
    private int mGrayColor;
    private int mSelectedColor;

    /** 文字 */
    private String [] mTextArray = {"1", "2", "3" ,"4" ,"5" , };
    private int [] mRangeArray;
    private int    heightCenter;
    private int mRange;
    private Bitmap mSliderBitmap;

    public RangeBar(Context context) {
        super(context);
        init(context);
    }

    public RangeBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RangeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(getResources().getColor(R.color.gray));

        // 初始化 滑块
        mLeftSlider = new Slider();
        mRightSlider = new Slider();
        mGrayColor = getResources().getColor(R.color.gray);
        mSelectedColor = getResources().getColor(R.color.colorPrimary);
        mSliderBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hm_range_piece);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mGrayColor);
        // draw bar background.
        canvas.drawRect(mSolidLeft, mSolidTop, mSolidRight, mSolidBottom, mPaint);

        // draw range bar color and rect.
        mPaint.setColor(mSelectedColor);
        canvas.drawRect(mLeftSlider.right, mSolidTop, mRightSlider.left, mSolidBottom, mPaint);
        // draw range point
        if (mRangeArray != null && mRangeArray.length > 0){
            int i = 0;
            for (; i < mRangeArray.length; i++){
                int cx = mRangeArray[i];
                if (cx >= mLeftSlider.center && cx <= mRightSlider.center) {
                    mPaint.setColor(mSelectedColor);
                } else {
                   mPaint.setColor(mGrayColor);
                }
                canvas.drawCircle(cx, heightCenter, 15, mPaint);
            }
        }
        // draw left slider.
        if (mLeftSlider.isTouching){
            mPaint.setColor(mSelectedColor);
        } else {
            mPaint.setColor(mGrayColor);
        }
        canvas.drawRect(mLeftSlider.left, mLeftSlider.top, mLeftSlider.right, mLeftSlider.bottom, mPaint);
        // draw right slider.
        if (mRightSlider.isTouching){
            mPaint.setColor(mSelectedColor);
        } else {
            mPaint.setColor(mGrayColor);
        }
        canvas.drawBitmap(mSliderBitmap, mRightSlider.left, mRightSlider.top, mPaint);
//        canvas.drawRect(mRightSlider.left, mRightSlider.top, mRightSlider.right, mRightSlider.bottom, mPaint);
//        canvas.drawRect(mRightSlider.left, mRightSlider.top, mRightSlider.right, mRightSlider.bottom, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // init center point
        int measuredWidth = getMeasuredWidth();
        int width = measuredWidth - getPaddingLeft() - getPaddingRight();
        heightCenter = width / 2;
        // 计算固定背景和进度条的 top 和 bottom
        mSolidTop = heightCenter - 10;
        mSolidBottom = mSolidTop + 20;
        mSolidLeft = 20;
        mSolidRight = measuredWidth - 20;
        // 两个滑块的位置
        mLeftSlider.center = mSolidLeft;
        mLeftSlider.left = mLeftSlider.center - 20;
        mLeftSlider.top = heightCenter - 30;
        mLeftSlider.right = mLeftSlider.center + 20;
        mLeftSlider.bottom = heightCenter + 30;

        mRightSlider.center = mSolidRight;
        mRightSlider.left = mRightSlider.center - 20;
        mRightSlider.top = heightCenter - 30;
        mRightSlider.right = mRightSlider.center + 20;
        mRightSlider.bottom = heightCenter + 30;
        // 初始化分段
        mRange = width / (mTextArray.length - 1);
        this.mRangeArray = new int[mTextArray.length];
        for (int i = 0;i< mTextArray.length ;i ++){
            this.mRangeArray[i] = mSolidLeft + mRange * i;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        float left = 0;
        float right = 0;
        switch (action){
            case MotionEvent.ACTION_DOWN:
                if (isTouchSlider(mLeftSlider, event)){
                    mLeftSlider.isTouching = true;
                    updateTouch(mLeftSlider, event);
//                    invalidate();
                } else if (isTouchSlider(mRightSlider, event)){
                    mRightSlider.isTouching = true;
//                    invalidate();
                    updateTouch(mRightSlider, event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mLeftSlider.isTouching) {
                    updateTouch(mLeftSlider, event);
                } else if (mRightSlider.isTouching){
                    updateTouch(mRightSlider, event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mLeftSlider.isTouching) {
                    autoScroll(mLeftSlider);
                } else if (mRightSlider.isTouching){
                    autoScroll(mRightSlider);
                }
                mRightSlider.isTouching = false;
                mLeftSlider.isTouching = false;
                invalidate();
                break;
        }
        return true;
//        return super.onTouchEvent(event);
    }

    private void autoScroll(Slider slider) {
        float distance = slider.center - getPaddingLeft();
        int position = (int) (distance / mRange);// 除数
        float remainder = distance % mRange;// 余数
//        Log.e("moose", "remainder=" + remainder + "  range=" + mRange);
        if (remainder <= (mRange / 2)){
            slider.center = mRangeArray[position];
        } else {
            int index = position + 1;
            slider.center = mRangeArray[index];
        }
        slider.left = slider.center - 20;
        slider.right = slider.center + 20;
    }

    private void updateTouch(Slider slider, MotionEvent event) {
        float x = event.getX();
        if (mLeftSlider.isTouching){
            // 当前是左边 slider， 判断是否越过右边 slider，如果是，不进行赋值操作
            if ((mRightSlider.center - x) < mRange ){
                slider.center = mRightSlider.center - mRange;
                slider.left = slider.center - 20;
                slider.right = slider.center + 20;
                invalidate();
                return;
            }
        } else if (mRightSlider.isTouching){
            // 当前是左边 slider， 判断是否越过右边 slider，如果是，不进行赋值操作
            if ((x - mLeftSlider.center) < mRange){
                slider.center = mLeftSlider.center + mRange;
                slider.left = slider.center - 20;
                slider.right = slider.center + 20;
                invalidate();
                return;
            }
        }
        if (x <= mSolidLeft) {
            slider.center = mSolidLeft;
            slider.left = mSolidLeft - 20;
            slider.right = mSolidLeft + 20;
        }
        Log.e("moose", "center=" + x + " right=" + mSolidRight);
        if (x >= mSolidRight){
            slider.center = mSolidRight;
            slider.right = mSolidRight + 20;
            slider.left = mSolidRight - 20;
        }
        if (x > mSolidLeft && x < mSolidRight){
            slider.center = x;
            slider.left = x - 20;
            slider.right = x + 20;
        }
        invalidate();
    }

    /** 判断是否触摸某个 Slider **/
    private boolean isTouchSlider(Slider slider, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        return x >= slider.left && x <= slider.right
                && y >= slider.top && y <= slider.bottom;
    }

    /** 滑块 */
    private class Slider {
        boolean isTouching;
        float top;
        float right;
        float bottom;
        float left;
        float center;// 中心点
    }

}
