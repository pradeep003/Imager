package com.ftcoding.imager.adapter

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowMetrics
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ftcoding.imager.R
import com.ftcoding.imager.components.ImageQualitySetting
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.databinding.ImagesItemBinding
import com.ftcoding.imager.repository.prefstore.PrefsStore
import com.ftcoding.imager.ui.home.HomeFragment
import com.ftcoding.imager.ui.user_profile_activity.UserProfileActivity
import com.ftcoding.imager.util.BlurHashDecoder
import com.ftcoding.imager.util.Constants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class HomeImagesAdapter(private val prefsStore: PrefsStore, private val listener: AdapterClickListener) :
    PagingDataAdapter<ImageResponse, HomeImagesAdapter.ImagesViewHolder>(ImagerComparator) {

    var bitmap: Bitmap? = null

    inner class ImagesViewHolder(val binding: ImagesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isLikedByUser = false

        fun bind(context: Context, response: ImageResponse, listener: AdapterClickListener) {
            binding.apply {
                isLikedByUser = response.likedByUser ?: false
                // blurHash = converting string to bitmap
                bitmap = BlurHashDecoder.decode(
                    response.blurHash,
                    12,
                    20
                )

                // check the imager_logo.png quality
                var imageQuality: String
                var profileImageQuality: String
                // check the imager_logo.png quality store in dataStore
                runBlocking {
                    imageQuality = prefsStore.getImageQuality.first()
                    profileImageQuality = prefsStore.getProfileImageQuality.first()
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
                // get profile imager_logo.png url according to setting
                val profileImageUrl = when (profileImageQuality) {
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
                // fetching imager_logo.png and blurHash
                Glide.with(itemView)
                    .load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(BitmapDrawable(Resources.getSystem(), bitmap))
                    .error(R.drawable.ic_error)
                    .into(ivMainImagesItem)

                Glide.with(itemView)
                    .load(profileImageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivUserImagesItem)

                val username = response.user?.username
                if (username != null) {
                    val usernameText = context.getString(R.string.photo_by)
                    tvUsernameImagesItem.text = String.format(usernameText, username)
                    tvBioImagesItem.text = response.user.bio

                    // click listener on username textview and redirect to unsplash user profile
                    tvUsernameImagesItem.setOnClickListener {
                        val url = "https://unsplash.com/@$username?utm_source=Imager&utm_medium=referral"
                        val customTabsIntent = CustomTabsIntent.Builder().build()
                        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        customTabsIntent.launchUrl(context, Uri.parse(url))
                    }
                    // redirect to unsplash website
                    binding.tvUnsplashImagesItem.setOnClickListener {
                        val url = "https://unsplash.com/?utm_source=Imager&utm_medium=referral"
                        val customTabsIntent = CustomTabsIntent.Builder().build()
                        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        customTabsIntent.launchUrl(context, Uri.parse(url))
                    }
                }

                // checking whether user liked photo and show like imageview according to it
                if (isLikedByUser) {
                    ivFavouriteImageItem.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.red
                        )
                    )
                } else {
                    ivFavouriteImageItem.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                }

                // redirecting to userProfileActivity
                // passing username through intent
                ivUserImagesItem.setOnClickListener {
                    val userIntent = Intent(context, UserProfileActivity::class.java)
                    userIntent.putExtra("username", response.user?.username)
                    context.startActivity(userIntent)
                }

                // callback method
                ivMainImagesItem.setOnClickListener {
                    listener.onImageClick(response.id!!)
                }

                // on click listener on like button
                // callback method
                ivFavouriteImageItem.setOnClickListener {
                    listener.onFavouriteClick(response.id!!, isLikedByUser).let { newFavouriteState ->
                        isLikedByUser = newFavouriteState
                        if (newFavouriteState) {
                            ivFavouriteImageItem.setColorFilter(
                                ContextCompat.getColor(
                                    context,
                                    R.color.red
                                )
                            )
                        } else {
                            ivFavouriteImageItem.setColorFilter(
                                ContextCompat.getColor(
                                    context,
                                    R.color.white
                                )
                            )}
                    }
                }
                ivDownloadImageItem.setOnClickListener {
                    listener.onDownloadClick(response.id!!)
                }
            }
        }
    }

    interface AdapterClickListener {
        fun onFavouriteClick(id: String, isFavourite: Boolean?) : Boolean

        fun onImageClick(id: String)

        fun onDownloadClick(id: String)
    }

    object ImagerComparator : DiffUtil.ItemCallback<ImageResponse>() {
        override fun areItemsTheSame(oldItem: ImageResponse, newItem: ImageResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ImageResponse, newItem: ImageResponse): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val binding = ImagesItemBinding.inflate(
            LayoutInflater.from(parent.context)
        )
        return ImagesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
//        val currentItem = differ.currentList[position]
        val context = holder.itemView.context
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(context, currentItem, listener)
        }
    }

}