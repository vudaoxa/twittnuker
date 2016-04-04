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

package de.vanita5.twittnuker.util;

import android.app.Application;
import android.os.Build;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;

import de.vanita5.twittnuker.BuildConfig;
import de.vanita5.twittnuker.Constants;

import io.fabric.sdk.android.Fabric;

public class TwidereBugReporter extends BugReporter implements Constants {

    @Override
    protected void logImpl(int priority, String tag, String msg) {
        Crashlytics.log(priority, tag, msg);
    }

    @Override
    protected void logExceptionImpl(@NonNull final Throwable throwable) {
        Crashlytics.logException(throwable);
    }

    @Override
    protected void initImpl(final Application application) {
        Fabric.with(application, new Crashlytics());
        Crashlytics.setBool("debug", BuildConfig.DEBUG);
        Crashlytics.setString("build.brand", Build.BRAND);
        Crashlytics.setString("build.device", Build.DEVICE);
        Crashlytics.setString("build.display", Build.DISPLAY);
        Crashlytics.setString("build.hardware", Build.HARDWARE);
        Crashlytics.setString("build.manufacturer", Build.MANUFACTURER);
        Crashlytics.setString("build.model", Build.MODEL);
        Crashlytics.setString("build.product", Build.PRODUCT);
    }

}