package myoo.votingapp.view.Activity

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.custom_actionbar.*
import myoo.votingapp.R
import myoo.votingapp.Utils.disable
import myoo.votingapp.Utils.enable
import myoo.votingapp.Utils.open
import myoo.votingapp.Utils.showAlert
import myoo.votingapp.Model.Meeting
import myoo.votingapp.viewmodel.MeetingViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * MainActivity : First screen for the app
 */
class AddNewMeetingActivity : AppCompatActivity() {

    //   private var mydb: DBHelper? = null

    private val userId by inject<String>(name = "userId")

    private val viewModel by viewModel<MeetingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //    mydb = DBHelper(this)
        setContentView(R.layout.activity_login)

        imageBackPress.visibility = View.VISIBLE
        txt_header.text = "Add New Meeting"
        imageBackPress.setOnClickListener { view -> finish() }

        observeResponse()

    }

    private fun observeResponse() {

        with(viewModel) {

            error.observe(this@AddNewMeetingActivity, Observer {
                it?.let { showAlert(it) }
                meeting_number.enable()
                password_et.disable()
            })
        }

    }

    fun login(v: View) {

        val meetingNo = meeting_number.text.toString()
        val passwordEnter = password_et.text.toString()

        if (viewModel.authenticateUser(passwordEnter)) {
            val meeting = Meeting("", meetingNo, userId, false)


            // Here check whether the meeting already exists. If yes, then dont create the meeting.

            // checking whether user has already voted, if yes then dont let them vote again.
            viewModel.meetingBasicDetails(meetingNo).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists())
                    {
                        var toast =  Toast.makeText(applicationContext, "Meeting number " + meetingNo +" is already taken. Try some different combination. ", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show()
                    }else{

                        viewModel.createMeeting(meeting)
                                .doOnError { enableEdt() }
                                .doOnSubscribe { disableedt() }
                                .doAfterSuccess { enableEdt() }
                                .subscribe({ meetingg ->
                                    open<PrepareAllVotingItemsScreen>() {
                                        //  it.putParcelable(PrepareAllVotingItemsScreen.MEETING, meetingg)
                                        it.putString(PrepareAllVotingItemsScreen.MeetingNo, meetingNo)
                                    }
                                    finish()
                                }, {})
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("mytag" , " error")
                }
            });




        }

        /*  SharedPreferences sharedpreferences = getSharedPreferences("votingapp", Context.MODE_PRIVATE);
        String password_value = sharedpreferences.getString("password", "smile");

        if (((EditText) (findViewById(R.id.meeting_number))).getText().toString().equals("")) {
            Toast.makeText(this, "Enter meeting number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (((EditText) (findViewById(R.id.password_et))).getText().toString().equals("")) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!((EditText) (findViewById(R.id.password_et))).getText().toString().equals(password_value)) {
            Toast.makeText(this, "Password incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sharedpreferences.edit();
        EditText meeting_numbere_et = (EditText) findViewById(R.id.meeting_number);

        String meeting_number = meeting_numbere_et.getText().toString();
        editor.putString("meeting_number", meeting_number);

        editor.commit();


        createNewTable(meeting_number);
        Intent intent = new Intent(getApplicationContext(), PrepareAllVotingItemsScreen.class);

        startActivity(intent);

        finish();*/
    }



    private fun disableedt() {
        meeting_number.disable()
        password_et.disable()
    }

    private fun enableEdt() {
        meeting_number.enable()
        password_et.enable()
    }

    /* private fun createNewTable(meeting_number: String): Boolean {

         return mydb!!.createVotingItemTable(meeting_number)

     }*/

    fun changePassword(v: View) {
        val intent = Intent(this@AddNewMeetingActivity, ForgetCodeActivity::class.java)
        startActivity(intent)

    }
}
