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

import de.vanita5.twittnuker.library.MicroBlogException;
import de.vanita5.twittnuker.library.twitter.model.AccountSettings;
import de.vanita5.twittnuker.library.twitter.model.Category;
import de.vanita5.twittnuker.library.twitter.model.IDs;
import de.vanita5.twittnuker.library.twitter.model.PageableResponseList;
import de.vanita5.twittnuker.library.twitter.model.Paging;
import de.vanita5.twittnuker.library.twitter.model.ProfileUpdate;
import de.vanita5.twittnuker.library.twitter.model.ResponseCode;
import de.vanita5.twittnuker.library.twitter.model.ResponseList;
import de.vanita5.twittnuker.library.twitter.model.SettingsUpdate;
import de.vanita5.twittnuker.library.twitter.model.User;
import de.vanita5.twittnuker.library.twitter.template.UserAnnotationTemplate;
import org.mariotaku.restfu.annotation.method.GET;
import org.mariotaku.restfu.annotation.method.POST;
import org.mariotaku.restfu.annotation.param.Param;
import org.mariotaku.restfu.annotation.param.Queries;
import org.mariotaku.restfu.annotation.param.Query;
import org.mariotaku.restfu.http.BodyType;
import org.mariotaku.restfu.http.mime.FileBody;

@SuppressWarnings("RedundantThrows")
@Queries(template = UserAnnotationTemplate.class)
public interface UsersResources {

    @POST("/blocks/create.json")
    User createBlock(@Param("user_id") String userId) throws MicroBlogException;

    @POST("/blocks/create.json")
    User createBlockByScreenName(@Query("screen_name") String screenName) throws MicroBlogException;

    @POST("/mutes/users/create.json")
    User createMute(@Param("user_id") String userId) throws MicroBlogException;

    @POST("/mutes/users/create.json")
    User createMuteByScreenName(@Query("screen_name") String screenName) throws MicroBlogException;

    @POST("/blocks/destroy.json")
    User destroyBlock(@Param("user_id") String userId) throws MicroBlogException;

    @POST("/blocks/destroy.json")
    User destroyBlockByScreenName(@Query("screen_name") String screenName) throws MicroBlogException;

    @POST("/mutes/users/destroy.json")
    User destroyMute(@Param("user_id") String userId) throws MicroBlogException;

    @POST("/mutes/users/destroy.json")
    User destroyMuteByScreenName(@Query("screen_name") String screenName) throws MicroBlogException;

    @GET("/account/settings.json")
    AccountSettings getAccountSettings() throws MicroBlogException;

    @GET("/blocks/ids.json")
    IDs getBlocksIDs(@Query Paging paging) throws MicroBlogException;

    @GET("/blocks/list.json")
    PageableResponseList<User> getBlocksList(@Query Paging paging) throws MicroBlogException;

    ResponseList<User> getMemberSuggestions(String categorySlug) throws MicroBlogException;

    @GET("/mutes/users/ids.json")
    IDs getMutesUsersIDs(Paging paging) throws MicroBlogException;

    @GET("/mutes/users/list.json")
    PageableResponseList<User> getMutesUsersList(@Query Paging paging) throws MicroBlogException;

    ResponseList<Category> getSuggestedUserCategories() throws MicroBlogException;

    ResponseList<User> getUserSuggestions(String categorySlug) throws MicroBlogException;

    @POST("/users/lookup.json")
    @BodyType(BodyType.FORM)
    ResponseList<User> lookupUsers(@Param(value = "user_id", arrayDelimiter = ',') String[] ids) throws MicroBlogException;

    @GET("/users/lookup.json")
    ResponseList<User> lookupUsersByScreenName(@Param(value = "screen_name", arrayDelimiter = ',') String[] screenNames) throws MicroBlogException;

    @POST("/account/remove_profile_banner.json")
    ResponseCode removeProfileBannerImage() throws MicroBlogException;

    @GET("/users/search.json")
    ResponseList<User> searchUsers(@Query("q") String query, @Query Paging paging) throws MicroBlogException;

    @GET("/users/show.json")
    User showUser(@Query("user_id") String userId) throws MicroBlogException;

    @GET("/users/show.json")
    User showUserByScreenName(@Query("screen_name") String screenName) throws MicroBlogException;

    @POST("/account/settings.json")
    AccountSettings updateAccountSettings(@Param SettingsUpdate settingsUpdate) throws MicroBlogException;

    @POST("/account/update_profile.json")
    User updateProfile(@Param ProfileUpdate profileUpdate) throws MicroBlogException;

    @POST("/account/update_profile_background_image.json")
    User updateProfileBackgroundImage(@Param("image") FileBody data, @Param("tile") boolean tile) throws MicroBlogException;

    @POST("/account/update_profile_background_image.json")
    User updateProfileBackgroundImage(@Param("media_id") long mediaId, @Param("tile") boolean tile) throws MicroBlogException;

    @POST("/account/update_profile_banner.json")
    ResponseCode updateProfileBannerImage(@Param("banner") FileBody data, @Param("width") int width,
                                          @Param("height") int height, @Param("offset_left") int offsetLeft,
                                          @Param("offset_top") int offsetTop)
            throws MicroBlogException;

    @POST("/account/update_profile_banner.json")
    ResponseCode updateProfileBannerImage(@Param("banner") FileBody data) throws MicroBlogException;

    @POST("/account/update_profile_image.json")
    User updateProfileImage(@Param("image") FileBody data) throws MicroBlogException;

    @GET("/account/verify_credentials.json")
    User verifyCredentials() throws MicroBlogException;
}