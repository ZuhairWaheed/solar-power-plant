package com.assessment.solar.controller

import com.assessment.solar.dto.PowerPlantOutput
import com.assessment.solar.dto.PowerPlantRequestDTO
import com.assessment.solar.dto.PowerPlantResponseDTO
import com.assessment.solar.service.PowerPlantService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
class PowerPlantControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var powerPlantService: PowerPlantService

    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(PowerPlantController(powerPlantService))
            .build()
        objectMapper = ObjectMapper()
    }

    companion object {
        private const val BASE_URI = "/solar-simulator"
    }

    @Nested
    inner class UploadPowerPlantsTest {

        @Test
        fun `uploadPowerPlants should return ok when file is valid`() {
            val mockFileContent = "[{\"name\":\"Plant1\", \"age\":100}, {\"name\":\"Plant2\", \"age\":500}]"
            val mockFile = MockMultipartFile("file", "plants.json", "application/json", mockFileContent.toByteArray())

            val responseDTO = PowerPlantResponseDTO(1000.0, emptyList())
            Mockito.`when`(powerPlantService.calculateEnergyProduction(Mockito.anyList(), Mockito.anyInt()))
                .thenReturn(responseDTO)

            mockMvc.perform(
                MockMvcRequestBuilders.multipart("$BASE_URI/upload")
                    .file(mockFile)
                    .param("days", "10")
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        }

        @Test
        fun `uploadPowerPlants should return bad request when file is empty`() {
            val mockFileContent = ""
            val mockFile = MockMultipartFile("file", "plants.json", "application/json", mockFileContent.toByteArray())

            mockMvc.perform(
                MockMvcRequestBuilders.multipart("$BASE_URI/upload")
                    .file(mockFile)
                    .param("days", "10")
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
        }
    }

    @Nested
    inner class LoadNetworkTest {

        @Test
        fun `loadNetwork should return RESET_CONTENT status`() {
            val powerPlants = listOf(
                PowerPlantRequestDTO("Plant1", 10),
                PowerPlantRequestDTO("Plant2", 5)
            )

            val jsonContent = objectMapper.writeValueAsString(powerPlants)

            mockMvc.perform(MockMvcRequestBuilders.post("$BASE_URI/load")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isResetContent)

            Mockito.verify(powerPlantService, Mockito.times(1)).loadPowerPlantNetwork(powerPlants)
        }
    }

    @Nested
    inner class GetTotalOutputTest {

        @Test
        fun `getTotalOutput should return correct total output`() {
            val days = 7
            val expectedTotalOutput = 1234.56

            Mockito.`when`(powerPlantService.calculateTotalOutputForTDays(days)).thenReturn(expectedTotalOutput)

            mockMvc.perform(MockMvcRequestBuilders.get("$BASE_URI/output/T:$days"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.total-output-in-kwh").value(expectedTotalOutput))

            Mockito.verify(powerPlantService, Mockito.times(1)).calculateTotalOutputForTDays(days)
        }
    }

    @Nested
    inner class GetNetworkStateWithOutputTest {

        @Test
        fun `getNetworkStateWithOutput should return correct network state`() {
            val days = 7
            val plantsWithOutput = listOf(
                PowerPlantOutput("Plant1", 500, 5000.0),
                PowerPlantOutput("Plant2", 750, 7500.0)
            )

            Mockito.`when`(powerPlantService.getNetworkStateWithTDays(days)).thenReturn(plantsWithOutput)

            val expectedResponse = objectMapper.writeValueAsString(plantsWithOutput)

            mockMvc.perform(MockMvcRequestBuilders.get("$BASE_URI/network/T:$days")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse))

            Mockito.verify(powerPlantService, Mockito.times(1)).getNetworkStateWithTDays(days)
        }
    }
}