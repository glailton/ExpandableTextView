package io.github.glailton.expandabletextview.demo.ui

import android.os.Bundle
import android.text.SpannableString
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import io.github.glailton.expandabletextview.EXPAND_TYPE_LAYOUT
import io.github.glailton.expandabletextview.EXPAND_TYPE_POPUP
import io.github.glailton.expandabletextview.demo.R
import io.github.glailton.expandabletextview.demo.databinding.FragmentExamplesBinding

class ExamplesFragment : Fragment() {

    private var _binding: FragmentExamplesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExamplesBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.expandTvProg
            .setAnimationDuration(500)
            .setReadMoreText("View More")
            .setReadLessText("View Less")
            .setCollapsedLines(3)
            .setIsExpanded(true)
            .setIsUnderlined(true)
            .setExpandType(EXPAND_TYPE_POPUP)
            .setEllipsizedTextColor(ContextCompat.getColor(requireActivity(), R.color.purple_200))

        binding.expandTvProg.text =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." +
                    "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat." +
                    "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur." +
                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."

        binding.expandTvProg.setOnClickListener {
            binding.expandTvProg.toggle()
            if (binding.expandTvVeryLong.isExpanded && binding.expandTvVeryLong.expandType == EXPAND_TYPE_LAYOUT)
                binding.expandTvVeryLong.toggle()
        }

        binding.expandTvSpannable
            .setAnimationDuration(500)
            .setReadMoreText("View More")
            .setReadLessText("View Less")
            .setCollapsedLines(3)
            .setIsExpanded(false)
            .setIsUnderlined(true)
            .setExpandType(EXPAND_TYPE_LAYOUT)
            .setEllipsizedTextColor(ContextCompat.getColor(requireActivity(), R.color.purple_200))

        binding.expandTvSpannable.setText(SpannableString("quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."), TextView.BufferType.SPANNABLE)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}