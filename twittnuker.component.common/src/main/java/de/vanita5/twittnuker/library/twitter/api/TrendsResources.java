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

package de.vanita5.twittnuker.library.twitter.api;

import org.mariotaku.restfu.annotation.method.GET;
import org.mariotaku.restfu.annotation.param.Query;

import de.vanita5.twittnuker.library.MicroBlogException;
import de.vanita5.twittnuker.library.twitter.model.GeoLocation;
import de.vanita5.twittnuker.library.twitter.model.Location;
import de.vanita5.twittnuker.library.twitter.model.ResponseList;
import de.vanita5.twittnuker.library.twitter.model.Trends;

/**
 * @author Joern Huxhorn - jhuxhorn at googlemail.com
 */
@SuppressWarnings("RedundantThrows")
public interface TrendsResources {

    @GET("/trends/available.json")
    ResponseList<Location> getAvailableTrends() throws MicroBlogException;

    @GET("/trends/available.json")
    ResponseList<Location> getAvailableTrends(@Query GeoLocation location) throws MicroBlogException;

    @GET("/trends/closest.json")
    ResponseList<Location> getClosestTrends(@Query GeoLocation location) throws MicroBlogException;

    @GET("/trends/place.json")
    ResponseList<Trends> getLocationTrends(@Query("id") int woeid) throws MicroBlogException;

}