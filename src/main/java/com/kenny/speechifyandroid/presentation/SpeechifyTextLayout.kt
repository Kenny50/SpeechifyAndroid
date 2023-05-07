package com.kenny.speechifyandroid.presentation

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import com.kenny.speechifyandroid.R
import com.kenny.speechifyandroid.model.response.WordChunk
import com.kenny.speechifyandroid.util.findNearestSmallerOrEqualValue
import kotlin.properties.Delegates.observable

//todo api flow
class SpeechifyTextLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    private val textContent: TextView by lazy {
        this.findViewById(R.id.text_content)
    }

    private val scLayout: ScrollView by lazy { this }

    private var curHighLightTextIndex: WordChunk? by observable(
        WordChunk(0, 0, 0, 0, "", "")
    ) { _, old, new ->
        if (old == new) return@observable
        highLightIndexRange(new?.start ?: 0, new?.end ?: 0)
        clearHighLightIndexRange(old?.start ?: 0, old?.end ?: 0)

        if (isCurrentHighLightAreaDisplayed() || !isScrolling) {
            scrollToCurrentLine(new?.end ?: 0)
        }
    }

    private var charIndexByLines: LongArray = LongArray(0)
    private val mainLooper = Looper.getMainLooper()

    var highLightTextManager: HighLightTextManager? = null
    var updateInterval: Long = 50
        set(value) {
            require(value > 0) { "interval time should be greater than 0" }
            field = value
        }
    var playerPosition: () -> Long = { 0L }
    var isScrolling = false

    init {
        @LayoutRes val layoutId: Int
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.Speechify,
            0, 0
        ).apply {
            layoutId = try {
                getResourceId(R.styleable.Speechify_textLayoutId, R.layout.speechify_text_layout)
            } catch (e: Exception) {
                R.layout.speechify_text_layout
            } finally {
                recycle()
            }
            inflate(context, layoutId, this@SpeechifyTextLayout)
        }
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        isScrolling =
            ev?.action == MotionEvent.ACTION_MOVE || ev?.action == MotionEvent.ACTION_SCROLL
        return super.onTouchEvent(ev)
    }

    //todo start when api back
    fun enableLooper() {
        Thread {
            while (true) {
                Handler(mainLooper).post {
                    curHighLightTextIndex =
                        highLightTextManager?.getHighLightTextIndexByTime(playerPosition())
                }
                Thread.sleep(updateInterval)
            }
        }.start()
    }

    fun setText(text: String) {
        textContent.setText(SpannableString(text), TextView.BufferType.SPANNABLE)
        textContent.post {
            updateCharIndexArray()
        }
        //get api
    }

    private fun isCurrentHighLightAreaDisplayed(): Boolean {
        val tvLayout = textContent.layout
        val scrolledY = scLayout.scrollY
        if (tvLayout == null) return false

        val firstVisibleLine = tvLayout.getLineForVertical(scrolledY)
        val lastVisibleLine = tvLayout.getLineForVertical(scrolledY + this.height)
        val curLine = charIndexByLines.findNearestSmallerOrEqualValue(
            curHighLightTextIndex?.end?.toLong() ?: 0L
        ) ?: 0
        return curLine in firstVisibleLine..lastVisibleLine
    }

    private fun updateCharIndexArray() {
        charIndexByLines = textContent.let { tv ->
            List((tv.lineCount - 1).coerceAtLeast(0)) {
                tv.layout.getLineEnd(it).toLong()
            }
        }.toLongArray()
    }

    private fun scrollToCurrentLine(lineIndex: Int) {
        bringPointIntoView(
            charIndexByLines.findNearestSmallerOrEqualValue(lineIndex.toLong()) ?: 0
        )
    }

    /**
     * call when time bar manually change, currently if time bar scroll to new position invisible, won't follow
     * */
    fun scrollToCurrentLine() {
        curHighLightTextIndex?.end?.toLong()?.let {
            bringPointIntoView(
                charIndexByLines.findNearestSmallerOrEqualValue(it) ?: 0
            )
        }
    }

    private fun bringPointIntoView(offset: Int) {
        val y = offset * textContent.lineHeight
        smoothScrollTo(0, y)
    }

    private fun getText(): Spannable {
        return textContent.text as Spannable
    }

    private fun highLightIndexRange(start: Int, end: Int, @ColorInt color: Int = Color.LTGRAY) {
        getText().setSpan(
            BackgroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
    }

    private fun clearHighLightIndexRange(
        start: Int,
        end: Int,
        @ColorInt color: Int = Color.TRANSPARENT
    ) {
        getText().setSpan(
            BackgroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
    }
}