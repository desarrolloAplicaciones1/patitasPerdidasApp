package com.uade.huellitas.domain.usecase.location

import com.uade.huellitas.domain.model.DeviceLocationResult
import com.uade.huellitas.domain.model.Location
import com.uade.huellitas.domain.repository.DeviceLocationRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCurrentDeviceLocationUseCaseTest {

    private lateinit var deviceLocationRepository: DeviceLocationRepository
    private lateinit var getCurrentDeviceLocationUseCase: GetCurrentDeviceLocationUseCase

    @Before
    fun setUp() {
        deviceLocationRepository = mockk()
        getCurrentDeviceLocationUseCase = GetCurrentDeviceLocationUseCase(deviceLocationRepository)
    }

    @Test
    fun `delegates current location lookup to repository`() = runTest {
        val expected = DeviceLocationResult.Available(Location(-34.6037, -58.3816))
        coEvery { deviceLocationRepository.getCurrentLocation() } returns expected

        val result = getCurrentDeviceLocationUseCase()

        assertEquals(expected, result)
        coVerify(exactly = 1) { deviceLocationRepository.getCurrentLocation() }
    }
}
