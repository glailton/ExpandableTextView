package io.github.glailton.expandabletextview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView

open class ExpandableTextView : AppCompatTextView, View.OnClickListener {

    private var isCollapsing: Boolean = false
    private lateinit var mAnimator: ValueAnimator
    private var mOriginalText: CharSequence? = null

    constructor(context: Context):
            super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?):
            super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):
            super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onClick(v: View?) {
        if (mAnimator.isRunning) {
            animatorReverse()
            return
        }

        val endPosition = animateTo()
        val startPosition = getHeight()

        mAnimator.setIntValues(startPosition, endPosition)
        animatorStart()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (lineCount <= COLLAPSED_MAX_LINES) {
            deEllipsize()
            this.isClickable = false
        } else {
            this.isClickable = true
            if (!mAnimator.isRunning && isCollapsed()) {
                ellipsizeColored()
            }
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        mOriginalText = text
        super.setText(text, type)
    }

    //private functions
    private fun init() {
        maxLines = COLLAPSED_MAX_LINES
        setOnClickListener(this)
        initAnimator()
    }

    private fun initAnimator() {
        mAnimator = ValueAnimator.ofInt(-1, -1)
            .setDuration(450)
        mAnimator.interpolator = AccelerateDecelerateInterpolator()
        mAnimator.addUpdateListener { updateHeight(it.animatedValue as Int) }

        mAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                if (isCollapsed()) {
                    isCollapsing = false
                    maxLines = Int.MAX_VALUE
                    deEllipsize()
                } else {
                    isCollapsing = true
                }
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (!isCollapsed() && isCollapsing) {
                    maxLines = COLLAPSED_MAX_LINES
                    ellipsizeColored()
                    isCollapsing = false
                }
                setWrapContent()
            }
        })
    }

    private fun updateHeight(animatedValue: Int) {
        val layoutParams: ViewGroup.LayoutParams = layoutParams
        layoutParams.height = animatedValue
        setLayoutParams(layoutParams)
    }

    private fun setWrapContent() {
        val layoutParams: ViewGroup.LayoutParams = layoutParams
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        setLayoutParams(layoutParams)
    }

    private fun animateTo(): Int {
        return if (isCollapsed()) {
            layout.height + getPaddingHeight()
        } else {
            layout.getLineBottom(COLLAPSED_MAX_LINES - 1) +
                    layout.bottomPadding + getPaddingHeight()
        }
    }

    private fun getPaddingHeight(): Int {
        return compoundPaddingBottom + compoundPaddingTop
    }

    private fun animatorStart() {
        mAnimator.start()
    }

    private fun animatorReverse() {
        isCollapsing = !isCollapsing
        mAnimator.reverse()
    }

    private fun isCollapsed(): Boolean {
        return Int.MAX_VALUE != maxLines
    }

    private fun ellipsizeColored() {
        val end: Int = layout.getLineEnd(COLLAPSED_MAX_LINES - 1)
        val text: CharSequence? = text

        val chars: Int = layout.getLineEnd(COLLAPSED_MAX_LINES - 1) -
                layout.getLineStart(COLLAPSED_MAX_LINES - 1)

        val additionalGap = 4

        if (chars + additionalGap < POSTFIX.length) {
            return
        }

        val builder = SpannableStringBuilder(text)
        builder.replace(end - POSTFIX.length, end, POSTFIX)
        builder.setSpan(ForegroundColorSpan(Color.BLACK), end - POSTFIX.length,
            end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        setTextNoCaching(builder)
    }

    private fun setTextNoCaching(text: CharSequence?) {
        super.setText(text, BufferType.NORMAL)
    }

    private fun deEllipsize() {
        super.setText(mOriginalText)
    }

    companion object {
        const val COLLAPSED_MAX_LINES = 3
        const val POSTFIX  = "...see more "
    }


}