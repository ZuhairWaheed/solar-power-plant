package com.assessment.solar.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class PowerPlantRequestDTO @JsonCreator constructor(
    @JsonProperty("name")
    val name: String,

    @JsonProperty("age")
    val age: Int
)