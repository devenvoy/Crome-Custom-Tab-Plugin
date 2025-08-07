package com.demo.hybrid


import FaceClusterer
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.demo.hybrid.databinding.ActivityMainBinding
import com.demo.hybrid.faceUtilities.ClusterListScreen
import com.demo.hybrid.faceUtilities.DatabaseProvider
import com.demo.hybrid.faceUtilities.EmbeddingExtractor
import com.demo.hybrid.faceUtilities.FaceEntity
import com.demo.hybrid.faceUtilities.FaceProcessingWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var embeddingExtractor: EmbeddingExtractor

    private val appDatabase by lazy {
        DatabaseProvider.getDatabase(this@MainActivity)
    }

    private val faceDao by lazy {
        appDatabase.faceDao()
    }

    private var clusters: Map<Int, List<FaceEntity>> = emptyMap()

    fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    fun isExternalStorageManager(): Boolean {
        return isRPlus() && Environment.isExternalStorageManager()
    }

    fun launchMediaManagementIntent(callback: () -> Unit) {
        Intent(Settings.ACTION_REQUEST_MANAGE_MEDIA).apply {
            data = "package:$packageName".toUri()
            try {
                startActivityForResult(this, 300)
            } catch (e: Exception) {
//                showErrorToast(e)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()
        embeddingExtractor = EmbeddingExtractor(this)
        binding.someBtn.setOnClickListener {
            /*if (isExternalStorageManager().not()) {
                launchMediaManagementIntent { }
                return@setOnClickListener
            }*/
            val workRequest = OneTimeWorkRequestBuilder<FaceProcessingWorker>().build()
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.id)
                .observe(this) { workInfo ->
                    if (workInfo != null && workInfo.state.isFinished) {
                        Toast.makeText(this, "Face scan complete", Toast.LENGTH_SHORT).show()
                        loadClustersAndUpdateUI()
                    }
                }
            WorkManager.getInstance(this).enqueue(workRequest)
        }

        binding.composeCheckbox.setContent {
            ClusterListScreen(
                clusters = clusters,
                onClusterClick = {
                    Toast.makeText(this, "Cluster $it clicked", Toast.LENGTH_SHORT).show()
                }
            )
        }
        binding.loadCompose.setOnClickListener {
            loadClustersAndUpdateUI()
        }
        loadClustersAndUpdateUI()
    }

    private fun loadClustersAndUpdateUI() {
        lifecycleScope.launch(Dispatchers.IO) {
            val groups = FaceClusterer.clusterFromDatabase(faceDao)
            withContext(Dispatchers.Main) {
                clusters = groups
                binding.composeCheckbox.setContent {
                    ClusterListScreen(
                        clusters = clusters,
                        onClusterClick = { clusterId ->
                            Toast.makeText(
                                this@MainActivity,
                                "Cluster $clusterId clicked",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }
}
