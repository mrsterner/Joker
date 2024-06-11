package dev.sterner.joker.game

enum class GameStage(val time: Int) {
    NONE(20 * 2),
    CHOICE_PHASE(20 * 2),
    PLAY_PHASE(20 * 2),
    DRAW_PHASE(20 * 2),
    EVAL_PHASE(20 * 2),
    RESTOCK_PHASE(20 * 1);

}