package myoo.votingapp.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.firebase.database.DatabaseReference
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import io.reactivex.Single
import myoo.votingapp.Utils.retrive
import myoo.votingapp.Model.Meeting
import myoo.votingapp.Utils.UserAuthenticate


class MeetingViewModel(private val userAuthenticate: UserAuthenticate,
                       private val repository: MeetingRepository, private val reference: FirebaseReference) : AppViewModel<List<Meeting>>() {


   // private val repository: MeetingRepository) : AppViewModel<List<Meeting>>() {
    // this was there earlier

    private val meetings = HashMap<String, Meeting>()

    private val mymeetings = HashMap<String, String>()

    val mymeetingsresponse = MutableLiveData<List<String>>()
    val myresponse = MutableLiveData<HashMap<String, String>>()
    val meetingDetails = MutableLiveData<Meeting>()





    fun createMeeting(meeting: Meeting): Single<Meeting> {
        return repository.creatMyMeeting(meeting)
                .compose(observeLoadingSingle())
    }

    fun authenticateUser(passCode: String): Boolean {
        if (userAuthenticate.autenticateUser(passCode)) {
            return true
        } else {
            error.value = "Enter Password is Incorrect"
            return false
        }
    }

    fun meetingBasicDetails(meetingNo : String) : DatabaseReference{

        return  reference.getMeetingDetailsRef(meetingNo)

    }

    fun getMeetingDetails(MeetingNo : String) {
      //  Log.d("mytag", "calling the method to retrieve meeting details ")
                reference.getMeetingDetailsRef(MeetingNo).retrive<Meeting>()
                .subscribe {
                    Log.d("mytag", "in subscribe callback with " + it.meetingNo )
                    meetingDetails.value = it }

    }

    fun retriveAllMeetings() {

        repository.retirveAllMeetings()
                .map { it -> putMeetingsInMap(it) }
                .subscribe { response.value = it }


    }

    fun retriveAllMyMeetings() {

        repository.retrieveOnlyMyMeetings()
                .map { it -> putMeetingsInMyMap(it) }
                .subscribe { mymeetingsresponse.value = it }

    }

    fun getMyMeetings(){

        myresponse.value = repository.retrieveMyMeetings()
    }




    private fun putMeetingsInMyMap(it: RxFirebaseChildEvent<String>): List<String> {
        when (it.eventType) {
            RxFirebaseChildEvent.EventType.ADDED -> mymeetings[it.value] = it.value
            RxFirebaseChildEvent.EventType.REMOVED -> mymeetings.remove(it.value)
            RxFirebaseChildEvent.EventType.CHANGED ->  mymeetings[it.value] = it.value
        }


        return mymeetings.values.toList()
    }

    private fun putMeetingsInMap(it: RxFirebaseChildEvent<Meeting>): List<Meeting> {
        when (it.eventType) {
            RxFirebaseChildEvent.EventType.ADDED -> meetings[it.value.meetingId] = it.value
            RxFirebaseChildEvent.EventType.REMOVED -> meetings.remove(it.value.meetingId)
            RxFirebaseChildEvent.EventType.CHANGED ->  meetings[it.value.meetingId] = it.value
        }


        return meetings.values.toList()
    }


}