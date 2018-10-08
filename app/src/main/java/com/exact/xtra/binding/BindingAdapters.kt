package com.exact.xtra.binding

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.exact.xtra.GlideApp

@SuppressLint("CheckResult")
@BindingAdapter("imageUrl", "circle", requireAll = false)
fun loadImage(imageView: ImageView, url: String?, circle: Boolean) {
    val request = GlideApp.with(imageView.context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .transition(DrawableTransitionOptions.withCrossFade())
    if (circle) {
        request.circleCrop()
    }
    request.into(imageView)
}

@BindingAdapter("visible")
fun setVisible(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

//@BindingAdapter("visible")
//fun setVisible(view: View, liveData: MediatorLiveData)

@BindingAdapter("enabled")
fun setEnabled(view: View, enabled: Boolean) {
    view.isEnabled = enabled
}