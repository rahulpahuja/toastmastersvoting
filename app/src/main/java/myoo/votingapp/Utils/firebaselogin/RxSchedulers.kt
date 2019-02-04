package myoo.votingapp.Utils.firebaselogin

import io.reactivex.Scheduler



abstract class RxSchedulers {

    abstract fun io(): Scheduler
    abstract fun main(): Scheduler
    abstract fun computation(): Scheduler


}