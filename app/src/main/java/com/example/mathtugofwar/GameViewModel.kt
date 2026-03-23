package com.example.mathtugofwar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

data class MathQuestion(val displayStr: String, val answer: Int)

data class GameState(
    val appMode: String = "LAUNCH", // LAUNCH, MODE_SELECTION, COUNTDOWN, PLAYING, GAME_OVER
    val ropePosition: Int = 0,
    val p1Question: MathQuestion = MathQuestion("", 0),
    val p2Question: MathQuestion = MathQuestion("", 0),
    val p1Input: String = "",
    val p2Input: String = "",
    val p1PreviewStatus: String = "IDLE", // IDLE, CORRECT, WRONG
    val p2PreviewStatus: String = "IDLE",
    val winner: Int = 0
)

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private val WINNING_STEPS = 5
    private val operations = listOf("+", "-", "*", "/")

    fun setAppMode(mode: String) {
        _uiState.update { it.copy(appMode = mode) }
    }

    fun startCountdown() {
        _uiState.update { it.copy(appMode = "COUNTDOWN") }
    }

    fun startGamePlay() {
        val q1 = generateMathQuestion()
        var q2 = generateMathQuestion()
        while (q1.displayStr == q2.displayStr && !q1.displayStr.contains("÷")) {
            q2 = generateMathQuestion()
        }

        _uiState.update {
            it.copy(
                appMode = "PLAYING",
                ropePosition = 0,
                p1Question = q1,
                p2Question = q2,
                p1Input = "",
                p2Input = "",
                p1PreviewStatus = "IDLE",
                p2PreviewStatus = "IDLE",
                winner = 0
            )
        }
    }

    private fun generateMathQuestion(): MathQuestion {
        val op = operations.random()
        return when (op) {
            "+" -> {
                val n1 = Random.nextInt(1, 20)
                val n2 = Random.nextInt(1, 20)
                MathQuestion("$n1 + $n2", n1 + n2)
            }
            "-" -> {
                val n1 = Random.nextInt(5, 20)
                val n2 = Random.nextInt(1, n1)
                MathQuestion("$n1 - $n2", n1 - n2)
            }
            "*" -> {
                val n1 = Random.nextInt(2, 10)
                val n2 = Random.nextInt(2, 10)
                MathQuestion("$n1 × $n2", n1 * n2)
            }
            else -> {
                val n2 = Random.nextInt(2, 10)
                val ans = Random.nextInt(2, 10)
                val n1 = n2 * ans
                MathQuestion("$n1 ÷ $n2", ans)
            }
        }
    }

    fun handleP1Input(key: String) {
        if (_uiState.value.appMode != "PLAYING") return
        val current = _uiState.value.p1Input
        var newStr = current

        if (key == "DEL") {
            if (current.isNotEmpty()) newStr = current.dropLast(1)
        } else if (key == "OK") {
            if (current.isNotEmpty()) {
                val num = current.toIntOrNull()
                if (num == _uiState.value.p1Question.answer) {
                    val newRope = _uiState.value.ropePosition - 1
                    _uiState.update { it.copy(ropePosition = newRope, p1PreviewStatus = "CORRECT") }
                    checkWin(newRope)
                    if (_uiState.value.appMode == "PLAYING") {
                        val q1 = generateMathQuestion()
                        _uiState.update { it.copy(p1Question = q1, p1Input = "", p1PreviewStatus = "IDLE") }
                    }
                } else {
                    _uiState.update { it.copy(p1PreviewStatus = "WRONG") }
                }
            }
        } else {
            if (current.length < 3) newStr += key
        }
        _uiState.update { it.copy(p1Input = newStr) }
    }

    fun handleP2Input(key: String) {
        if (_uiState.value.appMode != "PLAYING") return
        val current = _uiState.value.p2Input
        var newStr = current

        if (key == "DEL") {
            if (current.isNotEmpty()) newStr = current.dropLast(1)
        } else if (key == "OK") {
            if (current.isNotEmpty()) {
                val num = current.toIntOrNull()
                if (num == _uiState.value.p2Question.answer) {
                    val newRope = _uiState.value.ropePosition + 1
                    _uiState.update { it.copy(ropePosition = newRope, p2PreviewStatus = "CORRECT") }
                    checkWin(newRope)
                    if (_uiState.value.appMode == "PLAYING") {
                        val q2 = generateMathQuestion()
                        _uiState.update { it.copy(p2Question = q2, p2Input = "", p2PreviewStatus = "IDLE") }
                    }
                } else {
                    _uiState.update { it.copy(p2PreviewStatus = "WRONG") }
                }
            }
        } else {
            if (current.length < 3) newStr += key
        }
        _uiState.update { it.copy(p2Input = newStr) }
    }

    fun clearP1WrongStatus() {
        if (_uiState.value.p1PreviewStatus == "WRONG") {
            _uiState.update { it.copy(p1Input = "", p1PreviewStatus = "IDLE") }
        }
    }

    fun clearP2WrongStatus() {
        if (_uiState.value.p2PreviewStatus == "WRONG") {
            _uiState.update { it.copy(p2Input = "", p2PreviewStatus = "IDLE") }
        }
    }

    private fun checkWin(pos: Int) {
        if (pos <= -WINNING_STEPS) {
            _uiState.update { it.copy(appMode = "GAME_OVER", winner = 1) }
        } else if (pos >= WINNING_STEPS) {
            _uiState.update { it.copy(appMode = "GAME_OVER", winner = 2) }
        }
    }
}
