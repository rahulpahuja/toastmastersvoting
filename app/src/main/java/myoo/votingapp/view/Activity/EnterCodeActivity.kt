package myoo.votingapp.view.Activity

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_enter_code.*
import myoo.votingapp.R
import myoo.votingapp.Utils.AppSharedPreference
import myoo.votingapp.viewmodel.FirebaseUserUpdateViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


class EnterCodeActivity : AppCompatActivity(), View.OnClickListener {

    private var dots: Array<TextView?> = arrayOfNulls(4)
    internal var data = ""

    private val sharedpreferences by inject<AppSharedPreference>()

    private val viewmodel by viewModel<FirebaseUserUpdateViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_code)

        init()

        checkAndRetrivePasscode()

        // adding  dots
        addBottomDots(data.length)

    }

    /**
     * Check is getUsersListRef has passcode store locally
     * if not it will try to load from database
     * if both the above getUsersListRef case are false means
     * getUsersListRef login first time it will create a new pass code
     * for getUsersListRef
     * */
    private fun checkAndRetrivePasscode() {

        with(viewmodel) {

            if (!hasPassCodeLocally()) {
                viewmodel.retriveUserPassCode()
            }

            oldPassCode.observe(this@EnterCodeActivity, Observer {

                it?.run { storePassCodeLocally(it) }

            })

        }


    }

    private fun storePassCodeLocally(passCode: String) {
        sharedpreferences.passcode = passCode
    }

    fun init() {


        //Implementing onClick
        imageViewBack.setOnClickListener(this)
        btn_1.setOnClickListener(this)
        btn_2.setOnClickListener(this)
        btn_3.setOnClickListener(this)
        btn_4.setOnClickListener(this)
        btn_5.setOnClickListener(this)
        btn_6.setOnClickListener(this)
        btn_7.setOnClickListener(this)
        btn_8.setOnClickListener(this)
        btn_9.setOnClickListener(this)
        btn_0.setOnClickListener(this)
        txt_forget.setOnClickListener(this)
    }


    private fun addBottomDots(currentPage: Int) {
        layoutDots?.removeAllViews()
        if (currentPage == 0) {
            for (i in 0..3) {
                dots[i] = TextView(this)
                val textView = dots!![i]
                textView?.apply {
                    gravity = Gravity.CENTER
                    text = Html.fromHtml("&#8226;")
                    textSize = 50f
                    setPadding(15, 15, 15, 15)
                    setTextColor(resources.getColor(R.color.dot_in_active))
                    layoutDots?.addView(textView)
                }

            }
        } else {
            for (i in dots?.indices) {
                val textView = dots!![i]
                textView?.apply {
                    if (i >= currentPage) {
                        dots[i] = TextView(this@EnterCodeActivity)
                        textView.gravity = Gravity.CENTER
                        textView.text = Html.fromHtml("&#8226;")
                        textView.textSize = 50f
                        textView.setPadding(15, 15, 15, 15)
                        textView.setTextColor(resources.getColor(R.color.dot_in_active))
                        layoutDots!!.addView(textView)
                    } else {
                        dots[i] = TextView(this@EnterCodeActivity)
                        textView.gravity = Gravity.CENTER

                        textView.text = Html.fromHtml("&#8226;")
                        textView.textSize = 50f
                        textView.setPadding(15, 15, 15, 15)
                        textView?.setTextColor(resources.getColor(R.color.dot_in_inactive))
                        layoutDots!!.addView(textView)
                    }
                }
            }
        }


    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_1 -> {
                data = data + "1"
                CheckCode(data)
            }

            R.id.btn_2 -> {
                data = data + "2"
                CheckCode(data)
            }

            R.id.btn_3 -> {
                data = data + "3"
                CheckCode(data)
            }

            R.id.btn_4 -> {
                data = data + "4"
                CheckCode(data)
            }

            R.id.btn_5 -> {
                data = data + "5"
                CheckCode(data)
            }

            R.id.btn_6 -> {
                data = data + "6"
                CheckCode(data)
            }

            R.id.btn_7 -> {
                data = data + "7"
                CheckCode(data)
            }

            R.id.btn_8 -> {
                data = data + "8"
                CheckCode(data)
            }

            R.id.btn_9 -> {
                data = data + "9"
                CheckCode(data)
            }

            R.id.btn_0 -> {
                data = data + "0"
                CheckCode(data)
            }


            R.id.imageViewBack -> if (data.length != 0) {
                data = data.substring(0, data.length - 1)
                CheckCode(data)
            }
            R.id.txt_forget -> {
                val intent = Intent(this@EnterCodeActivity, ForgetCodeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }


    fun CheckCode(code: String) {
        if (data.length == 4) {
            addBottomDots(code.length)
            val sharedpreferences = getSharedPreferences("votingapp", Context.MODE_PRIVATE)
            if (!sharedpreferences.getString("password", "")!!.equals("", ignoreCase = true)) {
                val current_password = sharedpreferences.getString("password", "")
                if (current_password!!.equals(data, ignoreCase = true)) {
                    val intent = Intent(this@EnterCodeActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@EnterCodeActivity, "Invalid Password", Toast.LENGTH_LONG).show()
                    data = ""
                    addBottomDots(data.length)
                }
            } else {
                storePassCodeLocally(data)
                viewmodel.updatePassCode(data)
                val intent = Intent(this@EnterCodeActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        } else {
            addBottomDots(code.length)
        }
    }
}
