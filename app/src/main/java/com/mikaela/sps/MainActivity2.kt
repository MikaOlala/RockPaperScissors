package com.mikaela.sps

import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.os.Bundle
import android.widget.Button
import com.mikaela.sps.R

class MainActivity2 : AppCompatActivity() {
    private var button: Button? = null
    private val ll = LinearLayout(this)
    private var text: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.create)
        text = "gfdgfdg"
        val d = text
    }
}