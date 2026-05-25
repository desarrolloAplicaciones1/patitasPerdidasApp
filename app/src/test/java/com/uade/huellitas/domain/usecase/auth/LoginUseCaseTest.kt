package com.uade.huellitas.domain.usecase.auth

import com.uade.huellitas.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        userRepository = mockk()
        loginUseCase = LoginUseCase(userRepository)
    }

    @Test
    fun `returns uid on successful login`() = runTest {
        coEvery { userRepository.login("user@test.com", "pass123") } returns "uid-abc"
        val result = loginUseCase("user@test.com", "pass123")
        assertEquals("uid-abc", result)
    }

    @Test
    fun `delegates to repository with correct credentials`() = runTest {
        coEvery { userRepository.login("a@b.com", "1234") } returns "uid-1"
        loginUseCase("a@b.com", "1234")
        coVerify(exactly = 1) { userRepository.login("a@b.com", "1234") }
    }

    @Test(expected = RuntimeException::class)
    fun `propagates exception from repository`() = runTest {
        coEvery { userRepository.login(any(), any()) } throws RuntimeException("Credenciales inválidas")
        loginUseCase("user@test.com", "wrong")
    }
}
