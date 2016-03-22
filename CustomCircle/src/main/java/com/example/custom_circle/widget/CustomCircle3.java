package com.example.custom_circle.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.*;
import android.util.AttributeSet;
import android.view.View;

import com.clevergump.my_common_library.utils.DensityUtils;
import com.example.custom_circle.R;

/**
 * 带有自定义属性的圆+圆内的扇形.
 *
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2016/3/22 15:49
 * @projectName AndroidDemo-CustomViewsDemo
 */
public class CustomCircle3 extends View {

    private static final String TAG = CustomCircle3.class.getSimpleName();
    // 默认宽高, 单位dp.
    public static final int DEF_SIZE_IN_DP = 100;
    // 圆边框的默认宽度, 单位dp.
    public static final int DEF_CIRCLE_BORDER_WIDTH_IN_DP = 3;
    // 圆边框的默认颜色
    private static final int DEF_CIRCLE_BORDER_COLOR = Color.parseColor("#33B5E5");
    // 扇形的最大进度, 进度达到最大进度时, 这个扇形就是一个圆.
    private static final int DEF_MAX_PROGRESS = 100;
    // 扇形的默认绘制进度.
    private static final int DEF_PROGRESS = 30;
    // 绘制扇形的默认起始角度.
    private static final float DEF_PIE_STARGING_ANGLE = -90;


    // 绘制圆的画笔
    private Paint mBorderPaint;
    // 绘制圆内扇形的画笔
    private Paint mContentPaint;

    // 默认宽高
    private float mDefSize;
    // 边框的默认宽度
    private float mDefCircleBorderWidth;
    // 圆半径的默认值
    private float mDefCircleOuterRadius;
    // 圆内画弧线时的矩形外框.
    private RectF mInnerArcRectF;

    // 绘制圆时, 调用 Canvas.drawCircle()方法时需要传入的半径值.
    private float mCircleRadius;

    /****************** 下面是自定义属性 ***************************/
//    <!-- 如果圆的边框是有宽度(厚度)的, 那么 circleRadius特指该圆的外边框的半径, 实际在绘制该圆时,
//    绘制所用的半径是该 circleRadius减去圆边框厚度的 1/2后的值 -->
//    <attr name="circleOuterRadius" format="dimension"/>
//    <!-- 圆边框的宽度(厚度) -->
//    <attr name="circleBorderWidth" format="dimension" />
//    <!-- 圆边框的颜色 -->
//    <attr name="circleBorderColor" format="color"/>
//    <!-- 圆内扇形的颜色 -->
//    <attr name="innerPieColor" format="color" />
//    <!-- 绘制圆内扇形的起始角度 -->
//    <attr name="innerPieStartingAngle" format="float"/>
//    <!-- 绘制圆内扇形的最大进度 -->
//    <attr name="innerPieMaxProgress" format="integer"/>
//    <!-- 绘制圆内扇形的进度 -->
//    <attr name="innerPieProgress" format="integer"/>

    // 圆边框的颜色
    private int mCircleBorderColor;
    // 圆边框的宽度
    private float mCircleBorderWidth;
    // 圆的外边框的半径
    private float mCircleOuterRadius;
    // 圆的内边框的半径
    private float mCircleInnerRadius;
    // 内部扇形的颜色
    private int mInnerPieColor;
    // 内部扇形的最大进度. 达到最大进度时, 内部的扇形其实是一个圆形.
    private int mInnerPieMaxProgress;
    // 内部扇形当前的绘制进度.
    private int mInnerPieProgress;
    // 绘制扇形的起始角度
    private float mInnerPieStartingAngle;
    // 实际宽高的一半, 通常用来和用户设置的圆的外边框的半径进行比较, 然后选择二者中的较小者作为圆的外边框半径的实际值.
    private int mHalfSize;


    public CustomCircle3(Context context) {
        this(context, null);
    }

