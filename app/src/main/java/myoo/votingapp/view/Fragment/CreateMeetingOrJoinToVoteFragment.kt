package myoo.votingapp.view.Fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.custom_actionbar.*
import myoo.votingapp.R

import org.koin.android.ext.android.inject

import myoo.votingapp.view.Activity.JoinToVote
import myoo.votingapp.viewmodel.MeetingCandidatesViewModel
import android.widget.ArrayAdapter
import myoo.votingapp.Utils.disable
import myoo.votingapp.viewmodel.CurrentLocationListener
import java.util.*
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import kotlinx.android.synthetic.main.notification_template_lines_media.*
import myoo.votingapp.Model.LatLong
import org.koin.dsl.module.applicationContext


class CreateMeetingOrJoinToVoteFragment : Fragment() {

    protected var myalerDialog: AlertDialog? = null
    private val userId by inject<String>("userId")

    private val viewModel by inject<MeetingCandidatesViewModel>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.join_a_mmeting_frag, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpUI()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    fun findDistance(latlong1: LatLong , latlong2 : LatLong): Float{

        val loc1 = Location("")
        loc1.latitude = latlong1.latitude
        loc1.longitude = latlong1.longitude

        val loc2 = Location("")
        loc2.latitude = latlong2.latitude
        loc2.longitude = latlong2.longitude

        val distanceInMeters = loc1.distanceTo(loc2)
        Log.d("location ", " distanceInMeters was "  + distanceInMeters)
        return distanceInMeters

    }

