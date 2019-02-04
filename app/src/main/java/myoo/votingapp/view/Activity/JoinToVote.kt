package myoo.votingapp.view.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.util.Log
import android.view.View
import android.widget.*
import myoo.votingapp.view.Adapter.VotingAdapter
import myoo.votingapp.R

import org.koin.android.ext.android.inject


import myoo.votingapp.view.Activity.VotingActivity.Companion.isVoteSelectionScreen
import io.reactivex.functions.Consumer;
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.Flowable
import io.reactivex.Single
import myoo.votingapp.Model.*
import myoo.votingapp.Utils.disable
import myoo.votingapp.view.Adapter.CandidateArrayAdapter
import myoo.votingapp.viewmodel.MeetingCandidatesViewModel
import java.util.*


class JoinToVote : AppCompatActivity() {

    lateinit var context: Context

    private val viewModel by inject<MeetingCandidatesViewModel>()

    private val userId  by inject<String>("userId")

    internal var votingProcessWrapper: VotingProcessWrapperClass? = null
    internal var voting_item: String? = null

    // // declaring  all adapters
    lateinit var prepared_speaker_votingAdapter: CandidateArrayAdapter
    lateinit var role_taker_votingAdapter: CandidateArrayAdapter
    lateinit var tag_team_votingAdapter: CandidateArrayAdapter
    lateinit var table_topic_votingAdapter: CandidateArrayAdapter
    lateinit var evaluator_voting_adapter: CandidateArrayAdapter

    var cadidates_map = HashMap<String, ArrayList<Any>>()
    lateinit var card_alredy_voting: CardView
    lateinit var coordinatorLayout: CoordinatorLayout

    lateinit var custom_voting_item_list: ArrayList<String>
    private val meetingNo: String by lazy {
        intent.extras.getString(PrepareAllVotingItemsScreen.MeetingNo)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //   mydb = DBHelper(this)
        context = this

        setContentView(R.layout.activity_splash)
        custom_voting_item_list = ArrayList()


        // checking whether user has already voted, if yes then dont let them vote again.
        viewModel.votesCastedByMeInAMeetingRef(meetingNo).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("mytag" , " onDataChange")

                if(snapshot.exists())
                    Log.d("mytag" , " snpashot exists")
                if(snapshot.hasChildren())
                    Log.d("mytag" , " snpashot has children")



                if(snapshot.exists()&& snapshot.hasChildren())
                {
                    setContentView(R.layout.thank_you_for_voting)

                    val txt_message = findViewById<TextView>(R.id.message)

                    // txt_message.text = "You have already voted for one or more items for meeting "+ meetingNo

                    txt_message.text = "Using this device voting has been done once or more. If you have voted , you can help others in voting."


                    val button = findViewById<TextView>(R.id.button)

                    button.text = "Vote now"

                    button.setOnClickListener { view ->
                        //finish()
                        startVoting()
                        button.disable()
                    }

                }else{

                    /*
                    viewModel.initiateCandidates(meetingNo)
                            ?.subscribe(Consumer {
                                Log.d("checkselect", "in subscribe of initiateCandidates ")
                                PopulateVotingList()
                                //  PopulateVotingList()
                                votingProcessWrapper = VotingProcessWrapperClass()
                            })

                    */

                    startVoting()

                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("mytag" , " error")
            }
        });

