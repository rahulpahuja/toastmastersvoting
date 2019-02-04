package myoo.votingapp.view.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_prepare_all_the_voting_items.*
import kotlinx.android.synthetic.main.custom_actionbar.*
import myoo.votingapp.view.Adapter.VotingItemsAdapter
import myoo.votingapp.R
import myoo.votingapp.Utils.Util
import myoo.votingapp.Utils.gone
import myoo.votingapp.Utils.open
import myoo.votingapp.Utils.visible

import myoo.votingapp.viewmodel.MeetingCandidatesViewModel
import org.koin.android.ext.android.inject
import java.util.*



/**
 * PrepareAllVotingItemsScreen : buttons for different details of the particular meeting
 */
class PrepareAllVotingItemsScreen : AppCompatActivity() {


  //  private val meeting by lazy { intent.getParcelableExtra<Meeting>(MEETING) }

    private val meetingNo by lazy { intent.getStringExtra((MeetingNo)) }

    private val userId by inject<String>("userId")

    lateinit var adapter: VotingItemsAdapter

    private val voting_item_count_list= ArrayList<String>()

    private val viewModel by inject<MeetingCandidatesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prepare_all_the_voting_items)

        SetUpData()

        addAllTheRows()

        retriveVotingPeopleCount()

        observeCounts()

        /*
        if(meeting.createdby==userId){
            RRbelow.visible()
            rrTop.gone()
        }else{
            RRbelow.gone()
            rrTop.visible()
        }

        */
        RRbelow.visible()
        rrTop.gone()

/*
        rrTop.setOnClickListener {_->

            open<QualificationCheck> {
                it.putSerializable("custom_voting_item_list", getVotingArray())
            //    it.putParcelable(MEETING, meeting)
                it.putString(MeetingNo , meetingNo)

            }

        }

        */
    }

    private fun observeCounts() {

        viewModel.countResponse.observe(this,android.arch.lifecycle.Observer {
            voting_item_count_list.clear()
         it?.let { voting_item_count_list.addAll(it.map { it.toString() })}

            adapter.notifyDataSetChanged()
        })

    }

    private fun retriveVotingPeopleCount() {
       // viewModel.retirveCandidateCount(meeting.meetingId)
        viewModel.retirveCandidateCount(meetingNo)
    }

    fun SetUpData() {
        Log.d("mytag", " opened meeting no " + meetingNo  )
      //  txt_header.text = meeting.meetingNo
        txt_header.text = meetingNo
        imageBackPress.visibility = View.VISIBLE
        imageBackPress.setOnClickListener { finish() }
    }


    fun enterVotingProcessWithSelectedItems(v: View) {

        val set_server_dialogue = AlertDialog.Builder(this@PrepareAllVotingItemsScreen)
        set_server_dialogue.setTitle("Choose the items for which to conduct voting.")


       // val password_et = EditText(this)
        //password_et.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        //password_et.hint = "enter password"


        // Create LinearLayout
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val voting_item_list = getVotingArray()

        for (i in voting_item_list.indices) {

            val checkBox = CheckBox(this)
            checkBox.text = voting_item_list[i]

            checkBox.isChecked = true

            layout.addView(checkBox)
        }

      //  layout.addView(password_et)


        set_server_dialogue.setView(layout)


        set_server_dialogue.setPositiveButton("OK ", DialogInterface.OnClickListener { dialog, id ->
            val sharedpreferences = getSharedPreferences("votingapp", Context.MODE_PRIVATE)
            val password_value = sharedpreferences.getString("password", "smile")

            /*
            if (password_et.text.toString() == "") {
                Toast.makeText(applicationContext, "Enter Password", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (password_et.text.toString() != password_value) {
            Toast.makeText(applicationContext, "Password incorrect", Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }
*/
          //  return@OnClickListener

            val items_to_vote_for = ArrayList<String>()

            for (i in voting_item_list.indices) {
                if ((layout.getChildAt(i) as CheckBox).isChecked && Integer.parseInt(voting_item_count_list[i]) > 1) {
                    items_to_vote_for.add(voting_item_list[i])
                }
            }

            if (items_to_vote_for.size == 0) {
                Toast.makeText(this@PrepareAllVotingItemsScreen, "No candidate found for voting in this project. Please add candidate to prodeed.", Toast.LENGTH_LONG).show()
            } else {

                open<QualificationCheck> {
                    it.putSerializable("custom_voting_item_list", items_to_vote_for)
                    it.putString(MeetingNo, meetingNo)
                 //   it.putParcelable(MEETING, meeting)
                }
                return@OnClickListener
                dialog.dismiss()
            }
        })

        set_server_dialogue.setNegativeButton("Cancel ") { dialog, id -> dialog.dismiss() }

        val myalertobject = set_server_dialogue.create()
        //Show the dialog
        myalertobject.show()

    }

    fun getVotingArray(): ArrayList<String> {
        val voting_item_array = resources.getStringArray(R.array.voting_item_list)
        val voting_item_list = ArrayList(Arrays.asList(*voting_item_array))
        return voting_item_list
    }

    private fun addAllTheRows() {

        val voting_item_array = resources.getStringArray(R.array.voting_item_list)
        val voting_item_list = ArrayList(Arrays.asList(*voting_item_array))

        adapter = VotingItemsAdapter(this@PrepareAllVotingItemsScreen,
                voting_item_list,
                voting_item_count_list,
                VotingItemsAdapter.OnItemClickListener { cardDetails, position ->

                    open<ListOfCandidates> {
                        //    it.putParcelable(MEETING, meeting)
                        it.putString(MeetingNo, meetingNo)
                        it.putString("voting_item", cardDetails)
                    }
                })

        val mLayoutManager = GridLayoutManager(this@PrepareAllVotingItemsScreen, 2)
        recycler_view.layoutManager = mLayoutManager
        recycler_view.addItemDecoration(Util.GridSpacingItemDecoration(2, Util.dpToPx(0, this@PrepareAllVotingItemsScreen), true))
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = adapter
    }

    fun goToReults(v: View) {

        isPrepareallVotingResult = true

        /*
        open<CheckValidationCodeActivity> {
         //   it.putString("meeting",meeting.meetingId)
            it.putString("meeting",meetingNo)
        }
        */
        val intent = Intent(applicationContext, ResultActivity::class.java)

        intent.putExtra("meeting_number", meetingNo)

        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
    }

    companion object {
        val MEETING = "meeting_id"
        val MeetingNo = "MeetingNo"

        var isPrepareallVotingResult = false
    }
}
