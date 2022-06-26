package com.reel.unscramble

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.reel.unscramble.ui.game.GameFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.game_fragment, GameFragment())
            .commit()
    }
}