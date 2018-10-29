package com.exact.xtra.ui.downloads


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exact.xtra.model.OfflineVideo
import com.exact.xtra.repository.OfflineRepository
import com.iheartradio.m3u8.Encoding
import com.iheartradio.m3u8.Format
import com.iheartradio.m3u8.PlaylistParser
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
            val playlist = PlaylistParser(file.inputStream(), Format.EXT_M3U, Encoding.UTF_8).parse()
            for (track in playlist.mediaPlaylist.tracks) {
                File(track.uri).delete()
            }
            val directory = file.parentFile
            if (directory.list().size == 1) {
                file.delete()
                directory.delete()
            } else {
                file.delete()
            }
        } else {
            file.delete()
        }
    }
}
