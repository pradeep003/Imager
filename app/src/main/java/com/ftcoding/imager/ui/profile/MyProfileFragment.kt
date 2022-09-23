package com.ftcoding.imager.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ftcoding.imager.R
import com.ftcoding.imager.adapter.GridImageAdapter
import com.ftcoding.imager.adapter.HeaderFooterAdapter
import com.ftcoding.imager.api.Auth
import com.ftcoding.imager.components.NetworkCheck
import com.ftcoding.imager.databinding.FragmentMyProfileBinding
import com.ftcoding.imager.repository.prefstore.PrefsStore
import com.ftcoding.imager.repository.prefstore.PrefsStoreImpl
import com.ftcoding.imager.ui.bottom_sheet.BottomSheetFragment
import com.ftcoding.imager.ui.current_user_activity.UserActivity
import com.ftcoding.imager.util.Constants
import com.ftcoding.imager.util.asString
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var gridImageAdapter: GridImageAdapter
    private val viewModel by viewModels<MyProfileViewModel>()
    private lateinit var prefsStore: PrefsStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile, container, false)

        // initialize prefsStore
        prefsStore = PrefsStoreImpl(requireContext())

        binding.tryAgainButton.setOnClickListener {
            if (NetworkCheck.isNetworkAvailable(requireContext())) {
                binding.noInternetLayout.visibility = View.GONE
                viewModel.getMyProfile()
            }
        }

        if (NetworkCheck.isNetworkAvailable(requireContext())) {
            // get my profile
            binding.noInternetLayout.visibility = View.GONE
            viewModel.getMyProfile()
        } else {
            binding.noInternetLayout.visibility = View.VISIBLE
        }

        // if user is not login make login layout visibility Visible and vice versa
        viewModel.myProfileState.observe(viewLifecycleOwner) {
            if (it) {
                binding.loginLayout.visibility = View.GONE
            } else {
                binding.loginLayout.visibility = View.VISIBLE
            }
        }

        // redirect for login
        binding.loginButton.setOnClickListener {
            Auth(Constants.API_KEY, null).authorize(
                requireActivity(),
                Constants.redirectUri,
                Constants.scopes
            )

        }

        // observe myProfile state (current user profile) and update it
        viewModel.myProfile.observe(viewLifecycleOwner) { currentUser ->
            if (currentUser != null) {
                binding.apply {

                    // binding data variable user with myProfile state
                    user = viewModel.myProfile.value

                    Glide.with(this@MyProfileFragment)
                        .load(currentUser.profileImage?.medium)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.drawable.ic_user_image)
                        .into(ivProfilePhotoMyProfile)

                    llEditMyProfile.setOnClickListener {
                        val intent = Intent(context, UserActivity::class.java)
                        startActivity(intent)
                    }

                    setUpRecyclerView(currentUser.username)
                }
            }
        }

        // observe error state and show snackbar
        viewModel.errorState.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(
                binding.myProfileFragmentContainer,
                errorMessage.asString(requireContext()),
                Snackbar.LENGTH_LONG
            ).show()
        }


        return binding.root
    }

    private fun setUpRecyclerView(username: String) {
        gridImageAdapter = GridImageAdapter(prefsStore) { photoId ->
            // show bottom sheet when photo is clicked
            val bottomSheet = BottomSheetFragment(photoId)
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        binding.rvFavPhotoMyProfile.apply {
            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            adapter = gridImageAdapter.withLoadStateHeaderAndFooter(
                header = HeaderFooterAdapter { gridImageAdapter.retry() },
                footer = HeaderFooterAdapter { gridImageAdapter.retry() }
            )
            setHasFixedSize(true)
        }

        lifecycleScope.launch {
            viewModel.geUserLikePhotos(username = username).collectLatest {
                gridImageAdapter.submitData(it)
            }
        }

        // show or stop shimmer effect
        gridImageAdapter.addLoadStateListener { loadState ->
            when (loadState.source.refresh) {
                is LoadState.Loading -> {
                    binding.shimmerMyProfile.startShimmer()
                    binding.rvFavPhotoMyProfile.visibility = View.INVISIBLE
                }
                is LoadState.NotLoading -> {
                    binding.shimmerMyProfile.startShimmer()
                    binding.rvFavPhotoMyProfile.visibility = View.VISIBLE
                    binding.shimmerMyProfile.visibility = View.GONE
                }
                is LoadState.Error -> {
                    Toast.makeText(
                        context,
                        "Something went wrong...${(loadState.source.refresh as LoadState.Error).error.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}