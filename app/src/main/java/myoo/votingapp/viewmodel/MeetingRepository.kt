package myoo.votingapp.viewmodel

import android.util.Log
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import io.reactivex.Single
import myoo.votingapp.Utils.retrive
import myoo.votingapp.Utils.retriveChilds
import myoo.votingapp.Model.Meeting
import java.util.*


class MeetingRepository(private val reference: FirebaseReference) {
    var i = 1

     fun createNewMeeting(meeting: Meeting): Single<Meeting> {
        Log.d("mytag", "MeetingRepository -- createNewMeeting ");
        val databaseReference = reference.meetings().push()
        meeting.meetingId = databaseReference.key ?: ""

        var user = reference.getcurrentuserID();

        Log.d("mytag", "getUsersListRef was " + user);
        val userReference = reference.getUsersListRef().child(user).push()

        RxFirebaseDatabase.setValue(userReference, meeting)
                .toSingleDefault(meeting)

        Log.d("mytag", "meeting should have saved in users ");
        // here save the meeting id to getUsersListRef database too.


        return RxFirebaseDatabase.setValue(databaseReference, meeting)
                .toSingleDefault(meeting)


    }

    fun IntRange.random() =
            Random().nextInt((endInclusive + 1) - start) +  start

    fun creatMyMeeting(meeting: Meeting): Single<Meeting> {
        Log.d("mytag", "MeetingRepository -- createNewMeeting ");
        val databaseReference = reference.meetings().child(meeting.meetingNo)


        meeting.meetingId = databaseReference.key ?: ""

        var user = reference.getcurrentuserID();

        Log.d("mytag", "getUsersListRef was " + user);
        val userReference = reference.getUsersListRef().child(user).push()

        val meetingsCreatedByAUser = reference.getUsersListRef().child(user).child("meetingList").child(meeting.meetingNo)


        return RxFirebaseDatabase.setValue(meetingsCreatedByAUser, meeting.meetingNo)
                .toSingleDefault(meeting)
                .flatMap {
                    RxFirebaseDatabase.setValue(databaseReference, meeting)
                            .toSingleDefault(meeting)
                }.flatMap {

                    val meetingPasswordRef = reference.getMeetingPasswordRef(meeting.meetingId)
                    var password = ((10001..99999).random()).toString()

                     RxFirebaseDatabase.setValue(meetingPasswordRef, password).toSingleDefault(meeting)
                     }
    }

     fun retirveAllMeetings(): Flowable<RxFirebaseChildEvent<Meeting>> {
        //return reference.meetings().retriveChilds<Meeting>()

        return reference.meetings().retriveChilds<Meeting>()
    }

     fun retrieveMyMeetings(): HashMap<String, String> {
         val meetingsHashMap = HashMap<String, String>()
        var userID = reference.getcurrentuserID();
        Log.d("mytag", " in repository retrieveMyMeetings with userID " + userID )

        reference.meetingsListOfAUser(userID).retriveChilds<String>().map { meetingID ->
            Log.d("mytag", " got meeting id as "+ meetingID  )
            reference.getMeetingNumber(meetingID.value).retrive<String>()
                    .subscribe {
                        Log.d("mytag", " got meeting number as "+ it  )
                        meetingsHashMap.put(meetingID.value , it)}

        }.subscribe {
            Log.d("mytag", " doing nothing in outer subscribe "  )
        }


       return  meetingsHashMap
    }

     fun retrieveOnlyMyMeetings(): Flowable<RxFirebaseChildEvent<String>> {
        //return reference.meetings().retriveChilds<Meeting>()
        var userID = reference.getcurrentuserID();
        return reference.meetingsListOfAUser(userID).retriveChilds<String>()
    }


}