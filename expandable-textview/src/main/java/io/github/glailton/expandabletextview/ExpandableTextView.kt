package io.github.glailton.expandabletextview

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.Orientation.BOTTOM_TOP
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import io.github.glailton.expandabletextview.Constants.Companion.COLLAPSED_MAX_LINES
import io.github.glailton.expandabletextview.Constants.Companion.DEFAULT_ANIM_DURATION
import io.github.glailton.expandabletextview.Constants.Companion.DEFAULT_ELLIPSIZED_TEXT
import io.github.glailton.expandabletextview.Constants.Companion.EMPTY_SPACE
import io.github.glailton.expandabletextview.Constants.Companion.READ_LESS
import io.github.glailton.expandabletextview.Constants.Companion.READ_MORE
import java.lang.Integer.min
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Expand the text within layout
 */
const val EXPAND_TYPE_LAYOUT = 0

/**
 * Expand the text as a popup
 */
const val EXPAND_TYPE_POPUP = 1

/**
 * Default expand type which will layout
 */
const val EXPAND_TYPE_DEFAULT = EXPAND_TYPE_LAYOUT

/**
 * This value define how much space should be consumed by ellipsize text to represent itself.
 */
const val ELLIPSIZE_TEXT_LENGTH_MULTIPLIER = 2.0

class ExpandableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = R.attr.expandableTextView) : AppCompatTextView(context, attrs, defStyleAttr),
    View.OnClickListener {

    private var mOriginalText: CharSequence? = ""
    private var mCollapsedLines = 0
    private var mReadMoreText: CharSequence = READ_MORE
    private var mReadLessText: CharSequence = READ_LESS
    var isExpanded: Boolean = false
        private set
    private var mAnimationDuration: Int? = 0
    private var foregroundColor: Int? = 0
    private var initialText = ""
    private var isUnderlined: Boolean? = false
    private var mEllipsizeTextColor: Int? = 0
    var expandType = EXPAND_TYPE_DEFAULT
        private set
    private var fadeAnimationEnabled = true
    private lateinit var collapsedVisibleText: String

    override fun onClick(v: View?) {
        toggleExpandableTextView()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (initialText.isBlank()) {
            initialText = text.toString()
            post {
                collapsedVisibleText = collapsedVisibleText()
                //Override expand property in specific scenarios
                isExpanded = if (collapsedVisibleText.isAllTextVisible()) {
                    true
                } else when (expandType) {
                    EXPAND_TYPE_POPUP -> false
                    else -> isExpanded
                }
                configureMaxLines()
                setEllipsizedText(isExpanded)
                setForeground(isExpanded)
            }
        }
    }

    private fun toggleExpandableTextView() {
        if (collapsedVisibleText.isAllTextVisible()) return

        when (expandType) {
            EXPAND_TYPE_LAYOUT -> animateHeightTransition()
            EXPAND_TYPE_POPUP -> showPopupText()
            else -> throw UnsupportedOperationException("No toggle operation provided for expand type[$expandType]")
        }
    }


    private fun configureMaxLines(){
        if (mCollapsedLines < COLLAPSED_MAX_LINES){
            maxLines = when(expandType) {
                EXPAND_TYPE_LAYOUT -> if (isExpanded) COLLAPSED_MAX_LINES else mCollapsedLines
                EXPAND_TYPE_POPUP -> mCollapsedLines
                else -> maxLines
            }
        }
    }

    private fun animateHeightTransition() {
        val expanding = !isExpanded
        val startHeight = height

        isExpanded = expanding
        configureMaxLines()
        setEllipsizedText(expanding)

        measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        val endHeight = measuredHeight

        if (startHeight == endHeight) return //

        val duration = mAnimationDuration?.toLong() ?: DEFAULT_ANIM_DURATION.toLong()

        val animator = ValueAnimator.ofInt(startHeight, endHeight).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()

            addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                layoutParams.height = value
                requestLayout()

                clipBounds = Rect(0, 0, width, value)
            }

            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    visibility = View.VISIBLE
                    if (fadeAnimationEnabled) {
                        alpha = 0f
                        animate().alpha(1f).setDuration(duration / 2).start()
                    } else {
                        alpha = 1f
                    }
                }

                override fun onAnimationEnd(animation: Animator) {
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    requestLayout()
                    clipBounds = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    clipBounds = null
                }

                override fun onAnimationRepeat(animation: Animator) {}
            })
        }

        animator.start()
    }

    private fun showPopupText() {
        AlertDialog.Builder(context)
            .setTitle("")
            .setMessage(initialText)
            .setNegativeButton(android.R.string.ok, null)
            .show()
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        mOriginalText = text
        super.setText(text, type)
    }

    fun setReadMoreText(readMore: String): ExpandableTextView {
        mReadMoreText = readMore
        return this
    }

    fun setReadLessText(readLess: String): ExpandableTextView {
        mReadLessText = readLess
        return this
    }

    fun setCollapsedLines(collapsedLines: Int): ExpandableTextView {
        mCollapsedLines = collapsedLines
        return this
    }

    fun setIsExpanded(isExpanded: Boolean): ExpandableTextView {
        this.isExpanded = isExpanded
        return this
    }

    fun setAnimationDuration(animationDuration: Int): ExpandableTextView {
        mAnimationDuration = animationDuration
        return this
    }

    fun setIsUnderlined(isUnderlined: Boolean): ExpandableTextView {
        this.isUnderlined = isUnderlined
        return this
    }

    fun setEllipsizedTextColor(ellipsizeTextColor: Int): ExpandableTextView {
        mEllipsizeTextColor = ellipsizeTextColor
        return this
    }

    fun setForegroundColor(foregroundColor: Int): ExpandableTextView {
        this.foregroundColor = foregroundColor
        return this
    }

    fun setExpandType(expandType: Int): ExpandableTextView {
        this.expandType = expandType
        return this
    }

    fun setFadeAnimationEnabled(fadeAnimationEnabled: Boolean): ExpandableTextView {
        this.fadeAnimationEnabled = fadeAnimationEnabled
        return this
    }

    fun toggle() {
        toggleExpandableTextView()
    }

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
                expandType = getInt(R.styleable.ExpandableTextView_expandType, EXPAND_TYPE_DEFAULT)
                fadeAnimationEnabled = getBoolean(R.styleable.ExpandableTextView_fadeAnimationEnabled, true)

            } finally {
                this.recycle()
            }
        }

        configureMaxLines()
        setOnClickListener(this)
    }

    private fun setEllipsizedText(isExpanded: Boolean) {
        if (initialText.isBlank())
            return

        text = if (collapsedVisibleText.isAllTextVisible()){
            initialText
        } else{
            when(expandType){
                EXPAND_TYPE_POPUP -> getCollapseText()
                EXPAND_TYPE_LAYOUT -> if (isExpanded) getExpandText() else getCollapseText()
                else -> throw UnsupportedOperationException("No supported expand mechanism provided for expand type[$expandType]")
            }
        }
    }

    private fun getExpandText(): SpannableStringBuilder {
        return SpannableStringBuilder(initialText)
            .append(EMPTY_SPACE)
            .append(mReadLessText.toString().span())
    }

    private fun getCollapseText(): SpannableStringBuilder {

        val ellipseTextLength = ((mReadMoreText.length + DEFAULT_ELLIPSIZED_TEXT.length) * ELLIPSIZE_TEXT_LENGTH_MULTIPLIER).roundToInt()
        val textAvailableLength = max(0, collapsedVisibleText.length - ellipseTextLength)
        val ellipsizeAvailableLength = min(collapsedVisibleText.length, DEFAULT_ELLIPSIZED_TEXT.length)
        val readMoreAvailableLength = min(collapsedVisibleText.length - ellipsizeAvailableLength, mReadMoreText.length)

        return SpannableStringBuilder(collapsedVisibleText.substring(0, textAvailableLength))
            .append(DEFAULT_ELLIPSIZED_TEXT.substring(0, ellipsizeAvailableLength))
            .append(mReadMoreText.substring(0, readMoreAvailableLength).span())
    }

    private fun collapsedVisibleText(): String {
        try {
            var finalTextOffset = 0
            if (mCollapsedLines < COLLAPSED_MAX_LINES) {
                for (i in 0 until mCollapsedLines) {
                    val textOffset = layout.getLineEnd(i)
                    if (textOffset == initialText.length)
                        return initialText
                    else
                        finalTextOffset = textOffset
                }
                return initialText.substring(0, finalTextOffset)
            }else {
                return initialText
            }
        }catch (e: Exception){
            e.printStackTrace()
            return initialText
        }
    }

    private fun setForeground(isExpanded: Boolean) {
        foreground = GradientDrawable(BOTTOM_TOP, intArrayOf(foregroundColor!!, Color.TRANSPARENT))
        foreground.alpha = if (isExpanded) {
            MIN_VALUE_ALPHA
        } else {
            MAX_VALUE_ALPHA
        }
    }

    private fun String.isAllTextVisible(): Boolean = this == text.toString()

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
        const val MAX_VALUE_ALPHA = 255
        const val MIN_VALUE_ALPHA = 0
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
