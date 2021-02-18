package com.example.pagergallerypaging.adapter

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.gallerywithpaging.R
import com.example.pagergallerypaging.model.Hit
import com.example.pagergallerypaging.util.Constants.Companion.DATA_STATUS_CAN_LOAD_MORE
import com.example.pagergallerypaging.util.Constants.Companion.DATA_STATUS_NETWORK_ERROR
import com.example.pagergallerypaging.util.Constants.Companion.DATA_STATUS_NO_MORE
import com.example.pagergallerypaging.viewmodel.MyViewModel
import kotlinx.android.synthetic.main.gallery_cell.view.*
import kotlinx.android.synthetic.main.gallery_footer.view.*


/** ListAdapter流程:
 * 1. Adapter繼承ListAdapter<T, VH>
 * 2. 寫inner class MyViewHolder : RecyclerView.ViewHolder
 * 3. object DiffCallback : DiffUtil.ItemCallBack<T>()
 * 4. implement methods of adapter
 */
class GalleryAdapter(
    private val viewModel: MyViewModel
) : ListAdapter<Hit, GalleryAdapter.MyViewHolder>(DiffCallback) {

    companion object {
        const val NORMAL_VIEW_TYPE = 0
        const val FOOTER_VIEW_TYPE = 1
    }

    var footerViewStatus = DATA_STATUS_CAN_LOAD_MORE

    object DiffCallback : DiffUtil.ItemCallback<Hit>() {
        override fun areItemsTheSame(oldItem: Hit, newItem: Hit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Hit, newItem: Hit): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * perPage = n
     * itemCount = n + 1
     * position = 0 ~ n-1
     */
    override fun getItemCount(): Int {
        return super.getItemCount() + 1 // +1 for footer
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1){ // 最後一行
            FOOTER_VIEW_TYPE
        } else {
            NORMAL_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder: MyViewHolder

        if (viewType == NORMAL_VIEW_TYPE) {
            holder = MyViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_cell, parent, false))
            holder.itemView.setOnClickListener {
                Bundle().apply {
                    putSerializable("PHOTO", getItem(holder.adapterPosition)) // 傳送大圖
                    putInt("TOTAL", itemCount - 1) // 傳送總數
                    putInt("AT", holder.adapterPosition + 1) // 傳送目前張數
                    holder.itemView.findNavController().navigate(R.id.action_galleryFragment_to_pagerFragment,
                        this)
                }
            }
        } else {
            holder = MyViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_footer, parent, false))
                .also {
                    (it.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
                    it.itemView.setOnClickListener { itemView ->
                        itemView.pbFooter.visibility = View.VISIBLE
                        itemView.tvFooter.text = "Retrying..."
                        viewModel.fetchData()
                    }
                }
        }

        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // position > n-1, return
        if (position == itemCount - 1) {
            holder.itemView.apply {
                when(footerViewStatus) {
                    DATA_STATUS_CAN_LOAD_MORE -> {
                        pbFooter.visibility = View.VISIBLE
                        tvFooter.text = "Loading..."
                        isClickable = false
                    }
                    DATA_STATUS_NO_MORE -> {
                        pbFooter.visibility = View.GONE
                        tvFooter.text = "No more data"
                        isClickable = false
                    }
                    DATA_STATUS_NETWORK_ERROR -> {
                        pbFooter.visibility = View.GONE
                        tvFooter.text = "Network error, clck to retry..."
                        isClickable = true
                    }
                }
            }

            return
        }

        // position = 0 ~ n-1 加載, 先載入閃動Layout
        holder.itemView.shimmerLayoutCell.apply {
            setShimmerColor(0x55FFFFFF)
            setShimmerAngle(0)
            startShimmerAnimation()
        }
        // 再加載預覽圖
        Glide.with(holder.itemView)
            .load(getItem(position).previewURL) // out-of bound
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
                    return false.also { holder.itemView.shimmerLayoutCell?.stopShimmerAnimation() }
                }
            })
            .into(holder.itemView.ivPhoto)

    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}


