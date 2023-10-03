package com.mikaela.sps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseReference
    private lateinit var input: EditText
    private lateinit var warningInput: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        db = Firebase.database.getReference("room")

        val find = findViewById<Button>(R.id.find)
        val create = findViewById<Button>(R.id.create)
        input = findViewById(R.id.input)
        warningInput = findViewById(R.id.warning)

        create.setOnClickListener{
            checkIfGameExist(input.text.toString(), true)
        }

        find.setOnClickListener {
            checkIfGameExist(input.text.toString(), false)
        }

        val savedGame: Room? = Ius.getRoomFromPref(this, Ius.keySavedGame)
        if (savedGame!=null && savedGame.name.isNotEmpty())
            checkIfGameExist(savedGame.name, false)
    }

    private fun createGame() {
        var room = Room(input.text.toString(), 0, 0, 0, Ius.choiceWaiting, Ius.choiceWaiting)
        db.child(room.name).setValue(room).addOnSuccessListener {
            Ius.writeSharedPreferencesObject(this, Ius.keySavedGame, room)
            Ius.writeSharedPreferences(this@MainActivity, Ius.keyIsMyGame, "true")
            enterGame()
        }.addOnFailureListener{
            //TODO: make a toast
        }
    }

    private fun enterGame() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    private fun checkIfGameExist(input: String, createGame: Boolean) {
        db.child(input).addListenerForSingleValueEvent(object: ValueEventListener {
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
                else { // нашел игру
                    Ius.writeSharedPreferencesObject(this@MainActivity, Ius.keySavedGame, value)
                    enterGame()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("checkIfGameExist", "Failed to read value.", error.toException())
                //TODO: toast smth went wrong
            }
        })
    }
}