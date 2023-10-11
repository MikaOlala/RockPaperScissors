package com.mikaela.sps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseReference
    private lateinit var dbRoom: DatabaseReference
    private lateinit var input: EditText
    private lateinit var warningInput: TextView

    private var me: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        db = Firebase.database.getReference("players")
        dbRoom = Firebase.database.getReference("rooms")

        val find = findViewById<Button>(R.id.find)
        val create = findViewById<Button>(R.id.create)
        input = findViewById(R.id.input)
        warningInput = findViewById(R.id.warning)

        create.setOnClickListener{
            if (input.text.isNotEmpty())
                checkIfGameExist(input.text.toString(), true)
        }

        find.setOnClickListener {
            if (input.text.isNotEmpty())
                checkIfGameExist(input.text.toString(), false)
        }

        me = Ius.getPlayerFromPref(this, Ius.keyMe)
        if (me==null)
            createMe()

        if (me!=null && me!!.gameConnectedTo.isNotEmpty())
            enterGame()
    }

    private fun createGame() {
        me!!.gameConnectedTo = input.text.toString()
        me!!.gameOwner = true
        var room = Room(input.text.toString(), me!!.id, "")
        db.child(me!!.id).setValue(me).addOnSuccessListener {
            Ius.writeSharedPreferencesObject(this, Ius.keyMe, me!!)
            dbRoom.child(room.name).setValue(room).addOnSuccessListener {
                enterGame()
            }.addOnFailureListener{

            }
        }.addOnFailureListener{
            //TODO: make a toast
        }
    }

    private fun enterGame() {
        warningInput.text = ""
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    private fun checkIfGameExist(input: String, createGame: Boolean) {
        dbRoom.child(input).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue<Room>()
                if (value==null && createGame) { // я создаю игру
                    createGame()
                    return
                }
                if (value==null) { // ищу игру и ее нет
                    warningInput.text = getString(R.string.name_not_exists)
                    warningInput.visibility = View.VISIBLE
                    return
                }
                Log.d("checkIfGameExist", "Value is: $value")

                if (createGame) { // создаю игру, имя занято
                    warningInput.text = getString(R.string.name_exists)
                    warningInput.visibility = View.VISIBLE
                }
                else if (me!!.id==value.idSecond) { // нашел игру
                    me!!.gameConnectedTo = input
                    value.idSecond = me!!.id

                    updateData(value)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("checkIfGameExist", "Failed to read value.", error.toException())
                //TODO: toast smth went wrong
            }
        })
    }

    private fun updateData(room: Room) {
        db.child(me!!.id).setValue(me).addOnSuccessListener {
            db.child(room.name).setValue(room).addOnSuccessListener {
                enterGame()
            }
        }
    }

    private fun createMe() {
        val randomNumber = Random()
        val myData = Player("" + randomNumber.nextInt(99999) + "-" + Ius.getDateString(Date(), Ius.codePattern),
                        0, Ius.statusChoosing, false, "", false,
                    "", Ius.getDateString(Date(), Ius.standardPattern), "", "", "")

        db.setValue(me).addOnSuccessListener {
            me = myData
            Ius.writeSharedPreferencesObject(this, Ius.keyMe, me!!)
        }.addOnFailureListener{
            //TODO: toast
        }
    }
}