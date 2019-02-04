package myoo.votingapp.view.Fragment


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView

import java.util.ArrayList

import myoo.votingapp.view.Activity.AddCandidateActivity
import myoo.votingapp.view.Adapter.ClubDetailsAdapter
import myoo.votingapp.viewmodel.DBHelper
import myoo.votingapp.R
import myoo.votingapp.Utils.Permission



class ListOfClubMembersFragment : Fragment() {
    var mPage: Int = 0
    internal val REQUEST_ADD_CANDIDATE = 100
    internal val REQUEST_EDIT_CANDIDATE = 150
    lateinit var peopeleview: View
    lateinit var mydb: DBHelper
    internal var members_list: ArrayList<String>? = null
    internal var guests_list: ArrayList<String>? = null

    lateinit var permission: Permission

    lateinit var list: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mydb = DBHelper(activity!!)
        mPage = arguments!!.getInt(ARG_PAGE)
        permission = Permission()

        Log.d("checkfragment", "in on create of fragment")

        members_list = ArrayList()
        guests_list = ArrayList()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        Log.d("checkfragment", "in on createview  of fragment")
        when (mPage) {

            1 -> {
                peopeleview = inflater.inflate(R.layout.list_of_people, container, false)
                setFragment(1)
                peopeleview.findViewById<View>(R.id.fab).visibility = View.VISIBLE
                peopeleview.findViewById<View>(R.id.fab).setOnClickListener {
                    val sharedpreferences = activity!!.getSharedPreferences("votingapp", Context.MODE_PRIVATE)
                    val editor = sharedpreferences.edit()
                    editor.putString("meeting_number", "members")

                    editor.commit()
                    val intent = Intent(activity!!.applicationContext, AddCandidateActivity::class.java)
                    intent.putExtra("voting_item", "members")
                    intent.putExtra("request_code", REQUEST_ADD_CANDIDATE)

                    startActivityForResult(intent, REQUEST_ADD_CANDIDATE)
                }
            }

            2 -> {
                peopeleview = inflater.inflate(R.layout.list_of_people, container, false)
                setFragment(2)
                peopeleview.findViewById<View>(R.id.fab).visibility = View.VISIBLE
                peopeleview.findViewById<View>(R.id.fab).setOnClickListener {
                    val sharedpreferences = activity!!.getSharedPreferences("votingapp", Context.MODE_PRIVATE)
                    val editor = sharedpreferences.edit()
                    editor.putString("meeting_number", "guests")

                    editor.commit()
                    val intent = Intent(activity!!.applicationContext, AddCandidateActivity::class.java)

                    intent.putExtra("voting_item", "guests")
                    intent.putExtra("request_code", REQUEST_ADD_CANDIDATE)

                    startActivityForResult(intent, REQUEST_ADD_CANDIDATE)
                }
            }
        }
        return peopeleview
    }

    internal fun setFragment(fragment_numbner: Int) {

        list = peopeleview.findViewById<View>(R.id.listview_list_of_member) as ListView
        val heading = peopeleview.findViewById<View>(R.id.heading_for_list_of_member) as TextView


        when (fragment_numbner) {
            1 -> {
                heading.text = "Members"

                permission.doWorkOnPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,activity = activity!!,work ={ setListOfMembers(list)});


            }

            2 -> {
                heading.text = "Guests"

                permission.doWorkOnPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,activity = activity!!,work ={  setListOfGuests(list)});



            }
        }

    }

    internal fun setListOfMembers(list: ListView) {
        members_list = mydb.members
        val adapter = ClubDetailsAdapter(activity, members_list, this)
        list.adapter = adapter

    }

    fun editTheCandidate(position: Int) {
        val set_server_dialogue = AlertDialog.Builder(activity)
        val myalertobject: AlertDialog
        when (mPage) {

            1 -> {

                set_server_dialogue.setTitle(" Do you want to edit members's entries?")


                set_server_dialogue.setPositiveButton("Yes,edit! ") { dialog, id ->
                    val intent = Intent(activity, AddCandidateActivity::class.java)

                    intent.putExtra("voting_item", "members")
                    intent.putExtra("request_code", REQUEST_EDIT_CANDIDATE)

                    intent.putExtra("detailobject", members_list!![position])

                    intent.putExtra("meeting_number", "members")
                    intent.putExtra("position", position)

                    startActivityForResult(intent, REQUEST_EDIT_CANDIDATE)
                    //startActivityForResult(intent, REQUEST_EDIT_CANDIDATE,ActivityOptions.makeSceneTransitionAnimation(ListOfCandidates.this).toBundle());
                    dialog.dismiss()
                }

                set_server_dialogue.setNegativeButton("No ") { dialog, id -> dialog.dismiss() }

                myalertobject = set_server_dialogue.create()
                //Show the dialog
                myalertobject.show()
            }
            2 -> {

                set_server_dialogue.setTitle(" Do you want to edit guest's entries?")


                set_server_dialogue.setPositiveButton("Yes,edit! ") { dialog, id ->
                    val intent = Intent(activity, AddCandidateActivity::class.java)

                    intent.putExtra("voting_item", "guests")
                    intent.putExtra("request_code", REQUEST_EDIT_CANDIDATE)

                    intent.putExtra("detailobject", guests_list!![position])

                    intent.putExtra("meeting_number", "guests")
                    intent.putExtra("position", position)

                    startActivityForResult(intent, REQUEST_EDIT_CANDIDATE)
                    //startActivityForResult(intent, REQUEST_EDIT_CANDIDATE,ActivityOptions.makeSceneTransitionAnimation(ListOfCandidates.this).toBundle());
                    dialog.dismiss()
                }

                set_server_dialogue.setNegativeButton("No ") { dialog, id -> dialog.dismiss() }

                myalertobject = set_server_dialogue.create()
                //Show the dialog
                myalertobject.show()
            }
        }


    }


    internal fun setListOfGuests(list: ListView) {
        guests_list = mydb.guests
        val adapter = ClubDetailsAdapter(activity, guests_list, this)
        list.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (mPage) {
            1 -> {
                if (requestCode == REQUEST_ADD_CANDIDATE) {

                    // The resultCode is set by the DetailActivity
                    // By convention RESULT_OK means that whatever
                    // DetailActivity did was executed successfully
                    if (data?.extras!=null) {
                        val name = data.getStringExtra("detailobject")
                        members_list!!.add(name)
                        (list.adapter as ClubDetailsAdapter).notifyDataSetChanged()
                        mydb.insertMembers(name)
                    }

                }

                if (requestCode == REQUEST_EDIT_CANDIDATE) {

                    // The resultCode is set by the DetailActivity
                    // By convention RESULT_OK means that whatever
                    // DetailActivity did was executed successfully
                    if (data?.extras!=null) {
                        val position = data.extras!!.getInt("position")
                        val name = data.getStringExtra("detailobject")
                        members_list!![position] = name
                        (list.adapter as ClubDetailsAdapter).notifyDataSetChanged()
                        mydb.setMembers(members_list!!)

                    }
                }
            }
            2 -> {

                if (requestCode == REQUEST_ADD_CANDIDATE) {

                    // The resultCode is set by the DetailActivity
                    // By convention RESULT_OK means that whatever
                    // DetailActivity did was executed successfully
                    if (data?.extras!=null) {
                        val name = data.getStringExtra("detailobject")
                        guests_list!!.add(name)
                        (list.adapter as ClubDetailsAdapter).notifyDataSetChanged()
                        mydb.insertGuests(name)
                    }

                }

                if (requestCode == REQUEST_EDIT_CANDIDATE) {

                    // The resultCode is set by the DetailActivity
                    // By convention RESULT_OK means that whatever
                    // DetailActivity did was executed successfully
                    if (data?.extras!=null) {
                        val position = data.extras!!.getInt("position")
                        val name = data.getStringExtra("detailobject")
                        guests_list!![position] = name
                        (list.adapter as ClubDetailsAdapter).notifyDataSetChanged()
                        mydb.setguests(guests_list!!)

                    }
                }
            }
        }

    }

    companion object {
        val ARG_PAGE = "page_number"

        internal var members: ArrayList<String>? = null

        fun newInstance(page: Int): ListOfClubMembersFragment {

            val args = Bundle()
            args.putInt(ARG_PAGE, page)
            val fragment = ListOfClubMembersFragment()
            fragment.arguments = args

            return fragment
        }
    }
}
