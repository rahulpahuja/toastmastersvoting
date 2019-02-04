package myoo.votingapp.Model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
class LatLong(var latitude: Double = 0.0,
                      var longitude: Double = 0.0
                     ): Parcelable