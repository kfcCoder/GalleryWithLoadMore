package com.example.pagergallerypaging.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.gallerywithpaging.R
import com.example.pagergallerypaging.adapter.GalleryAdapter
import com.example.pagergallerypaging.repository.Repository
import com.example.pagergallerypaging.util.Constants.Companion.DATA_STATUS_NETWORK_ERROR
import com.example.pagergallerypaging.viewmodel.MyViewModel
import com.example.pagergallerypaging.viewmodel.MyViewModelFactory
import kotlinx.android.synthetic.main.fragment_gallery.*

class GalleryFragment : Fragment(R.layout.fragment_gallery) {
    private lateinit var viewModel: MyViewModel

    // 創建選單(swipeRefresh indicator)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    // 選中菜單之後行為
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.swipeIndicator -> {
                swipeRefreshLayoutGallery.isRefreshing = true // 顯示載入圖標
                viewModel.resetQuery()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true) // 顯示menu
        val repository = Repository()
        val factory = MyViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory).get(MyViewModel::class.java)

        val galleryAdapter = GalleryAdapter(viewModel)
        rvGallery.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = galleryAdapter
        }

        viewModel.photoListLive.observe(viewLifecycleOwner, Observer {
            galleryAdapter.submitList(it)
            swipeRefreshLayoutGallery.isRefreshing = false // 停止載入圖標
        })

        viewModel.dataStatusLive.observe(viewLifecycleOwner, Observer {
            galleryAdapter.footerViewStatus = it
            galleryAdapter.notifyItemChanged(galleryAdapter.itemCount - 1)
            if (it == DATA_STATUS_NETWORK_ERROR){
                swipeRefreshLayoutGallery.isRefreshing = false
            }

        })

        // 下拉刷新
        swipeRefreshLayoutGallery.setOnRefreshListener {
            viewModel.resetQuery()
        }

        rvGallery.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0) return // 向上滾動不作為

                // 向下滾動
                val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                val intArray = IntArray(2)
                layoutManager.findLastVisibleItemPositions(intArray) // Returns the 'adapter position' of the last visible view for each span.
                if (intArray[0] == galleryAdapter.itemCount - 1) { // 最後一行
                    viewModel.fetchData()
                }
            }
        })
    }


}