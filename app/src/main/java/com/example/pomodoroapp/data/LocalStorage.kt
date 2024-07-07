package com.example.pomodoroapp.data

import android.content.Context


class LocalStorage(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun getSoundInfo():Boolean{
        val soundValue = sharedPreferences.getBoolean("sound", false)
        return soundValue

    }

    fun setSoundInfo(soundValue:Boolean){
        editor.putBoolean("sound", soundValue)
        editor.commit()


    }

}