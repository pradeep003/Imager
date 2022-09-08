package com.ftcoding.imager.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ftcoding.imager.R
import com.ftcoding.imager.components.ImageQualitySetting
import com.ftcoding.imager.data.response.CollectionResponse
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.databinding.CollectionImagesItemBinding
import com.ftcoding.imager.repository.prefstore.PrefsStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CollectionImageAdapter(private val prefsStore: PrefsStore, private val callbackListener: CollectionClickListener) :
    PagingDataAdapter<CollectionResponse, CollectionImageAdapter.CollectionViewHolder>(
        CollectionComparator
    ) {
    private lateinit var collectionChildAdapter: CollectionChildAdapter

    inner class CollectionViewHolder(val binding: CollectionImagesItemBinding) :
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        private var lifecycleRegistry = LifecycleRegistry(this)

        suspend fun bind(response: CollectionResponse) {

            binding.apply {

                // start shimmer and invisible recycler view
                binding.shimmerCollectionChild.startShimmer()
                binding.rvCollection.visibility = View.INVISIBLE

                // get profile imager_logo.png url according to setting
                val profileImageUrl = when (prefsStore.getProfileImageQuality.first()) {
                    ImageQualitySetting.LOW.name -> {
                        response.user?.profileImage?.small
                    }
                    ImageQualitySetting.HIGH.name -> {
                        response.user?.profileImage?.large
                    }
                    else -> {
                        response.user?.profileImage?.medium
                    }
                }
                tvUsernameCollection.text = response.user?.username

                Glide.with(itemView)
                    .load(profileImageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_photo)
                    .error(R.drawable.ic_photo)
                    .into(ivProfileCollection)

                if (response.title != null) {
                    tvTitleCollection.text = response.title
                } else {
                    tvTitleCollection.visibility = View.GONE
                }
                if (response.description != null) {
                    tvDiscCollection.text = response.description
                } else {
                    tvDiscCollection.visibility = View.GONE
                }
            }

            // set up recycler view for show collection images
            setUpRecyclerView()
            // get a paging data through callback and submit it to collectionChildAdapter
            callbackListener.onPhotos(id = response.id).let {
                it.collectLatest { data ->
                    collectionChildAdapter.submitData(data)
                }
            }
        }

        // create a lifecycle
        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }

        private fun setUpRecyclerView() {
            collectionChildAdapter = CollectionChildAdapter(prefsStore) { photoId ->
                callbackListener.onImageClick(photoId)
            }
            binding.rvCollection.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = collectionChildAdapter
                setHasFixedSize(true)
            }
            collectionChildAdapter.addLoadStateListener { loadState ->
                when(loadState.source.refresh) {
                    is LoadState.Loading -> {
                        binding.shimmerCollectionChild.startShimmer()
                        binding.rvCollection.visibility = View.INVISIBLE
                    }
                    is LoadState.NotLoading -> {
                        binding.shimmerCollectionChild.stopShimmer()
                        binding.shimmerCollectionChild.visibility = View.GONE
                        binding.rvCollection.visibility = View.VISIBLE
                    }
                    is LoadState.Error -> {
                        binding.shimmerCollectionChild.stopShimmer()
                        binding.shimmerCollectionChild.visibility = View.GONE
                        binding.rvCollection.visibility = View.VISIBLE
                    }
                }

            }
        }


    }

    object CollectionComparator : DiffUtil.ItemCallback<CollectionResponse>() {
        override fun areItemsTheSame(
            oldItem: CollectionResponse,
            newItem: CollectionResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: CollectionResponse,
            newItem: CollectionResponse
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {

        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.binding.lifecycleOwner?.lifecycleScope?.launch {
                holder.bind(currentItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val binding = CollectionImagesItemBinding.inflate(
            LayoutInflater.from(parent.context)
        )
        // attack lifecycle owner to viewHolder
        val viewHolder = CollectionViewHolder(binding)
        binding.lifecycleOwner = viewHolder
        return CollectionViewHolder(binding)
    }

    // callback methods
    interface CollectionClickListener {

        fun onImageClick(id: String)

        fun onPhotos(id: String): Flow<PagingData<ImageResponse>>
    }
}