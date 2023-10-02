package com.mikaela.sps

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class GameActivity : AppCompatActivity() {
    private var room: Room? = null
    private val db = Firebase.database.getReference("room")
    private lateinit var myScore: TextView
    private lateinit var score: TextView
    private lateinit var choice1: TextView
    private lateinit var choice2: TextView

    private var isMyGame = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val roomName: TextView = findViewById(R.id.roomName)
        val meText: TextView
        val enemyText: TextView
        val myIcon: ImageView
        val icon: ImageView
        choice1 = findViewById(R.id.choice1)
        choice2 = findViewById(R.id.choice2)

        room = Ius.getRoomFromPref(this, Ius.keySavedGame)
        if (room == null) {
            //TODO: make a toast
            return
        }

        roomName.text = room!!.name

        isMyGame = Ius.readSharedPreferences(this, Ius.keyIsMyGame).equals("true")
        if (isMyGame) {
            myScore = findViewById(R.id.score1)
            score = findViewById(R.id.score2)
            meText = findViewById(R.id.player1)
            enemyText = findViewById(R.id.player2)
            myIcon = findViewById(R.id.icon1)
            icon = findViewById(R.id.icon2)
        }
        else {
            myScore = findViewById(R.id.score2)
            score = findViewById(R.id.score1)
            meText = findViewById(R.id.player2)
            enemyText = findViewById(R.id.player1)
            myIcon = findViewById(R.id.icon2)
            icon = findViewById(R.id.icon1)
        }

        meText.text = getString(R.string.you)
        enemyText.text = getString(R.string.enemy)
        refreshPoints()

        myIcon.setOnClickListener {
            openDialogChoice()
        }

        listenGame()
    }

    private fun listenGame() {
        db.child(room!!.name).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var roomSnapshot = snapshot.getValue<Room>()
                if (room==roomSnapshot)
                    Log.i("listenGame onDataChange", "objects are equal")

                room = roomSnapshot
                refreshChoices()
                refreshPoints()
            }

            @Suppress("UNREACHABLE_CODE")
            override fun onCancelled(error: DatabaseError) {
                TODO("toast error")

                object : CountDownTimer(3000, 3000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        listenGame()
                    }
                }.start()
            }

        })
    }

    private fun openDialogChoice() {
        var settingsMode = false
        val dialog = Ius.createDialog(this)

        val rock: ImageView = dialog.findViewById(R.id.rock)
        val paper: ImageView = dialog.findViewById(R.id.paper)
        val scissors: ImageView = dialog.findViewById(R.id.scissors)
        val settings: ImageView = dialog.findViewById(R.id.settings)

        settings.setOnClickListener {
            settingsMode = !settingsMode
        }

        rock.setOnClickListener{
            if (settingsMode) {
                //TODO: change image to custom
            }

            setMyChoice(Ius.choiceRock)
            dialog.cancel()
        }

        paper.setOnClickListener{
            if (settingsMode) {
                //TODO: change image to custom
            }

            setMyChoice(Ius.choicePaper)
            dialog.cancel()
        }

        scissors.setOnClickListener{
            if (settingsMode) {
                //TODO: change image to custom
            }

            setMyChoice(Ius.choiceScissors)
            dialog.cancel()
        }

        dialog.show()
    }

    private fun refreshChoices() {
        var first = room!!.choiceFirst
        var second = room!!.choiceSecond

        if (first != Ius.choiceWaiting && second == Ius.choiceWaiting) {
            choice1.text = Ius.choiceWaitingForYou
            return
        } else if (first == Ius.choiceWaiting && second != Ius.choiceWaiting) {
            choice2.text = Ius.choiceWaitingForYou
            return
        }

        if (first != Ius.choiceWaiting && second != Ius.choiceWaiting) {
            choice1.text = first
            choice2.text = second

            whoWon(first, second)
            db.child(room!!.name).setValue(room).addOnFailureListener{
                //TODO: toast smth went wrong
            }
        }
    }

    private fun setMyChoice(choice: String) {
        if (isMyGame)
            room!!.choiceFirst = choice
        else
            room!!.choiceSecond = choice

        db.child(room!!.name).setValue(room).addOnFailureListener{
            //TODO: toast smth went wrong
        }
    }

    @SuppressLint("SetTextI18n")
    private fun refreshPoints() {
        if (isMyGame) {
            myScore.text = getString(R.string.score) + " " + room!!.scoreFirst
            score.text = getString(R.string.score) + " " + room!!.scoreSecond
        } else {
            myScore.text = getString(R.string.score) + " " + room!!.scoreSecond
            score.text = getString(R.string.score) + " " + room!!.scoreFirst
        }
    }

    private fun addPointsToSecond() {
        room!!.scoreSecond = room!!.scoreSecond + 1
    }

    private fun addPointsToFirst() {
        room!!.scoreFirst = room!!.scoreFirst + 1
    }

    private fun whoWon(first: String, second: String) {
        if (first == second) {
            room!!.scoreDraw = room!!.scoreDraw + 1
        }
        else if(first==Ius.choiceRock && second==Ius.choicePaper) {
            addPointsToSecond()
        }
        else if (first == Ius.choiceRock && second == Ius.choiceScissors) {
            addPointsToFirst()
        }

        else if (first == Ius.choicePaper && second == Ius.choiceScissors) {
            addPointsToSecond()
        }
        else if (first == Ius.choicePaper && second == Ius.choiceRock) {
            addPointsToFirst()
        }

        else if (first == Ius.choiceScissors && second == Ius.choiceRock) {
            addPointsToSecond()
        }
        else if (first == Ius.choiceScissors && second == Ius.choicePaper) {
            addPointsToFirst()
        }
    }
}