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

package de.vanita5.twittnuker.view.holder

import android.view.View
import android.widget.CompoundButton
import de.vanita5.twittnuker.adapter.SelectableUsersAdapter
import de.vanita5.twittnuker.model.ParcelableUser
import de.vanita5.twittnuker.util.ThemeUtils
import de.vanita5.twittnuker.util.support.ViewSupport

class SelectableUserViewHolder(
        itemView: View,
        adapter: SelectableUsersAdapter
) : SimpleUserViewHolder<SelectableUsersAdapter>(itemView, adapter) {
    private val checkChangedListener = CompoundButton.OnCheckedChangeListener { view, value ->
        adapter.setItemChecked(layoutPosition, value)
    }

    init {
        ViewSupport.setBackground(itemView, ThemeUtils.getSelectableItemBackgroundDrawable(itemView.context))
        checkBox.visibility = View.VISIBLE
        itemView.setOnClickListener {
            checkBox.toggle()
        }
    }

    override fun displayUser(user: ParcelableUser) {
        super.displayUser(user)
        checkBox.setOnCheckedChangeListener(null)
        val key = adapter.getUserKey(layoutPosition)
        val locked = adapter.isItemLocked(key)
        if (locked) {
            itemView.isEnabled = false
            checkBox.isEnabled = false
            checkBox.isChecked = adapter.getLockedState(key)
        } else {
            itemView.isEnabled = true
            checkBox.isEnabled = true
            checkBox.isChecked = adapter.isItemChecked(key)
        }
        checkBox.setOnCheckedChangeListener(checkChangedListener)
    }

}