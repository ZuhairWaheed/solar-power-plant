package com.assessment.solar.exception


class GetNetworkStateFailedException(ex: Exception): RuntimeException(DESCRIPTION, ex) {

    companion object {
            const val DESCRIPTION = "Failed to get network state of power plants in T days:"
    }
}