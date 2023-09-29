package com.mikaela.sps

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.google.gson.Gson

class Ius : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        const val noData = "noData"
        const val keySavedGame = "savedGame"
        const val choiceWaiting = "waiting"
        const val choiceRock = "rock"
        const val choicePaper = "paper"
        const val choiceScissors = "scissors"
        private val toast: Toast? = null

        fun writeSharedPreferences(context: Context, key: String, value: String) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun readSharedPreferences(context: Context, key: String): String? {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences.getString(key, noData)
        }

        fun writeSharedPreferencesObject(context: Context, key: String, room: Room?) {
            val gson = Gson()
            val json = gson.toJson(room)
            writeSharedPreferences(context, key, json)
        }

        fun getRoomFromPref(context: Context, keyName: String): Room? {
            val json = readSharedPreferences(context, keyName)
            if (json == noData) return null
            val gson = Gson()
            return gson.fromJson(json, Room::class.java)
        }

    //    public static void showToast(View layout, Context context, String text, boolean success) {
        //        if (toast!=null)
        //            toast.cancel();
        //        if (!success) {
        ////            layout.setBackgroundColor(context.getResources().getColor(R.color.negative_toast));
        //        }
        //        TextView toastText = layout.findViewById(R.id.text);
        //
        //        toastText.setText(text);
        //
        //        toast = new Toast(context);
        //        toast.setGravity(Gravity.BOTTOM, 0, 80);
        //        toast.setDuration(Toast.LENGTH_LONG);
        //        toast.setView(layout);
        //        toast.show();
        //    }
    }
}