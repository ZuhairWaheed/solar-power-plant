package com.assessment.solar.service

import com.assessment.solar.dto.PowerPlantOutput
import com.assessment.solar.dto.PowerPlantRequestDTO
import com.assessment.solar.exception.CalculateTotalOutputFailedException
import com.assessment.solar.exception.GetNetworkStateFailedException
import com.assessment.solar.exception.LoadPowerPlantNetworkFailedException
import com.assessment.solar.model.PowerPlant
import com.assessment.solar.repository.PowerPlantRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class PowerPlantServiceTest {

    lateinit var powerPlantService: PowerPlantService

    @Mock
    lateinit var powerPlantRepository: PowerPlantRepository

    @BeforeEach
    fun beforeEach() {
        this.powerPlantService = PowerPlantService(powerPlantRepository)
    }

    private val plant1 = PowerPlantRequestDTO("Plant 1", 100)
    private val plant2 = PowerPlantRequestDTO("Plant 2", 250)
    private val expectedTotalEnergy = 546.616626008632

    @Nested
    inner class UploadPowerPlantsTest {

        @Test
        fun `calculateEnergyProduction - empty list returns zero output`() {
            val emptyList = emptyList<PowerPlantRequestDTO>()
            val response = powerPlantService.calculateEnergyProduction(emptyList, 10)

            assertEquals(0.0, response.producedKWh)
            assertTrue(response.Network.isEmpty())
        }

        @Test
        fun `calculateEnergyProduction - single plant with zero days returns total energy`() {
            val plants = listOf(plant1)
            val days = 0

            val response = powerPlantService.calculateEnergyProduction(plants, days)

            assertEquals(0.0, response.producedKWh)
            assertEquals(1, response.Network.size)
            assertEquals(plant1.name, response.Network[0].name)
            assertEquals(plant1.age, response.Network[0].age)
        }

        @Test
        fun `calculateEnergyProduction - multiple plants with positive days returns total energy and updated ages`() {
            val plants = listOf(plant1, plant2)
            val days = 5

            val response = powerPlantService.calculateEnergyProduction(plants, days)

            assertEquals(expectedTotalEnergy, response.producedKWh)
            assertEquals(plants.size, response.Network.size)

            for (i in plants.indices) {
                val expectedAge = plants[i].age + days
                assertEquals(expectedAge, response.Network[i].age)
                assertEquals(plants[i].name, response.Network[i].name)
            }
        }
    }

    @Nested
    inner class LoadNetworkTest {

        @Test
        fun `load network method should execute with success`() {
            val powerPlants = listOf(
                PowerPlantRequestDTO(name = "Plant1", age = 10),
                PowerPlantRequestDTO(name = "Plant2", age = 5)
            )
            val updatedPowerPlants = powerPlants.map { PowerPlant(it.name, it.age) }

            doNothing().`when`(powerPlantRepository).deleteAll()

            `when`(
                powerPlantRepository.saveAll(updatedPowerPlants)
            ).thenReturn(updatedPowerPlants)

            powerPlantService.loadPowerPlantNetwork(powerPlants)

            verify(powerPlantRepository, times(1)).deleteAll()
            verify(powerPlantRepository, times(1)).saveAll(updatedPowerPlants)
        }

        @Test
        fun `load network method throws exception when deleteAll fails`() {
            val powerPlants = listOf(
                PowerPlantRequestDTO(name = "Plant1", age = 10),
                PowerPlantRequestDTO(name = "Plant2", age = 5)
            )
            val exception = Exception()
            val expectedException = LoadPowerPlantNetworkFailedException(exception)

            `when`(powerPlantRepository.deleteAll()).thenThrow(expectedException)

            try {
                powerPlantService.loadPowerPlantNetwork(powerPlants)
            } catch (actualException: Exception) {
                verify(powerPlantRepository, times(1)).deleteAll()
                assertEquals(expectedException.javaClass, actualException.javaClass)
                assertEquals(expectedException.message, actualException.message)
            }
        }

        @Test
        fun `load network method throws exception when saveAll fails`() {
            val powerPlants = listOf(
                PowerPlantRequestDTO(name = "Plant1", age = 10),
                PowerPlantRequestDTO(name = "Plant2", age = 5)
            )

            val updatedPowerPlants = powerPlants.map { PowerPlant(it.name, it.age) }
            val exception = Exception()
            val expectedException = LoadPowerPlantNetworkFailedException(exception)

            doNothing().`when`(powerPlantRepository).deleteAll()

            `when`(
                powerPlantRepository.saveAll(updatedPowerPlants)
            ).thenThrow(expectedException)

            try {
                powerPlantService.loadPowerPlantNetwork(powerPlants)
            } catch (actualException: Exception) {
                verify(powerPlantRepository, times(1)).deleteAll()
                assertEquals(expectedException.javaClass, actualException.javaClass)
                assertEquals(expectedException.message, actualException.message)
            }
        }
    }

    @Nested
    inner class GetTotalOutputTest {

        @Test
        fun `calculateTotalOutputForTDays method should execute with success`() {
            val days = 2
            val powerPlants = listOf(
                PowerPlant(name = "Plant 1", age = 875),
                PowerPlant(name = "Plant 2", age = 475)
            )

            `when`(
                powerPlantRepository.findAll()
            ).thenReturn(powerPlants)

            val response = powerPlantService.calculateTotalOutputForTDays(days)

            verify(powerPlantRepository, times(1)).findAll()
            assertEquals(217.14993432163632, response)
        }
        @Test
        fun `calculateTotalOutputForTDays method throws exception when findAll fails`() {
            val days = 2
            val exception = Exception()
            val expectedException = CalculateTotalOutputFailedException(exception)

            `when`(
                powerPlantRepository.findAll()
            ).thenThrow(expectedException)

            try {
                powerPlantService.calculateTotalOutputForTDays(days)
            } catch (actualException: Exception) {
                verify(powerPlantRepository, times(1)).findAll()
                assertEquals(expectedException.javaClass, actualException.javaClass)
                assertEquals(expectedException.message, actualException.message)
            }
        }
    }

    @Nested
    inner class GetNetworkStateWithOutputTest {

        @Test
        fun `getNetworkStateWithOutput method should execute with success`() {
            val days = 2
            val powerPlants = listOf(
                PowerPlant(name = "Plant 1", age = 875),
                PowerPlant(name = "Plant 2", age = 570)
            )
            val expectedResponse = listOf(
                PowerPlantOutput(name = "Plant 1", age = 875, outputInKWh = 108.27472321261024),
                PowerPlantOutput(name = "Plant 2", age = 570, outputInKWh = 108.73259523362731)
            )

            `when`(
                powerPlantRepository.findAll()
            ).thenReturn(powerPlants)

            val actualResponse = powerPlantService.getNetworkStateWithTDays(days)
            assertEquals(expectedResponse[0].outputInKWh, actualResponse[0].outputInKWh)
            assertEquals(expectedResponse[1].outputInKWh, actualResponse[1].outputInKWh)

            verify(powerPlantRepository, times(1)).findAll()
        }

        @Test
        fun `method should return same output for power plants of same age`() {
            val days = 2
            val powerPlants = listOf(
                PowerPlant(name = "Plant 1", age = 875),
                PowerPlant(name = "Plant 2", age = 875)
            )
            val expectedResponse = listOf(
                PowerPlantOutput(name = "Plant 1", age = 875, outputInKWh = 108.27472321261024),
                PowerPlantOutput(name = "Plant 2", age = 570, outputInKWh = 108.27472321261024)
            )

            `when`(
                powerPlantRepository.findAll()
            ).thenReturn(powerPlants)

            val actualResponse = powerPlantService.getNetworkStateWithTDays(days)
            assertEquals(expectedResponse[0].outputInKWh, actualResponse[1].outputInKWh)

            verify(powerPlantRepository, times(1)).findAll()
        }

        @Test
        fun `getNetworkStateWithOutput method throws exception when findAll fails`() {
            val days = 2
            val exception = Exception()
            val expectedException = GetNetworkStateFailedException(exception)

            `when`(
                powerPlantRepository.findAll()
            ).thenThrow(expectedException)

            try {
                powerPlantService.getNetworkStateWithTDays(days)
            } catch (actualException: Exception) {
                verify(powerPlantRepository, times(1)).findAll()
                assertEquals(expectedException.javaClass, actualException.javaClass)
                assertEquals(expectedException.message, actualException.message)
            }
        }
    }

}