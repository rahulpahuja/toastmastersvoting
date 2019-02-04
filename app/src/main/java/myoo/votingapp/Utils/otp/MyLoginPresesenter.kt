package myoo.votingapp.Utils.otp

import com.google.firebase.auth.PhoneAuthCredential
import myoo.votingapp.view.Fragment.EnterOtp


class MyLoginPresesenter(val loginInteractor: MyLoginInteractor,
                        val view: EnterOtp)  {


     fun loginWithCredentials(phoneAuthCredential: PhoneAuthCredential?) {

        loginInteractor.loginWithCredentical(phoneAuthCredential)
                .subscribe({ view.onLoginSuccessFull(it) }, { view.onLoginFailed(it.message?:"") })

    }

    /**
     * Check the phone getUsersListRef enter is valid or not
     * before sending code on getUsersListRef no
     * */
     fun validate(): Boolean {
        return true
    }
}