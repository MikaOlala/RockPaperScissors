package com.mikaela.sps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val db = Firebase.database.getReference("room")
    private lateinit var input: EditText
    private lateinit var warningInput: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

        if (Ius.readSharedPreferences(this, Ius.keySavedGame)!=Ius.noData)
            enterGame()
    }

    private fun createGame() {
        var room = Room(input.text.toString(), 0, 0, 0, Ius.choiceWaiting, Ius.choiceWaiting)
        db.child(room.name).setValue(room).addOnSuccessListener {
            Ius.writeSharedPreferencesObject(this, Ius.keySavedGame, room)
            enterGame()
        }.addOnFailureListener{
            //TODO: make a toast
        }
    }

    private fun enterGame() {
        val intent = Intent()
        startActivity(intent)
    }

    private fun checkIfGameExist(input: String, createGame: Boolean) {
        db.child(input).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue<String>()
                Log.d("checkIfGameExist", "Value is: ${value.toString()}")

                if (createGame) {
                    warningInput.text = getString(R.string.name_exists)
                    warningInput.visibility = View.VISIBLE
                }
                else {
                    Ius.writeSharedPreferencesObject(this@MainActivity, Ius.keySavedGame, snapshot.getValue<Room>())
                    enterGame()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("checkIfGameExist", "Failed to read value.", error.toException())

                if (createGame)
                    createGame()
                else {
                    warningInput.text = getString(R.string.name_not_exists)
                    warningInput.visibility = View.VISIBLE
                }
            }
        })
    }
}