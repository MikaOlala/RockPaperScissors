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
    private var me: Player? = null
    private var enemy: Player? = null
    private var myId: String = ""
    private var enemyId: String = ""
    private val db = Firebase.database.getReference("players")
    private val dbRoom = Firebase.database.getReference("rooms")

    private lateinit var myScore: TextView
    private lateinit var score: TextView
    private lateinit var choice1: TextView
    private lateinit var choice2: TextView
    private lateinit var myChoice: TextView
    private lateinit var choice: TextView
    private lateinit var myIcon: ImageView
    private lateinit var icon: ImageView


    @SuppressLint("SetTextI18n", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val roomName: TextView = findViewById(R.id.roomName)
        choice1 = findViewById(R.id.choice1)
        choice2 = findViewById(R.id.choice2)

        me = Ius.getPlayerFromPref(this, Ius.keyMe)
        if (me == null) {
            //TODO: make a toast
            return
        }

        roomName.text = me!!.gameConnectedTo
        myId = me!!.id

        myScore = findViewById(R.id.score1)
        score = findViewById(R.id.score2)
        myIcon = findViewById(R.id.icon1)
        icon = findViewById(R.id.icon2)
        myChoice = findViewById(R.id.choice1)
        choice = findViewById(R.id.choice2)

        myIcon.setOnClickListener {
            openDialogChoice()
        }

        changeOnline(true)
        listenMyself()
        listenEnemy()
    }

    private fun listenMyself() {
        db.child(myId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var playerSnapshot = snapshot.getValue<Player>()
                if(playerSnapshot==null) {
                    Log.e("listenGame me", "is null")
                    //TODO: toast
                    return
                }
                if (me==playerSnapshot) {
                    Log.i("listenGame onDataChange", "objects are equal")
                    return
                }

                me = playerSnapshot
                if(enemy!=null)
                    refreshUi()
            }

            @Suppress("UNREACHABLE_CODE")
            override fun onCancelled(error: DatabaseError) {
                TODO("toast error")

                object : CountDownTimer(3000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        listenMyself()
                    }
                }.start()
            }
        })
    }

    private fun listenEnemy() {
        db.child(enemyId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var playerSnapshot = snapshot.getValue<Player>()
                if(playerSnapshot==null) {
                    Log.e("listenGame enemy", "is null")
                    //TODO: toast
                    return
                }
                if (enemy==playerSnapshot) {
                    Log.i("listenGame onDataChange", "objects are equal")
                    return
                }

                enemy = playerSnapshot
                refreshUi()
            }

            @Suppress("UNREACHABLE_CODE")
            override fun onCancelled(error: DatabaseError) {
                TODO("toast error")

                object : CountDownTimer(3000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        listenEnemy()
                    }
                }.start()
            }
        })
    }

    private fun openDialogChoice() {
        val dialog = Ius.createDialog(this)

        val rock: ImageView = dialog.findViewById(R.id.rock)
        val paper: ImageView = dialog.findViewById(R.id.paper)
        val scissors: ImageView = dialog.findViewById(R.id.scissors)

        rock.setOnClickListener{
            setMyChoice(Ius.choiceRock)
            dialog.cancel()
        }

        paper.setOnClickListener{
            setMyChoice(Ius.choicePaper)
            dialog.cancel()
        }

        scissors.setOnClickListener{
            setMyChoice(Ius.choiceScissors)
            dialog.cancel()
        }

        dialog.show()
    }

    @SuppressLint("DiscouragedApi")
    private fun setMyChoice(choice: String) {
        me!!.choice = choice
        setIcon(myIcon, choice)

        if (enemy!!.choice!=Ius.statusChoosing)
            whoWon(me!!.choice, enemy!!.choice)

        //TODO: add animation loader
        db.child(myId).setValue(me).addOnSuccessListener {
            refreshUi()
            db.child(enemyId).setValue(enemy)
        }.addOnFailureListener{
            //TODO: toast smth went wrong
        }
    }

    @SuppressLint("SetTextI18n")
    private fun refreshUi() {
        val first = me!!.choice
        val second = enemy!!.choice

        myScore.text = getString(R.string.score) + ": " + me!!.score
        score.text = getString(R.string.score) + ": " + enemy!!.score

        if (!enemy!!.isOnline) {
            choice.text
            return
        }

        if (second!=Ius.statusChoosing && first==Ius.statusChoosing) { // противник выбрал и ожидает вас
            choice.text = Ius.statusWaitingForYou
            return
        }

        choice.text = second
        myChoice.text = first

        if (second!=Ius.statusChoosing && first!=Ius.statusChoosing) {
            setIcon(myIcon, first)
            setIcon(icon, second)
        }
        else if (second==Ius.statusChoosing && first==Ius.statusChoosing) {
            setIcon(myIcon, "play")
            setIcon(icon, "play")
        }
    }

    private fun setIcon(icon: ImageView, choice: String) {
        val resId = resources.getIdentifier(choice, "drawable", packageName)
        icon.setImageResource(resId)
    }

    private fun addPointsToEnemy() {
        enemy!!.score = enemy!!.score + 1
    }

    private fun addPointsToMe() {
        me!!.score = me!!.score + 1
    }

    private fun whoWon(first: String, second: String) {
        if(first==Ius.choiceRock && second==Ius.choicePaper) {
            addPointsToEnemy()
        }
        else if (first == Ius.choiceRock && second == Ius.choiceScissors) {
            addPointsToMe()
        }

        else if (first == Ius.choicePaper && second == Ius.choiceScissors) {
            addPointsToEnemy()
        }
        else if (first == Ius.choicePaper && second == Ius.choiceRock) {
            addPointsToMe()
        }

        else if (first == Ius.choiceScissors && second == Ius.choiceRock) {
            addPointsToEnemy()
        }
        else if (first == Ius.choiceScissors && second == Ius.choicePaper) {
            addPointsToMe()
        }
    }

    private fun isRockPaperScissors(choice: String):Boolean {
        return choice==Ius.choiceRock || choice==Ius.choicePaper || choice==Ius.choiceScissors
    }

    private fun changeOnline(makeOnline: Boolean){
        if (makeOnline && !me!!.isOnline) {
            me!!.isOnline = true
            db.child(myId).setValue(me)
            return
        }

        if (me!!.isOnline) {
            me!!.isOnline = false
            db.child(myId).setValue(me)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        changeOnline(false)
    }
}