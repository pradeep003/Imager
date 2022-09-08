package com.ftcoding.imager.ui.user_profile_activity

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ftcoding.imager.R
import com.ftcoding.imager.adapter.GridImageAdapter
import com.ftcoding.imager.adapter.HeaderFooterAdapter
import com.ftcoding.imager.components.NetworkCheck
import com.ftcoding.imager.data.response.User
import com.ftcoding.imager.databinding.ActivityUserProfileBinding
import com.ftcoding.imager.repository.prefstore.PrefsStore
import com.ftcoding.imager.repository.prefstore.PrefsStoreImpl
import com.ftcoding.imager.ui.bottom_sheet.BottomSheetFragment
import com.ftcoding.imager.util.BlurHashDecoder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var gridImageAdapter: GridImageAdapter
    private var bitmap: Bitmap? = null
    private lateinit var prefsStore: PrefsStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_user_profile
        )
        viewModel = ViewModelProvider(this)[UserProfileViewModel::class.java]

        // prefsStore initialization
        prefsStore = PrefsStoreImpl(this)

        // get username from intent
        val username = intent.getStringExtra("username")

        binding.tryAgainButton.setOnClickListener {
            if (NetworkCheck.isNetworkAvailable(this)) {
                binding.noInternetLayout.visibility = View.GONE
                onBackPressed()
            }
        }

        if (NetworkCheck.isNetworkAvailable(this)) {
            if (username != null) {
                // if coverPhoto is null than get a random photo from getUserPhotos
                var coverPhoto: String? = null

                viewModel.getUserByUsername(username)

                val userProfileObserver = Observer<User?> {
                    if (it != null) {
                        binding.apply {
                            user = it
                            Glide.with(this@UserProfileActivity)
                                .load(it.profileImage?.medium)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .error(R.drawable.ic_error)
                                .placeholder(R.drawable.ic_photo)
                                .into(ivUserProfile)

                            it.tags?.custom?.forEach { custom ->
                                if (custom.title == "landing_page") {
                                    coverPhoto = custom.source?.coverPhoto?.urls?.regular
                                    bitmap = BlurHashDecoder.decode(
                                        custom.source?.coverPhoto?.blurHash,
                                        12, 14
                                    )
                                }
                            }

                            if (coverPhoto != null) {
                                Glide.with(this@UserProfileActivity)
                                    .load(coverPhoto)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .placeholder(BitmapDrawable(Resources.getSystem(), bitmap))
                                    .error(R.drawable.ic_error)
                                    .into(ivCoverUserProfile)
                            } else {
                                val urlBitmap = BlurHashDecoder.decode(
                                    it.photos?.get(0)?.blurHash,
                                    12, 20
                                )
                                Glide.with(this@UserProfileActivity)
                                    .load(it.photos?.get(0)?.urls?.regular)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .error(R.drawable.ic_error)
                                    .placeholder(BitmapDrawable(Resources.getSystem(), urlBitmap))
                                    .into(ivCoverUserProfile)
                            }
                        }
                    }
                }

                setUpRecyclerView(username)

                viewModel.userProfileState.observe(this, userProfileObserver)

            } else {
                // if username is null navigate back to home screen
//                onBackPressed()
            }
        } else {
//             show no internet layout when internet connection is false
            binding.noInternetLayout.visibility = View.VISIBLE
        }

        viewModel.errorState.observe(this) { errorMessage ->
            if (errorMessage.isNotBlank()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setUpRecyclerView(username: String) {

        // open bottom sheet to show a particular imager_logo.png
        gridImageAdapter = GridImageAdapter(prefsStore) { photoId ->
            lifecycleScope.launch {
                val bottomSheet = BottomSheetFragment(photoId)
                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
            }
        }

        binding.rvPhotosUserProfile.apply {
            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            adapter = gridImageAdapter.withLoadStateHeaderAndFooter(
                header = HeaderFooterAdapter { gridImageAdapter.retry() },
                footer = HeaderFooterAdapter { gridImageAdapter.retry() }
            )

            setHasFixedSize(true)
        }

        lifecycleScope.launch {
            viewModel.getUserPhotos(username).collectLatest {
                gridImageAdapter.submitData(it)
            }
        }


    }
}