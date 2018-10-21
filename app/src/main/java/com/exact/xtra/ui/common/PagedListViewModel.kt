package com.exact.xtra.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.exact.xtra.repository.Listing
import com.exact.xtra.repository.LoadingState
import io.reactivex.disposables.CompositeDisposable

abstract class PagedListViewModel<T> : ViewModel() {

    private val result = MutableLiveData<Listing<T>>()
    lateinit var list: LiveData<PagedList<T>>
        private set
    lateinit var loadingState: LiveData<LoadingState>
        private set
    lateinit var pagingState: LiveData<LoadingState>
        private set
    val loadedInitial = MediatorLiveData<Boolean>()

    protected val compositeDisposable = CompositeDisposable()
    //TODO sometimes list doesn't show, probably because submits empty list <- TRUE
    internal fun loadData(listing: Listing<T>, override: Boolean = false) {
        if (result.value == null || override) {
            if (loadedInitial.hasObservers()) {
                loadedInitial.removeSource(loadingState)
            }
            result.value = listing
            list = switchMap(result) { it.pagedList }
            loadingState = switchMap(result) { it.loadingState }
            pagingState = switchMap(result) { it.pagingState }
            loadedInitial.apply {
                addSource(loadingState) {
                    if (value == null) {
                        if (it == LoadingState.LOADED) postValue(true)
                        else if (it == LoadingState.FAILED) postValue(false)
                    }
                }
            }
        }
    }

    fun refresh() {
        result.value?.refresh?.invoke()
    }

    fun retry() {
        result.value?.retry?.invoke()
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}