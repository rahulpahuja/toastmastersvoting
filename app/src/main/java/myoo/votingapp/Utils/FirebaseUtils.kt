package myoo.votingapp.Utils



import android.net.Uri
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import durdinapps.rxfirebase2.DataSnapshotMapper
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import durdinapps.rxfirebase2.RxFirebaseDatabase
import durdinapps.rxfirebase2.RxFirebaseStorage
import io.reactivex.*
import io.reactivex.subjects.PublishSubject
import java.io.File

inline fun <reified T> Query.retrive(): Maybe<T> {
    return RxFirebaseDatabase.observeSingleValueEvent(
            this, T::class.java)
}

// this methods retrieves the childs one by one.
inline fun <reified T> Query.retriveChilds(): Flowable<RxFirebaseChildEvent<T>> {
    return RxFirebaseDatabase.observeChildEvent(this, T::class.java, BackpressureStrategy.BUFFER)
}

// this methods retrieves the whole list in one go
inline fun <reified T> Query.retriveList(): Single<MutableList<T>> {

    return RxFirebaseDatabase.observeSingleValueEvent(this, DataSnapshotMapper.listOf(T::class.java))
            .toSingle(listOf<T>())
}

inline fun <reified T> Query.retriveObserve(): Flowable<MutableList<T>> {
    return RxFirebaseDatabase.observeValueEvent(this, DataSnapshotMapper.listOf(T::class.java))
}

fun Query.observeCount(): Flowable<Pair<String?, Int>> {
    return RxFirebaseDatabase.observeValueEvent(this,BackpressureStrategy.BUFFER).map {
       Pair(it.key,it.children.count())
    }
}

 fun <T> DatabaseReference.save(value :T): Completable{

    return RxFirebaseDatabase.setValue(this,value)

}

fun  DatabaseReference.remove(): Completable{
    return RxFirebaseDatabase.setValue(this, null)

}

fun StorageReference.upload(file: File?,
                            progress: PublishSubject<Double>? = null,
                            defaultUrl: String = ""): Single<String> {

   return if (file == null){
       return Single.just(defaultUrl)
    }else {
        RxFirebaseStorage.putFile(this, Uri.fromFile(file))
                .map { progress?.onNext((100.0 * it.getBytesTransferred()) / it.getTotalByteCount()); it }
                .flatMap { retiriveLoadUrl(it) }
    }
}

private fun retiriveLoadUrl(storage: UploadTask.TaskSnapshot): Single<String> {
   return Single.create {emmiter->

        val downloadUrl = storage.storage.downloadUrl
       downloadUrl.addOnSuccessListener {
            emmiter.onSuccess(it.toString())
        }.addOnFailureListener {
           emmiter.onError(it)
       }

    }
}

fun Query.isExists(): Single<Boolean> {

    return Single.create<Boolean> { emitter ->


        addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                emitter.onSuccess(p0.exists())
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

}

