package myoo.votingapp.view.Activity

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.login_activity.*
import myoo.votingapp.R
import myoo.votingapp.view.Adapter.ViewAdapter
import myoo.votingapp.view.Fragment.Phone

import myoo.votingapp.view.Fragment.EnterOtp
import myoo.votingapp.Utils.firebaselogin.DigitClickListener
import myoo.votingapp.Utils.firebaselogin.PhoneNumberRetriver

class LoginActivity : AppCompatActivity(), PhoneNumberRetriver {


    private val digits by lazy { listOf(one, two, three, four, five, six, seven, eight, nine, txtzero, remove) }

    private lateinit var digitClickListener: DigitClickListener

    private lateinit var pagerAdapter: ViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        pagerAdapter = ViewAdapter(supportFragmentManager)
        vpager.adapter = pagerAdapter
        indicator.setViewPager(vpager)

        digitClickListener = pagerAdapter.getItem(0) as DigitClickListener

        setUpClicks()

        attachFragmentListenerWithPager()


    }

    private fun attachFragmentListenerWithPager() {
        vpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                changeClickObserver(position)
            }
        })
    }

    private fun changeClickObserver(position: Int) {
        val fragment = pagerAdapter.getItem(position)

        if (fragment is DigitClickListener) {
            digitClickListener = fragment
        }

        if(position==1){
            val item = pagerAdapter.getItem(position)
            if(item is EnterOtp) item.startLoginProcess()
        }
    }

    private fun setUpClicks() {

        digits.map { it.setOnClickListener(mDigitsClickListener) }

        next.setOnClickListener {
            vpager.currentItem = 1
        }

    }

    private val mDigitsClickListener = View.OnClickListener {
        with(digitClickListener) {
            when (it.id) {
                one.id -> push("1")
                two.id -> push("2")
                three.id -> push("3")
                four.id -> push("4")
                five.id -> push("5")
                six.id -> push("6")
                seven.id -> push("7")
                eight.id -> push("8")
                nine.id -> push("9")
                txtzero.id -> push("0")
                remove.id -> pop()
            }
        }
    }

    override fun getEnterPhoneNumber(): String {
        return (pagerAdapter.getItem(0) as Phone).getEnterPhoneNumber()
    }

    override fun onBackPressed() {
        if (vpager.currentItem == 0) {
            super.onBackPressed()
        } else {
            vpager.currentItem = 0
        }

    }


}
