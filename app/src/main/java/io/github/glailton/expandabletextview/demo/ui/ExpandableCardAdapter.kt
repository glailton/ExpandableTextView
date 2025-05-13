package io.github.glailton.expandabletextview.demo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.glailton.expandabletextview.ExpandableTextView
import io.github.glailton.expandabletextview.demo.R

class ExpandableCardAdapter(private val items: List<String>) :
    RecyclerView.Adapter<ExpandableCardAdapter.ExpandableCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpandableCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expandable_card, parent, false)
        return ExpandableCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpandableCardViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ExpandableCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val expandableTextView =
            itemView.findViewById<ExpandableTextView>(R.id.expandableTextView)

        fun bind(text: String) {
            expandableTextView.text = text
            expandableTextView
                .setReadMoreText(itemView.context.getString(R.string.view_more))
                .setReadLessText(itemView.context.getString(R.string.view_less))
                .setCollapsedLines(3)
                .setIsExpanded(false)
                .setIsUnderlined(true)
        }
    }
}
