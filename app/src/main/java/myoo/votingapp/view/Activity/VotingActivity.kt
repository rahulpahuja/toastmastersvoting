package myoo.votingapp.view.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import io.reactivex.functions.Consumer

import java.io.File
import java.util.ArrayList
import java.util.HashMap

import myoo.votingapp.view.Adapter.VotingAdapter
import myoo.votingapp.Model.EvaluatorDetail
import myoo.votingapp.Model.PreparedSpeakerDetail
import myoo.votingapp.Model.RoleTakerDetail
import myoo.votingapp.Model.TableTopicDetail
import myoo.votingapp.Model.TagTeamDetail
import myoo.votingapp.R
import myoo.votingapp.view.Activity.PrepareAllVotingItemsScreen.Companion.MeetingNo
import myoo.votingapp.Model.stringResource
import myoo.votingapp.Model.toCandidateType
import myoo.votingapp.viewmodel.MeetingCandidatesViewModel

import org.koin.android.ext.android.inject

class VotingActivity : AppCompatActivity() {
   // lateinit var voting_item_array: Array<String>
   // lateinit var voting_item_list: ArrayList<String>
    internal var votingProcessWrapper: VotingProcessWrapperClass? = null
    internal var voting_item: String? = null
    internal var prepared_speaker_name_list = ArrayList<String>()
    internal var prepared_speaker_project_list = ArrayList<String>()
    internal var prepared_speaker_title_list = ArrayList<String>()
    internal var prepared_speaker_disable_list = ArrayList<Boolean>()
    internal var prepared_speaker_photo_list = ArrayList<String>()
    internal var prepared_speaker_votes_list = ArrayList<Int>()
    internal var role_taker_name_list = ArrayList<String>()
    internal var role_taker_role_list = ArrayList<String>()
    internal var role_taker_disable_list = ArrayList<Boolean>()
    internal var role_taker_photo_list = ArrayList<String>()
    internal var role_taker_votes_list = ArrayList<Int>()
    internal var tag_team_name_list = ArrayList<String>()
    internal var tag_team_role_list = ArrayList<String>()
    internal var tag_team_disable_list = ArrayList<Boolean>()
    internal var tag_team_photo_list = ArrayList<String>()
    internal var tag_team_votes_list = ArrayList<Int>()
    internal var table_topic_name_list = ArrayList<String>()
    internal var table_topic_photo_list = ArrayList<String>()
    internal var table_topic_disable_list = ArrayList<Boolean>()
    internal var table_topic_speaker_votes_list = ArrayList<Int>()
    internal var evaluators_name_list = ArrayList<String>()
    internal var evaluators_photo_list = ArrayList<String>()
    internal var evaluators_disable_list = ArrayList<Boolean>()
    internal var evaluators_votes_list = ArrayList<Int>()

   /*
    private val meeting_number: Meeting by lazy {
        intent.extras.getParcelable<Meeting>(MEETING)
    }
*/
    private val meetingNo: String by lazy {
        intent.extras.getString(MeetingNo)
    }

    private val isMyMeeting: Boolean by lazy {
        intent.extras.getBoolean(IS_MY_MEETING)
    }

    lateinit var context: Context
    lateinit var prepared_speaker_votingAdapter: VotingAdapter
    lateinit var role_taker_votingAdapter: VotingAdapter
    lateinit var tag_team_votingAdapter: VotingAdapter
    lateinit var table_topic_votingAdapter: VotingAdapter
    lateinit var evaluator_voting_adapter: VotingAdapter
    lateinit var custom_voting_item_list: ArrayList<String>
    var cadidates_map = HashMap<String, ArrayList<Any>>()
    internal var votes_map = HashMap<String, ArrayList<Int>>()
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var card_alredy_voting: CardView
    // private var mydb: DBHelper? = null

    private val viewModel by inject<MeetingCandidatesViewModel>()

