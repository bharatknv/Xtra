package com.exact.twitch.ui.pagers

import android.content.Context
import androidx.fragment.app.FragmentManager

import com.exact.twitch.R

abstract class MediaPagerAdapter(private val context: Context, fm: FragmentManager) : ItemAwareFragmentPagerAdapter(fm) {

    override fun getPageTitle(position: Int): CharSequence? {
        val id = when (position) {
            0 -> R.string.live
            1 -> R.string.videos
            2 -> R.string.clips
            else -> 0
        }
        return context.getString(id)
    }

    override fun getCount(): Int = 3
}
