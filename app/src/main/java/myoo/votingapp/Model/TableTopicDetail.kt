package myoo.votingapp.Model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.io.Serializable

@Parcelize
class TableTopicDetail(var name: String="",
                       var isDisabled: Boolean=false,
                       @get: Exclude
                       @set: Exclude
                       var image: File? = null,
                       var imageUrl: String? ="",
                       var meetingNo: String ="", override var uniqueId: String="") : Parcelable,UniqueId