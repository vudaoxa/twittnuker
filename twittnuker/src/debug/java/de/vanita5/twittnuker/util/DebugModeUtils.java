/*
 * Twittnuker - Twitter client for Android
 *
 * Copyright (C) 2013-2017 vanita5 <mail@vanit.as>
 *
 * This program incorporates a modified version of Twidere.
 * Copyright (C) 2012-2017 Mariotaku Lee <mariotaku.lee@gmail.com>
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

package de.vanita5.twittnuker.util;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import de.vanita5.twittnuker.BuildConfig;

import okhttp3.OkHttpClient;

public class DebugModeUtils {

    private static RefWatcher sRefWatcher;

    private DebugModeUtils() {
    }

    public static void initForOkHttpClient(final OkHttpClient.Builder builder) {
    }

    public static void initForApplication(final Application application) {
        initLeakCanary(application);
    }

    static void initLeakCanary(Application application) {
        if (!BuildConfig.LEAK_CANARY_ENABLED) return;
        LeakCanary.enableDisplayLeakActivity(application);
        sRefWatcher = LeakCanary.install(application);
    }

    public static void watchReferenceLeak(final Object object) {
        if (sRefWatcher == null) return;
        sRefWatcher.watch(object);
    }
}