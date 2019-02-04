package myoo.votingapp.view.Fragment

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_list_of_all_the_meetings.*
import kotlinx.android.synthetic.main.custom_actionbar.*
import myoo.votingapp.view.Adapter.MeetingAdapter
import myoo.votingapp.view.Adapter.MyMeetingsAdapter
import myoo.votingapp.R
import myoo.votingapp.Utils.*
import myoo.votingapp.view.Activity.AddNewMeetingActivity
import myoo.votingapp.Model.Meeting
import myoo.votingapp.viewmodel.MeetingViewModel
import myoo.votingapp.view.Activity.PrepareAllVotingItemsScreen
import myoo.votingapp.view.Activity.PrepareAllVotingItemsScreen.Companion.MEETING
import myoo.votingapp.view.Activity.PrepareAllVotingItemsScreen.Companion.MeetingNo

import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


/**
 * A login screen that offers login via email/password.
 */
class ListOfAllMeetingFragment : Fragment() {

    private val userId by inject<String>("userId")

    private val myadapter: MyMeetingsAdapter by lazy {
        MyMeetingsAdapter(activity, onMyMeetingClicked())
    }
    private val adapter: MeetingAdapter by lazy {
        MeetingAdapter(activity, onMeetingClicked())
    }
    private val viewModel by viewModel<MeetingViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       val view =  inflater.inflate(R.layout.activity_list_of_all_the_meetings, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setGridlayout()

        setClickEvent()
        setUpUI()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //with is used to access an object's members and methods without having to refer to the object once per access.
        // It is (mostly) for abbreviating your code.
        with(viewModel){

            // get list of all the meetings from firebase and bind to adapter
           // retriveAllMeetings()
         //  response.observe(this@ListOfAllMeetingFragment, Observer { bindNewList(it) })

            retriveAllMyMeetings()
            mymeetingsresponse.observe(this@ListOfAllMeetingFragment, Observer { bindMyNewList(it) })

            isLoading.observe(this@ListOfAllMeetingFragment, Observer {
   //             if (it==true) progress.visible() else progress.gone()
            })
            error.observe(this@ListOfAllMeetingFragment, Observer {
                activity?.showAlert(it)
            })
        }

    }

    private fun generateArrayList(map : HashMap<String , String>?){
        //Creating a HashMap object

//Getting Collection of values from HashMap

        val values = map?.values

//Creating an ArrayList of values

        val listOfValues = ArrayList<String>(values)
        Log.d("mytag", "list size is " + listOfValues.size )

        listOfValues.forEach { Log.d("mytag" , "element is "+ it) }


    }

    private fun bindMyNewList(it: List<String>?) {

        Log.d("mytag", " bindMyNewList list size is " + it?.size )

        it?.let {
            val calculateUtils = MyStringDiffCalculateUtils<String>(myadapter.getOriginalList(), it)
            val diff = DiffUtil.calculateDiff(calculateUtils)
            diff.dispatchUpdatesTo(myadapter)

            myadapter.bindNewList(it);
        }
    }

    private fun bindNewList(it: List<Meeting>?) {

        it?.let {
            val calculateUtils = DiffCalculateUtils<Meeting>(adapter.getOriginalList(), it)
            val diff = DiffUtil.calculateDiff(calculateUtils)
            diff.dispatchUpdatesTo(adapter)

            adapter.bindNewList(it);
        }
    }

    fun setClickEvent() {

        fab.setOnClickListener { view ->
            val intent = Intent(activity, AddNewMeetingActivity::class.java)
            startActivity(intent)
        }
    }

    fun setUpUI() {
        txt_header.text = "Meeting"
    }


    private fun setGridlayout() {


        //   meeting_list = mydb.getListOfMeetings();
        fab.visibility = View.VISIBLE


        val mLayoutManager = GridLayoutManager(activity, 3)
        recycler_view.layoutManager = mLayoutManager
        recycler_view.addItemDecoration(Util.GridSpacingItemDecoration(3, Util.dpToPx(0, activity), true))
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = myadapter

    }

    private fun onMeetingClicked(): MeetingAdapter.OnItemClickListener {
        return object : MeetingAdapter.OnItemClickListener{
            override fun onCardClicked(cardDetails: Meeting?, position: Int) {

                val intent = Intent(activity!!.applicationContext, PrepareAllVotingItemsScreen::class.java)
                intent.putExtra(MEETING,cardDetails)
                startActivity(intent)

            }
        }
    }

    private fun onMyMeetingClicked(): MyMeetingsAdapter.OnItemClickListener {
        return object : MyMeetingsAdapter.OnItemClickListener{
            override fun onCardClicked(meetingNo: String, position: Int) {
              //  Log.d("mytag", "onCardClicked  with string " + cardDetails  )

                val intent = Intent(activity!!.applicationContext, PrepareAllVotingItemsScreen::class.java)
                intent.putExtra(MeetingNo ,meetingNo )
                //intent.putExtra("meeting_No" ,meeting_number)
                startActivity(intent)

                /*
                with(viewModel){

                    getMeetingDetails(cardDetails)


                    meetingDetails.observe(this@ListOfAllMeetingFragment, Observer {
                        Log.d("mytag", "got a callback in fragement for meeting no "  )
                        val intent = Intent(activity!!.applicationContext, PrepareAllVotingItemsScreen::class.java)
                        intent.putExtra(MEETING,it)
                        intent.putExtra(MeetingNo ,cardDetails )
                        //intent.putExtra("meeting_No" ,meeting_number)
                        startActivity(intent)
                    })
                }

                */


            }
        }
    }
}

