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
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import io.github.glailton.expandabletextview.Constants.Companion.COLLAPSED_MAX_LINES
import io.github.glailton.expandabletextview.Constants.Companion.DEFAULT_ANIM_DURATION
import io.github.glailton.expandabletextview.Constants.Companion.DEFAULT_ELLIPSIZED_TEXT
import io.github.glailton.expandabletextview.Constants.Companion.READ_LESS
import io.github.glailton.expandabletextview.Constants.Companion.READ_MORE

class ExpandableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = R.attr.expandableTextView) : AppCompatTextView(context, attrs, defStyleAttr),
    View.OnClickListener {

    private var mOriginalText: CharSequence? = ""
    private var mCollapsedLines: Int? = 0
    private var mReadMoreText: CharSequence = READ_MORE
    private var mReadLessText: CharSequence = READ_LESS
    private var isExpanded: Boolean = false
    private var mAnimationDuration: Int? = 0
    private var foregroundColor: Int? = 0
    private var initialText: String? = ""
    private var isUnderlined: Boolean? = false
    private var mEllipsizeTextColor: Int? = 0

    private lateinit var visibleText: String

    override fun onClick(v: View?) {
        toggle()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
            if (initialText.isNullOrBlank()) {
                initialText = text.toString()
                visibleText = visibleText()

                setEllipsizedText(isExpanded)
                setForeground(isExpanded)
            }
    }

    private fun toggle() {
        if (visibleText.isAllTextVisible()) {
            return
        }

        isExpanded = !isExpanded

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
                    if (!isExpanded)
                        setEllipsizedText(isExpanded)
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            })
        }

        setEllipsizedText(isExpanded)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        mOriginalText = text
        super.setText(text, type)
    }

    fun setReadMoreText(readMore: String) {
        mReadMoreText = readMore
    }

    fun setReadLessText(readLess: String) {
        mReadLessText = readLess
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
                isUnderlined = getBoolean(R.styleable.ExpandableTextView_isUnderlined, false)
                isExpanded = getBoolean(R.styleable.ExpandableTextView_isExpanded, false)
                mEllipsizeTextColor = getColor(R.styleable.ExpandableTextView_ellipsizeTextColor, Color.BLUE)
            } finally {
                this.recycle()
            }
        }

        if (!isExpanded)
            maxLines = mCollapsedLines!!
        setOnClickListener(this)
    }

    private fun setEllipsizedText(isExpanded: Boolean) {
        if (initialText?.isBlank()!!)
            return

        text = if (isExpanded || visibleText.isAllTextVisible() || mCollapsedLines!! == COLLAPSED_MAX_LINES) {
            SpannableStringBuilder(
                initialText.toString())                
                .append(mReadLessText.toString().span())
        } else {
            val endIndex = if (visibleText.length - (mReadMoreText.toString().length + DEFAULT_ELLIPSIZED_TEXT.length) < 0) visibleText.length
                else visibleText.length - (mReadMoreText.toString().length + DEFAULT_ELLIPSIZED_TEXT.length)
            SpannableStringBuilder(
                visibleText.substring(0, endIndex))
                .append(DEFAULT_ELLIPSIZED_TEXT)
                .append(mReadMoreText.toString().span())
        }
    }

    private fun visibleText(): String {
        try {
            var end = 0

            return if (mCollapsedLines!! < COLLAPSED_MAX_LINES) {
                for (i in 0 until mCollapsedLines!!) {
                    if (layout.getLineEnd(i) == 0)
                        break
                    else
                        end = layout.getLineEnd(i)
                }
                initialText?.substring(0, end - mReadMoreText.toString().length)!!
            }else {
                initialText!!
            }
        }catch (e: Exception){
            return initialText!!
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

@BindingAdapter("readMoreText")
fun ExpandableTextView.readMoreText(readMore: String) {
    this.setReadMoreText(readMore)
}

@BindingAdapter("readLessText")
fun ExpandableTextView.readLessText(readLess: String) {
    this.setReadLessText(readLess)
}
