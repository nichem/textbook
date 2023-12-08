package com.example.textbook.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.textbook.App
import com.example.textbook.AppViewModel
import com.example.textbook.R
import com.example.textbook.adapter.TextbookAdapter
import com.example.textbook.adapter.TextbookComparator
import com.example.textbook.database.Repository
import com.example.textbook.database.Textbook
import com.example.textbook.databinding.FragmentFavoriteBinding
import com.example.textbook.paging.TextbookPagingSource
import com.example.textbook.utils.getFile
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteFragment : Fragment() {
    private val appViewModel by lazy {
        ViewModelProvider(requireActivity())[AppViewModel::class.java]
    }

    private lateinit var binding: FragmentFavoriteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        binding = FragmentFavoriteBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.callback = textbookCallback
        binding.rv.adapter = adapter
        binding.rv.itemAnimator = null
        lifecycleScope.launch {
            appViewModel.favoriteFlow.collectLatest {
                adapter.submitData(it)
            }
        }
        appViewModel.selectItemLiveData.observe(this.viewLifecycleOwner) {
            if (it == null) binding.rv.clearFocus()
        }
        appViewModel.updateDownloadUILiveData.observe(this.viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }
    }

    private val adapter by lazy {
        TextbookAdapter(TextbookComparator())
    }

    private val textbookCallback = object : TextbookAdapter.Callback {
        override fun onFavoriteClick(adapter: TextbookAdapter, textbook: Textbook, position: Int) {
            AlertDialog.Builder(requireActivity())
                .setMessage("是否取消收藏${adapter.pureTitle(textbook.title)}?")
                .setPositiveButton("确认") { _, _ ->
                    lifecycleScope.launch {
                        val row = Repository.favoriteTextbook(textbook, false)
                        if (row > 0) {
                            adapter.refresh()
                            appViewModel.reloadAll()
                        }
                    }
                }
                .setNegativeButton("取消") { _, _ -> }
                .show()
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
            FavoriteFragment()
    }
}