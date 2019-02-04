package myoo.votingapp.Utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import myoo.votingapp.viewmodel.*


import myoo.votingapp.Utils.otp.*
import myoo.votingapp.Utils.firebaselogin.RxPhoneLoginAuth
import myoo.votingapp.Utils.firebaselogin.RxSchedulers
import myoo.votingapp.Utils.firebaselogin.RxSchedulersReal
import myoo.votingapp.view.Fragment.EnterOtp

import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module



val firebaseModule = module {

/**
 *  single Provide a single instance definition
 * (unique instance)
 */
    single { FirebaseAuth.getInstance() }
    single<RxSchedulers> { RxSchedulersReal() }
    single { FirebaseStorage.getInstance("gs://votingapp-d49b1.appspot.com").reference }
    single { FirebaseDatabase.getInstance().reference }
    single { FirebaseReference(get()) }
    single { Storage(get()) }

    single("userId") { FirebaseAuth.getInstance().currentUser?.uid ?: "" }
}

val loginModule = module {
  //  single<LoginInteractor> { LoginInteractorImp(get(), get()) }
    single { MyLoginInteractor(get(), get()) }

   // single<LoginPresenter> { (view: LoginView) -> LoginPresenterImp(get(),view) }

   // single { (view: LoginView) -> MyLoginPresesenter(get(),view) }

    single { (view: EnterOtp) -> MyLoginPresesenter(get(),view) }

    single { RxPhoneLoginAuth(PhoneAuthProvider.getInstance())}

}

val otpModule = module {
    viewModel { FirebaseUserUpdateViewModel(get(), get()) }
}

val appModule = module {
    single { AppSharedPreference(androidApplication()) }
}

val meetingModule = module {
    single { UserAuthenticate(get()) }
    //single<MeetingRepository> { MeetingRepository(get()) }
    single<MeetingRepository> { MeetingRepository(get()) }
    viewModel { MeetingViewModel(get(), get(), get()) }



    viewModel { MeetingCandidatesViewModel(get(),get()) }
}