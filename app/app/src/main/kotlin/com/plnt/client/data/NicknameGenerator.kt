package com.plnt.client.data

import kotlin.random.Random

/** Adjective+noun nickname for people who don't want to think of one. */
object NicknameGenerator {
    private val adjectives = listOf(
        "Quiet", "Swift", "Lone", "Iron", "Amber", "Rusty", "Faded", "Bold",
        "Hollow", "Sable", "Gilded", "Restless", "Wandering", "Sharp", "Dusty",
    )
    private val nouns = listOf(
        "Falcon", "Wolf", "Ember", "Anchor", "Compass", "Raven", "Fox", "Harbor",
        "Signal", "Drifter", "Sparrow", "Lantern", "Ridge", "Tide", "Wren",
    )

    fun random(): String {
        val adjective = adjectives[Random.nextInt(adjectives.size)]
        val noun = nouns[Random.nextInt(nouns.size)]
        val suffix = Random.nextInt(100)
        return "$adjective$noun$suffix"
    }
}
