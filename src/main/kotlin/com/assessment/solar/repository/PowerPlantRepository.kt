package com.assessment.solar.repository

import com.assessment.solar.model.PowerPlant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PowerPlantRepository : JpaRepository<PowerPlant, String> {

}