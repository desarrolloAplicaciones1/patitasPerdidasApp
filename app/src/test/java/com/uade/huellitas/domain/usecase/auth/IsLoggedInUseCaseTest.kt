package com.uade.huellitas.domain.usecase.auth

import com.uade.huellitas.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IsLoggedInUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var isLoggedInUseCase: IsLoggedInUseCase

    @Before
    fun setUp() {
        userRepository = mockk()
        isLoggedInUseCase = IsLoggedInUseCase(userRepository)
    }

    @Test
    fun `returns true when user is logged in`() {
        every { userRepository.isLoggedIn() } returns true
        assertTrue(isLoggedInUseCase())
    }

    @Test
    fun `returns false when user is not logged in`() {
        every { userRepository.isLoggedIn() } returns false
        assertFalse(isLoggedInUseCase())
    }

    @Test
    fun `delegates to repository exactly once`() {
        every { userRepository.isLoggedIn() } returns false
        isLoggedInUseCase()
        verify(exactly = 1) { userRepository.isLoggedIn() }
    }
}
