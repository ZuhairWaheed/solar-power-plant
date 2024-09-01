package com.assessment.solar.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PowerPlantTest {

    @Nested
    internal inner class PowerPlantTest {

        @Test
        fun `test Create New Object with default values`() {
            val powerPlant = PowerPlant()

            Assertions.assertNotNull(powerPlant.id)
            Assertions.assertNotNull(powerPlant.name)
            Assertions.assertNotNull(powerPlant.age)
            Assertions.assertNotNull(powerPlant.createdAt)
            Assertions.assertNotNull(powerPlant.updatedAt)
        }

        @Test
        fun `test Create New Object with values`() {
            val powerPlant = PowerPlant("PowerPlant 1", 875)

            Assertions.assertNotNull(powerPlant.id)
            Assertions.assertEquals("PowerPlant 1", powerPlant.name)
            Assertions.assertEquals(875, powerPlant.age)
            Assertions.assertNotNull(powerPlant.createdAt)
            Assertions.assertNotNull(powerPlant.updatedAt)
        }
    }
}