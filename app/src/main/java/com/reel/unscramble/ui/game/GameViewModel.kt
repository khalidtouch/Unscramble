package com.reel.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    val TAG = "GameFragmentViewModel"
    private var _score = MutableLiveData<Int>(0)
    val score: LiveData<Int> get() = _score

    private var _currentWordCount = MutableLiveData<Int>(0)
    val currentWordCount: LiveData<Int> get() = _currentWordCount

    private var _currentScrambledWord =  MutableLiveData<String>()
    val currentScrambledWord: LiveData<String> get() = _currentScrambledWord

    private var _count = MutableLiveData<Int>(0)
    val count: LiveData<Int> get() = _count

    private var wordsList: MutableLiveData<MutableList<String>> = MutableLiveData(mutableListOf())
    private lateinit var currentWord: String

    init {
        Log.d(TAG, ":ViewModel created ")
        getNextWord()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragmentViewModel", "GameViewModel destroyed!")
    }

    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()
        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }
        if (wordsList.value!!.contains(currentWord)) getNextWord() else {
            _currentScrambledWord.postValue(String(tempWord))
            _currentWordCount.postValue(_currentWordCount.value!!.plus(1))
            wordsList.value!!.add(currentWord)
        }
    }

    fun nextWord(): Boolean {
        return if (currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }

    private fun increaseScore() {
        _score.postValue(_score.value!! + SCORE_INCREASE)
    }

    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    fun reinitializeData() {
        _score.postValue(0)
        _currentWordCount.postValue(0)
        wordsList = MutableLiveData()
        getNextWord()
    }
}