package myoo.votingapp.view.Adapter


import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import io.reactivex.Flowable
import io.reactivex.functions.Consumer


import java.util.ArrayList
import java.util.Arrays

import myoo.votingapp.R
import myoo.votingapp.view.Fragment.VotingResultFragemnt
import myoo.votingapp.Model.CandidateType
import myoo.votingapp.Model.stringResource
import myoo.votingapp.Model.toCandidateType
import myoo.votingapp.Model.toRecourceValue

import myoo.votingapp.viewmodel.MeetingCandidatesViewModel


/**
 * Created by MA294214 on 4/30/2017.
 */

class ResultFragmentPagerAdapter(fm: FragmentManager,
                                 private val viewModel: MeetingCandidatesViewModel,
                                 private val context: AppCompatActivity,
                                 internal var meeting_number: String) : FragmentPagerAdapter(fm) {
    internal val PAGE_COUNT = 5
    private val tabTitles = arrayOf("Winners", "Prepared Speakers ", "Role takers", "Table topic ", "Evaluators")


    // private val mydb: DBHelper

    internal var cadidates_map = hashMapOf<String, ArrayList<Any>>()

    internal var votes_map = hashMapOf<String, ArrayList<Int>>()
    internal var voting_item_list = ArrayList<String>()

    private  var currentFragment: VotingResultFragemnt?= null
    private  var previewsValue: VotingResultFragemnt? = null

    init {
        //      mydb = DBHelper(this.context)
        fetchResultsFromSqlite()
    }


    internal fun loadResultsInObject() {
        VotingResultFragemnt.meeting_number = meeting_number
        VotingResultFragemnt.setCandidatesAndVotesMap(cadidates_map, votes_map, voting_item_list)
    }


    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getItem(position: Int): Fragment {
        val newInstance = VotingResultFragemnt.newInstance(position + 1)
        previewsValue = currentFragment
        currentFragment = newInstance
        return newInstance
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return super.instantiateItem(container, position)
    }


    override fun getPageTitle(position: Int): CharSequence? {
        // Generate title based on item position
        return tabTitles[position]
    }


    internal fun fetchResultsFromSqlite() {

        viewModel.retriveAllCandidates(meeting_number)

        viewModel.response.observe(context, Observer {

            populateCount()

        })


    }

    fun populateCount() {
        val voting_item_array = context.resources.getStringArray(R.array.voting_item_list)
        voting_item_list = ArrayList(Arrays.asList(*voting_item_array))



        Flowable.fromIterable(voting_item_list)
                .flatMapSingle {
                    Log.d("votescount", "in results page flatMapSingle" )
                    viewModel.submitCandidateAndVotesRequest(
                            context.stringResource(it).toCandidateType(), meeting_number)
                }
                .map { map ->
                    val candidates = map["candidates"] as ArrayList<Any>
                    val number_of_votes = map["number_of_votes"] as ArrayList<Int>
                    val candidateType = map["key"] as CandidateType

                    val string = context.getString(candidateType.toRecourceValue())

                    cadidates_map[string] = candidates
                    votes_map[string] = number_of_votes
                    Pair(cadidates_map, votes_map)
                }.last(Pair(hashMapOf(), hashMapOf()))
                .subscribe(Consumer {
                    loadResultsInObject()
                    currentFragment?.initData()
                    previewsValue?.initData()
                })

    }

}
