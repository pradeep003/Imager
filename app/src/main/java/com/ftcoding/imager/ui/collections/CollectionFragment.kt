package com.ftcoding.imager.ui.collections

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
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.ftcoding.imager.R
import com.ftcoding.imager.adapter.CollectionImageAdapter
import com.ftcoding.imager.components.NetworkCheck
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.databinding.FragmentCollectionBinding
import com.ftcoding.imager.repository.prefstore.PrefsStore
import com.ftcoding.imager.repository.prefstore.PrefsStoreImpl
import com.ftcoding.imager.ui.bottom_sheet.BottomSheetFragment
import com.ftcoding.imager.util.asString
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CollectionFragment : Fragment(), CollectionImageAdapter.CollectionClickListener {

    private var _binding: FragmentCollectionBinding? = null
    private val viewModel by viewModels<CollectionViewModel>()
    private lateinit var collectionImageAdapter: CollectionImageAdapter
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
            inflater, R.layout.fragment_collection, container, false
        )

        // prefsStore initialization
        prefsStore = PrefsStoreImpl(requireContext())

        binding.tryAgainButton.setOnClickListener {
            if (NetworkCheck.isNetworkAvailable(requireContext())) {
                //  setting up recycler view if network is available
                binding.noInternetLayout.visibility = View.GONE
                setUpRecyclerView()
            }
        }

        if (NetworkCheck.isNetworkAvailable(requireContext())) {
            //  setting up recycler view
            binding.noInternetLayout.visibility = View.GONE
            setUpRecyclerView()
        } else {
            binding.noInternetLayout.visibility = View.VISIBLE
        }


        // observing error state and if error exist show in scaffold
        viewModel.errorState.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(binding.collectionContainer, errorMessage.asString(requireContext()), Snackbar.LENGTH_LONG).show()

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {
        collectionImageAdapter = CollectionImageAdapter(prefsStore, this)
        binding.rvCollectionFragment.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = collectionImageAdapter
            setHasFixedSize(true)
        }
        // attach new data to recycler view
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.collectionList.collectLatest {
                collectionImageAdapter.submitData(it)
            }
        }
        // loading listener
        collectionImageAdapter.addLoadStateListener { loadState ->
            when (loadState.source.refresh) {
                is LoadState.Loading -> {
                    binding.shimmerCollectionLayout.startShimmer()
                    binding.rvCollectionFragment.visibility = View.INVISIBLE
                }
                is LoadState.NotLoading -> {
                    binding.shimmerCollectionLayout.stopShimmer()
                    binding.rvCollectionFragment.visibility = View.VISIBLE
                    binding.shimmerCollectionLayout.visibility = View.GONE
                }
                is LoadState.Error -> {
                    Toast.makeText(context, "Something went wrong...${(loadState.source.refresh as LoadState.Error).error.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onImageClick(id: String) {
        // show bottom sheet when user click on collection imager_logo.png
        viewLifecycleOwner.lifecycleScope.launch {
            val bottomSheet = BottomSheetFragment(photoId = id)
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
    }

    override fun onPhotos(id: String): Flow<PagingData<ImageResponse>> {
        // send paging data to collectionImageAdapter through callback method
        return viewModel.getCollectionPhotosById(id)
    }
}