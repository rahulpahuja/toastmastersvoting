package myoo.votingapp.viewmodel

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject


abstract class AppViewModel<R> : ViewModel(), LifecycleObserver {

    protected val disposables = CompositeDisposable()
    val response = MutableLiveData<R>()
    val error = MutableLiveData<String>()

    val isLoading = MutableLiveData<Boolean>()

   // val isLoadingReactive = BehaviorSubject.create<Boolean>()

    init {

        /*
        isLoading.observeForever {
            isLoadingReactive.onNext(it ?: false)
        }

        */
    }


    protected fun <T> observeLoading(): FlowableTransformer<T, T> {
        return FlowableTransformer {
            it.doOnComplete { isLoading.value = false }
                    .doOnSubscribe { isLoading.value = true }
                    .doOnError {
                        isLoading.value = false
                        error.value = it.message
                    }
        }
    }




    protected fun observeLoadingComplete(): CompletableTransformer {
        Log.d("mytag" , "in observeLoadingComplete")
        return CompletableTransformer {
            it.doOnComplete {
                Log.d("mytag" , "in observeLoadingComplete doOnComplete")
                isLoading.value = false }
                    .doOnSubscribe {
                        Log.d("mytag" , "in observeLoadingComplete doOnSubscribe")
                        isLoading.value = true }
                    .doOnError {
                        isLoading.value = false
                        error.value = it.message
                    }
        }
    }




    protected fun <T> observeLoadingSingle(): SingleTransformer<T, T> {
        Log.d("mytag" , "in observeLoadingSingle")
        return SingleTransformer {
            it.doOnSuccess {
                Log.d("mytag" , "in observeLoadingSingle doOnSuccess")
                isLoading.value = false }
                    .doOnSubscribe {
                        Log.d("mytag" , "in observeLoadingSingle doOnSubscribe")
                        isLoading.value = true }
                    .doOnError {
                        error.value = it.message
                        isLoading.value = false

                    }
        }
    }


    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }


}