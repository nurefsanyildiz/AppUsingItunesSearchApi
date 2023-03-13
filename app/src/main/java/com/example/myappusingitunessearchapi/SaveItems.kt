package com.example.myappusingitunessearchapi

import android.content.SharedPreferences
import android.util.Log
import com.example.myappusingitunessearchapi.models.ItunesResponse
import com.google.gson.Gson

class SaveItems {
    fun saveFile(item: ItunesResponse?, mPrefs: SharedPreferences?){
        try {
            val editor = mPrefs!!.edit()
            val gson: Gson = Gson();
            val json: String = gson.toJson(item);
            editor.putString("SearchResult_FaveItem", json);
            editor.commit();
        }catch (ex: Exception){
            Log.d("Error", ex.message.toString())
        }
    }
}