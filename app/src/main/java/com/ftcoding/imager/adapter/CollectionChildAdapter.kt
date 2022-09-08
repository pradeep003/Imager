package com.ftcoding.imager.adapter

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import com.ftcoding.imager.databinding.CollectionChildItemBinding
import com.ftcoding.imager.repository.prefstore.PrefsStore
import com.ftcoding.imager.util.BlurHashDecoder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class CollectionChildAdapter(private val prefsStore: PrefsStore, private val imageClickListener: (id: String) -> Unit) :
    PagingDataAdapter<ImageResponse, CollectionChildAdapter.ViewHolder>(ImagerComparator) {

    var bitmap : Bitmap? = null

    inner class ViewHolder(val binding: CollectionChildItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(response: ImageResponse) {
            binding.apply {

                // blur effect till imager_logo.png load
                bitmap = BlurHashDecoder.decode(
                    response.blurHash,
                    12, 14
                )

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

                Glide.with(itemView)
                    .load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(BitmapDrawable(Resources.getSystem(), bitmap))
                    .error(R.drawable.ic_photo)
                    .into(ivPhotoChildCollection)

                // when user clicked photo send a callback to collectionImageAdapter with the photo id
                ivPhotoChildCollection.setOnClickListener {
                    imageClickListener(response.id!!)
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
    ): ViewHolder {
        val binding = CollectionChildItemBinding.inflate(
            LayoutInflater.from(parent.context)
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

}