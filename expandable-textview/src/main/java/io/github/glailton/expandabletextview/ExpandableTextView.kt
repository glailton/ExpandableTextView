package io.github.glailton.expandabletextview

import android.animation.*
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.*
import android.graphics.drawable.GradientDrawable.Orientation.*
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import io.github.glailton.expandabletextview.Constants.Companion.COLLAPSED_MAX_LINES
import io.github.glailton.expandabletextview.Constants.Companion.DEFAULT_ANIM_DURATION
import io.github.glailton.expandabletextview.Constants.Companion.DEFAULT_ELLIPSIZED_TEXT
import io.github.glailton.expandabletextview.Constants.Companion.EMPTY_SPACE
import io.github.glailton.expandabletextview.Constants.Companion.READ_LESS
import io.github.glailton.expandabletextview.Constants.Companion.READ_MORE

open class ExpandableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = R.attr.expandableTextView) : AppCompatTextView(context, attrs, defStyleAttr),
    View.OnClickListener {

    private var isCollapsing: Boolean = false
//    private lateinit var mAnimator: ValueAnimator
    private var mOriginalText: CharSequence? = null
    private var mCollapsedLines: Int? = null
    private var mReadMoreText: CharSequence = READ_MORE
    private var mReadLessText: CharSequence = READ_LESS
    private var isExpanded: Boolean = false
    private var mAnimationDuration: Int? = null
    private var foregroundColor: Int? = null
    private var mEllipsizeText: String? = null
    private var initialText: String? = null
    private var isUnderlined: Boolean? = null
    private var mEllipsizeTextColor: Int? = null
    private var textClickableSpan: TextClickableSpan = TextClickableSpan()

    private lateinit var visibleText: String

    override fun onClick(v: View?) {
//        if (mAnimator.isRunning) {
//            animatorReverse()
//            return
//        }
//
//        val endPosition = animateTo()
//        val startPosition = height
//
//        mAnimator.setIntValues(startPosition, endPosition)
//        animatorStart()
        toggle()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
//        if (!mAnimator.isRunning) {
            if (initialText.isNullOrBlank()) {
                initialText = text.toString()
                visibleText = visibleText()

                setEllipsizedText(isExpanded)
                setForeground(isExpanded)
            }
//        }
    }

    private fun toggle() {
        if (visibleText.isAllTextVisible()) {
            return
        }

        isExpanded = !isExpanded!!

        maxLines = if (!isExpanded) {
            mCollapsedLines!!
        } else {
            COLLAPSED_MAX_LINES
        }

        val startHeight = measuredHeight

        measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        val endHeight = measuredHeight

        animationSet(startHeight, endHeight).apply {
            duration = mAnimationDuration?.toLong()!!
            start()

            addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    if (!isExpanded!!)
                        setEllipsizedText(isExpanded!!)
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            })
        }

        setEllipsizedText(isExpanded!!)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        mOriginalText = text
        super.setText(text, type)
    }

    //private functions
    init {
        context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView).apply {
            try {
                mCollapsedLines = getInt(R.styleable.ExpandableTextView_collapsedLines, COLLAPSED_MAX_LINES)
                mAnimationDuration = getInt(R.styleable.ExpandableTextView_animDuration, DEFAULT_ANIM_DURATION)
                mReadMoreText = getString(R.styleable.ExpandableTextView_readMoreText) ?: READ_MORE
                mReadLessText = getString(R.styleable.ExpandableTextView_readLessText) ?: READ_LESS
                foregroundColor = getColor(R.styleable.ExpandableTextView_foregroundColor, Color.TRANSPARENT)
                mEllipsizeText = getString(R.styleable.ExpandableTextView_ellipsizeText) ?: READ_MORE
                isUnderlined = getBoolean(R.styleable.ExpandableTextView_isUnderlined, false)
                isExpanded = getBoolean(R.styleable.ExpandableTextView_isExpanded, false)
                mEllipsizeTextColor = getColor(R.styleable.ExpandableTextView_ellipsizeTextColor, Color.BLUE)
            } finally {
                recycle()
            }
        }

        if (!isExpanded)
            maxLines = mCollapsedLines!!
        setOnClickListener(this)
