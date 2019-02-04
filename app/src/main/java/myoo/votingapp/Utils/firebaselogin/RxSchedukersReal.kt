package myoo.votingapp.Utils.firebaselogin

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class RxSchedulersReal : RxSchedulers() {
    override fun io(): Scheduler = Schedulers.io()

    override fun main() = AndroidSchedulers.mainThread()

    override fun computation() = Schedulers.computation()
}