package com.example.pagergallerypaging.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.gallerywithpaging.R
import com.example.pagergallerypaging.model.Hit
import kotlinx.android.synthetic.main.fragment_photo.*

class PhotoFragment : Fragment(R.layout.fragment_photo) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shimmerLayoutPhoto.apply {
            setShimmerColor(0x55FFFFFF)
            setShimmerAngle(0)
            startShimmerAnimation()
        }

        val arg = arguments?.getSerializable("PHOTO") as Hit
        Glide.with(photoView)
            .load(arg.largeImageURL)
            .placeholder(R.drawable.ic_photo_gray)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    // 停止閃動
                    return false.also { shimmerLayoutPhoto?.stopShimmerAnimation() }
                }
            })
            .into(photoView)

        // show 載入第幾張圖
        val totalCount = arguments?.getInt("TOTAL")
        val at = arguments?.getInt("AT")
        tvTag.text = "$at / $totalCount"
    }

}