    public CustomCircle3(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCircle3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * 设置当前进度.
     * @param progress
     */
    public void setProgress(int progress) {
        if (progress > mInnerPieMaxProgress) {
            progress = mInnerPieMaxProgress;
        }
        mInnerPieProgress = progress;
        if (isMainThread()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    /**
     * 获取当前进度.
     * @return
     */
    public int getProgress() {
        return mInnerPieProgress;
    }

    /**
     * 获取最大进度.
     * @return
     */
    public int getMaxProgress() {
        return mInnerPieMaxProgress;
    }

    /**
     * 将当前进度清零.
     */
    public void resetProgress() {
        mInnerPieProgress = 0;
    }

    /*********************** 私有方法 *********************************/

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        initDefValues(context);
        initCustomAttrs(context, attrs, defStyleAttr);
        initActualValues();
        initPaint();
    }

    /**
     * 初始化各个默认值
     * @param context
     */
    private void initDefValues(Context context) {
        mDefSize = DensityUtils.dip2px(context, DEF_SIZE_IN_DP);
        mDefCircleBorderWidth = DensityUtils.dip2px(getContext(), DEF_CIRCLE_BORDER_WIDTH_IN_DP);
        mDefCircleOuterRadius = mDefSize / 2;
    }

    /**
     * 获取自定义属性的值
     * @param context
     * @param attrs
     */
    private void initCustomAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) {
            return;
        }

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomCircle3, defStyleAttr, 0);
        int indexCount = a.getIndexCount();
        for (int i=0; i< indexCount; i++) {
            int index = a.getIndex(i);
            switch (index) {
                case R.styleable.CustomCircle3_circleBorderColor:
                    mCircleBorderColor = a.getColor(index, DEF_CIRCLE_BORDER_COLOR);
                    break;
                case R.styleable.CustomCircle3_circleBorderWidth:
                    // getDimension(), getDimensionPixelSize(), getDimensionPixelOffset()的对比:
                    // 不同点:
                    //      getDimension()方法返回 float.
                    //      getDimensionPixelSize(), getDimensionPixelOffset()方法都返回 int. (一个四舍五入, 一个直接舍弃小数部分).
                    // 相同点: 都会将我们设置的dp为单位的数值自动转换为以px为单位的数值, 所以无需我们操心了, 具体看源码.
                    mCircleBorderWidth = a.getDimension(index, mDefCircleBorderWidth);
                    break;
                case R.styleable.CustomCircle3_circleOuterRadius:
                    mCircleOuterRadius = a.getDimension(index, mDefCircleOuterRadius);
                    break;
                case R.styleable.CustomCircle3_innerPieColor:
                    mInnerPieColor = a.getColor(index, DEF_CIRCLE_BORDER_COLOR);
                    break;
                case R.styleable.CustomCircle3_innerPieMaxProgress:
                    // getInt() 和 getInteger()方法的区别:
                    //  getInt(): 如果实际设置的不是整数, 那么将会调用 Integer.decode(String) 将设置的数值强制转换为int值, 而不抛异常.
                    //  getInteger(): 如果实际设置的不是整数, 将会抛异常.
                    mInnerPieMaxProgress = a.getInt(index, DEF_MAX_PROGRESS);
                    break;
                case R.styleable.CustomCircle3_innerPieProgress:
                    mInnerPieProgress = a.getInt(index, DEF_PROGRESS);
                    break;
                case R.styleable.CustomCircle3_innerPieStartingAngle:
                    mInnerPieStartingAngle = a.getFloat(index, DEF_PIE_STARGING_ANGLE);
                    break;
                default:
                    break;
            }
        }

        a.recycle();
    }

    /**
     * 初始化一些实际使用的变量的数值, 这些变量通常是由多个自定义变量经过组合计算得到的.
     */
    private void initActualValues() {
        mCircleRadius = mCircleOuterRadius - mCircleBorderWidth / 2;
    }

    private void initPaint() {
        initBorderPaint();
        initContentPaint();
    }

    private void initBorderPaint() {
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(mCircleBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mCircleBorderWidth);
    }

    private void initContentPaint() {
        mContentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mContentPaint.setColor(mInnerPieColor);
        mContentPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 为了保证圆的外边框半径不能超过控件本身的1/2尺寸, 并且圆的边框厚度不能超过圆的外边框半径,
        // 需要重新计算相关数值.
        recalcValues();
        canvas.drawCircle(mHalfSize, mHalfSize, mCircleRadius, mBorderPaint);
        if (mInnerArcRectF == null) {
            float innerCircleOffset = mHalfSize - mCircleInnerRadius;
            mInnerArcRectF = new RectF(innerCircleOffset, innerCircleOffset,
                    getWidth() - innerCircleOffset, getHeight() - innerCircleOffset);
        }
        float sweepAngle = 1.0f * 360 * mInnerPieProgress / mInnerPieMaxProgress;
        canvas.drawArc(mInnerArcRectF, mInnerPieStartingAngle, sweepAngle, true, mContentPaint);
    }

    /**
     * 为了保证圆的外边框半径不能超过控件本身的1/2尺寸, 并且圆的边框厚度不能超过圆的外边框半径, 需要重新计算
     * 相关数值, 例如: 实际的外边框半径, 实际的内边框半径, 实际使用 drawCircle()方法绘制圆时需要传入的半径等.
     */
    private void recalcValues() {
        mHalfSize = getWidth() / 2;
        // 重新计算圆的外边框半径, 确保其值不能超过该控件本身的1/2尺寸.
        mCircleOuterRadius = Math.min(mHalfSize, mCircleOuterRadius);
        // 重新计算圆的边框厚度, 确保其值不能超过圆的外边框半径.
        mCircleBorderWidth = Math.min(mCircleOuterRadius, mCircleBorderWidth);

        // 重新计算使用 drawCircle()方法绘制圆时需要传入的半径值
        mCircleRadius = mCircleOuterRadius - mCircleBorderWidth / 2;
        // 重新计算圆的内边框的半径
        mCircleInnerRadius = mCircleRadius - mCircleBorderWidth / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        int measuredWidth = widthSpecSize;
        int measuredHeight = heightSpecSize;

        // 如果layout文件中设置layout_width 为 wrap_content, 那么就使用默认的宽度
        if (widthSpecMode == MeasureSpec.AT_MOST) {
            measuredWidth = (int) (mDefSize + 0.5f);
        }
        // 如果layout文件中设置layout_height 为 wrap_content, 那么就使用默认的高度
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            measuredHeight = (int) (mDefSize + 0.5f);
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    /**
     * 判断当前线程是否是主线程
     * @return
     */
    private boolean isMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }
}