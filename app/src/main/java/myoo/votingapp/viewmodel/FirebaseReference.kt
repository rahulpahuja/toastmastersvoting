package myoo.votingapp.viewmodel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference


class FirebaseReference(private val reference: DatabaseReference) {

    private val firebaseUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser


    val PREPARE_SPEAKAR = "preparedSpeaker"
    val ROLE_TAKER = "roleTaker"
    val EVALUTOR = "evaluators"
    val TABLE_TOPIC = "tableTopic"


    fun getUsersListRef(): DatabaseReference {
        return reference.child("users")
    }

    fun getcurrentuserID(): String {
        return firebaseUser?.uid ?: "0"
    }

    fun userPasscode(): DatabaseReference {
        return getUsersListRef().child(firebaseUser?.uid ?: "0").child("passcode")
    }

    fun meetings(): DatabaseReference {
        return reference.child("meeting")
    }

    fun meetingsListOfAUser(userId : String): DatabaseReference {
        //return reference.child(firebaseUser?.uid ?: "0").child("meeting")
        return  getUsersListRef().child(userId).child("meetingList")
    }


    fun getMeetingNumber(meetingId: String): DatabaseReference {
      //  Log.d("mytag", " in getMeetingNumber  with meeting id  "+ meetingId  )
        return meetings().child(meetingId).child("meetingNo")
    }

    fun getMeetingDetailsRef(meetingNo : String): DatabaseReference{
       // Log.d("mytag" ," in getMeetingDetailsRef with meetingNo " + meetingNo)
        return  meetings().child(meetingNo)
    }

    fun preparedSpeaker(meetingId: String): DatabaseReference {
        return meetingDetail(meetingId).child(PREPARE_SPEAKAR)
    }

     fun meetingDetail(meetingNo: String) =
            reference.child("meetingCandidates").child(meetingNo)


    fun isVotingActive(meetingId: String): DatabaseReference {
        return meetingDetail(meetingId).child("is_active")
    }

    fun getMeetingPasswordRef(meetingId: String): DatabaseReference{

        return  meetingDetail(meetingId).child("password")
    }

    fun getResultsRef(meetingId: String): DatabaseReference{

        return  reference.child("result").child(meetingId)
    }

    fun getLocationsRef(): DatabaseReference{

        return  reference.child("location")
    }

    fun roleTaker(meetingId: String): DatabaseReference {
        return meetingDetail(meetingId).child(ROLE_TAKER)
    }


    fun evaluators(meetingId: String): DatabaseReference {
        return meetingDetail(meetingId).child(EVALUTOR)
    }


    fun tableTopic(meetingId: String): DatabaseReference {
        return meetingDetail(meetingId).child(TABLE_TOPIC)
    }

    fun allTheVotesReference(): DatabaseReference {
        return reference.child("VotesCaptured")
    }

    fun voteCastedByMeForTableTopic(meetingId: String): DatabaseReference {
        return votesCastedByMeInAMeetingRef(meetingId).child(TABLE_TOPIC)
    }


    fun voteCastedByMeForPreaperedSpeaker(meetingId: String): DatabaseReference {
        return votesCastedByMeInAMeetingRef(meetingId).child(PREPARE_SPEAKAR)
    }


    fun voteCastedByMeForEvalutor(meetingId: String): DatabaseReference {
        return votesCastedByMeInAMeetingRef(meetingId).child(EVALUTOR)
    }


    fun voteCastedByMeForRoleTaker(meetingId: String): DatabaseReference {
        return votesCastedByMeInAMeetingRef(meetingId).child(ROLE_TAKER)
    }

    fun userViseVoteForMeetingRf(meetingId: String): DatabaseReference{

       return allTheVotesReference().child(meetingId)

    }

    fun votesCastedByMeInAMeetingRef(meetingId: String) = allTheVotesReference().child(meetingId).child(firebaseUser?.uid
            ?: "")

    fun updateVoteCountForPrepareSpeaker(meetingId: String): DatabaseReference {
        return resultOfAMeeting(meetingId).child(PREPARE_SPEAKAR)
    }

    fun updateVoteForRoleTaker(meetingId: String): DatabaseReference {
        return resultOfAMeeting(meetingId).child(ROLE_TAKER)
    }


    fun updateVoteForTableTopic(meetingId: String): DatabaseReference {
        return resultOfAMeeting(meetingId).child(TABLE_TOPIC)
    }


    fun updateVoteForEvalutor(meetingId: String): DatabaseReference {
        return resultOfAMeeting(meetingId).child(EVALUTOR)
    }


    fun resultOfAMeeting(meetingId: String): DatabaseReference {
        return resultRef().child(meetingId)
    }

    fun resultRef(): DatabaseReference {
        return reference.child("result")
    }
}