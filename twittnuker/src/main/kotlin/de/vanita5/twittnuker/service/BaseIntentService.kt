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

package de.vanita5.twittnuker.service

import android.app.IntentService
import com.twitter.Extractor
import com.twitter.Validator
import de.vanita5.twittnuker.util.*
import de.vanita5.twittnuker.util.dagger.GeneralComponentHelper
import javax.inject.Inject

abstract class BaseIntentService(tag: String) : IntentService(tag) {

    @Inject
    lateinit var preferences: SharedPreferencesWrapper
    @Inject
    lateinit var twitterWrapper: AsyncTwitterWrapper
    @Inject
    lateinit var notificationManager: NotificationManagerWrapper
    @Inject
    lateinit var validator: Validator
    @Inject
    lateinit var extractor: Extractor
    @Inject
    lateinit var userColorNameManager: UserColorNameManager

    override fun onCreate() {
        super.onCreate()
        GeneralComponentHelper.build(this).inject(this)
    }
}