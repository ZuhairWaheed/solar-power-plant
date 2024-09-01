package com.assessment.solar.exception


class CalculateTotalOutputFailedException(ex: Exception): RuntimeException(DESCRIPTION, ex) {

    companion object {
            const val DESCRIPTION = "Failed to calculate total output of power plants in T days:"
    }
}