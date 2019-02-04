package myoo.votingapp.view.Fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.enter_otp.*
import kotlinx.android.synthetic.main.toolbar.*
import myoo.votingapp.view.Activity.MainActivity
import myoo.votingapp.R
import myoo.votingapp.Utils.open
import myoo.votingapp.Utils.showAlert
import myoo.votingapp.Utils.showToast



import myoo.votingapp.Utils.firebaselogin.DigitClickListener
import myoo.votingapp.Utils.firebaselogin.PhoneNumberRetriver
import myoo.votingapp.Utils.firebaselogin.RxPhoneCredential
import myoo.votingapp.Utils.firebaselogin.RxPhoneLoginAuth
import myoo.votingapp.Utils.otp.MyLoginPresesenter
import myoo.votingapp.view.Activity.LoginActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf



class EnterOtp : Fragment(), DigitClickListener {


    private val phoneLogin: RxPhoneLoginAuth by inject<RxPhoneLoginAuth>()

    private val loginPresenter: MyLoginPresesenter by inject<MyLoginPresesenter> { parametersOf(this) }

    private var phoneNumber: String = ""

    private var phoneNumberRetriver: PhoneNumberRetriver? = null

    private var activityCallback: LoginActivity? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is PhoneNumberRetriver) {
            phoneNumberRetriver = context
        }

        if (context is LoginActivity){
            activityCallback = context
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.enter_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        otp_enter.setListener {
            if (it.length > 5) {
                mSendCodeClickListener.onClick(otp_enter)
            }
        }

        arrow_back.setOnClickListener {
            activityCallback?.onBackPressed()
        }

    }

    public fun startLoginProcess() {
        phoneNumber = "+91" + phoneNumberRetriver?.enterPhoneNumber ?: ""
        attemptlogin()
    }

    override fun onResume() {
        super.onResume()
        phoneNumber = phoneNumberRetriver?.enterPhoneNumber ?: ""
    }

    override fun push(digit: String) {
        val otp = otp_enter.otp
        otp_enter.otp = otp + digit
    }

    override fun pop() {
        val otp = otp_enter.otp
        if (otp.isNotBlank()) otp_enter.otp = otp.substring(0, otp.length - 1)

        otp_enter.fillLayout()
    }

    private val mSendCodeClickListener = View.OnClickListener {
        if (loginPresenter.validate()) {
            fillotpAndattemptAgain()
        }

    }

    private fun fillotpAndattemptAgain() {
        val credential = PhoneAuthProvider.getCredential(phoneLogin.verficationCode, otp_enter.otp)
        loginPresenter.loginWithCredentials(credential)

    }

    private fun attemptlogin() {
        phoneLogin.loginWithPhoneNumber(phoneNumber, activity!!)
                .subscribe({
                    run {
                        it.apply {
                            when (state) {
                                RxPhoneCredential.STATE.LOGIN -> {
                                    loginPresenter.loginWithCredentials(phoneAuthCredential)
                                }
                                else -> {
                                }
                            }
                        }
                    }
                }, { onLoginFailed(it.message!!) })
    }

     fun onLoginSuccessFull(user: FirebaseUser?) {
        showToast("login Successfull")
        activity?.open<MainActivity>()
    }

     fun onLoginFailed(cause: String) {
        otp_enter.otp = ""
       activity?.showAlert(cause)
    }

}
