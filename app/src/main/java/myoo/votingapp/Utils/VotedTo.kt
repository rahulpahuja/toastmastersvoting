package myoo.votingapp.Utils

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ServerValue


class VotedTo(var votedTo: String) {
    val votedOn: Any = ServerValue.TIMESTAMP

}