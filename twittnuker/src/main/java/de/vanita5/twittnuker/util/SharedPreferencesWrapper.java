/*
 * Twittnuker - Twitter client for Android
 *
 * Copyright (C) 2013-2015 vanita5 <mail@vanita5.de>
 *
 * This program incorporates a modified version of Twidere.
 * Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;

import de.vanita5.twittnuker.Constants;

import java.util.Map;
import java.util.Set;

public class SharedPreferencesWrapper implements Constants {

	private final SharedPreferences mPreferences;
    private final Class<?> mKeysClass;

    private SharedPreferencesWrapper(final SharedPreferences preferences, final Class<?> keysClass) {
		mPreferences = preferences;
        mKeysClass = keysClass;
	}

    public boolean contains(final String key) {
        return mPreferences.contains(key);
    }

	public SharedPreferences.Editor edit() {
		return mPreferences.edit();
	}

    public Map<String, ?> getAll() {
        return mPreferences.getAll();
    }

	public boolean getBoolean(final String key, final boolean defValue) {
		try {
			return mPreferences.getBoolean(key, defValue);
		} catch (final ClassCastException e) {
            if (Utils.isDebugBuild()) Log.w(LOGTAG, e);
			mPreferences.edit().remove(key).apply();
			return defValue;
		}
	}

    public float getFloat(final String key, final float defValue) {
        try {
            return mPreferences.getFloat(key, defValue);
        } catch (final ClassCastException e) {
            if (Utils.isDebugBuild()) Log.w(LOGTAG, e);
            mPreferences.edit().remove(key).apply();
            return defValue;
        }
    }

	public int getInt(final String key, final int defValue) {
		try {
			return mPreferences.getInt(key, defValue);
		} catch (final ClassCastException e) {
            if (Utils.isDebugBuild()) Log.w(LOGTAG, e);
			mPreferences.edit().remove(key).apply();
			return defValue;
		}
	}

	public long getLong(final String key, final long defValue) {
		try {
			return mPreferences.getLong(key, defValue);
		} catch (final ClassCastException e) {
            if (Utils.isDebugBuild()) Log.w(LOGTAG, e);
			mPreferences.edit().remove(key).apply();
			return defValue;
		}
	}

	public SharedPreferences getSharedPreferences() {
		return mPreferences;
	}

	public String getString(final String key, final String defValue) {
		try {
			return mPreferences.getString(key, defValue);
		} catch (final ClassCastException e) {
            if (Utils.isDebugBuild()) Log.w(LOGTAG, e);
			mPreferences.edit().remove(key).apply();
			return defValue;
		}
	}

    public Set<String> getStringSet(final String key, final Set<String> defValue) {
        try {
            return mPreferences.getStringSet(key, defValue);
        } catch (final ClassCastException e) {
            if (Utils.isDebugBuild()) Log.w(LOGTAG, e);
            mPreferences.edit().remove(key).apply();
            return defValue;
        }
    }

    public void registerOnSharedPreferenceChangeListener(final OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(final OnSharedPreferenceChangeListener listener) {
        mPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

	public static SharedPreferencesWrapper getInstance(final Context context, final String name, final int mode) {
        return getInstance(context, name, mode, null);
    }

    public static SharedPreferencesWrapper getInstance(final Context context, final String name, final int mode,
            final Class<?> keysClass) {
		final SharedPreferences prefs = context.getSharedPreferences(name, mode);
		if (prefs == null) return null;
        return new SharedPreferencesWrapper(prefs, keysClass);
	}

}