/*
        if(true)
        {
            setContentView(R.layout.thank_you_for_voting)

            val txt_message = findViewById<TextView>(R.id.message)

            txt_message.text = "You have already voted for one or more items for meeting "+ meetingNo

            val button = findViewById<TextView>(R.id.button)
            button.setOnClickListener { view ->
               finish()
            }



        }else{
            viewModel.initiateCandidates(meetingNo)
                    ?.subscribe(Consumer {
                        Log.d("checkselect", "in subscribe of initiateCandidates ")
                        PopulateVotingList()
                        //  PopulateVotingList()
                        votingProcessWrapper = VotingProcessWrapperClass()
                    })

        }

*/


    }

    fun startVoting(){
        viewModel.initiateCandidates(meetingNo)
                ?.subscribe(Consumer {
                    Log.d("checkselect", "in subscribe of initiateCandidates ")
                    PopulateVotingList()
                    //  PopulateVotingList()
                   // votingProcessWrapper = VotingProcessWrapperClass()
                })

    }
    fun PopulateVotingList() {

        /*
        populatecandidateList(resources.getText(R.string.prepared_speaker).toString())
        populatecandidateList(resources.getText(R.string.role_takers).toString())
        populatecandidateList(resources.getText(R.string.table_topics).toString())
        populatecandidateList(resources.getText(R.string.evaluators).toString())

        */
        //  populatecandidateList(resources.getText(R.string.tag_team).toString())

        // why I chained the asynch calls?
        // When we had 4 different independant  calls for different voting items,the app crashes sometimes when we press VOTE button quickly.
        // because by that time candidates details were not loaded and adapters were not formed.
        // because there are 4 different asynchronous calls how do i get to know when the last one was complete.
        // so , I chained the 4 asynchronous calls and after the last success I am initiating the voting process wrapper class.
        var item  = resources.getText(R.string.prepared_speaker).toString()
        viewModel.submitCandidateAndVotesRequest(stringResource(item).toCandidateType(),meetingNo)
                .subscribe(
                        Consumer {
                            populateList(it, item)
                            item = resources.getText(R.string.role_takers).toString()
                            viewModel.submitCandidateAndVotesRequest(stringResource(item).toCandidateType(),meetingNo)
                                    .subscribe(Consumer {
                                        populateList(it, item)
                                        item = resources.getText(R.string.table_topics).toString()
                                        viewModel.submitCandidateAndVotesRequest(stringResource(item).toCandidateType(),meetingNo)
                                                .subscribe(Consumer {
                                                    populateList(it, item)
                                                    item = resources.getText(R.string.evaluators).toString()
                                                    viewModel.submitCandidateAndVotesRequest(stringResource(item).toCandidateType(),meetingNo)
                                                            .subscribe(Consumer {
                                                                populateList(it, item)
                                                                votingProcessWrapper = VotingProcessWrapperClass()

                                                            })

                                                })

                                    })
                        }

                )

    }

    internal fun populatecandidateList(voting_item: String) {


        //  viewModel.submitCandidateAndVotesRequest(stringResource(voting_item).toCandidateType(),meeting_number.meetingId)
        viewModel.submitCandidateAndVotesRequest(stringResource(voting_item).toCandidateType(),meetingNo)
                .subscribe(Consumer {
                    Log.d("checkcandidates", "in subscribe of submitCandidateAndVotesRequest ")
                    populateList(it, voting_item)

                })


    }


    internal fun populatecandidateListAsync(voting_item: String) : Single<HashMap<String, Any>> {

        //  viewModel.submitCandidateAndVotesRequest(stringResource(voting_item).toCandidateType(),meeting_number.meetingId)
        return viewModel.submitCandidateAndVotesRequest(stringResource(voting_item).toCandidateType(),meetingNo)

    }

    fun populateList(map: HashMap<String, Any>, voting_item: String) {
        val candidates = map["candidates"] as ArrayList<Any>

        Log.d("checkcandidates", "in populateList with candidates size " + candidates.size)

        cadidates_map[voting_item] = candidates

        if(candidates.size >1){

            var candidateList  = convertToCandidateList(candidates) as ArrayList
            var count = 0;
            for (j in candidateList.indices) {

                if(!candidateList.get(j).isDisabled)
                    count++;

            }
            if(count > 1){
                custom_voting_item_list.add(voting_item)

                if (voting_item == resources.getText(R.string.prepared_speaker)) {
                    prepared_speaker_votingAdapter = CandidateArrayAdapter(context, convertToCandidateList(candidates) as ArrayList , voting_item)
                }
                if (voting_item == resources.getText(R.string.table_topics)) {
                    table_topic_votingAdapter = CandidateArrayAdapter(context, convertToCandidateList(candidates) as ArrayList , voting_item)
                }
                if (voting_item == resources.getText(R.string.role_takers)) {
                    role_taker_votingAdapter = CandidateArrayAdapter(context, convertToCandidateList(candidates) as ArrayList , voting_item)
                }
                if (voting_item == resources.getText(R.string.evaluators)) {
                    evaluator_voting_adapter = CandidateArrayAdapter(context, convertToCandidateList(candidates) as ArrayList , voting_item)
                }
                if (voting_item == resources.getText(R.string.tag_team)) {
                    tag_team_votingAdapter = CandidateArrayAdapter(context, convertToCandidateList(candidates) as ArrayList , voting_item)
                }
            }
        }
    }

    fun convertToCandidateList(candidates: ArrayList<Any> ): List<Candidate>{
        return candidates.map {
           // it as Candidate
            var candidate = Candidate()
            if(it is PreparedSpeakerDetail){
                candidate.name = it.name
                candidate.project = it.project
                candidate.title = it.title
                candidate.isDisabled = it.isDisabled
                candidate.photo = it.imageUrl

            }
            if(it is TableTopicDetail){
                candidate.name = it.name
                candidate.isDisabled = it.isDisabled
                candidate.photo = it.imageUrl
            }
            if(it is EvaluatorDetail){
                candidate.name = it.name
                candidate.isDisabled = it.isDisabled
                candidate.photo = it.imageUrl
            }
            if(it is RoleTakerDetail){
                candidate.name = it.name
                candidate.isDisabled = it.isDisabled
                candidate.role = it.role
                candidate.photo = it.imageUrl
            }
            candidate
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()

    }

    internal fun aVotePolled(voting_item: String, position: Int) {

        val candidates = cadidates_map[voting_item]
        val voteGivenTo = candidates!![position]

        viewModel.giveVoteTo(voteGivenTo,
                stringResource(voting_item).toCandidateType(),
                meetingNo)

    }

    fun Next(v: View) {
        votingProcessWrapper?.Next()
    }
    internal interface VotingProcessState {
        fun Next(wrapper: VotingProcessWrapperClass)
    }

    internal inner class PleaseVoteVotingProcessState : VotingProcessState {
        init {
            setContentView(R.layout.message_before_voting)

            val txt_message = findViewById<TextView>(R.id.message)

            txt_message.text = "The voting for meeting "+ meetingNo  +" is active. Press next to proceed for voting"
            val txt_header = findViewById<View>(R.id.txt_header) as TextView
            val imageBackPress = findViewById<View>(R.id.imageBackPress) as ImageView

            txt_header.text = "Vote"
            imageBackPress.visibility = View.VISIBLE
            imageBackPress.setOnClickListener {
                isVoteSelectionScreen = true
                val intent = Intent(applicationContext, CheckValidationCodeActivity::class.java)
                startActivity(intent)
            }
        }

        override fun Next(wrapper: VotingProcessWrapperClass) {
            wrapper.set_state_custom_logic(applicationContext.resources.getText(R.string.please_vote).toString())

        }

    }

    internal inner class PreparedSpeakerVotingProcessState : VotingProcessState {
        init {
            setContentView(R.layout.activity_voting_screen)
            (findViewById<View>(R.id.heading_voting_screen) as TextView).text = "Vote for " + resources.getText(R.string.prepared_speaker).toString()

            prepared_speaker_votingAdapter.selected_position = -1
            (findViewById<View>(R.id.voting_screen_list) as ListView).adapter = prepared_speaker_votingAdapter
        }

        override fun Next(wrapper: VotingProcessWrapperClass) {
            val selected_candidate = prepared_speaker_votingAdapter.selected_position

            if (selected_candidate < 0 ) {
                Toast.makeText(applicationContext, "Please select a candidate", Toast.LENGTH_SHORT).show()
                return
            }
            if (selected_candidate >= 0) {
                aVotePolled(resources.getText(R.string.prepared_speaker).toString(), selected_candidate)

            }
            wrapper.set_state_custom_logic(applicationContext.resources.getText(R.string.prepared_speaker).toString())

        }

    }

    internal inner class RoleTakerVotingProcessState : VotingProcessState {
        init {
            setContentView(R.layout.activity_voting_screen)
            (findViewById<View>(R.id.heading_voting_screen) as TextView).text = "Vote for " + resources.getText(R.string.role_takers).toString()

            Log.d("checkadapter", "In RoleTakerVotingProcessState going to set adapter ")

            (findViewById<View>(R.id.voting_screen_list) as ListView).adapter = role_taker_votingAdapter
        }

        override fun Next(wrapper: VotingProcessWrapperClass) {
            val selected_candidate = role_taker_votingAdapter.selected_position
            Log.d("result", "in voting  of role taker with index $selected_candidate")
            if (selected_candidate < 0 ) {
                Toast.makeText(applicationContext, "Please select a candidate", Toast.LENGTH_SHORT).show()
                return
            }
            if (selected_candidate >= 0) {
                aVotePolled(resources.getText(R.string.role_takers).toString(), selected_candidate)
            }
            //   wrapper.set_state(new TableTopicSpeakerVotingProcessState());
            wrapper.set_state_custom_logic(applicationContext.resources.getText(R.string.role_takers).toString())

        }
    }

    internal inner class TagTeamVotingProcessState : VotingProcessState {
        init {
            setContentView(R.layout.activity_voting_screen)
            (findViewById<View>(R.id.heading_voting_screen) as TextView).text = "Vote for " + resources.getText(R.string.tag_team).toString()

            Log.d("checkadapter", "In TagTeamVotingProcessState going to set adapter ")
            tag_team_votingAdapter.selected_position = -1
            (findViewById<View>(R.id.voting_screen_list) as ListView).adapter = tag_team_votingAdapter
        }

        override fun Next(wrapper: VotingProcessWrapperClass) {
            val selected_candidate = tag_team_votingAdapter.selected_position
            Log.d("result", "in voting  of tag team with index $selected_candidate")
            if (selected_candidate < 0 ) {
                Toast.makeText(applicationContext, "Please select a candidate", Toast.LENGTH_SHORT).show()
                return
            }
            if (selected_candidate >= 0) {
                aVotePolled(resources.getText(R.string.tag_team).toString(), selected_candidate)
            }
            //   wrapper.set_state(new TableTopicSpeakerVotingProcessState());
            wrapper.set_state_custom_logic(applicationContext.resources.getText(R.string.tag_team).toString())

        }
    }

    internal inner class TableTopicSpeakerVotingProcessState : VotingProcessState {
        init {
            setContentView(R.layout.activity_voting_screen)
            (findViewById<View>(R.id.heading_voting_screen) as TextView).text = "Vote for " + resources.getText(R.string.table_topics).toString()

            table_topic_votingAdapter.selected_position = -1
            (findViewById<View>(R.id.voting_screen_list) as ListView).adapter = table_topic_votingAdapter
        }

        override fun Next(wrapper: VotingProcessWrapperClass) {
            val selected_candidate = table_topic_votingAdapter.selected_position

            if (selected_candidate < 0 ) {
                Toast.makeText(applicationContext, "Please select a candidate", Toast.LENGTH_SHORT).show()
                return
            }
            if (selected_candidate >= 0) {
                aVotePolled(resources.getText(R.string.table_topics).toString(), selected_candidate)
            }

            // wrapper.set_state(new EvaluatorVotingProcessState());
            wrapper.set_state_custom_logic(applicationContext.resources.getText(R.string.table_topics).toString())
        }


    }

    internal inner class EvaluatorVotingProcessState : VotingProcessState {
        init {
            setContentView(R.layout.activity_voting_screen)
            (findViewById<View>(R.id.heading_voting_screen) as TextView).text = "Vote for " + resources.getText(R.string.evaluators).toString()

            evaluator_voting_adapter.selected_position = -1

            (findViewById<View>(R.id.voting_screen_list) as ListView).adapter = evaluator_voting_adapter

        }

        override fun Next(wrapper: VotingProcessWrapperClass) {
            val selected_candidate = evaluator_voting_adapter.selected_position

            if (selected_candidate < 0 ) {
                Toast.makeText(applicationContext, "Please select a candidate", Toast.LENGTH_SHORT).show()
                return
            }
            if (selected_candidate >= 0) {
                aVotePolled(resources.getText(R.string.evaluators).toString(), selected_candidate)
            }

            // wrapper.set_state(new ThankYouVotingProcessState());
            wrapper.set_state_custom_logic(applicationContext.resources.getText(R.string.evaluators).toString())
        }
    }

    internal inner class ThankYouVotingProcessState : VotingProcessState {
        init {

            setContentView(R.layout.thank_you_for_voting)

        }

        override fun Next(wrapper: VotingProcessWrapperClass) {
            //wrapper.set_state(new PleaseVoteVotingProcessState());
            // wrapper.set_state_custom_logic(applicationContext.resources.getText(R.string.thank_you).toString())
            finish()

        }
    }

    internal inner class VotingProcessWrapperClass {
        var m_current_voting_process: VotingProcessState? = null

        init {
            m_current_voting_process = PleaseVoteVotingProcessState()
        }

        fun set_state(s: VotingProcessState) {
            m_current_voting_process = s
        }

        fun set_state_custom_logic(current_item: String) {
            val next_item = getNextItemName(current_item)

            if (next_item == applicationContext.resources.getText(R.string.prepared_speaker).toString()) {
                m_current_voting_process = PreparedSpeakerVotingProcessState()

            } else if (next_item == applicationContext.resources.getText(R.string.role_takers).toString()) {
                m_current_voting_process = RoleTakerVotingProcessState()

            } else if (next_item == applicationContext.resources.getText(R.string.tag_team).toString()) {
                m_current_voting_process = TagTeamVotingProcessState()

            } else if (next_item == applicationContext.resources.getText(R.string.table_topics).toString()) {
                m_current_voting_process = TableTopicSpeakerVotingProcessState()

            } else if (next_item == applicationContext.resources.getText(R.string.evaluators).toString()) {
                m_current_voting_process = EvaluatorVotingProcessState()

            } else if (next_item == applicationContext.resources.getText(R.string.thank_you).toString()) {
                m_current_voting_process = ThankYouVotingProcessState()

            } else if (next_item == applicationContext.resources.getText(R.string.please_vote).toString()) {
                m_current_voting_process = PleaseVoteVotingProcessState()
            }

        }

        fun Next() {
            m_current_voting_process!!.Next(this)
        }

        fun goToThankYouPage() {
            m_current_voting_process = ThankYouVotingProcessState()
        }

        fun getNextItemName(current_item: String): String {

            val next_item: String
            if (current_item == applicationContext.resources.getText(R.string.please_vote).toString()) {

                next_item = custom_voting_item_list[0]
                return next_item
            }

            if (current_item == applicationContext.resources.getText(R.string.thank_you).toString()) {
                next_item = applicationContext.resources.getText(R.string.please_vote).toString()
                return next_item
            }

            val current_index = custom_voting_item_list.indexOf(current_item)

            if (current_index == custom_voting_item_list.size - 1) {
                next_item = applicationContext.resources.getText(R.string.thank_you).toString()
                return next_item
            }


            next_item = custom_voting_item_list[current_index + 1]
            return next_item

        }

    }


}
