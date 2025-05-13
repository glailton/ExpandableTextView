package io.github.glailton.expandabletextview.demo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.glailton.expandabletextview.demo.R

class RecyclerViewFragment : Fragment() {
    private val dummyTexts = listOf(
        "Texto curto.",
        "Este é um texto um pouco maior que deve ser truncado ao exibir no card.",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin a justo in sapien laoreet accumsan. Sed fringilla risus non massa ultrices. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin a justo in sapien laoreet accumsan. Sed fringilla risus non massa ultrices"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ExpandableCardAdapter(dummyTexts)
    }
}