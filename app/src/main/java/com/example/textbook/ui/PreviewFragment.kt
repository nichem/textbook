package com.example.textbook.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.PathUtils
import com.bumptech.glide.Glide
import com.example.textbook.AppViewModel
import com.example.textbook.R
import com.example.textbook.databinding.FragmentPreviewBinding
import com.example.textbook.utils.getFile
import com.github.barteksc.pdfviewer.util.FitPolicy
import java.io.File


class PreviewFragment : Fragment() {
    private val appViewModel by lazy {
        ViewModelProvider(requireActivity())[AppViewModel::class.java]
    }

    private lateinit var binding: FragmentPreviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_preview, container, false)
        binding = FragmentPreviewBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appViewModel.selectItemLiveData.observe(this.viewLifecycleOwner) {
            if (it == null) {
                binding.layoutPreview.isGone = true
                binding.pdfView.recycle()
                return@observe
            }
            val file = it.getFile(requireContext())
            if (file.exists()) {
                //pdf 展示
                binding.layoutPreview.isGone = true
                binding.pdfView.fromFile(file)
                    .swipeHorizontal(true)
                    .pageFitPolicy(FitPolicy.HEIGHT)
                    .load()
            } else {
                binding.layoutPreview.isGone = false
                if (it.preview.isNotEmpty())
                    Glide.with(binding.root)
                        .load(it.preview[0])
                        .into(binding.imageView1)
                if (it.preview.size > 1)
                    Glide.with(binding.root)
                        .load(it.preview[1])
                        .into(binding.imageView2)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PreviewFragment()
    }
}