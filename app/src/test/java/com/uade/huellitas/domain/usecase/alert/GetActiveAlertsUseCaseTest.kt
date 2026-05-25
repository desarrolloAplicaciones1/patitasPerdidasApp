package com.uade.huellitas.domain.usecase.alert

import com.uade.huellitas.makeAlert
import com.uade.huellitas.domain.model.AlertType
import com.uade.huellitas.domain.repository.AlertRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetActiveAlertsUseCaseTest {

    private lateinit var alertRepository: AlertRepository
    private lateinit var getActiveAlertsUseCase: GetActiveAlertsUseCase

    @Before
    fun setUp() {
        alertRepository = mockk()
        getActiveAlertsUseCase = GetActiveAlertsUseCase(alertRepository)
    }

    @Test
    fun `returns alerts emitted by repository`() = runTest {
        val alerts = listOf(makeAlert(id = "a1"), makeAlert(id = "a2"))
        every { alertRepository.getActiveAlerts() } returns flowOf(alerts)

        val result = getActiveAlertsUseCase().first()

        assertEquals(alerts, result)
    }

    @Test
    fun `returns empty list when repository emits none`() = runTest {
        every { alertRepository.getActiveAlerts() } returns flowOf(emptyList())

        val result = getActiveAlertsUseCase().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `delegates to repository exactly once`() = runTest {
        every { alertRepository.getActiveAlerts() } returns flowOf(emptyList())

        getActiveAlertsUseCase()

        verify(exactly = 1) { alertRepository.getActiveAlerts() }
    }

    @Test
    fun `returns mixed lost and found alerts`() = runTest {
        val alerts = listOf(
            makeAlert(id = "lost-1", type = AlertType.LOST),
            makeAlert(id = "found-1", type = AlertType.FOUND)
        )
        every { alertRepository.getActiveAlerts() } returns flowOf(alerts)

        val result = getActiveAlertsUseCase().first()

        assertEquals(2, result.size)
        assertEquals(AlertType.LOST,  result[0].type)
        assertEquals(AlertType.FOUND, result[1].type)
    }
}
