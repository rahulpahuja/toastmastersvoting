package myoo.votingapp.view.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.text.InputType
import android.util.Log
import android.view.View
import myoo.votingapp.R
import myoo.votingapp.Utils.open

import org.koin.android.ext.android.inject
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_voting_status_admin.*
import myoo.votingapp.Model.*
import myoo.votingapp.R.array.voting_item_list
import myoo.votingapp.Utils.retriveChilds
import myoo.votingapp.viewmodel.CurrentLocationListener
import myoo.votingapp.viewmodel.MeetingCandidatesViewModel
import java.util.*
import android.arch.lifecycle.Observer
import android.view.Gravity
import android.widget.*
import com.google.firebase.database.ValueEventListener
import myoo.votingapp.Utils.disable


class VotingStatusAdmin : AppCompatActivity() {

    lateinit var custom_voting_item_list: ArrayList<String>
    lateinit var switch: Switch

    internal var cadidates_map = hashMapOf<String, ArrayList<Any>>()

    internal var votes_map = hashMapOf<String, ArrayList<Int>>()

    var count = 10000;
  //  private var mFusedLocationClient: FusedLocationProviderClient? = null

    private val viewModel by inject<MeetingCandidatesViewModel>()
    protected var mLastLocation: Location? = null
    private val meetingNo: String by lazy {
        intent.extras.getString(PrepareAllVotingItemsScreen.MeetingNo)
    }

    private val isMyMeeting: Boolean by lazy {
        intent.extras.getBoolean(VotingActivity.IS_MY_MEETING)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting_status_admin)

        custom_voting_item_list = intent.extras!!.getSerializable("custom_voting_item_list") as ArrayList<String>

        var meeting_id_tv = findViewById(R.id.meeting_id) as TextView
        var meeting_password_tv = findViewById(R.id.meeting_password) as TextView

        meeting_id_tv.text = "Meeting id : "+ meetingNo
        viewModel.getMeetingPassword(meetingNo)
                .subscribe({

                    Log.d("checkpassword" , "password was " + it)
                    meeting_password_tv.text = "Password for Voting: " + it
                }
                )



        switch = findViewById(R.id.switch1) as Switch
        switch.setOnCheckedChangeListener( { buttonView, isChecked ->

            viewModel.setMeetingActive(meetingNo , isChecked).subscribe({

                /*
                if(isChecked)
                {

                    if (!checkPermissions()) {
                        requestPermissions()
                    } else {
                        getLastLocation()
                    }
                }

                */

                val msg = if (isChecked) "voting active" else "voting inactive"
                Toast.makeText(this@VotingStatusAdmin, msg, Toast.LENGTH_SHORT).show()
                switch.setText(msg)
            })

        })


     //   mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        if (!checkPermissions()) {

            // here request permission after explaining the need
            // implement permission granted callback
          //  requestPermissions()

            dialogForRequestingPermission()
        } else {

            updateMyCurrentLocToDatabase()
        }

    }

    fun updateMyCurrentLocToDatabase(){

        var locationListner = CurrentLocationListener.getInstance(this)

        locationListner.getLastLocation()
        locationListner
                .observe(this, Observer
                { var location = it as Location
                    Log.d("location ", "latitude is" +  location.latitude  )
                    Log.d("location ", "longitude is" +  location.longitude  )

                    viewModel.updateLocation(meetingNo , location)
                })
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this@VotingStatusAdmin,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                100)

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {

            100 ->{
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("mytag", "Permission has been denied by user")
                }else{

                    updateMyCurrentLocToDatabase()
                }

            }
        }
    }
    public fun dialogForRequestingPermission(){
        val dialogue = AlertDialog.Builder(this)
        dialogue.setTitle("Enable location to help nearby people find your meeting easily.")

       // val text = TextView(this)

        //text.text = "Enable location to help nearby people find your meeting easily."


        //dialogue.setView(text)

        dialogue.setPositiveButton("OK ", DialogInterface.OnClickListener { dialog, id ->

            requestPermissions();

        })

      //  dialogue.setNegativeButton("Cancel ") { dialog, id -> dialog.dismiss() }

        var myalerDialog = dialogue.create()

        //Show the dialog
        myalerDialog?.show()


    }
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }
    override fun onResume() {
        super.onResume()

        viewModel.initiateCandidates(meetingNo)
                ?.subscribe(Consumer {

                    populateCount(meetingNo)

                })
    }
    public fun clearVotes(view:View){

        viewModel.clearVotes(meetingNo);

    }
