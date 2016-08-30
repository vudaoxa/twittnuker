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

package de.vanita5.twittnuker.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.widget.SimpleCursorAdapter;

import de.vanita5.twittnuker.provider.TwidereDataStore;

public class TrendsAdapter extends SimpleCursorAdapter {
    private int mNameIdx;

    @Override
    public String getItem(int position) {
        final Cursor c = getCursor();
        if (c != null && !c.isClosed() && c.moveToPosition(position))
            return c.getString(mNameIdx);
        return null;
    }

    @Override
    @Nullable
    public Cursor swapCursor(Cursor c) {
        if (c != null) {
            mNameIdx = c.getColumnIndex(TwidereDataStore.CachedTrends.NAME);
        }
        return super.swapCursor(c);
    }

    public TrendsAdapter(final Context context) {
        super(context, android.R.layout.simple_list_item_1, null, new String[]{TwidereDataStore.CachedTrends.NAME},
                new int[]{android.R.id.text1}, 0);
    }

}