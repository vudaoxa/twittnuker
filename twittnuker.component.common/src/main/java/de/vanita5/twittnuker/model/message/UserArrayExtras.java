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

package de.vanita5.twittnuker.model.message;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

import de.vanita5.twittnuker.model.ParcelableUser;


@ParcelablePlease
@JsonObject
public class UserArrayExtras extends MessageExtras implements Parcelable {
    @JsonField(name = "users")
    ParcelableUser[] users;

    public ParcelableUser[] getUsers() {
        return users;
    }

    public void setUsers(final ParcelableUser[] users) {
        this.users = users;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        UserArrayExtrasParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<UserArrayExtras> CREATOR = new Creator<UserArrayExtras>() {
        public UserArrayExtras createFromParcel(Parcel source) {
            UserArrayExtras target = new UserArrayExtras();
            UserArrayExtrasParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public UserArrayExtras[] newArray(int size) {
            return new UserArrayExtras[size];
        }
    };
}