//        initAnimator()
    }

    private fun initAnimator() {
//        mAnimator = ValueAnimator.ofInt(-1, -1)
//            .setDuration(DEFAULT_ANIM_DURATION.toLong())
//        mAnimator.interpolator = AccelerateDecelerateInterpolator()
//        mAnimator.addUpdateListener { updateHeight(it.animatedValue as Int) }
//
//        mAnimator.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationStart(animation: Animator?) {
//                if (isCollapsed()) {
//                    isCollapsing = false
//                    maxLines = COLLAPSED_MAX_LINES
//                    setEllipsizedText(isExpanded)
//                } else {
//                    isCollapsing = true
//                }
//            }
//
//            override fun onAnimationEnd(animation: Animator?) {
//                if (!isCollapsed() && isCollapsing) {
//                    maxLines = mCollapsedLines!!
//                    isCollapsing = false
//                }
//                setWrapContent()
//            }
//        })
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
            layout.getLineBottom(mCollapsedLines!! - 1) +
                    layout.bottomPadding + getPaddingHeight()
        }
    }

    private fun getPaddingHeight(): Int {
        return compoundPaddingBottom + compoundPaddingTop
    }

    private fun animatorStart() {
//        mAnimator.start()
    }

    private fun animatorReverse() {
        isCollapsing = !isCollapsing
//        mAnimator.reverse()
    }

    private fun isCollapsed(): Boolean {
        return COLLAPSED_MAX_LINES != maxLines
    }

    private fun ellipsizeColored() {
        val end: Int = layout.getLineEnd(mCollapsedLines!! - 1)
        val text: CharSequence? = text

        val chars: Int = layout.getLineEnd(mCollapsedLines!! - 1) -
                layout.getLineStart(mCollapsedLines!! - 1)

        val additionalGap = 4

        if (chars + additionalGap < mReadMoreText!!.length) {
            return
        }

        val builder = SpannableStringBuilder(text)
        builder.replace(end - mReadMoreText!!.length, end + mReadMoreText!!.length, DEFAULT_ELLIPSIZED_TEXT + mReadMoreText + EMPTY_SPACE)
        Log.v(TAG, builder.toString())
        builder.setSpan(ForegroundColorSpan(Color.BLUE), end - mReadMoreText!!.length,
            end + mReadMoreText!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        setTextNoCaching(builder)
    }

    private fun setTextNoCaching(text: CharSequence?) {
        super.setText(text, BufferType.NORMAL)
    }

    private fun deEllipsize() {
        val text: CharSequence? = mOriginalText
        val builder = SpannableStringBuilder(text).append(EMPTY_SPACE + mReadLessText)
        builder.setSpan(ForegroundColorSpan(Color.BLUE), text!!.length,
            text.length +  mReadLessText!!.length + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        setTextNoCaching(builder)
    }

    inner class TextClickableSpan: ClickableSpan() {
        override fun onClick(widget: View) {
            isExpanded = !isExpanded
        }

//        override fun updateDrawState(ds: TextPaint) {
//            ds.color = anchorTextColor
//        }
    }

    private fun setEllipsizedText(isExpanded: Boolean) {
        if (initialText?.isBlank()!!)
            return

        text = if (isExpanded || visibleText.isAllTextVisible() || mCollapsedLines!! == COLLAPSED_MAX_LINES) {
            initialText
        } else {
            SpannableStringBuilder(
                visibleText.substring(0,
                    visibleText.length - (mEllipsizeText.orEmpty().length + DEFAULT_ELLIPSIZED_TEXT.length)))
                .append(DEFAULT_ELLIPSIZED_TEXT)
                .append(mEllipsizeText.orEmpty().span())
        }
    }

    private fun visibleText(): String {
        var end = 0

        return if (mCollapsedLines!! < COLLAPSED_MAX_LINES) {
            for (i in 0 until mCollapsedLines!!) {
                if (layout.getLineEnd(i) != 0)
                    end = layout.getLineEnd(i)
            }
            initialText?.substring(0, end - mEllipsizeText!!.length)!!
        }else {
            initialText!!
        }


    }

    private fun setForeground(isExpanded: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            foreground = GradientDrawable(BOTTOM_TOP, intArrayOf(foregroundColor!!, Color.TRANSPARENT))
            foreground.alpha = if (isExpanded) {
                MIN_VALUE_ALPHA
            } else {
                MAX_VALUE_ALPHA
            }
        }
    }

    private fun animationSet(startHeight: Int, endHeight: Int): AnimatorSet {
        return AnimatorSet().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                playTogether(
                    ObjectAnimator.ofInt(
                        this,
                        ANIMATION_PROPERTY_MAX_HEIGHT,
                        startHeight,
                        endHeight
                    ),
                    ObjectAnimator.ofInt(
                        this@ExpandableTextView.foreground,
                        ANIMATION_PROPERTY_ALPHA,
                        foreground.alpha,
                        MAX_VALUE_ALPHA - foreground.alpha
                    )
                )
            }
        }
    }

    private fun String.isAllTextVisible(): Boolean = this == text

    private fun String.span(): SpannableString =
        SpannableString(this).apply {
            setSpan(
                ForegroundColorSpan(mEllipsizeTextColor!!),
                0,
                this.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (isUnderlined!!)
                setSpan(
                    UnderlineSpan(),
                    0,
                    this.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
        }

    companion object {
        const val TAG = "ExpandableTextView"
        const val MAX_VALUE_ALPHA = 255
        const val MIN_VALUE_ALPHA = 0
        const val ANIMATION_PROPERTY_MAX_HEIGHT = "maxHeight"
        const val ANIMATION_PROPERTY_ALPHA = "alpha"
    }
}