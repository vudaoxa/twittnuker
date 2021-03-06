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

import android.content.Context
import android.os.Bundle
import android.support.v4.content.Loader
import de.vanita5.twittnuker.adapter.iface.ILoadMoreSupportAdapter
import de.vanita5.twittnuker.adapter.iface.ILoadMoreSupportAdapter.IndicatorPosition
import de.vanita5.twittnuker.annotation.Referral
import de.vanita5.twittnuker.constant.IntentConstants.*
import de.vanita5.twittnuker.loader.UserSearchLoader
import de.vanita5.twittnuker.model.ParcelableUser
import de.vanita5.twittnuker.model.UserKey

class SearchUsersFragment : ParcelableUsersFragment() {

    private var page = 1

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            page = savedInstanceState.getInt(EXTRA_PAGE, 1)
        }
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateUsersLoader(context: Context, args: Bundle, fromUser: Boolean): Loader<List<ParcelableUser>?> {
        val accountKey = args.getParcelable<UserKey>(EXTRA_ACCOUNT_KEY)
        val query = args.getString(EXTRA_QUERY)
        val page = args.getInt(EXTRA_PAGE, 1)
        return UserSearchLoader(context, accountKey, query, page, adapter.getData(), fromUser)
    }

    override fun onLoadFinished(loader: Loader<List<ParcelableUser>?>, data: List<ParcelableUser>?) {
        super.onLoadFinished(loader, data)
        if (loader is UserSearchLoader) {
            page = loader.page
        }
    }

    override fun onLoadMoreContents(@IndicatorPosition position: Long) {
        // Only supports load from end, skip START flag
        if (position and ILoadMoreSupportAdapter.START != 0L) return
        super.onLoadMoreContents(position)
        if (position == 0L) return
        val loaderArgs = Bundle(arguments)
        loaderArgs.putBoolean(EXTRA_FROM_USER, true)
        loaderArgs.putInt(EXTRA_PAGE, page + 1)
        loaderManager.restartLoader<List<ParcelableUser>>(0, loaderArgs, this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(EXTRA_PAGE, page)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        page = 1
        super.onDestroyView()
    }

    override val userReferral: String? = Referral.SEARCH_RESULT
}