package dev.sterner.joker.core

enum class Rank(val value: Int, val faced: Boolean) {
    TWO(2, false), THREE(3, false), FOUR(4, false), FIVE(5, false), SIX(6, false),
    SEVEN(7, false), EIGHT(8, false), NINE(9, false), TEN(10, false),

    JACK(11, true), QUEEN(12, true), KING(13, true), ACE(14, false)
}