    private val userId  by inject<String>("userId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //   mydb = DBHelper(this)
        context = this

        custom_voting_item_list = intent.extras!!.getSerializable("custom_voting_item_list") as ArrayList<String>

        for (i in custom_voting_item_list.indices) {
            Log.d("checkselect", "found " + custom_voting_item_list[i] + " in list")
        }

        // start the voting only if not already voted
        /*
        //viewModel.startVoting(meeting_number.meetingId)
        viewModel.startVoting(meetingNo)
                ?.subscribe(Consumer {

                    val newList = arrayListOf<String>()

                    it.forEach {
                        when (it) {
                            CandidateType.PREPARE_SPEAKER -> {
                                if (hasItem(R.string.prepared_speaker)) {
                                    newList.add(stringOf(R.string.prepared_speaker))
                                }
                            }
                            CandidateType.ROLE_TAKER -> {
                                if (hasItem(R.string.role_takers)) {
                                    newList.add(stringOf(R.string.role_takers))
                                }
                            }
                            CandidateType.TABLE_TOPIC -> {
                                if (hasItem(R.string.table_topics)) {
                                    newList.add(stringOf(R.string.table_topics))
                                }
                            }
                            CandidateType.EVALUTOR -> {
                                if (hasItem(R.string.evaluators)) {
                                    newList.add(stringOf(R.string.evaluators))
                                }
                            }
                        }
                    }

                    custom_voting_item_list = newList

                    PopulateVotingList()

                    votingProcessWrapper = VotingProcessWrapperClass()

                  //  if (newList.size == 0) votingProcessWrapper?.m_current_voting_process = ThankYouVotingProcessState()


                    voting_item_array = resources.getStringArray(R.array.voting_item_list)
                    voting_item_list = ArrayList(Arrays.asList(*voting_item_array))


                })


        */


        // start the voting without checking already polled votes
        viewModel.initiateCandidates(meetingNo)
                ?.subscribe(Consumer {

                    populdateVotingList()
                    votingProcessWrapper = VotingProcessWrapperClass()
                })



        //  setContentView(R.layout.activity_voting_screen);

    }

    fun hasItem(st: Int) = custom_voting_item_list.contains(stringOf(st))

    fun stringOf(st: Int) = resources.getString(st)

    fun populdateVotingList() {
        populatecandidateList(resources.getText(R.string.prepared_speaker).toString())
        populatecandidateList(resources.getText(R.string.role_takers).toString())
        populatecandidateList(resources.getText(R.string.table_topics).toString())
        populatecandidateList(resources.getText(R.string.evaluators).toString())
        //  populatecandidateList(resources.getText(R.string.tag_team).toString())
    }

    fun messageForAlreadyVoted(v: View) {
        messageForAlreadyVoted()
    }

    fun messageForAlreadyVoted() {

        Toast.makeText(this, "Your votes have been captured now. Please pass the device to next person", Toast.LENGTH_LONG).show()
    }

    fun candidateAlreadyVoted(v: View) {
        votingProcessWrapper?.goToThankYouPage()
    }

    internal fun populatecandidateList(voting_item: String) {

        //  viewModel.submitCandidateAndVotesRequest(stringResource(voting_item).toCandidateType(),meeting_number.meetingId)
        viewModel.submitCandidateAndVotesRequest(stringResource(voting_item).toCandidateType(),meetingNo)
                .subscribe(Consumer {
                    populateList(it, voting_item)

                })


    }


