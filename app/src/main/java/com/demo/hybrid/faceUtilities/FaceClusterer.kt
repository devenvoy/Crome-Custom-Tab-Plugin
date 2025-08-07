import com.demo.hybrid.faceUtilities.FaceDao
import com.demo.hybrid.faceUtilities.FaceEntity
import com.demo.hybrid.faceUtilities.toFaceEntry
import smile.clustering.DBSCAN

object FaceClusterer {

    suspend fun clusterFromDatabase(
        dao: FaceDao,
        epsilon: Double = 5.0,
        minPts: Int = 1
    ): Map<Int, List<FaceEntity>> {
        val entities = dao.getAllFaces()
        if (entities.isEmpty()) return emptyMap()

        val faceEntries = entities.map { it.toFaceEntry() }
        val data = faceEntries.map { it.embedding }.toTypedArray()

        val dbscan = DBSCAN.fit(data,minPts, epsilon)
        val labels = dbscan.y

        // Update DB with cluster IDs
        val updatedEntities = entities.mapIndexed { index, entity ->
            entity.copy(clusterId = if (labels[index] == -1) null else labels[index])
        }

        dao.updateFaces(updatedEntities)

        // Return map of clusterId to FaceEntity list
        return updatedEntities.filter { it.clusterId != null }
            .groupBy { it.clusterId!! }
    }
}
