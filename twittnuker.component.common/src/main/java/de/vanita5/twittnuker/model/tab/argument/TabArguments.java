/*
 *          Twittnuker - Twitter client for Android
 *
 *          This program incorporates a modified version of
 *          Twidere - Twitter client for Android
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.vanita5.twittnuker.model.tab.argument;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import de.vanita5.twittnuker.TwittnukerConstants;
import de.vanita5.twittnuker.annotation.CustomTabType;
import de.vanita5.twittnuker.model.UserKey;

import java.io.IOException;
import java.util.Arrays;

@JsonObject
public class TabArguments implements TwittnukerConstants {
    @JsonField(name = "account_id")
    String accountId;

    @JsonField(name = "account_keys")
    @Nullable
    UserKey[] accountKeys;

    @Nullable
    public UserKey[] getAccountKeys() {
        return accountKeys;
    }

    public void setAccountKeys(@Nullable UserKey[] accountKeys) {
        this.accountKeys = accountKeys;
    }

    public String getAccountId() {
        return accountId;
    }

    @CallSuper
    public void copyToBundle(@NonNull Bundle bundle) {
        final UserKey[] accountKeys = this.accountKeys;
        if (accountKeys != null && accountKeys.length > 0) {
            for (UserKey key : accountKeys) {
                if (key == null) return;
            }
            bundle.putParcelableArray(EXTRA_ACCOUNT_KEYS, accountKeys);
        } else if (accountId != null) {
            long id = Long.MIN_VALUE;
            try {
                id = Long.parseLong(accountId);
            } catch (NumberFormatException e) {
                // Ignore
            }
            if (id != Long.MIN_VALUE && id <= 0) {
                // account_id = -1, means no account selected
                bundle.putParcelableArray(EXTRA_ACCOUNT_KEYS, null);
                return;
            }
            bundle.putParcelableArray(EXTRA_ACCOUNT_KEYS, new UserKey[]{UserKey.valueOf(accountId)});
        }
    }

    @Override
    public String toString() {
        return "TabArguments{" +
                "accountId=" + accountId +
                ", accountKeys=" + Arrays.toString(accountKeys) +
                '}';
    }

    @Nullable
    public static TabArguments parse(@NonNull @CustomTabType String type, @Nullable String json) throws IOException {
        if (json == null) return null;
        switch (type) {
            case CustomTabType.HOME_TIMELINE:
            case CustomTabType.NOTIFICATIONS_TIMELINE:
            case CustomTabType.DIRECT_MESSAGES:
            case CustomTabType.TRENDS_SUGGESTIONS: {
                return LoganSquare.parse(json, TabArguments.class);
            }
            case CustomTabType.USER_TIMELINE:
            case CustomTabType.FAVORITES: {
                return LoganSquare.parse(json, UserArguments.class);
            }
            case CustomTabType.LIST_TIMELINE: {
                return LoganSquare.parse(json, UserListArguments.class);
            }
            case CustomTabType.SEARCH_STATUSES: {
                return LoganSquare.parse(json, TextQueryArguments.class);
            }
        }
        return null;
    }
}