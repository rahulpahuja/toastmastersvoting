package myoo.votingapp.Utils.otp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import io.reactivex.Observable
import myoo.votingapp.Utils.firebaselogin.RxSchedulers

class MyLoginInteractor(var auth: FirebaseAuth, val schedulers: RxSchedulers)  {

     fun loginWithCredentical(phoneAuthCredential: PhoneAuthCredential?): Observable<FirebaseUser> {

        return initLogin(phoneAuthCredential)


    }

    private fun initLogin(phoneAuthCredential: PhoneAuthCredential?): Observable<FirebaseUser> {
        return Observable.create<FirebaseUser>({ emitter ->
            run {

                auth.signInWithCredential(phoneAuthCredential!!)
                        .addOnCompleteListener({
                            run {

                                if (it.isSuccessful) {
                                    val user = it.result.user
                                    emitter.onNext(user)
                                } else {
                                    emitter.onError(Throwable(it.exception?.message))
                                }
                            }
                        })

            }
        })
    }


}