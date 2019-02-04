package myoo.votingapp.Utils

import myoo.votingapp.Utils.AppSharedPreference



class UserAuthenticate(private val sharedPreferences: AppSharedPreference) {


    fun autenticateUser(passCode: String): Boolean {
        return sharedPreferences.passcode.equals(passCode)
    }

}