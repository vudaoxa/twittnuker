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

package de.vanita5.twittnuker.extension.model

import android.content.Context
import com.twitter.Validator
import de.vanita5.twittnuker.annotation.AccountType
import de.vanita5.twittnuker.model.AccountDetails
import de.vanita5.twittnuker.model.account.AccountExtras
import de.vanita5.twittnuker.model.account.StatusNetAccountExtras
import de.vanita5.twittnuker.model.account.TwitterAccountExtras
import de.vanita5.twittnuker.model.account.cred.Credentials
import de.vanita5.twittnuker.model.account.cred.OAuthCredentials
import de.vanita5.twittnuker.task.twitter.UpdateStatusTask
import de.vanita5.twittnuker.util.MicroBlogAPIFactory
import de.vanita5.twittnuker.util.TwitterContentUtils

fun AccountDetails.isOfficial(context: Context): Boolean {
    val extra = this.extras
    if (extra is TwitterAccountExtras) {
        return extra.isOfficialCredentials
    }
    val credentials = this.credentials
    if (credentials is OAuthCredentials) {
        return TwitterContentUtils.isOfficialKey(context,
                credentials.consumer_key, credentials.consumer_secret)
    }
    return false
}

val AccountExtras.official: Boolean
    get() {
        if (this is TwitterAccountExtras) {
            return isOfficialCredentials
        }
        return false
    }


@JvmOverloads
fun <T> AccountDetails.newMicroBlogInstance(
        context: Context,
        includeEntities: Boolean = true,
        includeRetweets: Boolean = true,
        extraRequestParams: Map<String, String>? = MicroBlogAPIFactory.getExtraParams(type,
                includeEntities, includeRetweets),
        cls: Class<T>
): T {
    return credentials.newMicroBlogInstance(context, type, extraRequestParams, cls)
}

val AccountDetails.isOAuth: Boolean
    get() = credentials_type == Credentials.Type.OAUTH || credentials_type == Credentials.Type.XAUTH

val AccountDetails.mediaSizeLimit: UpdateStatusTask.SizeLimit
    get() = when (type) {
        AccountType.TWITTER -> {
            val imageLimit = AccountExtras.ImageLimit.ofSize(2048, 1536)
            val videoLimit = AccountExtras.VideoLimit.twitterDefault()
            UpdateStatusTask.SizeLimit(imageLimit, videoLimit)
        }
        else -> UpdateStatusTask.SizeLimit(AccountExtras.ImageLimit(), AccountExtras.VideoLimit.unsupported())
    }
/**
 * Text limit when composing a status, 0 for no limit
 */
val AccountDetails.textLimit: Int get() {
    if (type == null) {
        return Validator.MAX_TWEET_LENGTH
    }
    when (type) {
        AccountType.STATUSNET -> {
            val extras = this.extras as? StatusNetAccountExtras
            if (extras != null) {
                return extras.textLimit
            }
        }
    }
    return Validator.MAX_TWEET_LENGTH
}

val Array<AccountDetails>.textLimit: Int
    get() {
        var limit = -1
        forEach { details ->
            val currentLimit = details.textLimit
            if (currentLimit != 0) {
                if (limit <= 0) {
                    limit = currentLimit
                } else {
                    limit = Math.min(limit, currentLimit)
                }
            }
        }
        return limit
    }