    fun populateList(map: HashMap<String, Any>, voting_item: String) {
        val candidates = map["candidates"] as ArrayList<Any>
        val number_of_votes = map["number_of_votes"] as ArrayList<Int>

        Log.d("checkcandidates", "in populatecandidateList got candidates with size " + candidates.size)
        cadidates_map[voting_item] = candidates
        votes_map[voting_item] = number_of_votes

      //  val myMeeting =   userId==meeting_number.createdby;

        if (voting_item == resources.getText(R.string.prepared_speaker)) {

            prepared_speaker_name_list.clear()
            prepared_speaker_project_list.clear()
            prepared_speaker_title_list.clear()
            prepared_speaker_photo_list.clear()
            prepared_speaker_votes_list.clear()
            prepared_speaker_disable_list.clear()
            for (i in candidates.indices) {
                val detail = candidates[i] as PreparedSpeakerDetail
                val name = detail.name
                prepared_speaker_name_list.add(name)

                prepared_speaker_project_list.add(detail.name)
                prepared_speaker_disable_list.add(detail.isDisabled)
                prepared_speaker_title_list.add(detail.title)

                prepared_speaker_votes_list.add(number_of_votes[i])

                prepared_speaker_photo_list.add(detail.imageUrl)
            }

            votes_map[voting_item] = prepared_speaker_votes_list

        /*    prepared_speaker_votingAdapter = VotingAdapter(context, prepared_speaker_name_list,
                    prepared_speaker_title_list, prepared_speaker_project_list, prepared_speaker_photo_list,
                    voting_item, prepared_speaker_disable_list,myMeeting)  */

            prepared_speaker_votingAdapter = VotingAdapter(context, prepared_speaker_name_list,
                    prepared_speaker_title_list, prepared_speaker_project_list, prepared_speaker_photo_list,
                    voting_item, prepared_speaker_disable_list,isMyMeeting)

        }
        if (voting_item == resources.getText(R.string.role_takers)) {
            role_taker_name_list.clear()
            role_taker_role_list.clear()
            role_taker_photo_list.clear()
            role_taker_votes_list.clear()
            role_taker_disable_list.clear()
            for (i in candidates.indices) {
                val detail = candidates[i] as RoleTakerDetail
                val name = detail.name
                role_taker_name_list.add(name)
                role_taker_disable_list.add(detail.isDisabled)


                role_taker_role_list.add(detail.role)

                role_taker_votes_list.add(number_of_votes[i])

                role_taker_photo_list.add(detail.imageUrl ?: "")
            }

            votes_map[voting_item] = role_taker_votes_list
            /*
            role_taker_votingAdapter = VotingAdapter(context, role_taker_name_list, role_taker_role_list,
                    null, role_taker_photo_list, voting_item, role_taker_disable_list,myMeeting)
                    */

            role_taker_votingAdapter = VotingAdapter(context, role_taker_name_list, role_taker_role_list,
                    null, role_taker_photo_list, voting_item, role_taker_disable_list,isMyMeeting)
        }
        if (voting_item == resources.getText(R.string.table_topics)) {
            table_topic_name_list.clear()
            table_topic_photo_list.clear()
            table_topic_disable_list.clear()
            table_topic_speaker_votes_list.clear()
            for (i in candidates.indices) {
                val topicDetail = candidates[i] as TableTopicDetail
                val name = topicDetail.name
                table_topic_name_list.add(name)
                table_topic_disable_list.add(topicDetail.isDisabled)

                table_topic_speaker_votes_list.add(number_of_votes[i])

                table_topic_photo_list.add(topicDetail.imageUrl ?: "")
            }
            votes_map[voting_item] = table_topic_speaker_votes_list
            /*
            table_topic_votingAdapter = VotingAdapter(context, table_topic_name_list,
                    null, null, table_topic_photo_list, voting_item, table_topic_disable_list,myMeeting)
            */
            table_topic_votingAdapter = VotingAdapter(context, table_topic_name_list,
                    null, null, table_topic_photo_list, voting_item, table_topic_disable_list,isMyMeeting)

        }
        if (voting_item == resources.getText(R.string.evaluators)) {
            evaluators_name_list.clear()
            evaluators_disable_list.clear()
            evaluators_photo_list.clear()
            evaluators_votes_list.clear()
            for (i in candidates.indices) {
                val detail = candidates[i] as EvaluatorDetail
                val name = detail.name
                evaluators_name_list.add(name)
                evaluators_disable_list.add(detail.isDisabled)



                evaluators_votes_list.add(number_of_votes[i])

                evaluators_photo_list.add(detail.imageUrl ?: "")
            }
            votes_map[voting_item] = evaluators_votes_list
            /*
            evaluator_voting_adapter = VotingAdapter(context, evaluators_name_list,
                    null, null, evaluators_photo_list, voting_item, evaluators_disable_list,myMeeting)

                    */
            evaluator_voting_adapter = VotingAdapter(context, evaluators_name_list,
                    null, null, evaluators_photo_list, voting_item, evaluators_disable_list,isMyMeeting)

        }
        if (voting_item == resources.getText(R.string.tag_team)) {
            tag_team_name_list.clear()
            tag_team_role_list.clear()
            tag_team_photo_list.clear()
            tag_team_votes_list.clear()
            tag_team_disable_list.clear()
            for (i in candidates.indices) {
                val detail = candidates[i] as TagTeamDetail
                val name = detail.name
                tag_team_name_list.add(name)
                tag_team_disable_list.add(detail.isDisabled)
                //  val bitmap = getCandidatePhoto(name, voting_item)

                tag_team_role_list.add(detail.role)

                tag_team_votes_list.add(number_of_votes[i])

                tag_team_photo_list.add("")
            }

            votes_map[voting_item] = tag_team_votes_list

            /*
            tag_team_votingAdapter = VotingAdapter(context, tag_team_name_list, tag_team_role_list,
                    null, tag_team_photo_list, voting_item, tag_team_disable_list,myMeeting)

            */

            tag_team_votingAdapter = VotingAdapter(context, tag_team_name_list, tag_team_role_list,
                    null, tag_team_photo_list, voting_item, tag_team_disable_list,isMyMeeting)
        }
    }

