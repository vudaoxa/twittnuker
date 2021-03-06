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

package de.vanita5.twittnuker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v4.net.ConnectivityManagerCompat
import de.vanita5.twittnuker.TwittnukerConstants
import de.vanita5.twittnuker.app.TwittnukerApplication
import de.vanita5.twittnuker.util.DebugLog
import de.vanita5.twittnuker.util.dagger.DependencyHolder

class ConnectivityStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        DebugLog.d(RECEIVER_LOGTAG, String.format("Received Broadcast %s", intent), null)
        if (ConnectivityManager.CONNECTIVITY_ACTION != intent.action) return
        //TODO start streaming here?

        val appContext = context.applicationContext
        val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isNetworkMetered = ConnectivityManagerCompat.isActiveNetworkMetered(cm)
        DependencyHolder.get(context).mediaPreloader.isNetworkMetered = isNetworkMetered
    }

    companion object {

        private const val RECEIVER_LOGTAG = "TwidereConnectivity"
    }
}