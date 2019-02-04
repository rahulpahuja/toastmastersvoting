package myoo.votingapp.Utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle



inline fun <reified T> Activity.open(bundle: (Bundle) -> Unit = {}) {
    val intent = Intent(this, T::class.java)

    val bundle1 = Bundle()
    bundle(bundle1)
    intent.putExtras(bundle1)

    startActivity(intent)
}