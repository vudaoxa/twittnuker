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

package de.vanita5.twittnuker.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.twitter.Validator;

import org.apache.commons.lang3.math.NumberUtils;

import de.vanita5.twittnuker.Constants;
import de.vanita5.twittnuker.model.ParcelableCredentials;
import de.vanita5.twittnuker.model.StatusNetAccountExtra;

public class TwidereValidator implements Constants {

    private final Validator mValidator;

    public TwidereValidator() {
        mValidator = new Validator();
    }

    public static int getTextLimit(@NonNull ParcelableCredentials[] credentials) {
        int limit = -1;
        for (ParcelableCredentials credential : credentials) {
            int currentLimit = getTextLimit(credential);
            if (currentLimit != 0) {
                if (limit <= 0) {
                    limit = currentLimit;
                } else {
                    limit = Math.min(limit, currentLimit);
                }
            }
        }
        return limit;
    }

    /**
     * @param credentials Account for getting limit
     * @return Text limit, <= 0 if no limit
     */
    public static int getTextLimit(@NonNull ParcelableCredentials credentials) {
        if (credentials.account_type == null) {
            return Validator.MAX_TWEET_LENGTH;
        }
        switch (credentials.account_type) {
            case ParcelableCredentials.ACCOUNT_TYPE_STATUSNET: {
                StatusNetAccountExtra extra = JsonSerializer.parse(credentials.account_extras,
                        StatusNetAccountExtra.class);
                if (extra != null) {
                    return extra.getTextLimit();
                }
                break;
            }
        }
        return Validator.MAX_TWEET_LENGTH;
    }

    public int getTweetLength(@Nullable final String text) {
        if (text == null) return 0;
        return mValidator.getTweetLength(text);
    }

    public boolean isValidDirectMessage(final CharSequence text) {
        return !TextUtils.isEmpty(text);
    }

}