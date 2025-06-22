/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.hissain.android.demo.sensordatacollector.wear
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.hissain.android.demo.sensordatacollector.wear.databinding.ActivityMainBinding
import com.hissain.android.demo.sensordatacollector.wear.service.DataCollectionService
import com.hissain.android.demo.sensordatacollector.wear.storage.DataStorage
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataStorage: DataStorage
    private var dataCollectionService: DataCollectionService? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            updateUI()
        } else {
            Toast.makeText(this, "Permissions required for data collection", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataStorage = DataStorage(this)

        requestPermissions()
        setupUI()
        updateUI()
    }

    private fun requestPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION
        )

        val permissionsNeeded = requiredPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNeeded.isNotEmpty()) {
            permissionLauncher.launch(permissionsNeeded.toTypedArray())
        }
    }

    private fun setupUI() {
        binding.btnSettings.setOnClickListener {
            // Navigate to settings - for now show toast
            Toast.makeText(this, "Settings page - Coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        lifecycleScope.launch {
            val recordCount = dataStorage.getHeartRateDataCount()

            binding.statusText.text = "Ready for data collection"
            binding.recordCountText.text = "Records: $recordCount"
            binding.sensorStatusText.text = "Heart Rate Sensor: Available"
        }
    }
}