    fun  CurrentLocationListener.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<Location>) {
        observeForever(object : Observer<Location> {
            override fun onChanged(t: Location?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }


    public fun SelectOptionToJoin(){
        val dialogue = AlertDialog.Builder(activity)
      //  dialogue.setTitle("Select a meeting or Enter the meeting id ")

        val text1 = TextView(activity)
        text1.textSize = 20.0f
        text1.setPadding(20,20,20,20)
        text1.text = "Find Nearby Meeting"
        text1.setOnClickListener{


            if (!checkPermissions()) {
                requestPermissions()
            } else {
                myalerDialog?.dismiss()
                nearbyMeetings()
            }

        }

        val text2 = TextView(activity)
        text2.textSize = 20.0f
        text2.setPadding(20,20,20,20)
        text2.text = "Enter meeting id"
        text2.setOnClickListener{
            myalerDialog?.dismiss()
            joinToVoteDialogue()

        }

        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(text1)
        layout.addView(text2)

        dialogue.setView(layout)



        myalerDialog = dialogue.create()
        //Show the dialog
        myalerDialog?.show()



    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(activity as Context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(activity as Activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                100)

    }

    public fun nearbyMeetings(){
        val dialogue = AlertDialog.Builder(activity)
        //dialogue.setTitle("No meetings nearby")
        val meeting_number_et = EditText(activity)


        val list = ListView(activity)
        // var mylist = ArrayList(Arrays.asList(*resources.getStringArray(R.array.projectCategory_list)))
        val mylist = arrayListOf<String>()
        var myadapter =  ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mylist);

        var locationListner = CurrentLocationListener.getInstance(context)
        locationListner.getLastLocation()

        var mylocation = LatLong()
        locationListner
                .observeOnce(this, Observer
                { var location = it as Location
                    Log.d("location ", " my location latitude is " +  location.latitude  )
                    Log.d("location ", "in  location longitude is " +  location.longitude  )

                    mylocation = LatLong(location.latitude ,location.longitude)

                })


        dialogue.setView(list)

        val ad = dialogue.create()

        //Show the dialog
        ad.show()


        viewModel.getListOfTodaysMeeting()
                .map {
                    Log.d("todaysmeeting", "meeting was " + it)
                    var meetingNo = it
                    viewModel.getLocationOfAMeeting(it).subscribe({
                        var meetinglocation = it

                        if(findDistance(meetinglocation ,mylocation) < 100){
                            Log.d("todaysmeeting", "within range" )

                            dialogue.setTitle("Select a meeting")
                            mylist.add(meetingNo)
                            myadapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mylist);

                            list.adapter = myadapter

                            list.setOnItemClickListener {adapterView, view, i, l ->
                                Log.d("todaysmeeting", "in onItemSelected" )
                                var meetingNo = mylist.get(i)

                                // list.getChildAt(i).disable()
                                adapterView.disable()
                                ad?.dismiss()

                                viewModel.getMeetingPassword(meetingNo)
                                        .subscribe({

                                            enterThePasswordDialogue(meetingNo , it)

                                        })

                            }
                        }

                    })


                }
                .doOnTerminate {
                    Log.d("todaysmeeting" ,"in  doOnTerminate")
                }
                .doOnComplete {
                    Log.d("todaysmeeting" ,"in  doOnComplete")
                    myadapter =  ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mylist);
                    list.adapter = myadapter

                }
                .subscribe({

                    Log.d("todaysmeeting" ,"in  subscribe")
                }
                )



    }


    public fun joinToVoteDialogue(){
        val dialogue = AlertDialog.Builder(activity)
        dialogue.setTitle("Select a meeting or Enter the meeting id ")

        val meeting_number_et = EditText(activity)

        val list = ListView(activity)
       // var mylist = ArrayList(Arrays.asList(*resources.getStringArray(R.array.projectCategory_list)))
        val mylist = arrayListOf<String>()
        var myadapter =  ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mylist);

        var locationListner = CurrentLocationListener.getInstance(context)
        locationListner.getLastLocation()

        var mylocation = LatLong()
        locationListner
                .observeOnce(this, Observer
                { var location = it as Location
                    Log.d("location ", " my location latitude is " +  location.latitude  )
                    Log.d("location ", "in  location longitude is " +  location.longitude  )

                     mylocation = LatLong(location.latitude ,location.longitude)

                })

        viewModel.getListOfTodaysMeeting()
                .map {
                    Log.d("todaysmeeting", "meeting was " + it)
                    var meetingNo = it
                    viewModel.getLocationOfAMeeting(it).subscribe({
                            var meetinglocation = it

                        if(findDistance(meetinglocation ,mylocation) < 100){
                            Log.d("todaysmeeting", "within range" )

                            mylist.add(meetingNo)
                            myadapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mylist);
                            list.adapter = myadapter

                            list.setOnItemClickListener {adapterView, view, i, l ->
                                Log.d("todaysmeeting", "in onItemSelected" )
                                var meetingNo = mylist.get(i)

                                // list.getChildAt(i).disable()
                                adapterView.disable()
                                myalerDialog?.dismiss()

                                viewModel.getMeetingPassword(meetingNo)
                                        .subscribe({

                                            enterThePasswordDialogue(meetingNo , it)

                                        })

                            }
                    }

                    })


                }
                .doOnTerminate {
                    Log.d("todaysmeeting" ,"in  doOnTerminate")
                }
                .doOnComplete {
                    Log.d("todaysmeeting" ,"in  doOnComplete")
                    myadapter =  ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mylist);
                    list.adapter = myadapter

                }
                .subscribe({

                    Log.d("todaysmeeting" ,"in  subscribe")
                }
                )



        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
       // layout.addView(list)
       // layout.addView(meeting_number_et)

        dialogue.setView(meeting_number_et)

        dialogue.setPositiveButton("Enter ", DialogInterface.OnClickListener { dialog, id ->
            var meeting_no = meeting_number_et.text.toString()
            Log.d("mytag" , "meeting_no entered was "+meeting_no );
            Log.d("mytag" , "inside the setpositive button " );
            if (meeting_no == "") {
                Toast.makeText(activity, "Enter the meeting number", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }


            // checking whether user has already voted, if yes then dont let them vote again.
            viewModel.getMeetingStatusRef(meeting_no).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("mytag" , " onDataChange")
                    Log.d("mytag", snapshot.getKey())
                    var boo = true

                    if(!snapshot.exists())
                    {
                        var toast =  Toast.makeText(activity, "No meeting with meeting id " + meeting_no +" is active for voting", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show()
                    }else{
                        boo = snapshot.getValue() as Boolean
                        Log.d("mytag", "boo was" + boo)

                        if(boo){


                            /*
                            val intent = Intent(activity, JoinToVote::class.java)
                            intent.putExtra("MeetingNo", meeting_no)
                            startActivity(intent)
                            */

                            viewModel.getMeetingPassword(meeting_no)
                                    .subscribe({

                                        enterThePasswordDialogue(meeting_no , it)
                                        dialog.dismiss()
                                    })

                        }else{
                            var toast = Toast.makeText(activity, "Voting for meeting " + meeting_no +" is not active currently", Toast.LENGTH_LONG)
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show()
                        }

                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("mytag" , " error")
                }
            });

        })

        dialogue.setNegativeButton("Cancel ") { dialog, id -> dialog.dismiss() }

         myalerDialog = dialogue.create()




        //Show the dialog
        myalerDialog?.show()


    }

     fun enterThePasswordDialogue(meetingNo : String ,meeting_password : String){


        val dialogue = AlertDialog.Builder(activity)
        dialogue.setTitle("Enter the Password ")

         val myHexColor = "#4422CC22"

        val meeting_password_et = EditText(activity)
        meeting_password_et.setBackgroundColor(Color.parseColor(myHexColor))
         meeting_password_et.setPadding(30,30,10,10);

         val meetingnumbertv = TextView(activity)
         meetingnumbertv.text = "Meeting id : "+ meetingNo
         meetingnumbertv.setPadding(30,30,10,10);
         meetingnumbertv.setBackgroundColor(Color.parseColor(myHexColor))

         val layout = LinearLayout(context)
         layout.orientation = LinearLayout.VERTICAL
         layout.addView(meetingnumbertv)
         layout.addView(meeting_password_et)

         dialogue.setView(layout)

        dialogue.setPositiveButton("Enter ", DialogInterface.OnClickListener { dialog, id ->
            var password_entered = meeting_password_et.text.toString()

            if (password_entered == "") {
                Toast.makeText(activity, "Enter the meeting password", Toast.LENGTH_SHORT).show()

            }
            else if(! password_entered.equals(meeting_password)){
                Toast.makeText(activity, "Wrong password", Toast.LENGTH_SHORT).show()

            }else{

                val intent = Intent(activity, JoinToVote::class.java)
                intent.putExtra("MeetingNo", meetingNo)
                startActivity(intent)
                return@OnClickListener
            }



        })

        dialogue.setCancelable(false)

        dialogue.setNegativeButton("Cancel ") { dialog, id -> dialog.dismiss() }

        val myalertobject = dialogue.create()
        //Show the dialog
        myalertobject.show()


    }

    fun setUpUI() {
        txt_header.text = "Join a meeting"
    }


}

