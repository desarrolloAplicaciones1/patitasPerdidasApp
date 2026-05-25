package com.uade.huellitas.domain.usecase.auth

import com.uade.huellitas.domain.model.User
import com.uade.huellitas.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RegisterUserUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var registerUserUseCase: RegisterUserUseCase

    @Before
    fun setUp() {
        userRepository = mockk()
        registerUserUseCase = RegisterUserUseCase(userRepository)
    }

    @Test
    fun `returns uid from repository on success`() = runTest {
        coEvery { userRepository.register(any(), any()) } returns "new-uid"
        coEvery { userRepository.saveUserProfile(any()) } just Runs

        val result = registerUserUseCase("María", "maria@test.com", "pass123")
        assertEquals("new-uid", result)
    }

    @Test
    fun `saves user profile with data from registration`() = runTest {
        val savedUser = slot<User>()
        coEvery { userRepository.register("mail@test.com", "pass") } returns "uid-xyz"
        coEvery { userRepository.saveUserProfile(capture(savedUser)) } just Runs

        registerUserUseCase("Pedro", "mail@test.com", "pass")

        assertEquals("uid-xyz", savedUser.captured.uid)
        assertEquals("Pedro",        savedUser.captured.name)
        assertEquals("mail@test.com", savedUser.captured.email)
    }

    @Test
    fun `calls register before saveUserProfile`() = runTest {
        coEvery { userRepository.register(any(), any()) } returns "uid-1"
        coEvery { userRepository.saveUserProfile(any()) } just Runs

        registerUserUseCase("Ana", "ana@test.com", "1234")

        coVerify(exactly = 1) { userRepository.register("ana@test.com", "1234") }
        coVerify(exactly = 1) { userRepository.saveUserProfile(any()) }
    }

    @Test(expected = RuntimeException::class)
    fun `does not save profile when registration fails`() = runTest {
        coEvery { userRepository.register(any(), any()) } throws RuntimeException("Email ya registrado")

        registerUserUseCase("Ana", "ana@test.com", "1234")

        coVerify(exactly = 0) { userRepository.saveUserProfile(any()) }
    }
}
