package myoo.votingapp.viewmodel

import com.google.firebase.storage.StorageReference


class Storage(private val storage: StorageReference){

    fun candidatePicture(): StorageReference {
       return storage.child("Candidate")
    }

    fun evalutaor(): StorageReference {
        return candidatePicture().child("Evalutors")
    }
    fun prepareSpeaker(): StorageReference {
        return candidatePicture().child("prepareSpeaker")
    }
    fun roleTaker(): StorageReference {
        return candidatePicture().child("roleTaker")
    }

    fun table(): StorageReference {
        return candidatePicture().child("Table")
    }





}