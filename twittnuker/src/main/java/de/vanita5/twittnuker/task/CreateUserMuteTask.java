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

package de.vanita5.twittnuker.task;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.vanita5.twittnuker.library.MicroBlog;
import de.vanita5.twittnuker.library.MicroBlogException;
import de.vanita5.twittnuker.library.twitter.model.User;
import org.mariotaku.sqliteqb.library.Expression;
import de.vanita5.twittnuker.R;
import de.vanita5.twittnuker.model.AccountDetails;
import de.vanita5.twittnuker.model.ParcelableUser;
import de.vanita5.twittnuker.model.message.FriendshipTaskEvent;
import de.vanita5.twittnuker.util.DataStoreUtils;
import de.vanita5.twittnuker.provider.TwidereDataStore.Activities;
import de.vanita5.twittnuker.provider.TwidereDataStore.CachedRelationships;
import de.vanita5.twittnuker.provider.TwidereDataStore.Statuses;
import de.vanita5.twittnuker.util.Utils;

import static de.vanita5.twittnuker.constant.SharedPreferenceConstants.KEY_NAME_FIRST;

public class CreateUserMuteTask extends AbsFriendshipOperationTask {
    public CreateUserMuteTask(Context context) {
        super(context, FriendshipTaskEvent.Action.MUTE);
    }

    @NonNull
    @Override
    protected User perform(@NonNull MicroBlog twitter, @NonNull AccountDetails details,
                           @NonNull Arguments args) throws MicroBlogException {
        return twitter.createMute(args.getUserKey().getId());
    }

    @Override
    protected void succeededWorker(@NonNull MicroBlog twitter,
                                   @NonNull AccountDetails details,
                                   @NonNull Arguments args, @NonNull ParcelableUser user) {
        final ContentResolver resolver = getContext().getContentResolver();
        Utils.setLastSeen(getContext(), args.getUserKey(), -1);
        for (final Uri uri : DataStoreUtils.STATUSES_URIS) {
            final Expression where = Expression.and(
                    Expression.equalsArgs(Statuses.ACCOUNT_KEY),
                    Expression.equalsArgs(Statuses.USER_KEY)
            );
            final String[] whereArgs = {args.getAccountKey().toString(), args.getUserKey().toString()};
            resolver.delete(uri, where.getSQL(), whereArgs);
        }
        if (!user.is_following) {
            for (final Uri uri : DataStoreUtils.ACTIVITIES_URIS) {
                final Expression where = Expression.and(
                        Expression.equalsArgs(Activities.ACCOUNT_KEY),
                        Expression.equalsArgs(Activities.STATUS_USER_KEY)
                );
                final String[] whereArgs = {args.getAccountKey().toString(), args.getUserKey().toString()};
                resolver.delete(uri, where.getSQL(), whereArgs);
            }
        }
        // I bet you don't want to see this user in your auto complete list.
        final ContentValues values = new ContentValues();
        values.put(CachedRelationships.ACCOUNT_KEY, args.getAccountKey().toString());
        values.put(CachedRelationships.USER_KEY, args.getUserKey().toString());
        values.put(CachedRelationships.MUTING, true);
        resolver.insert(CachedRelationships.CONTENT_URI, values);
    }

    @Override
    protected void showSucceededMessage(@NonNull Arguments params, @NonNull ParcelableUser user) {
        final boolean nameFirst = getPreferences().getBoolean(KEY_NAME_FIRST);
        final String message = getContext().getString(R.string.muted_user, getManager().getDisplayName(user,
                nameFirst));
        Utils.showInfoMessage(getContext(), message, false);

    }

    @Override
    protected void showErrorMessage(@NonNull Arguments params, @Nullable Exception exception) {
        Utils.showErrorMessage(getContext(), R.string.action_muting, exception, true);
    }
}