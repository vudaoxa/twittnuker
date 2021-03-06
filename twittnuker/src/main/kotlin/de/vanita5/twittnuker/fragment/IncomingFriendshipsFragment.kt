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

package de.vanita5.twittnuker.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import de.vanita5.twittnuker.TwittnukerConstants.USER_TYPE_FANFOU_COM
import de.vanita5.twittnuker.adapter.ParcelableUsersAdapter
import de.vanita5.twittnuker.adapter.iface.IUsersAdapter
import de.vanita5.twittnuker.constant.IntentConstants.EXTRA_ACCOUNT_KEY
import de.vanita5.twittnuker.loader.CursorSupportUsersLoader
import de.vanita5.twittnuker.loader.IncomingFriendshipsLoader
import de.vanita5.twittnuker.model.UserKey
import de.vanita5.twittnuker.model.event.FriendshipTaskEvent
import de.vanita5.twittnuker.util.Utils
import de.vanita5.twittnuker.view.holder.UserViewHolder

class IncomingFriendshipsFragment : CursorUsersListFragment(), IUsersAdapter.RequestClickListener {

    override fun onCreateUsersLoader(context: Context, args: Bundle,
                                            fromUser: Boolean): CursorSupportUsersLoader {
        val accountKey = args.getParcelable<UserKey>(EXTRA_ACCOUNT_KEY)
        val loader = IncomingFriendshipsLoader(context, accountKey, adapter.getData(), fromUser)
        loader.cursor = nextCursor
        loader.page = nextPage
        return loader
    }

    override fun onCreateAdapter(context: Context): ParcelableUsersAdapter {
        val adapter = super.onCreateAdapter(context)
        val args = arguments
        val accountKey = args.getParcelable<UserKey>(EXTRA_ACCOUNT_KEY)
        if (accountKey == null) {
            adapter.requestClickListener = null
        } else if (USER_TYPE_FANFOU_COM == accountKey.host || Utils.isOfficialCredentials(context, accountKey)) {
            adapter.requestClickListener = this
        } else {
            adapter.requestClickListener = null
        }
        return adapter
    }

    override fun onAcceptClicked(holder: UserViewHolder, position: Int) {
        val user = adapter.getUser(position) ?: return
        twitterWrapper.acceptFriendshipAsync(user.account_key, user.key)
    }

    override fun onDenyClicked(holder: UserViewHolder, position: Int) {
        val user = adapter.getUser(position) ?: return
        twitterWrapper.denyFriendshipAsync(user.account_key, user.key)
    }

    @SuppressLint("SwitchIntDef")
    override fun shouldRemoveUser(position: Int, event: FriendshipTaskEvent): Boolean {
        if (!event.isSucceeded) return false
        when (event.action) {
            FriendshipTaskEvent.Action.BLOCK, FriendshipTaskEvent.Action.ACCEPT, FriendshipTaskEvent.Action.DENY -> {
                return true
            }
        }
        return false
    }
}