package com.example.textbook

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.example.textbook.App.Companion.PAGE_SIZE
import com.example.textbook.App.Companion.app
import com.example.textbook.database.Repository
import com.example.textbook.database.Textbook
import com.example.textbook.databinding.ActivityMainBinding
import com.example.textbook.paging.TextbookPagingSource
import com.example.textbook.ui.AllTextbookFragment
import com.example.textbook.ui.FavoriteFragment
import com.example.textbook.ui.PreviewFragment
import com.example.textbook.utils.DataUtils.generateData
import com.example.textbook.utils.DataUtils.isGenerate
import com.example.textbook.utils.DownloadUtil
import com.example.textbook.utils.getFile
import com.example.textbook.utils.isDownload
import com.example.textbook.utils.showAskDialog
import com.example.textbook.utils.showLoading
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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

        appViewModel.selectItemLiveData.observe(this) {
            if (it == null) {
                val fragment =
                    supportFragmentManager.findFragmentByTag("PreviewFragment") ?: return@observe
                if (fragment is PreviewFragment) fragment.saveLastPage()
                supportFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.flPreview, PreviewFragment.newInstance(), "PreviewFragment")
                    .commit()
            }
        }

        appViewModel.downloadLiveData.observe(this) {
            download(it)
        }
        showAuthor()
    }

    private fun initDatabase() = lifecycleScope.launch {
        if (isGenerate()) {
            val loading = showLoading("初始化数据中...", false)
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
        else if (appViewModel.isPreviewState()) appViewModel.quitSelectItem()
        else {
            val tmp = SystemClock.elapsedRealtime()
            if (tmp - time > 1000L) ToastUtils.showShort("再按返回退出应用")
            else super.onBackPressed()
            time = tmp
        }
    }

    private var downloadJob: Job? = null
    private fun download(textbook: Textbook) {
        val downloadFile = textbook.getFile(this@MainActivity)
        if (downloadFile.isDownload()) {
            showAskDialog("是否删除《${textbook.title}》？") {
                downloadFile.delete()
                appViewModel.updateDownloadUI()
            }
            return
        }
        val loading = showLoading("", canCancelable = true) {
            ToastUtils.showShort("下载已取消")
            if (downloadFile.exists()) downloadFile.delete()
            downloadJob?.cancel()
        }

        downloadJob = lifecycleScope.launch {
            //下载过程
            DownloadUtil.download(
                textbook.download,
                downloadFile,
                object : DownloadUtil.OnDownloadListener {
                    override fun onDownloadSuccess() {
                        runOnUiThread {
                            appViewModel.updateDownloadUI()
                            loading.dismiss()
                        }
                    }

                    override fun onDownloading(progress: Int) {
                        runOnUiThread {
                            loading.setMessage("${textbook.title}\n已下载：${progress}%")
                        }
                    }

                    override fun onDownloadFailed() {
                        runOnUiThread {
                            ToastUtils.showShort("下载失败")
                            loading.dismiss()
                        }
                        if (downloadFile.exists()) downloadFile.delete()
                    }

                }
            )
        }
    }

    private fun showAuthor() {
        if (Repository.isShowAuthor) {
            AlertDialog.Builder(this)
                .setMessage("作者：dlearn")
                .setPositiveButton("不再弹出") { _, _ ->
                    Repository.isShowAuthor = false
                }
                .setNegativeButton("下次还弹") { _, _ -> }
                .show()
        }
    }

}