package myoo.votingapp.Utils.firebaselogin


import com.google.firebase.auth.PhoneAuthCredential


data class RxPhoneCredential(var state: STATE,
                             var phoneAuthCredential: PhoneAuthCredential?) {

    companion object {
        var CODE_NOT_AVAILBLE = "-222"
    }

    enum class STATE {
        LOGIN,
        CODESEND
    }
}