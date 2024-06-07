package dev.sterner.joker.core

enum class Modifier(val jokerExclusive: Boolean) {
    FOIL(false),
    HOLOGRAPHIC(false),
    POLYCHROME(false),
    NEGATIVE(true)
}