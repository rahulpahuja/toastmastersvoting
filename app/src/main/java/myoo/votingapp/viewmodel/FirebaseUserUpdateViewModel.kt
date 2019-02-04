package myoo.votingapp.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import durdinapps.rxfirebase2.RxFirebaseDatabase
import myoo.votingapp.Utils.AppSharedPreference
import myoo.votingapp.Utils.retrive


/**
 * Update And store passcode to database
 * */

class FirebaseUserUpdateViewModel(private val reference: FirebaseReference,
                                  private val sharedPreference: AppSharedPreference) : ViewModel() {

    val oldPassCode = MutableLiveData<String>()

    /**
     * Retrive Passcode from database
     * */
    fun retriveUserPassCode() {
        reference.userPasscode().retrive<String>()
                .subscribe { oldPassCode.value = it }
    }

    /**
     * Check passcode in present locally
     * */

    fun hasPassCodeLocally(): Boolean {
        return sharedPreference.passcode.isNotBlank()
    }

    fun updatePassCode(newCode: String) {
        RxFirebaseDatabase.setValue(reference.userPasscode(), newCode).subscribe()
    }

}