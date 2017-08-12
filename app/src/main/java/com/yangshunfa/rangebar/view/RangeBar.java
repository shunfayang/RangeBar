package com.yangshunfa.rangebar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.yangshunfa.rangebar.R;

/**
 * Created by yangshunfa on 2017/8/10.
 * tips: 左右两端可选的 Range Bar ；仿制美团；可回弹；
 */

public class RangeBar extends View {

    private int mSolidColor;
    private int mTextColor;
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
    private OnRangeSelectedListener mListener;

    /** 文字 */
    private CharSequence [] mTextArray = {"10", "232", "323" ,"43" ,"5" , "2383"};
    private int [] mRangeArray;
    private int    heightCenter;
    private int mRange;
    private Bitmap mSliderBitmap;
    private int mSliderResId;
    private int mBitmapHeight;
    private int mBitmapWidth;
    private int leftAndRight;
    private int topAndBottom;
    private float mRangeHeight;
    private float mTextSize;
    private float mVerticalLineHeight;
    private float mContentPadding;
    private int mDefaultLeftPosition;
    private int mDefaultRightPosition;

    public RangeBar(Context context) {
        super(context);
        init(context);
    }

    public RangeBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mGrayColor = getResources().getColor(R.color.gray);
        mTextColor = getResources().getColor(R.color.gray);
        mSelectedColor = getResources().getColor(R.color.colorPrimary);
        // init attrs
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.RangeBar);
        mSelectedColor = t.getColor(R.styleable.RangeBar_rangeColor, mSelectedColor);
        mTextColor = t.getColor(R.styleable.RangeBar_textColor, mTextColor);
        mSolidColor = t.getColor(R.styleable.RangeBar_solidBackgroundColor, mGrayColor);
        mSliderResId = t.getResourceId(R.styleable.RangeBar_sliderDrawable, 0);
        mRangeHeight = t.getDimension(R.styleable.RangeBar_rangeHeight, 10);
        mTextSize = t.getDimension(R.styleable.RangeBar_textSize, context.getResources().getDimension(R.dimen.moose_range_bar_text_size));
        mVerticalLineHeight = t.getDimension(R.styleable.RangeBar_verticalLineHeight, context.getResources().getDimension(R.dimen.moose_range_bar_line_height));
        mContentPadding = t.getDimension(R.styleable.RangeBar_verticalPadding, context.getResources().getDimension(R.dimen.moose_range_bar_padding));
        mTextArray = t.getTextArray(R.styleable.RangeBar_textArray);
//        if (mTextArray)
        // init
        init(context);
    }

    public RangeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setTextSize(mTextSize);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(mGrayColor);

        // 初始化 滑块
        mLeftSlider = new Slider();
        mRightSlider = new Slider();
        if (mSliderResId != 0){
            mSliderBitmap = BitmapFactory.decodeResource(getResources(), mSliderResId);
        }else {
            mSliderBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hm_range_piece);
        }
        mBitmapHeight = mSliderBitmap.getHeight();
        mBitmapWidth = mSliderBitmap.getWidth();
        leftAndRight = mBitmapWidth / 2;
        topAndBottom = mBitmapHeight / 2;
//        Log.d("moose", "bitmap height= " + mBitmapHeight + " width=" + mBitmapWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // first, calculate left and right by Slider.position. 通过position 去计算 left 和 right。
        if (mDefaultLeftPosition >= 0 || mDefaultRightPosition >= 0){
            mLeftSlider.center = mSolidLeft + mRange * mDefaultLeftPosition;
            mLeftSlider.left = mLeftSlider.center - leftAndRight;
            mLeftSlider.right = mLeftSlider.center + leftAndRight;

            mRightSlider.center = mSolidLeft + mRange * mDefaultRightPosition;
            mRightSlider.left = mRightSlider.center - leftAndRight;
            mRightSlider.right = mRightSlider.center + leftAndRight;
            mDefaultLeftPosition = -1;
            mDefaultRightPosition = -1;
        }

        mPaint.setColor(mSolidColor);
        // draw bar background. 固定背景
        canvas.drawRect(mSolidLeft, mSolidTop, mSolidRight, mSolidBottom, mPaint);

        // draw range bar color and rect.选中位置
        mPaint.setColor(mSelectedColor);
        canvas.drawRect(mLeftSlider.right, mSolidTop, mRightSlider.left, mSolidBottom, mPaint);
        // draw range point
        if (mRangeArray != null && mRangeArray.length > 0 && mTextArray != null && mTextArray.length > 0){
            int i = 0;
            for (; i < mRangeArray.length; i++){
                int cx = mRangeArray[i];
                if (cx >= mLeftSlider.center && cx <= mRightSlider.center) {
                    mPaint.setColor(mSelectedColor);
                } else {
                   mPaint.setColor(mGrayColor);
                }
//                canvas.drawCircle(cx, heightCenter, 15, mPaint);
                float cy = heightCenter - (topAndBottom + mVerticalLineHeight);
                canvas.drawText(mTextArray[i].toString(), cx - (getTextWidth(mTextArray[i].toString()) / 2), cy  - mContentPadding * 2, mPaint);
                canvas.drawLine(cx , cy - mContentPadding, cx , cy + mVerticalLineHeight - mContentPadding, mPaint);
            }
        }
        // draw left slider.
        if (mLeftSlider.isTouching){
            mPaint.setColor(mSelectedColor);
        } else {
            mPaint.setColor(mGrayColor);
        }
