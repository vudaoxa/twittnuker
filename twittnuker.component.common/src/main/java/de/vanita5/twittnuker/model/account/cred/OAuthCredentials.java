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

package de.vanita5.twittnuker.model.account.cred;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;


@ParcelablePlease
@JsonObject
public class OAuthCredentials extends Credentials implements Parcelable {
    @JsonField(name = "consumer_key")
    public String consumer_key;
    @JsonField(name = "consumer_secret")
    public String consumer_secret;

    @JsonField(name = "access_token")
    public String access_token;
    @JsonField(name = "access_token_secret")
    public String access_token_secret;

    @JsonField(name = "same_oauth_signing_url")
    public boolean same_oauth_signing_url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        OAuthCredentialsParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<OAuthCredentials> CREATOR = new Creator<OAuthCredentials>() {
        public OAuthCredentials createFromParcel(Parcel source) {
            OAuthCredentials target = new OAuthCredentials();
            OAuthCredentialsParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public OAuthCredentials[] newArray(int size) {
            return new OAuthCredentials[size];
        }
    };
}
