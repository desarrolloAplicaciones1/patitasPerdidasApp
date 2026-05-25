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

class DeleteAlertUseCaseTest {

    private lateinit var alertRepository: AlertRepository
    private lateinit var deleteAlertUseCase: DeleteAlertUseCase

    @Before
    fun setUp() {
        alertRepository = mockk()
        deleteAlertUseCase = DeleteAlertUseCase(alertRepository)
    }

    @Test
    fun `delegates deleteAlert to repository`() = runTest {
        val alert = makeAlert(id = "to-delete")
        coEvery { alertRepository.deleteAlert(alert) } just Runs

        deleteAlertUseCase(alert)

        coVerify(exactly = 1) { alertRepository.deleteAlert(alert) }
    }

    @Test(expected = RuntimeException::class)
    fun `propagates exception from repository`() = runTest {
        coEvery { alertRepository.deleteAlert(any()) } throws RuntimeException("Error al eliminar")

        deleteAlertUseCase(makeAlert())
    }
}
