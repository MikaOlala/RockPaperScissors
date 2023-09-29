package com.mikaela.sps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class GameActivity : AppCompatActivity() {
    private var room: Room? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val roomName: TextView = findViewById(R.id.roomName)
        val score1: TextView = findViewById(R.id.score1)
        val score2: TextView = findViewById(R.id.score2)
        val choice1: TextView = findViewById(R.id.choice1)
        val choice2: TextView = findViewById(R.id.choice2)

        room = Ius.getRoomFromPref(this, Ius.keySavedGame)
        if (room==null) {
            //TODO: make a toast
            return
        }

        roomName.text = room!!.name
    }
}