package com.exact.xtra.ui.downloads


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exact.xtra.model.OfflineVideo
import com.exact.xtra.repository.OfflineRepository
import java.io.File
import javax.inject.Inject

class DownloadsViewModel @Inject internal constructor(
        private val repository: OfflineRepository) : ViewModel() {

    val loaded = MutableLiveData<Boolean>()

    fun load() = repository.loadAll().also { loaded.postValue(true) }

    fun delete(video: OfflineVideo) {
        repository.delete(video)
        val file = File(video.url)
        if (video.vod) {
            file.parentFile.deleteRecursively()
        } else {
            file.delete()
        }
    }
}