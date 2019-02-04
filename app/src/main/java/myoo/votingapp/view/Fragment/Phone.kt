package myoo.votingapp.view.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.phone.*

import myoo.votingapp.R
import myoo.votingapp.Utils.firebaselogin.DigitClickListener



class Phone : Fragment(), DigitClickListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.phone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phone_number.setOnTouchListener { v, event -> true }
    }

    override fun push(digit: String) {
        phone_number.append(digit)
    }

    override fun pop() {
        val text = phone_number.text
        phone_number.setText("")
       if (text.isNotBlank()) phone_number.append(text.substring(0,text.length-1))
    }

    fun getEnterPhoneNumber(): String {
        return phone_number.text.toString()
    }
}
