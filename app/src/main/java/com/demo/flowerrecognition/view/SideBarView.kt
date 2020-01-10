package com.demo.flowerrecognition.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Nullable
import com.demo.flowerrecognition.R


class AZSideBarView @JvmOverloads constructor(
    context: Context?, @Nullable attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    private var mBackgroundColor = 0
    private var mStrokeColor = 0
    private var mTextColor = 0
    private var mTextSize = 0
    private var mSelectTextColor = 0
    private var mSelectTextSize = 0
    private var mHintTextColor = 0
    private var mHintTextSize = 0
    private var mHintCircleRadius = 0
    private var mHintCircleColor = 0
    private var mHintShape = 0
    private var mContentPadding = 0
    private var mBarPadding = 0
    private var mBarWidth = 0
    private var mLetters: List<String>? = null
    private var mSlideBarRect: RectF? = null
    private lateinit var mTextPaint: TextPaint
    private lateinit var mPaint: Paint
    private var mSelect = 0
    private var mPreSelect = 0
    private var mNewSelect = 0
    private var mRatioAnimator: ValueAnimator? = null
    private var mAnimationRatio = 0f
    private var mListener: OnLetterChangeListener? = null
    private var mTouchY = 0
    private fun initAttribute(attrs: AttributeSet?, defStyleAttr: Int) {
        val typeArray =
            context.obtainStyledAttributes(attrs, R.styleable.AZSideBarView, defStyleAttr, 0)
        mBackgroundColor = typeArray.getColor(
            R.styleable.AZSideBarView_backgroundColor,
            Color.parseColor("#F9F9F9")
        )
        mStrokeColor = typeArray.getColor(
            R.styleable.AZSideBarView_strokeColor,
            Color.parseColor("#000000")
        )
        mTextColor = typeArray.getColor(
            R.styleable.AZSideBarView_textColor,
            Color.parseColor("#969696")
        )
        mTextSize = typeArray.getDimensionPixelOffset(
            R.styleable.AZSideBarView_textSize,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 10f,
                resources.displayMetrics
            ).toInt()
        )
        mSelectTextColor = typeArray.getColor(
            R.styleable.AZSideBarView_selectTextColor,
            Color.parseColor("#FF0000")
        )
        mSelectTextSize = typeArray.getDimensionPixelOffset(
            R.styleable.AZSideBarView_selectTextSize,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 10f,
                resources.displayMetrics
            ).toInt()
        )
        mHintTextColor = typeArray.getColor(
            R.styleable.AZSideBarView_hintTextColor,
            Color.parseColor("#FFFFFF")
        )
        mHintTextSize = typeArray.getDimensionPixelOffset(
            R.styleable.AZSideBarView_hintTextSize,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16f,
                resources.displayMetrics
            ).toInt()
        )
        mHintCircleRadius = typeArray.getDimensionPixelOffset(
            R.styleable.AZSideBarView_hintCircleRadius,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 24f,
                resources.displayMetrics
            ).toInt()
        )
        mHintCircleColor = typeArray.getColor(
            R.styleable.AZSideBarView_hintCircleColor,
            Color.parseColor("#bef9b81b")
        )
        mHintShape = typeArray.getInteger(R.styleable.AZSideBarView_hintShape, 0)
        mContentPadding = typeArray.getDimensionPixelOffset(
            R.styleable.AZSideBarView_contentPadding,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2f,
                resources.displayMetrics
            ).toInt()
        )
        mBarPadding = typeArray.getDimensionPixelOffset(
            R.styleable.AZSideBarView_barPadding,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 6f,
                resources.displayMetrics
            ).toInt()
        )
        this.mBarWidth = typeArray.getDimensionPixelOffset(
            R.styleable.AZSideBarView_barWidth,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 0f,
                resources.displayMetrics
            ).toInt()
        )
        if (mBarWidth == 0) {
            mBarWidth = 2 * mTextSize
        }
        typeArray.recycle()
    }

    private fun initData() {
        mLetters =
            listOf(*context.resources.getStringArray(R.array.slide_bar_value_list))
        mTextPaint = TextPaint()
        this.mPaint = Paint()
        this.mPaint.isAntiAlias = true
        mSelect = -1
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mSlideBarRect == null) {
            mSlideBarRect = RectF()
        }
        val contentLeft = measuredWidth - mBarWidth - mBarPadding.toFloat()
        val contentRight = measuredWidth - mBarPadding.toFloat()
        val contentTop = mBarPadding.toFloat()
        val contentBottom = (measuredHeight - mBarPadding).toFloat()
        mSlideBarRect!![contentLeft, contentTop, contentRight] = contentBottom
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制slide bar 上字母列表
        drawLetters(canvas)
        //绘制选中时的提示信息(圆＋文字)
        drawHint(canvas)
        //绘制选中的slide bar上的那个文字
        drawSelect(canvas)
    }

    /**
     * 给定文字的center获取文字的base line
     */
    private fun getTextBaseLineByCenter(center: Float, paint: TextPaint, size: Int): Float {
        paint.textSize = size.toFloat()
        val fontMetrics = paint.fontMetrics
        val height = fontMetrics.bottom - fontMetrics.top
        return center + height / 2 - fontMetrics.bottom
    }


    /**
     * 绘制slide bar 上字母列表
     */
    private fun drawLetters(canvas: Canvas) { //绘制圆角矩形
        mPaint.style = Paint.Style.FILL
        mPaint.color = mBackgroundColor
        canvas.drawRoundRect(mSlideBarRect!!, mBarWidth / 2.0f, mBarWidth / 2.0f, mPaint)
        //绘制描边
        mPaint.style = Paint.Style.STROKE
        mPaint.color = mStrokeColor
        canvas.drawRoundRect(mSlideBarRect!!, mBarWidth / 2.0f, mBarWidth / 2.0f, mPaint)
        //顺序绘制文字
        val itemHeight =
            (mSlideBarRect!!.bottom - mSlideBarRect!!.top - mContentPadding * 2) / mLetters!!.size
        for (index in mLetters!!.indices) {
            val baseLine: Float = getTextBaseLineByCenter(
                mSlideBarRect!!.top + mContentPadding + itemHeight * index + itemHeight / 2,
                this.mTextPaint,
                mTextSize
            )
            mTextPaint.color = mTextColor
            mTextPaint.textSize = mTextSize.toFloat()
            mTextPaint.textAlign = Paint.Align.CENTER
            val pointX =
                mSlideBarRect!!.left + (mSlideBarRect!!.right - mSlideBarRect!!.left) / 2.0f
            canvas.drawText(mLetters!![index], pointX, baseLine, mTextPaint)
        }
    }

    /**
     * 绘制选中时的提示信息(圆＋文字)
     */
    private fun drawSelect(canvas: Canvas) {
        if (mSelect != -1) {
            mTextPaint.color = mSelectTextColor
            mTextPaint.textSize = mSelectTextSize.toFloat()
            mTextPaint.textAlign = Paint.Align.CENTER
            val itemHeight =
                (mSlideBarRect!!.bottom - mSlideBarRect!!.top - mContentPadding * 2) / mLetters!!.size
            val baseLine: Float = getTextBaseLineByCenter(
                mSlideBarRect!!.top + mContentPadding + itemHeight * mSelect + itemHeight / 2,
                mTextPaint,
                mTextSize
            )
            val pointX =
                mSlideBarRect!!.left + (mSlideBarRect!!.right - mSlideBarRect!!.left) / 2.0f
            canvas.drawText(mLetters!![mSelect], pointX, baseLine, mTextPaint)
        }
    }

    /**
     * 绘制选中的slide bar上的那个文字
     */
    private fun drawHint(canvas: Canvas) { //x轴的移动路径
        val circleCenterX = measuredWidth + mHintCircleRadius -
                (-measuredWidth / 2 + (measuredWidth + mHintCircleRadius)) * mAnimationRatio
        mPaint.style = Paint.Style.FILL
        mPaint.color = mHintCircleColor
        if (mHintShape == 0) {
            canvas.drawCircle(
                circleCenterX,
                measuredHeight / 2.0f,
                mHintCircleRadius.toFloat(),
                mPaint
            )
        } else {
            canvas.drawRect(
                circleCenterX - mHintCircleRadius,
                measuredHeight / 2.0f - mHintCircleRadius,
                circleCenterX + mHintCircleRadius,
                measuredHeight / 2.0f + mHintCircleRadius,
                mPaint
            )
        }
        // 绘制圆中心的提示字符
        if (mSelect != -1) {
            val target = mLetters!![mSelect]
            val textY: Float = getTextBaseLineByCenter(
                measuredHeight / 2.0f,
                this.mTextPaint,
                mHintTextSize
            )
            mTextPaint.color = mHintTextColor
            mTextPaint.textSize = mHintTextSize.toFloat()
            mTextPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(target, circleCenterX, textY, mTextPaint)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val y = event.y
        val x = event.x
        mPreSelect = mSelect
        mNewSelect = (y / (mSlideBarRect!!.bottom - mSlideBarRect!!.top) * mLetters!!.size).toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (x < mSlideBarRect!!.left || y < mSlideBarRect!!.top || y > mSlideBarRect!!.bottom) {
                    return false
                }
                mTouchY = y.toInt()
                startAnimator(1.0f)
            }
            MotionEvent.ACTION_MOVE -> {
                mTouchY = y.toInt()
                if (mPreSelect != mNewSelect) {
                    if (mNewSelect >= 0 && mNewSelect < mLetters!!.size) {
                        mSelect = mNewSelect
                        if (mListener != null) {
                            mListener!!.onLetterChange(mLetters!![mNewSelect])
                        }
                    }
                }
                invalidate()
                invalidate()
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                startAnimator(0f)
                mSelect = -1
            }
            else -> {
            }
        }
        return true
    }

    private fun startAnimator(value: Float) {
        if (mRatioAnimator == null) {
            mRatioAnimator = ValueAnimator()
        }
        mRatioAnimator!!.cancel()
        mRatioAnimator!!.setFloatValues(value)
        mRatioAnimator!!.addUpdateListener { valueAnimator ->
            mAnimationRatio = valueAnimator.animatedValue as Float
            //球弹到位的时候，并且点击的位置变了，即点击的时候显示当前选择位置
            if (mAnimationRatio == 1f && mPreSelect != mNewSelect) {
                if (mNewSelect >= 0 && mNewSelect < mLetters!!.size) {
                    mSelect = mNewSelect
                    if (mListener != null) {
                        mListener!!.onLetterChange(mLetters!![mNewSelect])
                    }
                }
            }
            invalidate()
        }
        mRatioAnimator!!.start()
    }

    fun setOnLetterChangeListener(listener: OnLetterChangeListener?) {
        mListener = listener
    }

    fun setLetters(letters: List<String>) {
        mLetters = letters
        this.invalidate()
    }

    interface OnLetterChangeListener {
        fun onLetterChange(letter: String?)
    }

    init {
        initAttribute(attrs, defStyleAttr)
        initData()
    }
}