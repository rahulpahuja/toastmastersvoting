package myoo.votingapp.Utils

import android.app.Activity
import android.support.v4.app.Fragment
import android.widget.Toast



fun Activity.showToast(string: String){
     showToastt(this,string)
}

fun Fragment.showToast(string: String){
    activity?.showToast(string)
}

 fun showToastt(activity: Activity,string: String) {
    Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
}