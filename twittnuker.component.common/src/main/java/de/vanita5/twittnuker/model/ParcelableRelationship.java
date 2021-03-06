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

package de.vanita5.twittnuker.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

import org.mariotaku.library.objectcursor.annotation.CursorField;
import org.mariotaku.library.objectcursor.annotation.CursorObject;
import de.vanita5.twittnuker.model.util.UserKeyCursorFieldConverter;
import de.vanita5.twittnuker.provider.TwidereDataStore;
import de.vanita5.twittnuker.provider.TwidereDataStore.CachedRelationships;

@ParcelablePlease
@CursorObject(valuesCreator = true, tableInfo = true)
public class ParcelableRelationship implements Parcelable {

    @CursorField(value = CachedRelationships.ACCOUNT_KEY, converter = UserKeyCursorFieldConverter.class)
    public UserKey account_key;

    @CursorField(value = CachedRelationships.USER_KEY, converter = UserKeyCursorFieldConverter.class)
    public UserKey user_key;

    @CursorField(CachedRelationships.FOLLOWING)
    public boolean following;

    @CursorField(CachedRelationships.FOLLOWED_BY)
    public boolean followed_by;

    @CursorField(CachedRelationships.BLOCKING)
    public boolean blocking;

    @CursorField(CachedRelationships.BLOCKED_BY)
    public boolean blocked_by;

    @CursorField(CachedRelationships.MUTING)
    public boolean muting;

    @CursorField(CachedRelationships.RETWEET_ENABLED)
    public boolean retweet_enabled;

    @CursorField(value = CachedRelationships._ID, excludeWrite = true, type = TwidereDataStore.TYPE_PRIMARY_KEY)
    public long _id;

    public boolean can_dm;

    public boolean filtering;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelableRelationshipParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<ParcelableRelationship> CREATOR = new Creator<ParcelableRelationship>() {
        public ParcelableRelationship createFromParcel(Parcel source) {
            ParcelableRelationship target = new ParcelableRelationship();
            ParcelableRelationshipParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ParcelableRelationship[] newArray(int size) {
            return new ParcelableRelationship[size];
        }
    };
}
