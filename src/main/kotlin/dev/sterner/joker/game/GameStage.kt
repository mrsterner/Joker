package dev.sterner.joker.game

enum class GameStage(val time: Int) {
    NONE(20 * 5),
    CHOICE_PHASE(20 * 5),
    PLAY_PHASE(20 * 5),
    DRAW_PHASE(20 * 5),
    EVAL_PHASE(20 * 5);

}