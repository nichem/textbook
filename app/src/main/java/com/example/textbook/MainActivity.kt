package com.example.textbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.example.textbook.App.Companion.PAGE_SIZE
import com.example.textbook.App.Companion.app
import com.example.textbook.databinding.ActivityMainBinding
import com.example.textbook.paging.TextbookPagingSource
import com.example.textbook.ui.AllTextbookFragment
import com.example.textbook.ui.FavoriteFragment
import com.example.textbook.utils.DataUtils.generateData
import com.example.textbook.utils.DataUtils.isGenerate
import com.example.textbook.utils.showLoading
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val appViewModel by lazy {
        ViewModelProvider(this)[AppViewModel::class.java]
    }

    private val tabList = listOf("收藏", "书库")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initDatabase()
        binding.vp2.adapter = adapter
        TabLayoutMediator(binding.tl, binding.vp2) { tab, pos ->
            tab.text = tabList[pos]
        }.attach()

        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val key = v.text.trim().toString()
                if (key.isNotBlank()) {
                    appViewModel.search(key)
                    //滑到书库界面
                    binding.vp2.setCurrentItem(1, true)
                }
                KeyboardUtils.hideSoftInput(this)
            }
            true
        }
    }

    private fun initDatabase() = lifecycleScope.launch {
        if (isGenerate()) {
            val loading = showLoading("初始化数据中...")
            generateData(assets)
            loading.dismiss()
        }
    }

    private val adapter = object : FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return tabList.size
        }

        override fun createFragment(position: Int): Fragment {
            Log.d("test", "createFragment $position")
            return when (position) {
                0 -> FavoriteFragment.newInstance()
                else -> AllTextbookFragment.newInstance()
            }
        }

    }

    private var time = 0L
    override fun onBackPressed() {
        if (appViewModel.isSearchState()) appViewModel.quitSearch()
        else {
            val tmp = SystemClock.elapsedRealtime()
            if (tmp - time > 1000L) ToastUtils.showShort("再按返回退出应用")
            else super.onBackPressed()
            time = tmp
        }
    }


}