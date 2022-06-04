package io.github.glailton.expandabletextview

import android.widget.TextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

fun TextView.getTextLineCount(text: String, lineCount: (Int) -> (Unit)) {
    val params: PrecomputedTextCompat.Params = TextViewCompat.getTextMetricsParams(this)
    val ref: WeakReference<TextView>? = WeakReference(this)

    GlobalScope.launch(Dispatchers.Default) {
        val text = PrecomputedTextCompat.create(text, params)
        GlobalScope.launch(Dispatchers.Main) {
            ref?.get()?.let { textView ->
                TextViewCompat.setPrecomputedText(textView, text)
                lineCount.invoke(textView.lineCount)
            }
        }
    }
}