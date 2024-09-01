package com.assessment.solar.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "power_plant")
data class PowerPlant(

    @Column(name = "name", nullable = false)
    val name: String = "",

    @Column(name = "age", nullable = false)
    val age: Int = 0

): BaseEntity()