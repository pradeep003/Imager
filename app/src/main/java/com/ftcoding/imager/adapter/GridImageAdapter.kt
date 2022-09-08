package com.ftcoding.imager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ftcoding.imager.R
import com.ftcoding.imager.components.ImageQualitySetting
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.databinding.GridImagesItemBinding
import com.ftcoding.imager.repository.prefstore.PrefsStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class GridImageAdapter(private val prefsStore: PrefsStore, private val onImageClickListener: (id: String) -> Unit) :
    PagingDataAdapter<ImageResponse, GridImageAdapter.GridImageViewHolder>(ImagerComparator) {

    inner class GridImageViewHolder(val binding: GridImagesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(response: ImageResponse) {

            // check the imager_logo.png quality
            var imageQuality: String

            // check the imager_logo.png quality store in dataStore
            runBlocking {
                imageQuality = prefsStore.getImageQuality.first()
            }

            // get imager_logo.png url according to setting
            val imageUrl = when (imageQuality) {
                ImageQualitySetting.LOW.name -> {
                    response.urls?.small
                }
                ImageQualitySetting.HIGH.name -> {
                    response.urls?.full
                }
                else -> {
                    response.urls?.regular
                }
            }

            binding.apply {
                Glide.with(itemView)
                    .load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_photo)
                    .error(R.drawable.ic_photo)
                    .into(ivImageGridItem)

                // set a click listener on grid imager_logo.png
                // attach callback to imager_logo.png and send photo id
                ivImageGridItem.setOnClickListener {
                    onImageClickListener(response.id!!)
                }
            }
        }
    }

    object ImagerComparator : DiffUtil.ItemCallback<ImageResponse>() {
        override fun areItemsTheSame(oldItem: ImageResponse, newItem: ImageResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ImageResponse, newItem: ImageResponse): Boolean {
            return oldItem == newItem
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GridImageViewHolder {
        val binding = GridImagesItemBinding.inflate(
            LayoutInflater.from(parent.context)
        )
        return GridImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridImageViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

}