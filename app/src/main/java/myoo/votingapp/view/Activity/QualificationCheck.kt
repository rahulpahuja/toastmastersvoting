package myoo.votingapp.view.Activity

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_list_of_candidates.*
import kotlinx.android.synthetic.main.custom_actionbar.*

import java.io.File
import java.util.ArrayList

import myoo.votingapp.Model.Candidate
import myoo.votingapp.Model.CandidateList
import myoo.votingapp.Model.EvaluatorDetail
import myoo.votingapp.Model.PreparedSpeakerDetail
import myoo.votingapp.Model.RoleTakerDetail
import myoo.votingapp.Model.TableTopicDetail
import myoo.votingapp.Model.TagTeamDetail
import myoo.votingapp.R
import myoo.votingapp.Utils.Util
import myoo.votingapp.Utils.open
import myoo.votingapp.view.Activity.PrepareAllVotingItemsScreen.Companion.MeetingNo

import org.koin.android.ext.android.inject
import  myoo.votingapp.view.Activity.VotingActivity.Companion.IS_MY_MEETING;
import myoo.votingapp.viewmodel.MeetingCandidatesViewModel

/**
 * PrepareAllVotingItemsScreen : buttons for different details of the particular meeting
 */
class QualificationCheck : AppCompatActivity() {

    /*
    private val meeting_detail by lazy {
        intent.extras.getParcelable<Meeting>(MEETING)
    }

    */

    private val meetingNo by lazy {
        intent.extras.getString(MeetingNo)
    }

    private  val userId by inject<String>("userId")

    internal var candidateslist_list = ArrayList<CandidateList>()
    internal var list1 = ArrayList<Candidate>()
    internal var list2 = ArrayList<Candidate>()
    internal var list3 = ArrayList<Candidate>()
    internal var list4 = ArrayList<Candidate>()
    internal var listprepaed = ArrayList<Candidate>()
    internal var listtabletopic = ArrayList<Candidate>()
    internal var listRoleTaker = ArrayList<Candidate>()
    internal var listEvalutor = ArrayList<Candidate>()
    internal var candidates: List<Any>? = null
    //  private DBHelper mydb;

    private val viewModel by inject<MeetingCandidatesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //    mydb = new DBHelper(this);
        setContentView(R.layout.activity_list_verify_candidtes)


        txt_sub_header.visibility = View.VISIBLE
        txt_sub_header.text = "Please select the disqualify candidates"

        imageBackPress.visibility = View.VISIBLE
        txt_header.text = "Qualification Check"
        imageBackPress.setOnClickListener { finish() }


        //viewModel.retriveAllCandidates(meeting_detail.meetingNo)
        viewModel.retriveAllCandidates(meetingNo)

