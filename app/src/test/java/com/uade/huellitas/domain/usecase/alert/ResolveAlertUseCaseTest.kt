package com.uade.huellitas.domain.usecase.alert

import com.uade.huellitas.makeAlert
import com.uade.huellitas.domain.model.AlertStatus
import com.uade.huellitas.domain.repository.AlertRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ResolveAlertUseCaseTest {

    private lateinit var alertRepository: AlertRepository
    private lateinit var resolveAlertUseCase: ResolveAlertUseCase

    @Before
    fun setUp() {
        alertRepository = mockk()
        resolveAlertUseCase = ResolveAlertUseCase(alertRepository)
    }

    @Test
    fun `delegates resolveAlert to repository`() = runTest {
        val alert = makeAlert(id = "to-resolve")
        coEvery { alertRepository.resolveAlert(alert) } just Runs

        resolveAlertUseCase(alert)

        coVerify(exactly = 1) { alertRepository.resolveAlert(alert) }
    }

    @Test
    fun `passes the exact alert object to repository`() = runTest {
        val capturedAlert = slot<com.uade.huellitas.domain.model.Alert>()
        val activeAlert = makeAlert(status = AlertStatus.ACTIVE)
        coEvery { alertRepository.resolveAlert(capture(capturedAlert)) } just Runs

        resolveAlertUseCase(activeAlert)

        assertEquals(activeAlert.id, capturedAlert.captured.id)
    }
}
