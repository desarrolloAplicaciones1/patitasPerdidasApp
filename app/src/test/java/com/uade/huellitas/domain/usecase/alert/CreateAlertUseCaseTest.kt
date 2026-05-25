package com.uade.huellitas.domain.usecase.alert

import com.uade.huellitas.makeAlert
import com.uade.huellitas.domain.repository.AlertRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CreateAlertUseCaseTest {

    private lateinit var alertRepository: AlertRepository
    private lateinit var createAlertUseCase: CreateAlertUseCase

    @Before
    fun setUp() {
        alertRepository = mockk()
        createAlertUseCase = CreateAlertUseCase(alertRepository)
    }

    @Test
    fun `delegates saveAlert to repository with correct alert`() = runTest {
        val alert = makeAlert(id = "new-alert")
        coEvery { alertRepository.saveAlert(alert) } just Runs

        createAlertUseCase(alert)

        coVerify(exactly = 1) { alertRepository.saveAlert(alert) }
    }

    @Test(expected = RuntimeException::class)
    fun `propagates exception from repository`() = runTest {
        coEvery { alertRepository.saveAlert(any()) } throws RuntimeException("Error de red")

        createAlertUseCase(makeAlert())
    }
}
