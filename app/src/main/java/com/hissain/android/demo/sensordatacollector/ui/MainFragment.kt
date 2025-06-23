package com.hissain.android.demo.sensordatacollector.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hissain.android.demo.sensordatacollector.R
import java.io.File
import java.time.Instant
import java.time.LocalDate

class MainFragment : Fragment() {

    private lateinit var labelButton: Button
    private lateinit var connectionStatus: TextView
    private lateinit var dataCollectionStatus: TextView
    private lateinit var recordCountStatus: TextView
    private lateinit var lastSyncStatus: TextView
    private lateinit var lastLabelStatus: TextView
    private lateinit var profileButton: Button
    private lateinit var settingsButton: Button

    private var recordCount = 0
    private var lastLabelTime: String = "N/A"
    private var lastSyncTime: String = "N/A"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        labelButton = view.findViewById(R.id.labelButton)
        connectionStatus = view.findViewById(R.id.connectionStatus)
        dataCollectionStatus = view.findViewById(R.id.dataCollectionStatus)
        recordCountStatus = view.findViewById(R.id.recordCountStatus)
        lastSyncStatus = view.findViewById(R.id.lastSyncStatus)
        lastLabelStatus = view.findViewById(R.id.lastLabelStatus)
        profileButton = view.findViewById(R.id.profileButton)
        settingsButton = view.findViewById(R.id.settingsButton)

        labelButton.setOnClickListener {
            onHotflashReported()
        }

        profileButton.setOnClickListener {
            findNavController().navigate(R.id.nav_home)
        }

        settingsButton.setOnClickListener {
            findNavController().navigate(R.id.nav_dashboard)
        }

        updateStatusUI()

        return view
    }

    private fun onHotflashReported() {
        val timestamp = Instant.now().toString()
        lastLabelTime = timestamp

        // Store label into local storage
        saveLabelToCSV(timestamp)

        Toast.makeText(requireContext(), "Hotflash recorded", Toast.LENGTH_SHORT).show()
        updateStatusUI()
    }

    private fun updateStatusUI() {
        // These should be replaced with real implementations
        val isConnected = true // assume true
        val isCollecting = true // assume true

        connectionStatus.text = "Watch Connection: ${if (isConnected) "Connected ✅" else "Disconnected ❌"}"
        dataCollectionStatus.text = "Data Collection: ${if (isCollecting) "Active 📡" else "Idle ⏸"}"
        recordCountStatus.text = "Records Synced: $recordCount"
        lastSyncStatus.text = "Last Sync: $lastSyncTime"
        lastLabelStatus.text = "Last Hotflash: $lastLabelTime"
    }

    private fun saveLabelToCSV(timestamp: String) {
        // Append to "labels_YYYY-MM-DD.csv"
        val date = LocalDate.now().toString()
        val filename = "labels_$date.csv"
        val file = File(requireContext().filesDir, filename)

        if (!file.exists()) file.appendText("timestamp,label\n")
        file.appendText("$timestamp,hotflash\n")
    }
}