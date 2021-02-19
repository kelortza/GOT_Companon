package com.e.got_compagnon.service
import android.content.Context
class UserManager(context: Context)  {
    private val sharedPreferencesFileName = "userInfo"
    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE)

    private val accessTokenKey = "accessToken"

    fun getAccessToken(): String? {
        return sharedPreferences.getString(accessTokenKey, null)
    }

    fun saveAccessToken(token: String) {
        sharedPreferences.edit().putString(accessTokenKey, token).apply()
    }
}