/*
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && task.result != null) {
                        mLastLocation = task.result

                        Log.d("location" , "(mLastLocation )!!.latitude) " + (mLastLocation )!!.latitude)
                        Log.d("location" , "(mLastLocation )!!.longitude) " + (mLastLocation )!!.longitude)


                        viewModel.updateLocation(meetingNo , mLastLocation as Location)

                    } else {
                        Log.d("location", "getLastLocation:exception")

                    }
                }
    }

    */
    fun clearTheVoetsDailog(v: View) {

        val set_server_dialogue = AlertDialog.Builder(this@VotingStatusAdmin)
        set_server_dialogue.setTitle(" This will clear the all the votes for this meeting.Are you sure? ")

        val password_et = EditText(this)



        set_server_dialogue.setPositiveButton("Yes! ", DialogInterface.OnClickListener { dialog, id ->


            viewModel.clearVotesAsynch(meetingNo).subscribe( {
                populateCount(meetingNo)


            }
            )

            dialog.dismiss()
            return@OnClickListener
        })

        set_server_dialogue.setNegativeButton("Cancel ") { dialog, id -> dialog.dismiss() }

        val myalertobject = set_server_dialogue.create()
        //Show the dialog
        myalertobject.show()

    }
    public  fun voting(view: View){

        open<VotingActivity> {
            it.putSerializable("custom_voting_item_list", custom_voting_item_list)
            //   it.putParcelable(MEETING, meeting_detail)
            it.putBoolean(VotingActivity.IS_MY_MEETING, true)
            it.putString(PrepareAllVotingItemsScreen.MeetingNo, meetingNo)
        }
    }


    public  fun showNumberOfVotes(view: View){
        populateCount(meetingNo)

    }
    fun populateCount(meetingId: String , votingItem: String) {

        var results = viewModel.getMeetingResultsRef(meetingId).child(votingItem)
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // A new message has been added
                // onChildAdded() will be called for each node at the first time
                Log.e("firebaseiterate", "onChildAdded:" + dataSnapshot!!.key)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.e("firebaseiterate", "onChildChanged:" + dataSnapshot!!.key)

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.e("firebaseiterate", "onChildRemoved:" + dataSnapshot!!.key)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.e("firebaseiterate", "onChildMoved:" + dataSnapshot!!.key)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("firebaseiterate", "onCancelled:")
            }
        }

        results!!.addChildEventListener(childEventListener)


    }

    fun populateCount(meetingId: String) {
        val voting_item_array = application.resources.getStringArray(R.array.voting_item_list)
        var voting_item_list = ArrayList(Arrays.asList(*voting_item_array))

        Log.d("votescount", "In populateCount voting_item_list size is " + voting_item_list.size )



        Flowable.fromIterable(voting_item_list)
                .flatMapSingle {
                    Log.d("votescount", "in  flatMapSingle" )
                    viewModel.submitCandidateAndVotesRequest(
                            application.stringResource(it).toCandidateType(), meetingId)


                }
                .map {

                    map ->
                    Log.d("votescount", "in the map" )
                    //val candidates = map["candidates"] as ArrayList<Any>
                    val number_of_votes = map["number_of_votes"] as ArrayList<Int>

                    if(number_of_votes.size > 0 ){
                        var mycount = 0
                        number_of_votes.forEach { mycount += it }

                            Log.d("votescount", "votes count " + mycount)
                            count = mycount
                            var tv = findViewById(R.id.totalvotes) as TextView
                            tv.text = "Total votes till now : " + count


                    }


                    count

                }.last(Log.d("votescount", " count " + count))
                ?.subscribe(Consumer {
                    Log.d("votescount", " count " + count)
                })


    }

    fun updateNumberOfVotes(){


    }



    fun myfun(){

        viewModel.setMeetingActive(meetingNo , true).subscribe({

        })
    }
}
