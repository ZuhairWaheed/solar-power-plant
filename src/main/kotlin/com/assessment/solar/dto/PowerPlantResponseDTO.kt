package com.assessment.solar.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PowerPlantResponseDTO(
    val producedKWh: Double,
    val Network: List<PowerPlantNetwork>
)


data class PowerPlantNetwork(
    val name: String,
    val age: Int
)
