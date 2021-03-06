/*
 * Twittnuker - Twitter client for Android
 *
 * Copyright (C) 2013-2017 vanita5 <mail@vanit.as>
 *
 * This program incorporates a modified version of Twidere.
 * Copyright (C) 2012-2017 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.vanita5.twittnuker.view.holder.message

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.list_item_message_entry.view.*
import de.vanita5.twittnuker.R
import de.vanita5.twittnuker.adapter.MessagesEntriesAdapter
import de.vanita5.twittnuker.extension.loadProfileImage
import de.vanita5.twittnuker.extension.model.getSummaryText
import de.vanita5.twittnuker.extension.model.getTitle
import de.vanita5.twittnuker.extension.model.notificationDisabled
import de.vanita5.twittnuker.extension.model.timestamp
import de.vanita5.twittnuker.model.ParcelableMessageConversation
import de.vanita5.twittnuker.model.ParcelableMessageConversation.ConversationType


class MessageEntryViewHolder(itemView: View, val adapter: MessagesEntriesAdapter) : RecyclerView.ViewHolder(itemView) {

    private val content by lazy { itemView.content }
    private val time by lazy { itemView.time }
    private val name by lazy { itemView.name }
    private val text by lazy { itemView.text }
    private val profileImage by lazy { itemView.profileImage }
    private val typeIndicator by lazy { itemView.typeIndicator }
    private val stateIndicator by lazy { itemView.stateIndicator }
    private val readIndicator by lazy { itemView.readIndicator }
    private val unreadCount by lazy { itemView.unreadCount }

    init {
        val textSize = adapter.textSize
        name.setPrimaryTextSize(textSize * 1.05f)
        name.setSecondaryTextSize(textSize * 0.95f)
        text.textSize = textSize
        time.textSize = textSize * 0.85f

        profileImage.style = adapter.profileImageStyle

        itemView.setOnClickListener {
            adapter.listener?.onConversationClick(layoutPosition)
        }
        profileImage.setOnClickListener {
            adapter.listener?.onProfileImageClick(layoutPosition)
        }
    }

    fun display(conversation: ParcelableMessageConversation) {
        if (adapter.drawAccountColors) {
            content.drawEnd(conversation.account_color)
        }else {
            content.drawEnd()
        }
        val (name, secondaryName) = conversation.getTitle(itemView.context,
                adapter.userColorNameManager, adapter.nameFirst)
        this.time.time = conversation.timestamp
        this.name.name = name
        this.name.screenName = secondaryName
        this.name.updateText(adapter.bidiFormatter)
        this.text.text = conversation.getSummaryText(itemView.context, adapter.userColorNameManager,
                adapter.nameFirst)
        if (conversation.is_outgoing) {
            readIndicator.visibility = View.VISIBLE
            readIndicator.setImageResource(R.drawable.ic_message_type_outgoing)
        } else {
            readIndicator.visibility = View.GONE
        }
        if (conversation.conversation_type == ConversationType.ONE_TO_ONE) {
            typeIndicator.visibility = View.GONE
        } else {
            typeIndicator.visibility = View.VISIBLE
        }
        if (conversation.notificationDisabled) {
            stateIndicator.visibility = View.VISIBLE
            stateIndicator.setImageResource(R.drawable.ic_message_type_speaker_muted)
        } else {
            stateIndicator.visibility = View.GONE
        }
        adapter.requestManager.loadProfileImage(adapter.context, conversation,
                adapter.profileImageStyle, profileImage.cornerRadius,
                profileImage.cornerRadiusRatio).into(profileImage)
        if (conversation.unread_count > 0) {
            unreadCount.visibility = View.VISIBLE
            unreadCount.text = conversation.unread_count.toString()
        } else {
            unreadCount.visibility = View.GONE
        }
    }

    companion object {
        const val layoutResource = R.layout.list_item_message_entry
    }

}
