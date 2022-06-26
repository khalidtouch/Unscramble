package com.reel.unscramble.ui.game


import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.reel.unscramble.R
import com.reel.unscramble.databinding.FragmentGameBinding

class GameFragment : Fragment(), IUserResponse {
    val TAG = "GameFragment"

    private lateinit var binding: FragmentGameBinding
    private val viewModel: GameViewModel by viewModels()
    private val userResponse: IUserResponse = this

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        Log.d("GameFragment", "GameFragment created/re-created!")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }


        binding.score.text = getString(R.string.score, 0)
        binding.wordCount.text = getString(R.string.word_count, 0, MAX_NO_OF_WORDS)
    }

    override fun onResume() {
        super.onResume()
        updateNextWordOnScreen()
    }

    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
            updateNextWordOnScreen()
        } else showFinalScoreDialog()
    }

    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString().lowercase().trim()

        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            userResponse.onSuccess()
            if (viewModel.nextWord()) {
                updateNextWordOnScreen()
            } else showFinalScoreDialog()
        } else {
            setErrorTextField(true)
            userResponse.onFailure("Oops!, Try again")
        }
    }


    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
        updateNextWordOnScreen()
    }

    private fun exitGame() {
        activity?.finish()
    }

    private fun updateNextWordOnScreen() {
        viewModel.currentScrambledWord.observe(viewLifecycleOwner) { word ->
            binding.textViewUnscrambledWord.text = word
            Log.d(TAG, "updateNextWordOnScreen: $word")
        }
        viewModel.currentWordCount.observe(viewLifecycleOwner) { wordCount ->
            binding.wordCount.text = getString(R.string.word_count, wordCount, MAX_NO_OF_WORDS)
        }
        viewModel.score.observe(viewLifecycleOwner) { score ->
            binding.score.text = getString(R.string.score, score)
        }
    }

    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("GameFragment", "GameFragment destroyed!")
    }

    private fun showFinalScoreDialog() {
        viewModel.score.observe(viewLifecycleOwner) { score ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.congratulations))
                .setMessage(getString(R.string.you_scored, score))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.exit)) { _, _ ->
                    exitGame()
                }
                .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                    restartGame()
                }
                .show()
        }

    }


    private fun flashResponseColor(fromColorId: Int, toColorId: Int, duration: Long) {
        val from = ContextCompat.getColor(requireContext(), fromColorId)
        val to = ContextCompat.getColor(requireContext(), toColorId)
        val colorAnimation = ObjectAnimator.ofObject(
            binding.ScrollViewId,
            "backgroundColor", ArgbEvaluator(),
            from, to
        )
        colorAnimation.duration = duration
        colorAnimation.start()
    }

    override fun onSuccess() {
        flashResponseColor(fromColorId = R.color.green, toColorId = R.color.white, 1000)

    }

    @SuppressLint("Recycle")
    override fun onFailure(message: String) {
        flashResponseColor(fromColorId = R.color.red_700, toColorId = R.color.white, 1000)
    }

    private fun resetColor() {
        binding.ScrollViewId.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.white)
        )
    }
}

interface IUserResponse {
    fun onSuccess()
    fun onFailure(message: String)
}