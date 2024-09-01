package com.assessment.solar.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BaseEntityTest {

    @Nested
    internal inner class BaseEntityTest {

        @Test
        fun `test Create New Object`() {
            val baseEntity = BaseEntity()
            Assertions.assertNotNull(baseEntity.id)
            Assertions.assertNotNull(baseEntity.createdAt)
            Assertions.assertNotNull(baseEntity.updatedAt)
        }
    }
}