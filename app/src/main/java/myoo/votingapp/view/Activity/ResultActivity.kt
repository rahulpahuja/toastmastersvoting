package myoo.votingapp.view.Activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.custom_actionbar.*
import myoo.votingapp.view.Adapter.ResultFragmentPagerAdapter
import myoo.votingapp.R
import myoo.votingapp.viewmodel.MeetingCandidatesViewModel
import org.koin.android.ext.android.inject

class ResultActivity : AppCompatActivity() {
    var viewPager : ViewPager? = null

    private val meeting_number: String by lazy {
        intent.extras.getString("meeting_number")
    }

    private val viewModel by inject<MeetingCandidatesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.results)

        setFragments()

        SetUpData()
    }


    fun SetUpData() {
        txt_header.text = "View Results"
        imageBackPress.visibility = View.VISIBLE
        imageBackPress.setOnClickListener { finish() }
    }


    internal fun setFragments() {
        // Get the ViewPager and set it's PagerAdapter so that it can display items
         viewPager = findViewById<View>(R.id.viewpager) as ViewPager


        viewPager?.adapter = ResultFragmentPagerAdapter(supportFragmentManager,
                viewModel,
                this, meeting_number)



        // Give the TabLayout the ViewPager
        val tabLayout = findViewById<View>(R.id.sliding_tabs) as TabLayout
        tabLayout.setupWithViewPager(viewPager)
    }
    public fun swipeToPreparedSpeakersVotes(){
        viewPager?.currentItem = 1
    }
    public fun swipeToRoletakersVotes(){
        viewPager?.currentItem = 2
    }
    public fun swipeToTTVotes(){
        viewPager?.currentItem = 3
    }
    public fun swipeToEvaluatorsVotes(){
        viewPager?.currentItem = 4
    }


}




