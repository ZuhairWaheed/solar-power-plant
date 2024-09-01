package com.assessment.solar

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableAutoConfiguration
class SolarPowerPlantApplication

fun main(args: Array<String>) {
	runApplication<SolarPowerPlantApplication>(*args)
}
