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

package de.vanita5.twittnuker.loader

import android.accounts.AccountManager
import android.content.Context
import android.util.Log
import de.vanita5.twittnuker.R
import de.vanita5.twittnuker.library.MicroBlog
import de.vanita5.twittnuker.library.MicroBlogException
import de.vanita5.twittnuker.library.twitter.model.User
import de.vanita5.twittnuker.TwittnukerConstants
import de.vanita5.twittnuker.extension.model.newMicroBlogInstance
import de.vanita5.twittnuker.model.AccountDetails
import de.vanita5.twittnuker.model.ListResponse
import de.vanita5.twittnuker.model.ParcelableUser
import de.vanita5.twittnuker.model.UserKey
import de.vanita5.twittnuker.model.util.AccountUtils
import de.vanita5.twittnuker.model.util.ParcelableUserUtils
import java.util.*

abstract class MicroBlogAPIUsersLoader(
        context: Context,
        val accountKey: UserKey?,
        data: List<ParcelableUser>?,
        fromUser: Boolean
) : ParcelableUsersLoader(context, data, fromUser) {

    private val profileImageSize = context.getString(R.string.profile_image_size)

    override fun loadInBackground(): List<ParcelableUser> {
        if (accountKey == null) {
            return ListResponse.getListInstance(MicroBlogException("No Account"))
        }
        val am = AccountManager.get(context)
        val details = AccountUtils.getAccountDetails(am, accountKey, true) ?:
                return ListResponse.getListInstance(MicroBlogException("No Account"))
        val twitter: MicroBlog = details.newMicroBlogInstance(context = context, cls = MicroBlog::class.java)
        val data = data
        val users: List<User>
        try {
            users = getUsers(twitter, details)
        } catch (e: MicroBlogException) {
            Log.w(TwittnukerConstants.LOGTAG, e)
            return ListResponse.getListInstance(data)
        }

        var pos = data.size
        for (user in users) {
            if (hasId(user.id)) {
                continue
            }
            val item = ParcelableUserUtils.fromUser(user, accountKey, pos.toLong(),
                    profileImageSize = profileImageSize)
            processUser(item)
            data.add(item)
            pos++
        }
        processUsersData(data)
        return ListResponse.getListInstance(data)
    }

    protected open fun processUser(user: ParcelableUser) {

    }

    @Throws(MicroBlogException::class)
    protected abstract fun getUsers(twitter: MicroBlog, details: AccountDetails): List<User>

    protected open fun processUsersData(list: MutableList<ParcelableUser>) {
        Collections.sort(data)
    }
}