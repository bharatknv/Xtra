package com.exact.xtra.ui.common

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.exact.xtra.GlideApp
import com.exact.xtra.R
import com.exact.xtra.databinding.ChatListItemBinding
import com.exact.xtra.model.chat.ChatMessage
import com.exact.xtra.model.chat.Emote
import com.exact.xtra.ui.DataBoundViewHolder
import java.util.Random
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.forEach
import kotlin.collections.map


class ChatAdapter : ListAdapter<ChatMessage, DataBoundViewHolder<ChatListItemBinding>>(
        object : DiffUtil.ItemCallback<ChatMessage>() {
            override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
                return oldItem.message == newItem.message
            }

        }) {

    private val twitchColors = intArrayOf(-65536, -16776961, -16744448, -5103070, -32944, -6632142, -47872, -13726889, -2448096, -2987746, -10510688, -14774017, -38476, -7722014, -16711809)
    private val random = Random()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<ChatListItemBinding> {
        return DataBoundViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.chat_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: DataBoundViewHolder<ChatListItemBinding>, position: Int) {
        val chatMessage = getItem(position)
        val builder = SpannableStringBuilder()
        val badgesUrl = "https://static-cdn.jtvnw.net/chat-badges/"
        val images = ArrayList<Emote>()
        var index = 0
        chatMessage.badges?.forEach { (id, version) ->
            val url: String? = when (id) {
                "admin" -> badgesUrl + "admin.png"
                "bits" -> {
                    val count = version.toInt()
                    val color = when {
                        count < 100 -> "gray"
                        count < 1000 -> "purple"
                        count < 5000 -> "green"
                        count < 10000 -> "blue"
                        else -> "red"
                    }
                    "https://static-cdn.jtvnw.net/bits/dark/static/$color/1" //TODO change theme based on app theme
                }
                "broadcaster" -> badgesUrl + "broadcaster.png"
                "global_mod" -> badgesUrl + "globalmod.png"
                "moderator" -> badgesUrl + "mod.png"
                "subscriber" -> chatMessage.subscriberBadge?.imageUrl2x
                "staff" -> badgesUrl + "staff.png"
                "turbo" -> badgesUrl + "turbo.png"
                "sub-gifter" -> "https://static-cdn.jtvnw.net/badges/v1/4592e9ea-b4ca-4948-93b8-37ac198c0433/1"
                "premium" -> "https://static-cdn.jtvnw.net/badges/v1/a1dd5073-19c3-4911-8cb4-c464a7bc1510/1"
                "partner" -> "https://static-cdn.jtvnw.net/badges/v1/d12a2e27-16f6-41d0-ab77-b780518f00a3/2"
                "clip-champ" -> "https://static-cdn.jtvnw.net/badges/v1/f38976e0-ffc9-11e7-86d6-7f98b26a9d79/2"
                else -> null
            }
            url?.let {
                builder.append(" ")
                images.add(Emote(url, index, ++index))
            }
        }
        //TODO add if mentions user make message red
        builder.append(chatMessage.displayName).append(": ").append(chatMessage.message)
        val userColor = chatMessage.color
        val color = when (userColor) {
            null -> getRandomColor().also { chatMessage.color = it.toString() }
            else -> if (userColor.startsWith("#")) Color.parseColor(userColor) else userColor.toInt()
        }
        val userNameLength = chatMessage.displayName.length
        builder.setSpan(ForegroundColorSpan(color), index, index + userNameLength, SPAN_EXCLUSIVE_EXCLUSIVE)
        chatMessage.emotes?.let {
            val copy = it.map { e -> e.copy() }
            index += userNameLength + 2
            val emotesUrl = "https://static-cdn.jtvnw.net/emoticons/v1/"
            for (e in copy) {
                builder.replace(index + e.begin, index + e.end + 1, " ")
                val length = e.end - e.begin
                for (e1 in copy) {
                    if (e.begin < e1.begin) {
                        e1.begin -= length
                        e1.end -= length
                    }
                }
                e.end -= length
            }
            copy.forEach { (id, begin, end) -> images.add(Emote("$emotesUrl$id/2.0", index + begin, index + end + 1)) }
        }
        holder.binding.message = builder
        loadImages(holder.binding, images)
    }

    private fun loadImages(binding: ChatListItemBinding, images: List<Emote>) {
        images.forEach { (id, begin, end) ->
            GlideApp.with(binding.textView)
                    .load(id)
                    .override(40, 40)
                    .into(object : SimpleTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            resource.setBounds(0, 0, resource.intrinsicWidth, resource.intrinsicHeight)
                            binding.message?.setSpan(ImageSpan(resource), begin, end, SPAN_EXCLUSIVE_EXCLUSIVE)
                            binding.textView.text = binding.message//TODO notify differently
                        }
                    })
        }
    }

    private fun getRandomColor(): Int = twitchColors[random.nextInt(twitchColors.size)]
}