package com.reel.unscramble.utils

sealed class UserResponseState {
    object AnswerIsCorrect : UserResponseState()
    data class Failure(val message: String) : UserResponseState()
}