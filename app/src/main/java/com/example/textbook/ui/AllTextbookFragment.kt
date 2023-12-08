package com.example.textbook.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingDataAdapter
import androidx.paging.cachedIn
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.textbook.App
import com.example.textbook.AppViewModel
import com.example.textbook.R
import com.example.textbook.adapter.TextbookAdapter
import com.example.textbook.adapter.TextbookComparator
import com.example.textbook.database.Repository
import com.example.textbook.database.Textbook
import com.example.textbook.databinding.FragmentAllTextbookBinding
import com.example.textbook.paging.TextbookPagingSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AllTextbookFragment : Fragment() {
    private val appViewModel by lazy {
        ViewModelProvider(requireActivity())[AppViewModel::class.java]
    }

    private lateinit var binding: FragmentAllTextbookBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_all_textbook, container, false)
        binding = FragmentAllTextbookBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.callback = textbookCallback
        binding.rv.adapter = adapter
        binding.rv.itemAnimator = null
        lifecycleScope.launch {
            appViewModel.allFlow.collectLatest {
                adapter.submitData(it)
            }
        }
        appViewModel.selectItemLiveData.observe(this.viewLifecycleOwner) {
            if (it == null) binding.rv.clearFocus()
        }
        appViewModel.updateDownloadUILiveData.observe(this.viewLifecycleOwner) {
            Log.d("test","++++++++++++++++++++++++++")
            adapter.notifyDataSetChanged()
        }
    }

    private val adapter by lazy {
        TextbookAdapter(TextbookComparator())
    }

    private val textbookCallback = object : TextbookAdapter.Callback {
        override fun onFavoriteClick(adapter: TextbookAdapter, textbook: Textbook, position: Int) {
            lifecycleScope.launch {
                val row = Repository.favoriteTextbook(textbook, !textbook.isFavorite)
                if (row > 0) {
                    textbook.isFavorite = !textbook.isFavorite
                    adapter.notifyItemChanged(position)
                    appViewModel.reloadFavorite()
                }
            }
        }

        override fun onItemClick(adapter: TextbookAdapter, textbook: Textbook, position: Int) {
            appViewModel.selectItem(textbook)
        }

        override fun onDownloadClick(adapter: TextbookAdapter, textbook: Textbook, position: Int) {
            appViewModel.download(textbook)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AllTextbookFragment()
    }
}