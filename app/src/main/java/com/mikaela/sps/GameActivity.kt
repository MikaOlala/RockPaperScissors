package com.mikaela.sps

import android.R.attr.logo
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
    private lateinit var myChoice: TextView
    private lateinit var enemyChoice: TextView

    private lateinit var myIcon: ImageView
    private lateinit var icon: ImageView

    private var isMyGame = false
    private var choosingIsLocked = false

    @SuppressLint("SetTextI18n", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val roomName: TextView = findViewById(R.id.roomName)
        val meText: TextView
        val enemyText: TextView
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
            myChoice = findViewById(R.id.choice1)
            enemyChoice = findViewById(R.id.choice2)
        }
        else {
            myScore = findViewById(R.id.score2)
            score = findViewById(R.id.score1)
            meText = findViewById(R.id.player2)
            enemyText = findViewById(R.id.player1)
            myIcon = findViewById(R.id.icon2)
            icon = findViewById(R.id.icon1)
            myChoice = findViewById(R.id.choice2)
            enemyChoice = findViewById(R.id.choice1)
        }

        meText.text = getString(R.string.you)
        enemyText.text = getString(R.string.enemy)
        refreshPoints()

        myIcon.setOnClickListener {
            if (!choosingIsLocked)
                openDialogChoice()
        }

        changeOnline(true)
        listenGame()
    }

    private fun listenGame() {
        db.child(room!!.name).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var roomSnapshot = snapshot.getValue<Room>()
                if(roomSnapshot==null) {
                    Log.i("listenGame onDataChange", "room is null")
                    //TODO: toast
                    return
                }
                if (room==roomSnapshot) {
                    Log.i("listenGame onDataChange", "objects are equal")
                    return
                }

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

            setMyChoice(Ius.statusRock)
            dialog.cancel()
        }

        paper.setOnClickListener{
            if (settingsMode) {
                //TODO: change image to custom
            }

            setMyChoice(Ius.statusPaper)
            dialog.cancel()
        }

        scissors.setOnClickListener{
            if (settingsMode) {
                //TODO: change image to custom
            }

            setMyChoice(Ius.statusScissors)
            dialog.cancel()
        }

        dialog.show()
    }

    private fun refreshChoices() {
        val first = room!!.choiceFirst
        val second = room!!.choiceSecond

        if (!isMyGame && isRockPaperScissors(first) && !isRockPaperScissors(second)) {
            choice1.text = Ius.statusWaitingForYou
            return
        } else if (isMyGame && !isRockPaperScissors(first) && isRockPaperScissors(second)) {
            choice2.text = Ius.statusWaitingForYou
            return
        }

        if (isRockPaperScissors(first) && isRockPaperScissors(second)) {
            choice1.text = first
            choice2.text = second

            whoWon(first, second)
            db.child(room!!.name).setValue(room).addOnFailureListener{
                //TODO: toast smth went wrong
            }

            object : CountDownTimer(3000, 3000) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    room!!.choiceFirst = Ius.statusChoosing
                    room!!.choiceSecond = Ius.statusChoosing
                    db.child(room!!.name).setValue(room).addOnFailureListener{
                        //TODO: toast smth went wrong
                    }
                }
            }.start()
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun setMyChoice(choice: String) {
        if (isMyGame) {
            room!!.choiceFirst = choice
            choice1.text = choice
        }
        else {
            room!!.choiceSecond = choice
            choice2.text = choice
        }
        db.child(room!!.name).setValue(room).addOnSuccessListener {
            val resId = resources.getIdentifier(choice, "drawable", packageName)
            myIcon.setImageResource(resId)


        }.addOnFailureListener{
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
        else if(first==Ius.statusRock && second==Ius.statusPaper) {
            addPointsToSecond()
        }
        else if (first == Ius.statusRock && second == Ius.statusScissors) {
            addPointsToFirst()
        }

        else if (first == Ius.statusPaper && second == Ius.statusScissors) {
            addPointsToSecond()
        }
        else if (first == Ius.statusPaper && second == Ius.statusRock) {
            addPointsToFirst()
        }

        else if (first == Ius.statusScissors && second == Ius.statusRock) {
            addPointsToSecond()
        }
        else if (first == Ius.statusScissors && second == Ius.statusPaper) {
            addPointsToFirst()
        }
    }

    private fun isRockPaperScissors(choice: String):Boolean {
        return choice==Ius.statusRock || choice==Ius.statusPaper || choice==Ius.statusScissors
    }

    private fun changeOnline(online: Boolean){
        if (online && room!!.getMyChoice(isMyGame)==Ius.statusOffline) {
            room!!.setMyChoice(Ius.statusChoosing, isMyGame)
            db.child(room!!.name).setValue(room)
            return
        }

        if (room!!.getMyChoice(isMyGame)!=Ius.statusOffline) {
            room!!.setMyChoice(Ius.statusOffline, isMyGame)
            db.child(room!!.name).setValue(room)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        changeOnline(false)
    }
}