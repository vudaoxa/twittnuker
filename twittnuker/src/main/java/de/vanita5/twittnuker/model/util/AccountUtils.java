/*
 *  Twittnuker - Twitter client for Android
 *
 *  Copyright (C) 2013-2016 vanita5 <mail@vanit.as>
 *
 *  This program incorporates a modified version of Twidere.
 *  Copyright (C) 2012-2016 Mariotaku Lee <mariotaku.lee@gmail.com>
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

package de.vanita5.twittnuker.model.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.vanita5.twittnuker.extension.AccountExtensionsKt;
import de.vanita5.twittnuker.model.AccountDetails;
import de.vanita5.twittnuker.model.UserKey;

import static de.vanita5.twittnuker.TwittnukerConstants.ACCOUNT_TYPE;

public class AccountUtils {

    @Nullable
    public static Account findByAccountKey(@NonNull AccountManager am, @NonNull UserKey userKey) {
        for (Account account : getAccounts(am)) {
            if (userKey.equals(AccountExtensionsKt.getAccountKey(account, am))) {
                return account;
            }
        }
        return null;
    }

    public static Account[] getAccounts(@NonNull AccountManager am) {
        //noinspection MissingPermission
        return am.getAccountsByType(ACCOUNT_TYPE);
    }

    public static AccountDetails[] getAllAccountDetails(@NonNull AccountManager am, @NonNull Account[] accounts) {
        AccountDetails[] details = new AccountDetails[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            details[i] = getAccountDetails(am, accounts[i]);
        }
        return details;
    }

    @Nullable
    public static AccountDetails getAccountDetails(@NonNull AccountManager am, @NonNull UserKey accountKey) {
        final Account account = findByAccountKey(am, accountKey);
        if (account == null) return null;
        return getAccountDetails(am, account);
    }

    public static AccountDetails getAccountDetails(@NonNull AccountManager am, @NonNull Account account) {
        AccountDetails details = new AccountDetails();
        details.key = AccountExtensionsKt.getAccountKey(account, am);
        details.account = account;
        details.color = AccountExtensionsKt.getColor(account, am);
        details.position = AccountExtensionsKt.getPosition(account, am);
        details.credentials = AccountExtensionsKt.getCredentials(account, am);
        details.user = AccountExtensionsKt.getAccountUser(account, am);
        details.activated = AccountExtensionsKt.isAccountActivated(account, am);
        details.type = AccountExtensionsKt.getAccountType(account, am);
        details.credentials_type = AccountExtensionsKt.getCredentialsType(account, am);
        return details;
    }
}