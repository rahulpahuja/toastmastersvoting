package myoo.votingapp.Utils

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat



class Permission {

    companion object {
        private const val PERMISSION_RESULT = 134
    }

    private var work: (() -> Unit)? = null


    fun doWorkOnPermission(vararg permission: String, activity: Activity, work: () -> Unit) {

        if (!activity.isAllAccepted(*permission)) {
            ActivityCompat.requestPermissions(activity,
                    permission,
                    PERMISSION_RESULT)
            this.work = work
        } else {
            work.invoke()
        }


    }

    private fun Activity.isAllAccepted(vararg permission: String): Boolean {
        var allAccepted = true
        for (value in permission) {
            allAccepted = ContextCompat.checkSelfPermission(this,
                    value) == PackageManager.PERMISSION_GRANTED

            if (!allAccepted) break

        }

        return allAccepted

    }

    fun onRequestPermissionsResult(requestCode: Int,
                                   permissions: Array<out String>,
                                   grantResults: IntArray) {

        if (requestCode == PERMISSION_RESULT) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                work?.invoke()
            }
        }

    }
}
