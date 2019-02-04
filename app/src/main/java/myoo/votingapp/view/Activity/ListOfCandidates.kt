package myoo.votingapp.view.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_list_of_candidates.*
import kotlinx.android.synthetic.main.custom_actionbar.*
import myoo.votingapp.view.Adapter.CustomArrayAdapter
import myoo.votingapp.viewmodel.DBHelper
import myoo.votingapp.Model.EvaluatorDetail
import myoo.votingapp.Model.PreparedSpeakerDetail
import myoo.votingapp.Model.RoleTakerDetail
import myoo.votingapp.Model.TableTopicDetail
import myoo.votingapp.R
import myoo.votingapp.Utils.invisible
import myoo.votingapp.Utils.showAlert
import myoo.votingapp.Utils.visible
import myoo.votingapp.view.Activity.PrepareAllVotingItemsScreen.Companion.MEETING
import myoo.votingapp.view.Activity.PrepareAllVotingItemsScreen.Companion.MeetingNo
import myoo.votingapp.Model.stringResource
import myoo.votingapp.Model.toCandidateType

import myoo.votingapp.viewmodel.MeetingCandidatesViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class ListOfCandidates : AppCompatActivity() {

    internal var voting_item: String? = null
    internal val REQUEST_ADD_CANDIDATE = 100
    internal val REQUEST_EDIT_CANDIDATE = 150
    private val mydb: DBHelper? = null

    /*
    private val meeting: Meeting by lazy {
        intent.getParcelableExtra<Meeting>(MEETING)
    }
    */
    private val meetingNo: String by lazy {
        intent.getStringExtra(MeetingNo)
    }


    internal var name_list = ArrayList<String>()
    internal var project_list = ArrayList<String>()
    internal var title_list = ArrayList<String>()
    internal var photo_list = ArrayList<String?>()

    internal var role_list = ArrayList<String>()

    private val viewModel by viewModel<MeetingCandidatesViewModel>()

    private val userId by inject<String>("userId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // mydb = new DBHelper(this);

        setContentView(R.layout.activity_list_of_candidates)

        /*
        if (userId == meeting.createdby) {
         fab.visible()
        }else{
            fab.gone()
        }

        */

        fab.visible()

        fab.setOnClickListener {
            addACandidate(it)
        }

        SetUpData()

       // viewModel.retriveCandidates(stringResource(voting_item).toCandidateType(), meeting.meetingNo)
        viewModel.retriveCandidates(stringResource(voting_item).toCandidateType(), meetingNo)

        observeChanges()
    }

    private fun observeChanges() {

        viewModel.response.observe(this, android.arch.lifecycle.Observer {
            it?.let { populateCandidateList(it, voting_item) }
        })

        viewModel.error.observe(this, android.arch.lifecycle.Observer {
            showAlert(it)
        })

        viewModel.isLoading.observe(this, android.arch.lifecycle.Observer {

            if (it == true) {
                circle_loading_view.visible()
                circle_loading_view.startIndeterminate()
            } else {
                circle_loading_view.stopOk()
                circle_loading_view.invisible()
            }

        })


    }


    fun SetUpData() {
        val bundle = intent.extras
        voting_item = bundle!!.getString("voting_item")

        txt_header.text = voting_item
        imageBackPress.visibility = View.VISIBLE
        imageBackPress.setOnClickListener { finish() }
    }

    fun editTheCandidate(position: Int) {


        val set_server_dialogue = AlertDialog.Builder(this@ListOfCandidates)
        set_server_dialogue.setTitle(" Do you want to edit candidate's entries?")


        set_server_dialogue.setPositiveButton("Yes,edit! ") { dialog, id ->
            val intent = Intent(applicationContext, AddCandidateActivity::class.java)

            intent.putExtra("voting_item", voting_item)
            intent.putExtra("request_code", REQUEST_EDIT_CANDIDATE)


            val bundle = Bundle().apply {
              //  putString(MEETING, meeting.meetingNo)
                putString(MEETING, meetingNo)
            }

            intent.putExtras(bundle)

            if (voting_item == resources.getText(R.string.prepared_speaker)) {

                val details = candidates?.get(position) as PreparedSpeakerDetail?

                intent.putExtra("detailobject", details)

            } else if (voting_item == resources.getText(R.string.role_takers)) {

                val details = candidates?.get(position) as RoleTakerDetail?

                intent.putExtra("detailobject", details)

            } else if (voting_item == resources.getText(R.string.table_topics)) {

                val details = candidates?.get(position) as TableTopicDetail?

                intent.putExtra("detailobject", details)

            } else if (voting_item == resources.getText(R.string.evaluators)) {

                val details = candidates?.get(position) as EvaluatorDetail?

                intent.putExtra("detailobject", details)

            }


            intent.putExtra("position", position)
          //  intent.putExtra("meeting", meeting)
            intent.putExtra("meetingNo", meetingNo)

            startActivityForResult(intent, REQUEST_EDIT_CANDIDATE)
            //startActivityForResult(intent, REQUEST_EDIT_CANDIDATE,ActivityOptions.makeSceneTransitionAnimation(ListOfCandidates.this).toBundle());
            dialog.dismiss()
        }

        set_server_dialogue.setNegativeButton("No ") { dialog, id -> dialog.dismiss() }

        val myalertobject = set_server_dialogue.create()
        //Show the dialog
        myalertobject.show()


    }

    fun deleteTheCandidate(position: Int) {
        val set_server_dialogue = AlertDialog.Builder(this@ListOfCandidates)
        set_server_dialogue.setTitle(" Do you want to delete candidate's entries?")


        set_server_dialogue.setPositiveButton("Yes,delete! ") { dialog, id ->

            // mydb.setVotingCandidates(candidates, meeting, voting_item);

            /*
            viewModel.removeCandidate(candidates!![position],
            stringResource(voting_item).toCandidateType(),
            meeting.meetingNo)

*/
            viewModel.removeCandidate(candidates!![position],
                    stringResource(voting_item).toCandidateType(),
                    meetingNo)


            dialog.dismiss()
        }

        set_server_dialogue.setNegativeButton("No ") { dialog, id -> dialog.dismiss() }

        val myalertobject = set_server_dialogue.create()
        //Show the dialog
        myalertobject.show()


    }

    fun addACandidate(v: View) {

        val intent = Intent(applicationContext, AddCandidateActivity::class.java)

        intent.putExtra("voting_item", voting_item)
        intent.putExtra("request_code", REQUEST_ADD_CANDIDATE)

       // intent.putExtra(MEETING, meeting.meetingNo)
        intent.putExtra(MEETING, meetingNo)

        startActivityForResult(intent, REQUEST_ADD_CANDIDATE)
    }

    override fun onStart() {
        super.onStart()

    }


    private var candidates: List<Any>? = null

    internal fun populateCandidateList(candidates: List<Any>?, voting_item: String?) {


        this.candidates = candidates;

       // val myMeeting = userId == meeting.createdby
        val myMeeting = true

        name_list.clear()
        photo_list.clear()
        role_list.clear()
        title_list.clear()
        project_list.clear()

        try {

            if (voting_item == resources.getText(R.string.prepared_speaker)) {

                for (i in candidates!!.indices) {
                    val detail = candidates[i] as PreparedSpeakerDetail
                    val name = detail.name
                    name_list.add(name)
                    project_list.add(detail.project)
                    title_list.add(detail.title)
                    photo_list.add(detail.imageUrl)
                }
                val adapter = CustomArrayAdapter(this,
                        name_list,
                        title_list,
                        project_list,
                        photo_list,
                        voting_item, myMeeting)
                listview.adapter = adapter
            }

            if (voting_item == resources.getText(R.string.table_topics)) {
                name_list.clear()
                photo_list.clear()

                for (i in candidates!!.indices) {
                    var detail = candidates[i] as TableTopicDetail
                    val name = detail.name
                    name_list.add(name)

                    photo_list.add(detail.imageUrl)
                }
                val adapter = CustomArrayAdapter(this, name_list, title_list, project_list, photo_list, voting_item, myMeeting)
                listview.adapter = adapter
            }

            if (voting_item == resources.getText(R.string.role_takers)) {
                name_list.clear()
                photo_list.clear()
                role_list.clear()

                for (i in candidates!!.indices) {
                    val detail = candidates[i] as RoleTakerDetail
                    val name = detail.name
                    name_list.add(name)

                    photo_list.add(detail.imageUrl)
                    role_list.add(detail.role)
                }
                val adapter = CustomArrayAdapter(this, name_list, role_list, project_list, photo_list, voting_item, myMeeting)
                listview.adapter = adapter
            }
            if (voting_item == resources.getText(R.string.evaluators)) {
                name_list.clear()
                photo_list.clear()

                for (i in candidates!!.indices) {
                    var detail = candidates[i] as EvaluatorDetail
                    val name = detail.name
                    name_list.add(name)

                    photo_list.add(detail.imageUrl)
                }
                val adapter = CustomArrayAdapter(this, name_list, title_list, project_list, photo_list, voting_item, myMeeting)
                listview.adapter = adapter
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

        return
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // First we need to check if the requestCode matches the one we used.
        if (resultCode == Activity.RESULT_OK && data?.extras != null) {


            if (voting_item == resources.getText(R.string.prepared_speaker)) {

                val detail = data.extras.getParcelable("detailobject") as PreparedSpeakerDetail?

                detail?.let {
                 //   detail.meetingNo = meeting.meetingNo
                    detail.meetingNo = meetingNo
                    viewModel.addVotingPreparedSpeakerCandidate(detail)
                }

                //  mydb.addVotingItem(detail, voting_item, meeting, 0);

            } else if (voting_item == resources.getText(R.string.role_takers)) {

                val detail = data.extras.getParcelable("detailobject") as RoleTakerDetail?

                //  mydb.addVotingItem(detail, voting_item, meeting, 0);
                detail?.let {
                    //   detail.meetingNo = meeting.meetingNo
                    detail.meetingNo = meetingNo
                    viewModel.saveRoleTaker(detail)

                }

            } else if (voting_item == resources.getText(R.string.table_topics)) {
                val detail = data.extras.getParcelable("detailobject") as TableTopicDetail?
                // mydb.addVotingItem(detail, voting_item, meeting, 0);

                detail?.let {
                    //   detail.meetingNo = meeting.meetingNo
                    detail.meetingNo = meetingNo
                    viewModel.saveTableTopic(detail)

                }

            } else if (voting_item == resources.getText(R.string.evaluators)) {

                val detail = data.extras.getParcelable("detailobject") as EvaluatorDetail?

                detail?.let {
                    //   detail.meetingNo = meeting.meetingNo
                    detail.meetingNo = meetingNo
                    viewModel.saveEvaluators(detail)

                }


                // mydb.addVotingItem(detail, voting_item, meeting, 0);
            }

        }


        /* if (requestCode == REQUEST_EDIT_CANDIDATE) {

             // The resultCode is set by the DetailActivity
             // By convention RESULT_OK means that whatever
             // DetailActivity did was executed successfully
             if (resultCode == 2) {

                 val position = data.extras!!.getInt("position")

                 if (voting_item == resources.getText(R.string.prepared_speaker)) {

                     val detail = data.extras!!.getSerializable("detailobject") as PreparedSpeakerDetail



                     candidates!![position] = detail
                     // mydb.setVotingCandidates(candidates, meeting, voting_item);
                 } else if (voting_item == resources.getText(R.string.role_takers)) {

                     val detail = data.extras!!.getSerializable("detailobject") as RoleTakerDetail

                     candidates!![position] = detail
                     // mydb.setVotingCandidates(candidates, meeting, voting_item);
                 } else if (voting_item == resources.getText(R.string.table_topics)) {

                     val detail = data.extras!!.getSerializable("detailobject") as TableTopicDetail


                     candidates!![position] = detail
                     // mydb.setVotingCandidates(candidates, meeting, voting_item);
                 } else if (voting_item == resources.getText(R.string.evaluators)) {

                     val detail = data.extras!!.getSerializable("detailobject") as EvaluatorDetail
                     candidates!![position] = detail
                     // mydb.setVotingCandidates(candidates, meeting, voting_item);
                 }


             }

         }*/
    }

    companion object {


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
