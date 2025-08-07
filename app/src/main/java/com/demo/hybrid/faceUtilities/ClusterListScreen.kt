package com.demo.hybrid.faceUtilities

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import coil3.compose.AsyncImage

@Composable
fun ClusterListScreen(clusters: Map<Int, List<FaceEntity>>, onClusterClick: (Int) -> Unit) {
    LazyColumn {
        clusters.forEach { (clusterId, faces) ->
            stickyHeader {
                Text(text = "Cluster $clusterId", style = MaterialTheme.typography.h6)
            }
            item {
                FlowRow(
                    modifier = Modifier
                        .clickable { onClusterClick(clusterId) }
                        .padding(16.dp)) {
                    faces.fastForEachIndexed { i, face -> ClusterItem(face) }
                }
            }
        }
    }
}

@Composable
fun ClusterItem(face: FaceEntity) {
    AsyncImage(
        model = face.imageUri,
        contentDescription = null,
        modifier = Modifier.padding(6.dp).size(64.dp)
    )
}
