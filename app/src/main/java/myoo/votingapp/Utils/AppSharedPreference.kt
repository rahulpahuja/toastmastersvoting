package myoo.votingapp.Utils

import android.content.Context




const val PASSCODE_PREF_NAME = "votingapp"

const val PASSCODE = "password"

class AppSharedPreference(context: Context) {

    private val pref = context.getSharedPreferences(PASSCODE_PREF_NAME, Context.MODE_PRIVATE);

    var passcode: String
        get() = pref.getString(PASSCODE, "")
        set(value) = pref.edit().putString(PASSCODE, value).apply()


}






