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

package de.vanita5.twittnuker.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.squareup.otto.Bus
import org.mariotaku.kpreferences.KPreferences
import de.vanita5.twittnuker.fragment.iface.IBaseFragment
import de.vanita5.twittnuker.preference.RingtonePreference
import de.vanita5.twittnuker.util.KeyboardShortcutsHandler
import de.vanita5.twittnuker.util.UserColorNameManager
import de.vanita5.twittnuker.util.dagger.GeneralComponentHelper
import de.vanita5.twittnuker.util.sync.SyncController

import javax.inject.Inject

abstract class BasePreferenceFragment : PreferenceFragmentCompat(), IBaseFragment<BasePreferenceFragment> {
    private var ringtonePreferenceKey: String? = null

    @Inject
    lateinit var keyboardShortcutHandler: KeyboardShortcutsHandler
    @Inject
    lateinit var userColorNameManager: UserColorNameManager
    @Inject
    lateinit var kPreferences: KPreferences
    @Inject
    lateinit var syncController: SyncController
    @Inject
    lateinit var bus: Bus

    private val actionHelper = IBaseFragment.ActionHelper(this)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            ringtonePreferenceKey = savedInstanceState.getString(EXTRA_RINGTONE_PREFERENCE_KEY)
        }
        super.onActivityCreated(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        GeneralComponentHelper.build(context).inject(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(EXTRA_RINGTONE_PREFERENCE_KEY, ringtonePreferenceKey)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        requestFitSystemWindows()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PICK_RINGTONE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val ringtone = data.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                    if (ringtonePreferenceKey != null) {
                        val ringtonePreference = findPreference(ringtonePreferenceKey) as RingtonePreference?
                        if (ringtonePreference != null) {
                            ringtonePreference.value = ringtone?.toString()
                        }
                    }
                    ringtonePreferenceKey = null
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference is RingtonePreference) {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, preference.ringtoneType)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, preference.isShowDefault)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, preference.isShowSilent)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI)

            val existingValue = preference.value // TODO
            if (existingValue != null) {
                // Empty value means "Silent"
                val uri = existingValue.takeIf(String::isNotEmpty)?.let(Uri::parse)
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri)
            } else {
                // No ringtone has been selected, set to the default
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_NOTIFICATION_URI)
            }
            startActivityForResult(intent, REQUEST_PICK_RINGTONE)
            ringtonePreferenceKey = preference.key
            return true
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun executeAfterFragmentResumed(useHandler: Boolean, action: (BasePreferenceFragment) -> Unit) {
        actionHelper.executeAfterFragmentResumed(useHandler, action)
    }

    override fun fitSystemWindows(insets: Rect) {
        listView.setPadding(insets.left, insets.top, insets.right, insets.bottom)
    }

    companion object {

        private val REQUEST_PICK_RINGTONE = 301
        private val EXTRA_RINGTONE_PREFERENCE_KEY = "internal:ringtone_preference_key"
    }

}