        viewModel.response.observe(this, Observer {

            it?.let { populateCandidateList(it);findDisQulifiedCount(it) }

        })

    }


    private fun populateCandidateList(candidates: List<Any>) {

        this.candidates =  candidates

        list1.clear()
        list2.clear()
        list3.clear()
        list4.clear()


        listprepaed.clear()
        listtabletopic.clear()
        listRoleTaker.clear()
        listEvalutor.clear()

        try {
            for (i in candidates.indices) {
                if (candidates[i] is PreparedSpeakerDetail) {


                    val speakerDetail = candidates[i] as PreparedSpeakerDetail
                    val name = speakerDetail.name
                    // val bitmap = getBitmap(name, resources.getText(R.string.prepared_speaker).toString())
                    val candidate = Candidate()
                    candidate.setName(name)
                    candidate.setVoting_item(resources.getText(R.string.prepared_speaker).toString())
                    candidate.setProject(speakerDetail.project)
                    candidate.setTitle(speakerDetail.title)
                    candidate.setPhoto(speakerDetail.imageUrl)
                    candidate.setisDisabled(speakerDetail.isDisabled)
                    candidate.setRole("")
                    candidate.positionOfCandidate = i

                    list1.add(candidate)
                }


                if (candidates[i] is TableTopicDetail) {

                    val topicDetail = candidates[i] as TableTopicDetail
                    val name = topicDetail.name
                    //  val bitmap = getBitmap(name, resources.getText(R.string.table_topics).toString())
                    val candidate = Candidate()
                    candidate.setName(name)
                    candidate.setVoting_item(resources.getText(R.string.table_topics).toString())
                    candidate.setProject("")
                    candidate.setTitle("")
                    candidate.setPhoto(topicDetail.imageUrl)
                    candidate.setRole("")
                    candidate.setisDisabled(topicDetail.isDisabled)
                    candidate.positionOfCandidate = i

                    list2.add(candidate)
                }

                if (candidates[i] is RoleTakerDetail) {

                    val takerDetail = candidates[i] as RoleTakerDetail
                    val name = takerDetail.name
                    //    val bitmap = getBitmap(name, resources.getText(R.string.role_takers).toString())
                    val candidate = Candidate()
                    candidate.setName(name)
                    candidate.setVoting_item(resources.getText(R.string.role_takers).toString())
                    candidate.setProject("")
                    candidate.setTitle("")
                    candidate.setPhoto(takerDetail.imageUrl)
                    candidate.setRole(takerDetail.role)
                    candidate.setisDisabled(takerDetail.isDisabled)
                    candidate.positionOfCandidate = i

                    list3.add(candidate)
                }


                if (candidates[i] is EvaluatorDetail) {

                    val detail = candidates[i] as EvaluatorDetail
                    val name = detail.name
                    //   val bitmap = getBitmap(name, resources.getText(R.string.evaluators).toString())
                    val candidate = Candidate()
                    candidate.setName(name)
                    candidate.setVoting_item(resources.getText(R.string.evaluators).toString())
                    candidate.setProject("")
                    candidate.setTitle("")
                    candidate.setPhoto(detail.imageUrl)
                    candidate.setRole("")
                    candidate.setisDisabled(detail.isDisabled)
                    candidate.positionOfCandidate = i

                    list4.add(candidate)
                }
            }

            candidateslist_list.clear()

            if (list1.size != 0) {
                val candidateList = CandidateList()
                candidateList.setCandidateArrayLista(list1)
                candidateList.voting_item = resources.getText(R.string.prepared_speaker).toString()
                candidateslist_list.add(candidateList)
            }

            if (list2.size != 0) {
                val candidateList2 = CandidateList()
                candidateList2.setCandidateArrayLista(list2)
                candidateList2.voting_item = resources.getText(R.string.table_topics).toString()
                candidateslist_list.add(candidateList2)
            }

            if (list3.size != 0) {
                val candidateList3 = CandidateList()
                candidateList3.setCandidateArrayLista(list3)
                candidateList3.voting_item = resources.getText(R.string.role_takers).toString()
                candidateslist_list.add(candidateList3)
            }

            if (list4.size != 0) {
                val candidateList4 = CandidateList()
                candidateList4.setCandidateArrayLista(list4)
                candidateList4.voting_item = resources.getText(R.string.evaluators).toString()
                candidateslist_list.add(candidateList4)
            }


            val adapter = VerifyListAdapter(this)
            listview.adapter = adapter
            setListViewHeightBasedOnChildren(listview)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return
    }

    internal fun findDisQulifiedCount(candidates: List<Any>?) {

        listprepaed.clear()
        listtabletopic.clear()
        listRoleTaker.clear()
        listEvalutor.clear()

        try {
            for (i in candidates!!.indices) {
                if (candidates[i] is PreparedSpeakerDetail) {


                    if (!(candidates[i] as PreparedSpeakerDetail).isDisabled) {
                        val candidate = Candidate()
                        candidate.setisDisabled((candidates[i] as PreparedSpeakerDetail).isDisabled)
                        listprepaed.add(candidate)
                    }

                }


                if (candidates[i] is TableTopicDetail) {

                    if (!(candidates[i] as TableTopicDetail).isDisabled) {
                        val candidate = Candidate()
                        candidate.setisDisabled((candidates[i] as TableTopicDetail).isDisabled)
                        listtabletopic.add(candidate)
                    }
                }

                if (candidates[i] is RoleTakerDetail) {

                    if (!(candidates[i] as RoleTakerDetail).isDisabled) {
                        val candidate = Candidate()
                        candidate.setisDisabled((candidates[i] as RoleTakerDetail).isDisabled)
                        listRoleTaker.add(candidate)
                    }
                }


                if (candidates[i] is EvaluatorDetail) {

                    if (!(candidates[i] as EvaluatorDetail).isDisabled) {
                        val candidate = Candidate()
                        candidate.setisDisabled((candidates[i] as EvaluatorDetail).isDisabled)
                        listEvalutor.add(candidate)
                    }
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

        return
    }

    internal fun getBitmap(name: String, voting_item: String): Bitmap? {

        val root = Environment.getExternalStorageDirectory().toString()
       // val myDir = File("$root/voting app /meeting $meeting_detail/$voting_item")
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

        fun startTheVotingProcess(view: View) {

        val custom_voting_item_list = intent.extras.getSerializable("custom_voting_item_list") as ArrayList<String>

        val items_to_vote_for = ArrayList<String>()

        for (i in custom_voting_item_list.indices) {
            if (custom_voting_item_list[i].equals(resources.getString(R.string.prepared_speaker), ignoreCase = true)) {
                if (listprepaed.size > 1) {
                    items_to_vote_for.add(custom_voting_item_list[i])
                }
            }

            if (custom_voting_item_list[i].equals(resources.getString(R.string.table_topics), ignoreCase = true)) {
                if (listtabletopic.size > 1) {
                    items_to_vote_for.add(custom_voting_item_list[i])
                }
            }

            if (custom_voting_item_list[i].equals(resources.getString(R.string.role_takers), ignoreCase = true)) {
                if (listRoleTaker.size > 1) {
                    items_to_vote_for.add(custom_voting_item_list[i])
                }
            }

            if (custom_voting_item_list[i].equals(resources.getString(R.string.evaluators), ignoreCase = true)) {
                if (listEvalutor.size > 1) {
                    items_to_vote_for.add(custom_voting_item_list[i])
                }
            }
        }

        if (items_to_vote_for.size == 0) {
            Toast.makeText(this@QualificationCheck, "No candidate found for voting in this project. Please add candidate to prodeed.", Toast.LENGTH_LONG).show()
        } else {

            viewModel.setMeetingActive(meetingNo , true).subscribe({
                open<VotingStatusAdmin> {
                    it.putSerializable("custom_voting_item_list", items_to_vote_for)
                    //   it.putParcelable(MEETING, meeting_detail)
                    it.putBoolean(IS_MY_MEETING , true)
                    it.putString(MeetingNo, meetingNo)
                }
                finish()
            })


            /*
            open<VotingStatusAdmin> {
                it.putSerializable("custom_voting_item_list", items_to_vote_for)
             //   it.putParcelable(MEETING, meeting_detail)
                it.putBoolean(IS_MY_MEETING , true)
                it.putString(MeetingNo, meetingNo)
            }

finish()
            */

            finish()
        }


    }

    fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter
                ?: // pre-condition
                return

        var totalHeight = 0
        val desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.AT_MOST)
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
            totalHeight += listItem.measuredHeight
        }

        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
        listView.layoutParams = params
        listView.requestLayout()
    }

    inner class BannerAdapter(private val mContext: Context       // for saving the current context
                              , imagelist: ArrayList<Candidate>) : RecyclerView.Adapter<BannerAdapter.MyViewHolder>() {
        internal var imagelist = ArrayList<Candidate>()


        internal var d: Drawable? = null
        internal var d1: Drawable? = null


        init {
            this.imagelist = imagelist

            this.imagelist = imagelist
            d = VectorDrawableCompat.create(mContext.resources, R.drawable.ic_visibility_black_24dp, mContext.theme)
            d1 = VectorDrawableCompat.create(mContext.resources, R.drawable.ic_visibility_off_black_24dp, mContext.theme)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.candidate_verify, parent, false)

            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val album = imagelist[position]

            holder.deletebutton.setImageDrawable(if (imagelist[position].getisDisabled()) d1 else d)
            holder.RRData.setOnClickListener { holder.deletebutton.performClick() }
            if (imagelist[position].getisDisabled() == true) {
                holder.RRRow.visibility = View.VISIBLE
            }

            /*
            if (meeting_detail.createdby == userId) {
                holder.deletebutton.setOnClickListener {
                    deleteTheCandidate(imagelist[position].positionOfCandidate,
                            imagelist[position].getisDisabled(),
                            imagelist[position].getVoting_item(), position) }
            }

            */
                // keep the delete option always. No need to check if (meeting_detail.createdby == userId) .
            // This activity will only be opening if that was true

            holder.deletebutton.setOnClickListener {
                deleteTheCandidate(imagelist[position].positionOfCandidate,
                        imagelist[position].getisDisabled(),
                        imagelist[position].getVoting_item(), position) }


            holder.rowView!!.setBackgroundColor(if (imagelist[position].getisDisabled()) Color.parseColor("#FFDADBDC") else Color.TRANSPARENT)


            holder.name.text = "Name: " + imagelist[position].getName()
            if (imagelist.size > position && imagelist[position].getProject() != null) {
                holder.project.visibility = View.VISIBLE
                holder.project.text = "Project:  " + imagelist[position].getProject()
            } else
                holder.project.visibility = View.GONE
            if (imagelist.size > position && imagelist[position].getTitle() != null) {
                holder.title.visibility = View.VISIBLE
                holder.title.text = "Title:  " + "\"" + imagelist[position].getTitle() + "\""
            } else
                holder.title.visibility = View.GONE
            if (imagelist.size > position && imagelist[position].getPhoto() != null) {

                Glide.with(holder.photo).load(imagelist[position].getPhoto()).into(holder.photo)

            }
            // holder.photo.setImageBitmap(imagelist[position].getPhoto())


        }

        override fun getItemCount(): Int {
            return imagelist.size
        }

        fun deleteTheCandidate(position: Int, isDisabled: Boolean, voting_item: String, oripo: Int) {
            val set_server_dialogue = AlertDialog.Builder(this@QualificationCheck)
            set_server_dialogue.setTitle(if (isDisabled) " Do you want to qualify candidate?" else " Do you want to disqualify candidate?")


            set_server_dialogue.setPositiveButton("Yes ") { dialog, id ->
                val canidate = candidates!![position]
                if (voting_item == resources.getText(R.string.prepared_speaker)) {
                    (canidate as PreparedSpeakerDetail).isDisabled = !isDisabled
                }

                if (voting_item == resources.getText(R.string.table_topics)) {
                    (canidate as TableTopicDetail).isDisabled = !isDisabled
                }

                if (voting_item == resources.getText(R.string.role_takers)) {
                    (canidate as RoleTakerDetail).isDisabled = !isDisabled
                }
                if (voting_item == resources.getText(R.string.tag_team)) {
                    (canidate as TagTeamDetail).isDisabled = !isDisabled
                }
                if (voting_item == resources.getText(R.string.evaluators)) {
                    (canidate as EvaluatorDetail).isDisabled = !isDisabled
                }

                //   candidates.remove(position);
                //    mydb.setVotingCandidatesDisqualified(candidates, meeting_detail, voting_item);




                val candidate = imagelist[oripo]
                candidate.setisDisabled(!imagelist[oripo].getisDisabled())
                imagelist[oripo] = candidate

                notifyItemChanged(oripo)

             //   viewModel.disqualifyTheCandidate(meeting_detail.meetingId, canidate)

                viewModel.disqualifyTheCandidate(meetingNo, canidate)

                findDisQulifiedCount(candidates)
                //populateCandidateList(candidates);



                dialog.dismiss()
            }

            set_server_dialogue.setNegativeButton("No ") { dialog, id -> dialog.dismiss() }

            val myalertobject = set_server_dialogue.create()
            //Show the dialog
            myalertobject.show()


        }

        inner class MyViewHolder(myView: View) : RecyclerView.ViewHolder(myView) {

            var rowView: View? = null
            internal var name: TextView
            internal var project: TextView
            internal var title: TextView
            internal var photo: ImageView
            internal var row: RelativeLayout
            internal var RRRow: RelativeLayout
            internal var RRData: RelativeLayout
            internal var deletebutton: ImageView

            init {

                name = myView.findViewById<View>(R.id.name_of_cadidate) as TextView

                title = myView.findViewById<View>(R.id.title_of_cadidate) as TextView
                project = myView.findViewById<View>(R.id.project_of_cadidate) as TextView
                photo = myView.findViewById<View>(R.id.photo) as ImageView

                row = myView.findViewById<View>(R.id.completerow) as RelativeLayout
                RRRow = myView.findViewById<View>(R.id.RRRow) as RelativeLayout
                RRData = myView.findViewById<View>(R.id.RRData) as RelativeLayout

                deletebutton = myView.findViewById<View>(R.id.deletebutton) as ImageView
                this.rowView = myView


            }
        }

    }

    inner class VerifyListAdapter(context: Context)  // initialise class variables with passed parameters
        : ArrayAdapter<CandidateList>(context, R.layout.group_list_of_candidate, candidateslist_list) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            var myView = convertView
            val viewHolder: ViewHolder

            if (myView == null) {

                val inflater = context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


                myView = inflater.inflate(R.layout.group_list_of_candidate, parent, false)

                viewHolder = ViewHolder()

                // asset declaration for row
                viewHolder.listview_list_of_member = myView!!.findViewById<View>(R.id.listview_list_of_member) as RecyclerView
                viewHolder.heading_for_list_of_member = myView.findViewById<View>(R.id.heading_for_list_of_member) as TextView
                myView.tag = viewHolder

            }

            val holder = myView.tag as ViewHolder

            holder.heading_for_list_of_member!!.text = candidateslist_list[position].voting_item

            val adapter = BannerAdapter(this@QualificationCheck, candidateslist_list[position].getCandidateArrayLista())

            val mLayoutManager = GridLayoutManager(this@QualificationCheck, 1)
            holder.listview_list_of_member!!.layoutManager = mLayoutManager
            holder.listview_list_of_member!!.addItemDecoration(Util.GridSpacingItemDecoration(1, Util.dpToPx(0, this@QualificationCheck), true))
            holder.listview_list_of_member!!.itemAnimator = DefaultItemAnimator()
            holder.listview_list_of_member!!.adapter = adapter

            holder.listview_list_of_member!!.isNestedScrollingEnabled = false

            return myView
        }

        internal inner class ViewHolder {

            // On screen asset declarations
            var listview_list_of_member: RecyclerView? = null
            var heading_for_list_of_member: TextView? = null

        }


    }

}
