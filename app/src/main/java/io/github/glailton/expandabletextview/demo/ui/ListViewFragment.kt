package io.github.glailton.expandabletextview.demo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.content.ContextCompat
import io.github.glailton.expandabletextview.EXPAND_TYPE_DEFAULT
import io.github.glailton.expandabletextview.ExpandableTextView
import io.github.glailton.expandabletextview.demo.R
import io.github.glailton.expandabletextview.demo.databinding.FragmentListViewBinding

class ListViewFragment : Fragment() {

    private var _binding: FragmentListViewBinding? = null
    val binding get() = _binding

    private val dummyTexts = listOf(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        "Short text.",
        "Another very very very very very very very very very very long text that will probably need to be collapsed when displayed! dshgsdfgdfgkjdgçsdjfglpgçsodifghçdlkjb´sdopigj´wpothjw´srpofbjsd~çlbm ´sjb ´pofbj ´dspofb s´pfobgj[sapfogj´dsaopfigjádiogh´-qeir9jgádpofbgj´~dsofpbjdóibj´sdoifbjdosfibjsdfbn´sfioghj´sdfophjsdfplhgsj~sdfpjge"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListViewBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.listView)
        listView.adapter = object : ArrayAdapter<String>(requireActivity(), R.layout.item, R.id.listview_review_record_content, dummyTexts) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val rowView = super.getView(position, convertView, parent)

                val reviewContent = rowView.findViewById<ExpandableTextView>(R.id.listview_review_record_content)
                reviewContent.text = getItem(position)

                reviewContent
                    .setReadMoreText(getString(R.string.view_more))
                    .setReadLessText(getString(R.string.view_less))
                    .setCollapsedLines(3)
                    .setIsExpanded(false)
                    .setIsUnderlined(true)
                    .setExpandType(EXPAND_TYPE_DEFAULT)
                    .setEllipsizedTextColor(ContextCompat.getColor(parent.context, R.color.purple_700))

                return rowView
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}