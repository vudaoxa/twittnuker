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

package de.vanita5.twittnuker.task

import android.content.ContentValues
import android.content.Context
import de.vanita5.twittnuker.R
import de.vanita5.twittnuker.constant.nameFirstKey
import de.vanita5.twittnuker.library.MicroBlog
import de.vanita5.twittnuker.library.MicroBlogException
import de.vanita5.twittnuker.library.twitter.model.User
import de.vanita5.twittnuker.model.AccountDetails
import de.vanita5.twittnuker.model.ParcelableUser
import de.vanita5.twittnuker.model.event.FriendshipTaskEvent
import de.vanita5.twittnuker.provider.TwidereDataStore.CachedRelationships
import de.vanita5.twittnuker.util.Utils

class DestroyUserMuteTask(context: Context) : AbsFriendshipOperationTask(context, FriendshipTaskEvent.Action.UNMUTE) {

    @Throws(MicroBlogException::class)
    override fun perform(twitter: MicroBlog, details: AccountDetails,
                         args: AbsFriendshipOperationTask.Arguments): User {
        return twitter.destroyMute(args.userKey.id)
    }

    override fun succeededWorker(twitter: MicroBlog,
                                 details: AccountDetails,
                                 args: AbsFriendshipOperationTask.Arguments, user: ParcelableUser) {
        val resolver = context.contentResolver
        // I bet you don't want to see this user in your auto complete list.
        val values = ContentValues()
        values.put(CachedRelationships.ACCOUNT_KEY, args.accountKey.toString())
        values.put(CachedRelationships.USER_KEY, args.userKey.toString())
        values.put(CachedRelationships.MUTING, false)
        resolver.insert(CachedRelationships.CONTENT_URI, values)
    }

    override fun showSucceededMessage(params: AbsFriendshipOperationTask.Arguments, user: ParcelableUser) {
        val nameFirst = kPreferences[nameFirstKey]
        val message = context.getString(R.string.unmuted_user, manager.getDisplayName(user,
                nameFirst))
        Utils.showInfoMessage(context, message, false)

    }

    override fun showErrorMessage(params: AbsFriendshipOperationTask.Arguments, exception: Exception?) {
        Utils.showErrorMessage(context, R.string.action_unmuting, exception, true)
    }
}