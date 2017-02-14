/*
 * Twittnuker - Twitter client for Android
 *
 * Copyright (C) 2013-2016 vanita5 <mail@vanit.as>
 *
 * This program incorporates a modified version of Twidere.
 * Copyright (C) 2012-2016 Mariotaku Lee <mariotaku.lee@gmail.com>
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

package de.vanita5.twittnuker.model.util

import android.support.annotation.FloatRange
import de.vanita5.twittnuker.library.twitter.model.DMResponse
import de.vanita5.twittnuker.library.twitter.model.DirectMessage
import de.vanita5.twittnuker.model.ParcelableMedia
import de.vanita5.twittnuker.model.ParcelableMessage
import de.vanita5.twittnuker.model.ParcelableMessage.MessageType
import de.vanita5.twittnuker.model.UserKey
import de.vanita5.twittnuker.model.message.MessageExtras
import de.vanita5.twittnuker.model.message.StickerExtras
import de.vanita5.twittnuker.util.InternalTwitterContentUtils

/**
 *
 */
object ParcelableMessageUtils {

    fun fromMessage(accountKey: UserKey, message: DirectMessage, outgoing: Boolean,
            @FloatRange(from = 0.0, to = 1.0) sortIdAdj: Double = 0.0): ParcelableMessage {
        val result = message(accountKey, message, sortIdAdj)
        result.is_outgoing = outgoing
        if (outgoing) {
            result.conversation_id = outgoingConversationId(message.senderId, message.recipientId)
        } else {
            result.conversation_id = incomingConversationId(message.senderId, message.recipientId)
        }
        return result
    }

    fun fromEntry(accountKey: UserKey, entry: DMResponse.Entry): ParcelableMessage? {
        when {
            entry.message != null -> {
                return ParcelableMessage().apply { applyMessage(accountKey, entry.message) }
            }
            entry.conversationCreate != null -> {
                return ParcelableMessage().apply { applyConversationCreate(accountKey, entry.conversationCreate) }
            }
        }
        return null
    }

    fun incomingConversationId(senderId: String, recipientId: String): String {
        return "$recipientId-$senderId"
    }

    fun outgoingConversationId(senderId: String, recipientId: String): String {
        return "$senderId-$recipientId"
    }

    private fun ParcelableMessage.applyMessage(accountKey: UserKey, message: DMResponse.Entry.Message) {
        this.commonEntry(accountKey, message)

        val data = message.messageData

        this.sender_key = UserKey(data.senderId.toString(), accountKey.host)
        this.recipient_key = UserKey(data.recipientId.toString(), accountKey.host)
        this.is_outgoing = this.sender_key == accountKey

        val (type, extras, media) = typeAndExtras(data)
        val (text, spans) = InternalTwitterContentUtils.formatDirectMessageText(data)
        this.message_type = type
        this.text_unescaped = text
        this.extras = extras
        this.spans = spans
        this.media = media
    }

    private fun ParcelableMessage.applyConversationCreate(accountKey: UserKey, message: DMResponse.Entry.Message) {
        this.commonEntry(accountKey, message)
        this.message_type = MessageType.CONVERSATION_CREATE
        this.is_outgoing = false
    }

    private fun ParcelableMessage.commonEntry(accountKey: UserKey, message: DMResponse.Entry.Message) {
        this.account_key = accountKey
        this.id = message.id.toString()
        this.conversation_id = message.conversationId
        this.message_timestamp = message.time
        this.local_timestamp = this.message_timestamp
        this.sort_id = this.message_timestamp
    }

    private fun message(
            accountKey: UserKey,
            message: DirectMessage,
            @FloatRange(from = 0.0, to = 1.0) sortIdAdj: Double = 0.0
    ): ParcelableMessage {
        val result = ParcelableMessage()
        result.account_key = accountKey
        result.id = message.id
        result.sender_key = UserKeyUtils.fromUser(message.sender)
        result.recipient_key = UserKeyUtils.fromUser(message.recipient)
        result.message_timestamp = message.createdAt.time
        result.local_timestamp = result.message_timestamp
        result.sort_id = result.message_timestamp + (499 * sortIdAdj).toLong()

        val (type, extras) = typeAndExtras(message)
        val (text, spans) = InternalTwitterContentUtils.formatDirectMessageText(message)
        result.message_type = type
        result.extras = extras
        result.text_unescaped = text
        result.spans = spans
        result.media = ParcelableMediaUtils.fromEntities(message)
        return result
    }

    private fun typeAndExtras(message: DirectMessage): Pair<String, MessageExtras?> {
        val singleUrl = message.urlEntities?.singleOrNull()
        if (singleUrl != null) {
            if (singleUrl.expandedUrl.startsWith("https://twitter.com/i/stickers/image/")) {
                return Pair(MessageType.STICKER, StickerExtras(singleUrl.expandedUrl))
            }
        }
        return Pair(MessageType.TEXT, null)
    }

    private fun typeAndExtras(data: DMResponse.Entry.Message.Data): Triple<String, MessageExtras?, Array<ParcelableMedia>?> {
        val attachment = data.attachment ?: return Triple(MessageType.TEXT, null, null)
        when {
            attachment.photo != null -> {
                val photo = attachment.photo
                val media = arrayOf(ParcelableMediaUtils.fromMediaEntity(photo))
                return Triple(MessageType.TEXT, null, media)
            }
            attachment.sticker != null -> {
                val sticker = attachment.sticker
                val image = sticker.images["size_2x"] ?: sticker.images.values.firstOrNull() ?:
                        return Triple(MessageType.TEXT, null, null)
                val extras = StickerExtras(image.url)
                extras.displayName = sticker.displayName
                return Triple(MessageType.STICKER, extras, null)
            }
        }
        return Triple(MessageType.TEXT, null, null)
    }
}