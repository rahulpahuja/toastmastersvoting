package myoo.votingapp.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import myoo.votingapp.Utils.DiffCalculate


@Parcelize
data class Meeting(var meetingId: String="",
                   val meetingNo: String="",
                   val createdby: String="",
                   val isPoolingStarted: Boolean=false) : DiffCalculate<Meeting>, Parcelable {
    override fun areBothSame(newItem: Meeting): Boolean {
        return meetingId == newItem.meetingId
    }

    override fun areContentSame(newItem: Meeting): Boolean {
        return meetingNo == newItem.meetingNo && meetingId == newItem.meetingId
    }
}