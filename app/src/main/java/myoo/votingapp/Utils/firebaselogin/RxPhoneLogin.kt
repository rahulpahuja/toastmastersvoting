package myoo.votingapp.Utils.firebaselogin

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.util.concurrent.TimeUnit


class RxPhoneLoginAuth(var provider: PhoneAuthProvider) {

    lateinit var credentical: PhoneAuthCredential

    lateinit var verficationCode: String

    fun loginWithPhoneNumber(phoneNo: String, activity: Activity): Observable<RxPhoneCredential> {

        return Observable.create<RxPhoneCredential> { emitter: ObservableEmitter<RxPhoneCredential> ->
            run {

                val mCallback = object : OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential?) {

                        val phoneCredential = RxPhoneCredential(RxPhoneCredential.STATE.LOGIN, credential)
                        emitter.onNext(phoneCredential)
                        emitter.onComplete()
                    }

                    override fun onVerificationFailed(error: FirebaseException?) {
                        emitter.onError(Throwable(error?.message))
                    }

                    override fun onCodeSent(verificationCode: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
                        super.onCodeSent(verificationCode, p1)

                        val credential = PhoneAuthProvider.getCredential(verificationCode!!, RxPhoneCredential.CODE_NOT_AVAILBLE)
                        val phoneCredential = RxPhoneCredential(RxPhoneCredential.STATE.CODESEND, credential)
                        this@RxPhoneLoginAuth.verficationCode = verificationCode
                        emitter.onNext(phoneCredential)

                    }
                }

                provider.verifyPhoneNumber(
                        phoneNo,
                        60,
                        TimeUnit.SECONDS,
                        activity,
                        mCallback)

            }
        }

    }


}