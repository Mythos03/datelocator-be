package com.datelocator.datelocatorbe.user

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class Genders {
    Male,
    Female,
    Other;

    @JsonValue
    override fun toString(): String = name.uppercase()

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromString(value: String): Genders? = try {
            valueOf(value.uppercase())
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}