package com.ftcoding.imager.ui.bottom_sheet

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ftcoding.imager.R
import com.ftcoding.imager.components.ImageDownloadManager
import com.ftcoding.imager.databinding.FragmentBottomSheetBinding
import com.ftcoding.imager.util.BlurHashDecoder
import com.ftcoding.imager.util.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@AndroidEntryPoint
class BottomSheetFragment(
    private val photoId: String
) : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BottomSheetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // viewModel initialize
        viewModel = ViewModelProvider(this)[BottomSheetViewModel::class.java]
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_bottom_sheet, container, false
        )

        binding.shimmerBottomSheet.startShimmer()
        binding.containerBottomSheet.visibility = View.INVISIBLE

        viewModel.getPhotoById(photoId)
        var bitmap: Bitmap?

        binding.apply {

            viewModel.isLoadingState.observe(this@BottomSheetFragment) {isLoading ->
                if (isLoading == false) {
                    shimmerBottomSheet.stopShimmer()
                    containerBottomSheet.visibility = View.VISIBLE
                    shimmerBottomSheet.visibility = View.GONE
                } else {
                    shimmerBottomSheet.startShimmer()
                    containerBottomSheet.visibility = View.INVISIBLE
                }
            }

            viewModel.imageResponse.observe(this@BottomSheetFragment) { imageResponse ->
                if (imageResponse != null) {

                    // git main imager_logo.png blurHash bitmap and use it as a placeholder
                    bitmap = BlurHashDecoder.decode(
                        imageResponse.blurHash,
                        12, 20
                    )

                    // photo credit and redirect to unsplash
                    val username = imageResponse.user?.username
                    if (username != null) {
                        val usernameText = requireContext().getString(R.string.photo_by)
                        binding.tvCreatedAtPhotoBottomSheet.text = String.format(usernameText, username)
                        binding.tvCreatedAtPhotoBottomSheet.setOnClickListener {
                            val url = "https://unsplash.com/@$username?utm_source=Imager&utm_medium=referral"
                            val customTabsIntent = CustomTabsIntent.Builder().build()
                            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
                        }
                        binding.tvUnsplashAtPhotoBottomSheet.setOnClickListener {
                            val url = "https://unsplash.com/?utm_source=Imager&utm_medium=referral"
                            val customTabsIntent = CustomTabsIntent.Builder().build()
                            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
                        }
                    }

                    // binding variable photo to imageResponse from viewModel
                    binding.photo = imageResponse

                    // fetch imager_logo.png from url
                    Glide.with(this@BottomSheetFragment)
                        .load(imageResponse.urls?.regular)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(BitmapDrawable(Resources.getSystem(), bitmap))
                        .error(R.drawable.ic_error)
                        .into(ivMainImageBottomSheet)

                    Glide.with(this@BottomSheetFragment)
                        .load(imageResponse.user?.profileImage?.medium)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.drawable.ic_user_image)
                        .into(ivUserPhotoBottomSheet)
                }
            }

            viewModel.isLikedPhoto.observe(this@BottomSheetFragment) { isFavourite ->
                if (isFavourite == true) {
                    context?.let { context ->
                        ivFavouritePhotoBottomSheet.setColorFilter(
                            ContextCompat.getColor(
                                context,
                                R.color.red
                            )
                        )
                    }
                } else {
                    context?.let { context ->
                        ivFavouritePhotoBottomSheet.setColorFilter(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    }
                }
            }

            // like or unlike photo on imager_logo.png
            binding.ivFavouritePhotoBottomSheet.setOnClickListener {
                if (viewModel.isLikedPhoto.value == true) {
                    viewModel.unLikePhoto(photoId)
                } else {
                    viewModel.likePhoto(photoId)
                }
            }

            // download imager_logo.png implementation
            binding.ivDownloadPhotoBottomSheet.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    val url = viewModel.downloadPhoto(photoId)
                    val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE

                    val executor = Executors.newSingleThreadExecutor()
                    executor.execute {
                        context?.let { context ->

                            // check permission
                            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                                ActivityCompat.requestPermissions(
                                    requireActivity(), arrayOf(permission),
                                    Constants.STORAGE_PERMISSION_CODE
                                )
                            } else {
                                if (url != null) {
                                    ImageDownloadManager.beginDownload(context, url)
                                }
                            }
                        }
                    }
                }
            }
        }
        return binding.root
    }

}