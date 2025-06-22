package com.hissain.android.demo.sensordatacollector.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.hissain.android.demo.sensordatacollector.MainActivity
import com.hissain.android.demo.sensordatacollector.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        val wearableManager = mainActivity.getWearableManager()

        // Observe connection status
        lifecycleScope.launch {
            wearableManager.connectionStatus.collect { isConnected ->
                binding.connectionStatus.text = if (isConnected) {
                    "✓ Watch Connected"
                } else {
                    "✗ Watch Disconnected"
                }
                binding.connectionStatus.setTextColor(
                    if (isConnected)
                        requireContext().getColor(android.R.color.holo_green_dark)
                    else
                        requireContext().getColor(android.R.color.holo_red_dark)
                )
            }
        }

        // Observe collection status
        lifecycleScope.launch {
            wearableManager.collectionStatus.collect { status ->
                binding.collectionStatus.text = if (status.isCollecting) {
                    "Data Collection: Active"
                } else {
                    "Data Collection: Stopped"
                }

                binding.recordCount.text = "Records: ${status.totalRecords}"
                binding.lastSync.text = if (status.lastSyncTime > 0) {
                    "Last Sync: ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                        .format(java.util.Date(status.lastSyncTime))}"
                } else {
                    "Last Sync: Never"
                }
            }
        }

        // Control buttons
        binding.btnStartCollection.setOnClickListener {
            wearableManager.startDataCollection()
        }

        binding.btnStopCollection.setOnClickListener {
            wearableManager.stopDataCollection()
        }

        binding.btnSyncData.setOnClickListener {
            wearableManager.syncData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
