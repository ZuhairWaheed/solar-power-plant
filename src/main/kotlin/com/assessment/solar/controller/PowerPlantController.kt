package com.assessment.solar.controller

import com.assessment.solar.dto.PowerPlantOutput
import com.assessment.solar.dto.PowerPlantRequestDTO
import com.assessment.solar.dto.PowerPlantResponseDTO
import com.assessment.solar.service.PowerPlantService
import com.fasterxml.jackson.databind.ObjectMapper
import org.jetbrains.annotations.NotNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/solar-simulator")
class PowerPlantController(val powerPlantService: PowerPlantService) {

    @PostMapping("/upload")
    fun uploadPowerPlants(
        @NotNull @RequestParam("file") file: MultipartFile,
        @RequestParam("days") T: Int
    ): ResponseEntity<PowerPlantResponseDTO> {

        //validation to make sure that the resources are not utilised over bad input i.e: empty file
        if (file.isEmpty) {
            return ResponseEntity.badRequest().build()
        }

        //convert the file contents into list of dto for simplicity
        val plants = ObjectMapper().readValue(file.bytes, Array<PowerPlantRequestDTO>::class.java).toList()
        val response = powerPlantService.calculateEnergyProduction(plants, T)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/load")
    fun loadNetwork(@RequestBody plants: List<PowerPlantRequestDTO>): ResponseEntity<Unit> {
        powerPlantService.loadPowerPlantNetwork(plants)
        return ResponseEntity.status(HttpStatus.RESET_CONTENT).build()
    }

    @GetMapping("/output/T:{days}")
    fun getTotalOutput(@PathVariable("days") days: Int): Map<String, Double> {
        val totalOutput = powerPlantService.calculateTotalOutputForTDays(days)
        //return ResponseEntity.ok(TotalOutputDTO(totalOutputInKWh = totalOutput))
        //used to map to make sure that the output is exactly similar to the one in the assessment user story
        return mapOf("total-output-in-kwh" to totalOutput)
    }

    @GetMapping("/network/T:{days}")
    fun getNetworkStateWithOutput(@PathVariable("days") days: Int): ResponseEntity<List<PowerPlantOutput>> {
        val plantsWithOutput = powerPlantService.getNetworkStateWithTDays(days)
        return ResponseEntity.ok(plantsWithOutput)
    }

}