    override fun onBackPressed() {
        if (votingProcessWrapper?.m_current_voting_process !is ThankYouVotingProcessState) {

            val set_server_dialogue = AlertDialog.Builder(this@VotingActivity)
            set_server_dialogue.setTitle("To exit the voting process,enter the password ")

            val password_et = EditText(this)
            set_server_dialogue.setView(password_et)
            password_et.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD

            set_server_dialogue.setPositiveButton("Exit! ", DialogInterface.OnClickListener { dialog, id ->
                if (password_et.text.toString() == "") {
                    Toast.makeText(applicationContext, "Enter Password", Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                val sharedpreferences = getSharedPreferences("votingapp", Context.MODE_PRIVATE)
                val password_value = sharedpreferences.getString("password", "smile")

                if (password_et.text.toString() != password_value) {
                    Toast.makeText(applicationContext, "Password incorrect", Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }

                finish()
                dialog.dismiss()
            })

            set_server_dialogue.setNegativeButton("Cancel ") { dialog, id -> dialog.dismiss() }

            val myalertobject = set_server_dialogue.create()
            //Show the dialog
            myalertobject.show()

        } else {
            super.onBackPressed()
        }

    }

    internal fun aVotePolled(voting_item: String, position: Int) {
        /*  Log.d("checkvotes", "a vote polled")
          val votes_list = votes_map[voting_item]
          val new_count = votes_list?.get(position)?.plus(1)
          votes_list?.set(position, new_count?:0)

          votes_map[voting_item] = votes_list!!*/

        val candidates = cadidates_map[voting_item]
        val voteGivenTo = candidates!![position]
/*
        viewModel.giveVoteTo(voteGivenTo,
                stringResource(voting_item).toCandidateType(),
                meeting_number.meetingId)
*/
        viewModel.giveVoteTo(voteGivenTo,
                stringResource(voting_item).toCandidateType(),
                meetingNo)

    }

    fun Next(v: View) {
        votingProcessWrapper?.Next()
    }

    fun goToReults(v: View) {


        val set_server_dialogue = AlertDialog.Builder(this@VotingActivity)
        set_server_dialogue.setTitle(" To get the result, enter the password and press yes.")

        val password_et = EditText(this)

        password_et.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        set_server_dialogue.setView(password_et)

        set_server_dialogue.setPositiveButton("Yes! ", DialogInterface.OnClickListener { dialog, id ->
            val sharedpreferences = getSharedPreferences("votingapp", Context.MODE_PRIVATE)
            val password_value = sharedpreferences.getString("password", "smile")

            if (password_et.text.toString() == "") {
                Toast.makeText(applicationContext, "Enter Password", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (password_et.text.toString() != password_value) {
                Toast.makeText(applicationContext, "Password incorrect", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }


            val intent = Intent(applicationContext, ResultActivity::class.java)

          //  intent.putExtra("meeting_number", meeting_number)
            intent.putExtra("meeting_number", meetingNo)

            startActivity(intent)
            dialog.dismiss()
        })

        set_server_dialogue.setNegativeButton("Cancel ") { dialog, id -> dialog.dismiss() }

        val myalertobject = set_server_dialogue.create()
        //Show the dialog
        myalertobject.show()

    }

    internal fun getBitmap(name: String): Bitmap? {

        val root = Environment.getExternalStorageDirectory().toString()
       // val myDir = File("$root/voting app /meeting $meeting_number/$voting_item")
        val myDir = File("$root/voting app /meeting $meetingNo/$voting_item")

        if (myDir.exists())
            Log.d("findphoto", "myDir.exists() is true ")
        Log.d("findphoto", "myDir is  " + myDir.toString())

        val fname = "$name.jpg"
        val file = File(myDir, fname)

        Log.d("findphoto", "file is  " + file.toString())
        val bitmap: Bitmap
        if (file.exists()) {
            Log.d("findphoto", "file.exists() is true ")
            bitmap = BitmapFactory.decodeFile(file.absolutePath)

            return bitmap
        }
        Log.d("findphoto", "file.exists() is false ")
        return null
    }

    internal fun getCandidatePhoto(name: String, voting_item: String): Bitmap? {
        val root = Environment.getExternalStorageDirectory().toString()
       // val myDir = File("$root/voting app /meeting $meeting_number/$voting_item")
        val myDir = File("$root/voting app /meeting $meetingNo/$voting_item")

        if (myDir.exists())
            Log.d("findphoto", "myDir.exists() is true ")
        Log.d("findphoto", "myDir is  " + myDir.toString())

        val fname = "$name.jpg"
        val file = File(myDir, fname)

        Log.d("findphoto", "file is  " + file.toString())
        val bitmap: Bitmap
        if (file.exists()) {
            Log.d("findphoto", "file.exists() is true ")
            bitmap = BitmapFactory.decodeFile(file.absolutePath)

            return bitmap
        }
        Log.d("findphoto", "file.exists() is false ")
        return null
    }

    fun getIndexOfMaxVotes(list: ArrayList<Int>): Int {

        var index_of_max = -1
        var max_votes = 0

        for (i in list.indices) {

            if (list[i] > max_votes) {
                index_of_max = i
                max_votes = list[i]
            }
        }
        return index_of_max
    }

    internal fun saveAllVotes() {

        for (i in custom_voting_item_list.indices) {
            val cadidates = cadidates_map[custom_voting_item_list[i]]
            val votes_list = votes_map[custom_voting_item_list[i]]

            for (j in votes_list?.indices!!) {
                Log.d("checkvotes", "votes_list.get(j)  is" + votes_list[j])
            }
            // DBHelper().setVotingCandidatesAndVotes(cadidates, meeting_number, custom_voting_item_list[i], votes_list)
        }
    }

    internal interface VotingProcessState {
        fun Next(wrapper: VotingProcessWrapperClass)
    }

    internal inner class PleaseVoteVotingProcessState : VotingProcessState {
        init {
            setContentView(R.layout.activity_before_voting)

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

            prepared_speaker_votingAdapter.adapter.selected_position = -1
            (findViewById<View>(R.id.voting_screen_list) as ListView).adapter = prepared_speaker_votingAdapter.adapter
        }

        override fun Next(wrapper: VotingProcessWrapperClass) {
            val selected_candidate = prepared_speaker_votingAdapter.adapter.selected_position
            Log.d("toast", "prepared_speaker_name_list.size is" + prepared_speaker_name_list.size)
            if (selected_candidate < 0 && prepared_speaker_name_list.size > 0) {
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
            role_taker_votingAdapter.adapter.selected_position = -1
            (findViewById<View>(R.id.voting_screen_list) as ListView).adapter = role_taker_votingAdapter.adapter
        }

        override fun Next(wrapper: VotingProcessWrapperClass) {
            val selected_candidate = role_taker_votingAdapter.adapter.selected_position
            Log.d("result", "in voting  of role taker with index $selected_candidate")
            if (selected_candidate < 0 && role_taker_name_list.size > 0) {
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
            tag_team_votingAdapter.adapter.selected_position = -1
            (findViewById<View>(R.id.voting_screen_list) as ListView).adapter = tag_team_votingAdapter.adapter
        }

        override fun Next(wrapper: VotingProcessWrapperClass) {
            val selected_candidate = tag_team_votingAdapter.adapter.selected_position
            Log.d("result", "in voting  of tag team with index $selected_candidate")
            if (selected_candidate < 0 && tag_team_name_list.size > 0) {
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

            table_topic_votingAdapter.adapter.selected_position = -1
            (findViewById<View>(R.id.voting_screen_list) as ListView).adapter = table_topic_votingAdapter.adapter
        }

        override fun Next(wrapper: VotingProcessWrapperClass) {
            val selected_candidate = table_topic_votingAdapter.adapter.selected_position

            if (selected_candidate < 0 && table_topic_name_list.size > 0) {
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

            evaluator_voting_adapter.adapter.selected_position = -1

            (findViewById<View>(R.id.voting_screen_list) as ListView).adapter = evaluator_voting_adapter.adapter

        }

        override fun Next(wrapper: VotingProcessWrapperClass) {
            val selected_candidate = evaluator_voting_adapter.adapter.selected_position

            if (selected_candidate < 0 && evaluators_name_list.size > 0) {
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

            setContentView(R.layout.activity_after_voting_screen)
            card_alredy_voting = findViewById<View>(R.id.card_alredy_voting) as CardView

            // saveAllVotes()

            coordinatorLayout = findViewById<View>(R.id.coordinatorLayout) as CoordinatorLayout
            val snackbar = Snackbar
                    .make(coordinatorLayout, "Your votes have been captured ", Snackbar.LENGTH_LONG)

            snackbar.show()

            card_alredy_voting.setOnClickListener {
                val snackbar = Snackbar
                        .make(coordinatorLayout, "Your votes have been captured ", Snackbar.LENGTH_LONG)

                snackbar.show()
            }

            //saveAllVotes();
        }

        override fun Next(wrapper: VotingProcessWrapperClass) {
            //wrapper.set_state(new PleaseVoteVotingProcessState());
            wrapper.set_state_custom_logic(applicationContext.resources.getText(R.string.thank_you).toString())

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

    companion object {

        var isVoteSelectionScreen = false
        var isPrepareallVotingResult = false

        var IS_MY_MEETING ="EntryPoint"
        val isExternalStorageReadOnly: Boolean
            get() {
                val extStorageState = Environment.getExternalStorageState()
                return if (Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState) {
                    true
                } else false
            }

        val isExternalStorageAvailable: Boolean
            get() {
                val extStorageState = Environment.getExternalStorageState()
                return if (Environment.MEDIA_MOUNTED == extStorageState) {
                    true
                } else false
            }
    }



}


