package myoo.votingapp.Model

import android.graphics.Bitmap
import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
class PreparedSpeakerDetail(var name: String="",
                            var title: String="",
                            var project: String="",
                            var isDisabled: Boolean=false,
                            @get: Exclude
                            @set: Exclude
                            var image: File? = null,
                            var imageUrl: String ="",  override var uniqueId: String ="")// this.photo = photo;
    : Parcelable, UniqueId {

    var meetingNo: String = ""


}
