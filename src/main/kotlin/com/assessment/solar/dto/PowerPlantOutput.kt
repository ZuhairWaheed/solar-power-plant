package com.assessment.solar.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PowerPlantOutput(
    val name: String,
    val age: Int,
    val outputInKWh: Double
)
