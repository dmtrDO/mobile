package com.example.npp_radiation_monitor

import com.example.npp_radiation_monitor.data.local.RadiationDao
import com.example.npp_radiation_monitor.data.local.RadiationEntity
import com.example.npp_radiation_monitor.data.model.RadiationReading
import com.example.npp_radiation_monitor.data.model.RadiationStatus
import com.example.npp_radiation_monitor.data.repository.RadiationRepository
import com.example.npp_radiation_monitor.data.repository.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Юніт-тести перевірки бізнес-логіки та допоміжних методів аналізу радіаційного фону.
 */
class RadiationUnitTests {

    @Test
    fun testRadiationStatusThresholds() {
        assertEquals(RadiationStatus.SAFE, RadiationStatus.fromValue(0.12f))
        assertEquals(RadiationStatus.WARNING, RadiationStatus.fromValue(0.45f))
        assertEquals(RadiationStatus.DANGER, RadiationStatus.fromValue(0.85f))
    }

    @Test
    fun testShortDescriptionAndRecommendations() {
        val safeReading = RadiationReading(1, 0.12f, "10:00:00")
        val warningReading = RadiationReading(2, 0.45f, "10:15:00")
        val dangerReading = RadiationReading(3, 0.85f, "10:30:00")

        assertEquals("Природний радіаційний фон у межах безпечної норми.", safeReading.shortDescription)
        assertTrue(warningReading.shortDescription.contains("Увага"))
        assertTrue(dangerReading.shortDescription.contains("НЕБЕЗПЕКА"))

        assertNotNull(safeReading.detailedRecommendations)
        assertNotNull(warningReading.detailedRecommendations)
        assertNotNull(dangerReading.detailedRecommendations)
    }

    @Test
    fun testFilteringAndSortingLogic() {
        val repository = RadiationRepository(dummyDao())
        val sampleReadings = listOf(
            RadiationReading(1, 0.12f, "10:00:00"),
            RadiationReading(2, 0.45f, "10:15:00"),
            RadiationReading(3, 0.85f, "10:30:00")
        )

        val filteredByWarning = repository.filterAndSortReadings(
            readings = sampleReadings,
            searchQuery = "",
            statusFilter = RadiationStatus.WARNING,
            sortOrder = SortOrder.BY_TIME_DESC
        )
        assertEquals(1, filteredByWarning.size)
        assertEquals(2, filteredByWarning[0].id)

        val sortedByValueDesc = repository.filterAndSortReadings(
            readings = sampleReadings,
            searchQuery = "",
            statusFilter = null,
            sortOrder = SortOrder.BY_VALUE_DESC
        )
        assertEquals(0.85f, sortedByValueDesc[0].value, 0.001f)
        assertEquals(0.12f, sortedByValueDesc[2].value, 0.001f)
    }

    @Test
    fun testMetricsCalculation() {
        val repository = RadiationRepository(dummyDao())
        val sampleReadings = listOf(
            RadiationReading(1, 0.10f, "10:00:00"),
            RadiationReading(2, 0.50f, "10:15:00"),
            RadiationReading(3, 0.90f, "10:30:00")
        )

        val metrics = repository.calculateMetrics(sampleReadings)
        assertEquals(0.50f, metrics.averageValue, 0.01f)
        assertEquals(0.90f, metrics.maxValue, 0.01f)
        assertEquals(3, metrics.totalCount)
        assertEquals(1, metrics.dangerCount)
        assertEquals(1, metrics.warningCount)
        assertEquals(1, metrics.safeCount)
    }

    private fun dummyDao() = object : RadiationDao {
        override suspend fun insertReading(reading: RadiationEntity): Long = 1L
        override suspend fun insertAll(readings: List<RadiationEntity>) {}
        override fun getAllReadings(): Flow<List<RadiationEntity>> = flowOf(emptyList())
        override suspend fun updateReading(reading: RadiationEntity) {}
        override suspend fun deleteReading(reading: RadiationEntity) {}
        override suspend fun deleteReadingById(id: Int) {}
        override suspend fun clearAll() {}
        override fun searchReadings(query: String): Flow<List<RadiationEntity>> = flowOf(emptyList())
    }
}
