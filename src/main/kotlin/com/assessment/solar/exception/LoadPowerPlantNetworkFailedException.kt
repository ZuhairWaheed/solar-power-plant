package com.assessment.solar.exception


class LoadPowerPlantNetworkFailedException(ex: Exception): RuntimeException(DESCRIPTION, ex) {

    companion object {
            const val DESCRIPTION = "Failed to load power plant network:"
    }
}