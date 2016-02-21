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

package de.vanita5.twittnuker.fragment.support;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.vanita5.twittnuker.Constants;
import de.vanita5.twittnuker.R;
import de.vanita5.twittnuker.adapter.DummyStatusHolderAdapter;
import de.vanita5.twittnuker.model.Draft;
import de.vanita5.twittnuker.model.ParcelableStatus;
import de.vanita5.twittnuker.model.ParcelableStatusUpdate;
import de.vanita5.twittnuker.service.BackgroundOperationService;
import de.vanita5.twittnuker.util.AsyncTwitterWrapper;
import de.vanita5.twittnuker.util.DataStoreUtils;
import de.vanita5.twittnuker.util.EditTextEnterHandler;
import de.vanita5.twittnuker.util.LinkCreator;
import de.vanita5.twittnuker.util.MenuUtils;
import de.vanita5.twittnuker.util.ThemeUtils;
import de.vanita5.twittnuker.view.ComposeEditText;
import de.vanita5.twittnuker.view.StatusTextCountView;
import de.vanita5.twittnuker.view.holder.StatusViewHolder;
import de.vanita5.twittnuker.view.holder.iface.IStatusViewHolder;

import static de.vanita5.twittnuker.util.Utils.isMyRetweet;

public class RetweetQuoteDialogFragment extends BaseSupportDialogFragment implements
        Constants, DialogInterface.OnClickListener {

    public static final String FRAGMENT_TAG = "retweet_quote";
    private PopupMenu mPopupMenu;

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        final ParcelableStatus status = getStatus();
        if (status == null) return;
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE: {
                final AsyncTwitterWrapper twitter = mTwitterWrapper;
                if (twitter == null) return;
                retweetOrQuote(twitter, status);
                break;
            }
            case DialogInterface.BUTTON_NEUTRAL: {
                final Intent intent = new Intent(INTENT_ACTION_QUOTE);
                final Menu menu = mPopupMenu.getMenu();
                final MenuItem quoteOriginalStatus = menu.findItem(R.id.quote_original_status);
                intent.putExtra(EXTRA_STATUS, status);
                intent.putExtra(EXTRA_QUOTE_ORIGINAL_STATUS, quoteOriginalStatus.isChecked());
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Context wrapped = ThemeUtils.getDialogThemedContext(getActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(wrapped);
        final Context context = builder.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.dialog_status_quote_retweet, null);
        final DummyStatusHolderAdapter adapter = new DummyStatusHolderAdapter(context);
        adapter.setShouldShowAccountsColor(true);
        final IStatusViewHolder holder = new StatusViewHolder(adapter, view.findViewById(R.id.item_content));
        final ParcelableStatus status = getStatus();

        assert status != null;

        builder.setView(view);
        builder.setTitle(R.string.retweet_quote_confirm_title);
        if (isMyRetweet(status)) {
            builder.setPositiveButton(R.string.cancel_retweet, this);
        } else if (!status.user_is_protected) {
            builder.setPositiveButton(R.string.retweet, this);
        }
        builder.setNeutralButton(R.string.quote, this);
        builder.setNegativeButton(android.R.string.cancel, null);

        holder.displayStatus(status, false, true);

        view.findViewById(R.id.item_menu).setVisibility(View.GONE);
        view.findViewById(R.id.action_buttons).setVisibility(View.GONE);
        view.findViewById(R.id.item_content).setFocusable(false);
        view.findViewById(R.id.comment_container).setVisibility(status.user_is_protected ? View.GONE : View.VISIBLE);
        final ComposeEditText mEditComment = (ComposeEditText) view.findViewById(R.id.edit_comment);
        mEditComment.setAccountId(status.account_id);

        final boolean sendByEnter = mPreferences.getBoolean(KEY_QUICK_SEND);
        final EditTextEnterHandler enterHandler = EditTextEnterHandler.attach(mEditComment, new EditTextEnterHandler.EnterListener() {
            @Override
            public boolean shouldCallListener() {
                return true;
            }

            @Override
            public boolean onHitEnter() {
                final AsyncTwitterWrapper twitter = mTwitterWrapper;
                final ParcelableStatus status = getStatus();
                if (twitter == null || status == null) return false;
                retweetOrQuote(twitter, status);
                dismiss();
                return true;
            }
        }, sendByEnter);
        enterHandler.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTextCount(getDialog(), s, status);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        final View commentMenu = view.findViewById(R.id.comment_menu);

        mPopupMenu = new PopupMenu(context, commentMenu, Gravity.NO_GRAVITY,
                R.attr.actionOverflowMenuStyle, 0);
        commentMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupMenu.show();
            }
        });
        commentMenu.setOnTouchListener(mPopupMenu.getDragToOpenListener());
        mPopupMenu.inflate(R.menu.menu_dialog_comment);
        final Menu menu = mPopupMenu.getMenu();
        MenuUtils.setMenuItemAvailability(menu, R.id.quote_original_status,
                status.retweet_id > 0 || status.quoted_id > 0);
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.isCheckable()) {
                    item.setChecked(!item.isChecked());
                    return true;
                }
                return false;
            }
        });

        final Dialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                updateTextCount(dialog, mEditComment.getText(), status);
            }
        });
        return dialog;
    }

    private void updateTextCount(DialogInterface dialog, CharSequence s, ParcelableStatus status) {
        if (!(dialog instanceof AlertDialog)) return;
        final AlertDialog alertDialog = (AlertDialog) dialog;
        final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (positiveButton == null) return;
        if (s.length() > 0) {
            positiveButton.setText(R.string.comment);
        } else if(isMyRetweet(status)) {
            positiveButton.setText(R.string.cancel_retweet);
            positiveButton.setTextColor(getResources().getColor(R.color.material_red));
        } else {
            positiveButton.setText(R.string.retweet);
        }
        final String statusLink = LinkCreator.getTwitterStatusLink(status).toString();
        final StatusTextCountView textCountView = (StatusTextCountView) alertDialog.findViewById(R.id.comment_text_count);
        textCountView.setTextCount(mValidator.getTweetLength(s + " " + statusLink));
    }

    private ParcelableStatus getStatus() {
        final Bundle args = getArguments();
        if (!args.containsKey(EXTRA_STATUS)) return null;
        return args.getParcelable(EXTRA_STATUS);
    }

    private void retweetOrQuote(AsyncTwitterWrapper twitter, ParcelableStatus status) {
        final Dialog dialog = getDialog();
        if (dialog == null) return;
        final EditText editComment = ((EditText) dialog.findViewById(R.id.edit_comment));
        if (editComment.length() > 0) {
            final Menu menu = mPopupMenu.getMenu();
            final MenuItem quoteOriginalStatus = menu.findItem(R.id.quote_original_status);
            final MenuItem linkToQuotedStatus = menu.findItem(R.id.link_to_quoted_status);
            final Uri statusLink;
            if (!status.is_quote || !quoteOriginalStatus.isChecked()) {
                statusLink = LinkCreator.getTwitterStatusLink(status.user_screen_name, status.id);
            } else {
                statusLink = LinkCreator.getTwitterStatusLink(status.quoted_user_screen_name, status.quoted_id);
            }
            final String commentText = editComment.getText() + " " + statusLink;
            ParcelableStatusUpdate update = new ParcelableStatusUpdate();
            update.text = commentText;
            update.accounts = DataStoreUtils.getAccounts(getContext(), status.account_id);
            if (linkToQuotedStatus.isChecked()) {
                update.in_reply_to_status = status;
            }
            update.is_possibly_sensitive = status.is_possibly_sensitive;
            BackgroundOperationService.updateStatusesAsync(getContext(), Draft.Action.QUOTE, update);
        } else if (isMyRetweet(status)) {
            twitter.cancelRetweetAsync(status.account_id, status.id, status.my_retweet_id);
        } else {
            twitter.retweetStatusAsync(status.account_id, status.id);
        }
    }

    public static RetweetQuoteDialogFragment show(final FragmentManager fm, final ParcelableStatus status) {
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_STATUS, status);
        final RetweetQuoteDialogFragment f = new RetweetQuoteDialogFragment();
        f.setArguments(args);
        f.show(fm, FRAGMENT_TAG);
        return f;
    }
}