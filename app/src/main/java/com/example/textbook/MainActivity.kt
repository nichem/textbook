package com.example.textbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.textbook.databinding.ActivityMainBinding
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

    private val tabList = listOf("收藏", "书库")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initDatabase()
        binding.vp2.adapter = adapter
        TabLayoutMediator(binding.tl, binding.vp2) { tab, pos ->
            tab.text = tabList[pos]
        }.attach()
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


}