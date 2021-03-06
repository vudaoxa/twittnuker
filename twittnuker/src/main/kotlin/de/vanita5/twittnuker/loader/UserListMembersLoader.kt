/*
 *  Twittnuker - Twitter client for Android
 *
 *  Copyright (C) 2013-2017 vanita5 <mail@vanit.as>
 *
 *  This program incorporates a modified version of Twidere.
 *  Copyright (C) 2012-2017 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.vanita5.twittnuker.loader

import android.content.Context

import de.vanita5.twittnuker.library.MicroBlog
import de.vanita5.twittnuker.library.MicroBlogException
import de.vanita5.twittnuker.library.twitter.model.PageableResponseList
import de.vanita5.twittnuker.library.twitter.model.Paging
import de.vanita5.twittnuker.library.twitter.model.User
import de.vanita5.twittnuker.model.AccountDetails
import de.vanita5.twittnuker.model.ParcelableUser
import de.vanita5.twittnuker.model.UserKey

class UserListMembersLoader(
        context: Context,
        accountKey: UserKey,
        private val listId: String?,
        private val userKey: UserKey?,
        private val screenName: String?,
        private val listName: String?,
        data: List<ParcelableUser>?,
        fromUser: Boolean
) : CursorSupportUsersLoader(context, accountKey, data, fromUser) {

    @Throws(MicroBlogException::class)
    override fun getCursoredUsers(twitter: MicroBlog, details: AccountDetails, paging: Paging): PageableResponseList<User> {
        if (listId != null) {
            return twitter.getUserListMembers(listId, paging)
        } else if (listName != null) {
            if (userKey != null) {
                return twitter.getUserListMembers(listName.replace(' ', '-'), userKey.id, paging)
            } else if (screenName != null) {
                return twitter.getUserListMembersByScreenName(listName.replace(' ', '-'), screenName, paging)
            }
        }
        throw MicroBlogException("list_id or list_name and user_id (or screen_name) required")
    }

}