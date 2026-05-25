package com.uade.huellitas.domain.usecase.alert

import com.uade.huellitas.makeAlert
import com.uade.huellitas.domain.repository.AlertRepository
import com.uade.huellitas.domain.repository.UserRepository
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

class GetMyAlertsUseCaseTest {

    private lateinit var alertRepository: AlertRepository
    private lateinit var userRepository: UserRepository
    private lateinit var getMyAlertsUseCase: GetMyAlertsUseCase

    @Before
    fun setUp() {
        alertRepository = mockk()
        userRepository  = mockk()
        getMyAlertsUseCase = GetMyAlertsUseCase(alertRepository, userRepository)
    }

    @Test
    fun `returns empty flow when currentUserId is null`() = runTest {
        every { userRepository.currentUserId } returns null

        val result = getMyAlertsUseCase().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `returns alerts from repository when user is logged in`() = runTest {
        val alerts = listOf(makeAlert(ownerId = "user-1"), makeAlert(id = "alert-2", ownerId = "user-1"))
        every { userRepository.currentUserId } returns "user-1"
        every { alertRepository.getMyAlerts("user-1") } returns flowOf(alerts)

        val result = getMyAlertsUseCase().first()

        assertEquals(alerts, result)
    }

    @Test
    fun `does not query repository when currentUserId is null`() = runTest {
        every { userRepository.currentUserId } returns null

        getMyAlertsUseCase().first()

        verify(exactly = 0) { alertRepository.getMyAlerts(any()) }
    }

    @Test
    fun `queries repository with the correct user id`() = runTest {
        every { userRepository.currentUserId } returns "uid-99"
        every { alertRepository.getMyAlerts("uid-99") } returns flowOf(emptyList())

        getMyAlertsUseCase().first()

        verify(exactly = 1) { alertRepository.getMyAlerts("uid-99") }
    }
}
