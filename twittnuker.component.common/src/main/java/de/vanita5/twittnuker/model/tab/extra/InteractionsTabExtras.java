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

package de.vanita5.twittnuker.model.tab.extra;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

import de.vanita5.twittnuker.constant.IntentConstants;

@ParcelablePlease
@JsonObject
public class InteractionsTabExtras extends TabExtras implements Parcelable {

    @ParcelableThisPlease
    @JsonField(name = "my_following_only")
    boolean myFollowingOnly;

    @ParcelableThisPlease
    @JsonField(name = "mentions_only")
    boolean mentionsOnly;

    public boolean isMyFollowingOnly() {
        return myFollowingOnly;
    }

    public void setMyFollowingOnly(boolean myFollowingOnly) {
        this.myFollowingOnly = myFollowingOnly;
    }

    public boolean isMentionsOnly() {
        return mentionsOnly;
    }

    public void setMentionsOnly(boolean mentionsOnly) {
        this.mentionsOnly = mentionsOnly;
    }

    @Override
    public void copyToBundle(Bundle bundle) {
        super.copyToBundle(bundle);
        bundle.putBoolean(IntentConstants.EXTRA_MY_FOLLOWING_ONLY, myFollowingOnly);
        bundle.putBoolean(IntentConstants.EXTRA_MENTIONS_ONLY, mentionsOnly);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        InteractionsTabExtrasParcelablePlease.writeToParcel(this, dest, flags);
    }

    @Override
    public String toString() {
        return "InteractionsTabExtras{" +
                "myFollowingOnly=" + myFollowingOnly +
                ", mentionsOnly=" + mentionsOnly +
                "} " + super.toString();
    }

    public static final Creator<InteractionsTabExtras> CREATOR = new Creator<InteractionsTabExtras>() {
        @Override
        public InteractionsTabExtras createFromParcel(Parcel source) {
            InteractionsTabExtras target = new InteractionsTabExtras();
            InteractionsTabExtrasParcelablePlease.readFromParcel(target, source);
            return target;
        }

        @Override
        public InteractionsTabExtras[] newArray(int size) {
            return new InteractionsTabExtras[size];
        }
    };
}