//        canvas.drawRect(mLeftSlider.left, mLeftSlider.top, mLeftSlider.right, mLeftSlider.bottom, mPaint);
        canvas.drawBitmap(mSliderBitmap, mLeftSlider.left, mLeftSlider.top, mPaint);
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

    private float getTextWidth(String text){
        return mPaint.measureText(text);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // init center point
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int width = measuredWidth - getPaddingLeft() - getPaddingRight();
        heightCenter = measuredHeight * 2 / 3;

        // 计算固定背景和进度条的 top 和 bottom
        int rangeHeight = (int) (mRangeHeight / 2);
        mSolidTop = heightCenter - rangeHeight;
        mSolidBottom = heightCenter + rangeHeight;
        mSolidLeft = getPaddingLeft() + leftAndRight;
        mSolidRight = measuredWidth - getPaddingRight() - leftAndRight;

//        mSolidLeft = 20;
//        mSolidRight = measuredWidth - 20;

        // 两个滑块的位置
        mLeftSlider.center = mSolidLeft;
        mLeftSlider.left = mLeftSlider.center - leftAndRight;
        mLeftSlider.right = mLeftSlider.center + leftAndRight;
        mLeftSlider.top = heightCenter - topAndBottom;
        mLeftSlider.bottom = heightCenter + topAndBottom;

        mRightSlider.center = mSolidRight;
        mRightSlider.left = mRightSlider.center - leftAndRight;
        mRightSlider.right = mRightSlider.center + leftAndRight;
        mRightSlider.top = heightCenter - topAndBottom;
        mRightSlider.bottom = heightCenter + topAndBottom;
        // 初始化分段
        mRange = (width - mBitmapWidth ) / (mTextArray.length - 1);
        this.mRangeArray = new int[mTextArray.length];
        for (int i = 0;i< mTextArray.length ;i ++){
            this.mRangeArray[i] = mSolidLeft + mRange * i;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
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

    /* 每次松开手指，自动滚动到区间点； */
    private void autoScroll(Slider slider) {
        float distance = slider.center - getPaddingLeft() - leftAndRight;
        int position = (int) (distance / mRange);// 除数
        float remainder = distance % mRange;// 余数
//        Log.e("moose", "remainder=" + remainder + "  range=" + mRange);
        if (remainder <= (mRange / 2)){
            slider.center = mRangeArray[position];
            slider.position = position;
        } else {
            int index = position + 1;
            slider.position = index;
            slider.center = mRangeArray[index];
        }
        // selected listener. 选中监听。
        if (mListener != null){
            mListener.onRangeSelected(mLeftSlider.position, mRightSlider.position);
        }
        slider.left = slider.center - leftAndRight;
        slider.right = slider.center + leftAndRight;
    }

    private void updateTouch(Slider slider, MotionEvent event) {
        float x = event.getX();
        if (mLeftSlider.isTouching){
            // 当前是左边 slider， 判断是否越过右边 slider，如果是，不进行赋值操作
            if ((mRightSlider.center - x) < mRange ){
                slider.center = mRightSlider.center - mRange;
                slider.left = slider.center - leftAndRight;
                slider.right = slider.center + leftAndRight;
                invalidate();
                return;
            }
        } else if (mRightSlider.isTouching){
            // 当前是左边 slider， 判断是否越过右边 slider，如果是，不进行赋值操作
            if ((x - mLeftSlider.center) < mRange){
                slider.center = mLeftSlider.center + mRange;
                slider.left = slider.center - leftAndRight;
                slider.right = slider.center + leftAndRight;
                invalidate();
                return;
            }
        }
        if (x <= mSolidLeft) {
            slider.center = mSolidLeft;
            slider.left = mSolidLeft - leftAndRight;
            slider.right = mSolidLeft + leftAndRight;
        }
//        Log.e("moose", "center=" + x + " right=" + mSolidRight);
        if (x >= mSolidRight){
            slider.center = mSolidRight;
            slider.right = mSolidRight + leftAndRight;
            slider.left = mSolidRight - leftAndRight;
        }
        if (x > mSolidLeft && x < mSolidRight){
            slider.center = x;
            slider.left = x - leftAndRight;
            slider.right = x + leftAndRight;
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
        // 当前选中的位置
        int position;
        float top;
        float right;
        float bottom;
        float left;
        float center;// 中心点
    }

    /**
     * set default range
     * 设置默认选中范围
     * @param leftSelect 左边选中角标
     * @param rightSelect 右边选中角标
     */
    public void setRange(int leftSelect, int rightSelect){
        int length = mTextArray.length;
        if (leftSelect >= length || rightSelect >= length){
            throw new RuntimeException("leftSelect or rightSelect must be less than TextArray.length. leftSelect 和 rightSelect必须小于 String 数组的长度。");
        } else if (leftSelect < 0|| rightSelect < 0){
            throw new RuntimeException("leftSelect or rightSelect must be greater than zero. leftSelect 和 rightSelect必须大于零。");
        } else if (leftSelect == rightSelect){
            throw new RuntimeException("leftSelect must be less than rightSelect. leftSelect 必须小于 rightSelect。");
        }
        this.mDefaultLeftPosition = leftSelect;
        this.mDefaultRightPosition = rightSelect;
        mLeftSlider.position = leftSelect;
        mRightSlider.position = rightSelect;
        invalidate();
    }

    /**
     * Get range position from left Slider.
     * 获取左边位置
     */
    public int getLeftRangePosition(){
        return mLeftSlider.position;
    }

    /**
     * Get range position from right Slider.
     * 获取右边位置
     */
    public int getRightRangePosition(){
        return mRightSlider.position;
    }

    public void setOnRangeSelectedListener(OnRangeSelectedListener l){
        this.mListener = l;
    }

    public interface OnRangeSelectedListener{
        /**
         * return selected position. 返回选中的位置
         * @param left
         * @param right
         */
        void onRangeSelected(int left, int right);
    }

}
