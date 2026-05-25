package com.uade.huellitas.domain.usecase.alert

import com.uade.huellitas.makeAlert
import com.uade.huellitas.domain.repository.AlertRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetAlertByIdUseCaseTest {

    private lateinit var alertRepository: AlertRepository
    private lateinit var getAlertByIdUseCase: GetAlertByIdUseCase

    @Before
    fun setUp() {
        alertRepository = mockk()
        getAlertByIdUseCase = GetAlertByIdUseCase(alertRepository)
    }

    @Test
    fun `returns alert when found`() = runTest {
        val alert = makeAlert(id = "alert-42")
        coEvery { alertRepository.getById("alert-42") } returns alert

        val result = getAlertByIdUseCase("alert-42")

        assertEquals(alert, result)
    }

    @Test
    fun `returns null when alert does not exist`() = runTest {
        coEvery { alertRepository.getById("nonexistent") } returns null

        val result = getAlertByIdUseCase("nonexistent")

        assertNull(result)
    }

    @Test
    fun `delegates to repository with the given id`() = runTest {
        coEvery { alertRepository.getById("id-7") } returns null

        getAlertByIdUseCase("id-7")

        coVerify(exactly = 1) { alertRepository.getById("id-7") }
    }
}
