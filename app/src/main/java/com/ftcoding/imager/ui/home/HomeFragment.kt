package com.ftcoding.imager.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ftcoding.imager.R
import com.ftcoding.imager.adapter.HeaderFooterAdapter
import com.ftcoding.imager.adapter.HomeImagesAdapter
import com.ftcoding.imager.components.ImageDownloadManager
import com.ftcoding.imager.components.NetworkCheck
import com.ftcoding.imager.databinding.FragmentHomeBinding
import com.ftcoding.imager.repository.prefstore.PrefsStore
import com.ftcoding.imager.repository.prefstore.PrefsStoreImpl
import com.ftcoding.imager.ui.bottom_sheet.BottomSheetFragment
import com.ftcoding.imager.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

@AndroidEntryPoint
class HomeFragment : Fragment(), HomeImagesAdapter.AdapterClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var homeImagesAdapter: HomeImagesAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var prefsStore: PrefsStore

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )

        prefsStore = PrefsStoreImpl(requireContext())

        // click listener on retry button
        binding.tryAgainButton.setOnClickListener {
            // if network is available remove noInternetLayout and fetch data for recycler view
            if (NetworkCheck.isNetworkAvailable(requireContext())) {
                binding.noInternetLayout.visibility = View.GONE
                setUpRecyclerView()
            }
        }

        swipeRefreshLayout = binding.swipeRlHomeFragment

        // check for internet connection
        if (NetworkCheck.isNetworkAvailable(requireContext())) {
            binding.noInternetLayout.visibility = View.GONE

            // initializing default imager_logo.png recycler view
            setUpRecyclerView()

            binding.searchBarHome.isSubmitButtonEnabled = true
            // fetch new searched list
            binding.searchBarHome.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        if (query.isNotEmpty()) {
                            setUpSearchRecyclerView(query)
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {

                    return false
                }
            }

            )
        } else {
            // show noInternetLayout when internet connection is available
            binding.noInternetLayout.visibility = View.VISIBLE
        }

        // observing error state and showing in scaffold
        viewModel.errorState.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNullOrBlank()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }

        // on swipe set the default recycler view
        swipeRefreshLayout.setOnRefreshListener {
            setUpRecyclerView()
            swipeRefreshLayout.isRefreshing = false
        }

        return binding.root
    }

    // use this recycler view when user search for photo
    private fun setUpSearchRecyclerView(query: String) {
        homeImagesAdapter = HomeImagesAdapter(prefsStore, this)
        binding.rvHomeFragment.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = homeImagesAdapter
            setHasFixedSize(true)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchPhoto(query).collectLatest {
                homeImagesAdapter.submitData(it)
            }
        }
    }

    private fun setUpRecyclerView() {
        homeImagesAdapter = HomeImagesAdapter(prefsStore, this)
        binding.rvHomeFragment.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = homeImagesAdapter.withLoadStateHeaderAndFooter(
                header = HeaderFooterAdapter {
                    homeImagesAdapter.retry()
                },
                footer = HeaderFooterAdapter {
                    homeImagesAdapter.retry()
                }
            )
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.imagesList.collectLatest {
                homeImagesAdapter.submitData(it)
            }
        }

        homeImagesAdapter.addLoadStateListener { loadState ->
            when (loadState.source.refresh) {
                is LoadState.Loading -> {
                    binding.shimmerLyHomeFg.startShimmer()
                    binding.rvHomeFragment.visibility = View.INVISIBLE
                }
                is LoadState.NotLoading -> {
                    binding.shimmerLyHomeFg.startShimmer()
                    binding.rvHomeFragment.visibility = View.VISIBLE
                    binding.shimmerLyHomeFg.visibility = View.GONE
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

    override fun onFavouriteClick(id: String, isFavourite: Boolean?): Boolean {
        // check what's the new state of isFavourite button
        // callback method logic

        return runBlocking {
            val imageResponse = if (isFavourite == true) {
                viewModel.unLikePhoto(id)
            } else {
                viewModel.likePhoto(id)
            }
            imageResponse?.likedByUser ?: false
        }
    }

    override fun onImageClick(id: String) {
        // show bottom sheet
        context?.let {
            val bottomSheet = BottomSheetFragment(id)
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
    }

    override fun onDownloadClick(id: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val url = viewModel.downloadPhoto(id)
            val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE

            val executor = Executors.newSingleThreadExecutor()
            executor.execute {
                context?.let { context ->
                    // check permission
                    if (ContextCompat.checkSelfPermission(
                            context,
                            permission
                        ) == PackageManager.PERMISSION_DENIED
                    ) {
                        // Requesting the permission
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(permission),
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