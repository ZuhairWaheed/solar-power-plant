package com.assessment.solar.service

import com.assessment.solar.dto.PowerPlantNetwork
import com.assessment.solar.dto.PowerPlantOutput
import com.assessment.solar.dto.PowerPlantRequestDTO
import com.assessment.solar.dto.PowerPlantResponseDTO
import com.assessment.solar.exception.CalculateTotalOutputFailedException
import com.assessment.solar.exception.GetNetworkStateFailedException
import com.assessment.solar.exception.LoadPowerPlantNetworkFailedException
import com.assessment.solar.model.PowerPlant
import com.assessment.solar.repository.PowerPlantRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("PowerPlantService")
class PowerPlantService(
    val powerPlantRepository: PowerPlantRepository
) {

    private val logger = LoggerFactory.getLogger(PowerPlantService::class.java)

    /**
     * Calculates the energy production of the uploaded power plant network during T days.
     *
     * @param powerPlants The list of uploaded power plant network.
     * @param days The number of days for the calculation.
     * @return The PowerPlantResponseDTO - energy produced in KWH and the uploaded power plant network.
     */
    fun calculateEnergyProduction(powerPlants: List<PowerPlantRequestDTO>, days: Int): PowerPlantResponseDTO {

        //total energy in kwh
        var totalEnergy = 0.0

        //loop over the given power plant network to calculate the output during the given days
        val updatedPowerPlants: List<PowerPlantNetwork> = powerPlants.map { plant ->
            val energyProduced = calculatePlantOutput(plant.age, days)

            totalEnergy += energyProduced

            //create network of updated power plants with updated age
            val newAge = plant.age + days
            PowerPlantNetwork(
                name = plant.name,
                age = newAge
            )
        }
        return PowerPlantResponseDTO(totalEnergy, updatedPowerPlants)
    }

    /**
     * Clears the previous state and loads a network of power plants.
     *
     * @param plants The list of uploaded power plant network.
     * @throws LoadPowerPlantNetworkFailedException in case of any error event.
     */
    @Transactional
    fun loadPowerPlantNetwork(plants: List<PowerPlantRequestDTO>) {
        try {
            // Clear previous state
            powerPlantRepository.deleteAll()

            val updatedPowerPlants = plants.map { plant ->
                PowerPlant(plant.name, plant.age)
            }
            // Save new power plants
            powerPlantRepository.saveAll(updatedPowerPlants)
        } catch (ex: Exception) {
            logger.error("Failed to loadPowerPlantNetwork: ", ex)
            throw LoadPowerPlantNetworkFailedException(ex)
        }
    }

    /**
     * Fetches all the power plants and Calculates their energy output during T days.
     *
     * @param days The number of days for the calculation.
     * @return The total energy output produced in KWH.
     * @throws CalculateTotalOutputFailedException in case of any error event.
     */
    fun calculateTotalOutputForTDays(days: Int): Double {

        //total energy output in kwh
        val totalOutput: Double

        try {
            //fetch the existing power plants
            val powerPlants = powerPlantRepository.findAll()

            totalOutput = powerPlants.sumOf { plant ->
                calculatePlantOutput(plant.age, days)
            }

        } catch (ex: Exception) {
            logger.error("Failed to calculateTotalOutputForTDays: ", ex)
            throw CalculateTotalOutputFailedException(ex)
        }
        return totalOutput
    }

    /**
     * Calculates the energy production of the uploaded power plant network.
     *
     * @param days The number of days for the calculation.
     * @return The List of PowerPlantOutput - energy produced in KWH by each powerplant along with its age and name.
     * @throws GetNetworkStateFailedException in case of any error event.
     */
    fun getNetworkStateWithTDays(days: Int): List<PowerPlantOutput> {
        val powerPlantOutput: List<PowerPlantOutput>

        try {
            //fetch the existing power plants
            val powerPlants = powerPlantRepository.findAll()

            powerPlantOutput = powerPlants.map { plant ->
                val newAge = plant.age + days
                PowerPlantOutput(
                    name = plant.name,
                    age = newAge,
                    outputInKWh = calculatePlantOutput(plant.age, days)
                )
            }
        } catch (ex: Exception) {
            logger.error("Failed to calculateTotalOutputForTDays: ", ex)
            throw GetNetworkStateFailedException(ex)
        }
        return powerPlantOutput
    }

    /**
     * Implements the energy production calculation logic.
     *
     * @param initialAge The initial age of the power plant for the calculation.
     * @param days The number of days for the calculation.
     * @return The energy output produced in KWH.
     */
    private fun calculatePlantOutput(initialAge: Int, days: Int): Double {

        // Simplified to 1000 full sun hours per year
        val fullSunHoursPerDay = 1000 / 365.0

        //total energy in kwh
        var totalEnergy = 0.0

        //looping over the given days to calculate output at every age
        for (day in 0 until days) {
            val currentAge = initialAge + day

            if (isCurrentAgeGreaterThanRequiredAgeAndLessThanBreakdownAge(currentAge)) {

                //energy output in KW in days
                val dailyOutput = 20 * (1 - (currentAge / 365.0 * 0.005))

                //assuming that we have to multiply with sun hours to get energy output in KWH
                totalEnergy += dailyOutput * fullSunHoursPerDay
            }
        }
        return totalEnergy
    }

    private fun isCurrentAgeGreaterThanRequiredAgeAndLessThanBreakdownAge(currentAge: Int) =
        currentAge > 60 && currentAge < 25 * 365
}
