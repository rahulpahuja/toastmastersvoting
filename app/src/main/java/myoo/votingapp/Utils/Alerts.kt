package myoo.votingapp.Utils


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context

fun Context.showAlert(message: String?, work: () -> Unit = { }) {

    if (message.isNullOrEmpty()) return

    val builder = AlertDialog.Builder(this)

    builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok", { dialog, id ->
                //do things
                work.invoke()
                dialog.dismiss()
            })

    val alert = builder.create()
    alert.show()
    alert.getButton(Dialog.BUTTON_NEGATIVE).setAllCaps(false)
    alert.getButton(Dialog.BUTTON_POSITIVE).setAllCaps(false)

}

fun Context.showConfirmAlert(message: String?, positiveText: String?
                             , negativetext: String?
                             , onConfirmed: () -> Unit = {}
                             , onCancel: () -> Unit = { }) {

    if (message.isNullOrEmpty()) return

    val builder = AlertDialog.Builder(this)

    builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton(positiveText, { dialog, id ->
                //do things

                onConfirmed.invoke()
                dialog.dismiss()
            })
            .setNegativeButton(negativetext, { dialog, id ->
                //do things

                onCancel.invoke()
                dialog.dismiss()
            })

    val alert = builder.create()
    alert.show()
    alert.getButton(Dialog.BUTTON_NEGATIVE).setAllCaps(false)
    alert.getButton(Dialog.BUTTON_POSITIVE).setAllCaps(false)

}