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

package de.vanita5.twittnuker.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.FixedLinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.mariotaku.chameleon.Chameleon;
import org.mariotaku.chameleon.ChameleonUtils;
import org.mariotaku.chameleon.ChameleonView;
import de.vanita5.twittnuker.R;
import de.vanita5.twittnuker.adapter.decorator.DividerItemDecoration;
import de.vanita5.twittnuker.util.ThemeUtils;
import de.vanita5.twittnuker.view.iface.PagerIndicator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TabPagerIndicator extends RecyclerView implements PagerIndicator, ChameleonView {


    private final int mStripHeight;
    private final TabPagerIndicatorAdapter mIndicatorAdapter;
    private final TabLayoutManager mLayoutManager;
    private final DividerItemDecoration mItemDecoration;
    private ViewPager mViewPager;
    private PagerAdapter mPagerProvider;

    private OnPageChangeListener mPageChangeListener;
    private int mOption;
    private int mHorizontalPadding, mVerticalPadding;
    private int mColumns;

    public TabPagerIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ViewCompat.setLayoutDirection(this, ViewCompat.LAYOUT_DIRECTION_LTR);
        final Resources res = getResources();
        mIndicatorAdapter = new TabPagerIndicatorAdapter(this);
        mItemDecoration = new DividerItemDecoration(context, HORIZONTAL);
        mStripHeight = res.getDimensionPixelSize(R.dimen.element_spacing_small);
        setOverScrollMode(OVER_SCROLL_NEVER);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        setLayoutManager(mLayoutManager = new TabLayoutManager(context, this));
        setItemContext(context);
        setAdapter(mIndicatorAdapter);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabPagerIndicator);
        setTabExpandEnabled(a.getBoolean(R.styleable.TabPagerIndicator_tabExpandEnabled, false));
        setHorizontalPadding(a.getDimensionPixelSize(R.styleable.TabPagerIndicator_tabHorizontalPadding, 0));
        setVerticalPadding(a.getDimensionPixelSize(R.styleable.TabPagerIndicator_tabVerticalPadding, 0));
        setStripColor(a.getColor(R.styleable.TabPagerIndicator_tabStripColor, 0));
        setIconColor(a.getColor(R.styleable.TabPagerIndicator_tabIconColor, 0));
        setLabelColor(a.getColor(R.styleable.TabPagerIndicator_tabLabelColor, ThemeUtils.getTextColorPrimary(context)));
        //noinspection WrongConstant
        setTabDisplayOption(a.getInt(R.styleable.TabPagerIndicator_tabDisplayOption, DisplayOption.ICON));
        setTabShowDivider(a.getBoolean(R.styleable.TabPagerIndicator_tabShowDivider, false));
        final int dividerVerticalPadding = a.getDimensionPixelSize(R.styleable.TabPagerIndicator_tabDividerVerticalPadding, 0);
        final int dividerHorizontalPadding = a.getDimensionPixelSize(R.styleable.TabPagerIndicator_tabDividerHorizontalPadding, 0);
        mItemDecoration.setPadding(dividerHorizontalPadding, dividerVerticalPadding,
                dividerHorizontalPadding, dividerVerticalPadding);
        mItemDecoration.setDecorationStart(0);
        mItemDecoration.setDecorationEndOffset(1);
        a.recycle();
    }

    public TabPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public TabPagerIndicator(Context context) {
        this(context, null);
    }

    public int getCount() {
        return mIndicatorAdapter.getItemCount();
    }

    public Context getItemContext() {
        return mIndicatorAdapter.getItemContext();
    }

    public int getColumns() {
        if (mColumns > 0) return mColumns;
        return 1;
    }

    public void setColumns(int columns) {
        mColumns = columns;
        notifyDataSetChanged();
    }

    public void setItemContext(Context context) {
        mIndicatorAdapter.setItemContext(context);
    }

    public int getStripHeight() {
        return mStripHeight;
    }

    @Override
    public void notifyDataSetChanged() {
        mIndicatorAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPageChangeListener == null) return;
        mPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        notifyDataSetChanged();
        if (mPageChangeListener == null) return;
        smoothScrollToPosition(position);
        mPageChangeListener.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mPageChangeListener == null) return;
        mPageChangeListener.onPageScrollStateChanged(state);
    }

    public void setBadge(int position, int count) {
        mIndicatorAdapter.setBadge(position, count);
    }

    public void clearBadge() {
        mIndicatorAdapter.clearBadge();
    }

    public void setDisplayBadge(boolean display) {
        mIndicatorAdapter.setDisplayBadge(display);
    }

    public void setIconColor(int color) {
        mIndicatorAdapter.setIconColor(color);
    }

    public void setLabelColor(int color) {
        mIndicatorAdapter.setLabelColor(color);
    }

    public void setStripColor(int color) {
        mIndicatorAdapter.setStripColor(color);
    }

    public void setTabDisplayOption(@DisplayOption int flags) {
        mOption = flags;
    }

    private void dispatchTabClick(int position) {
        final int currentItem = getCurrentItem();
        setCurrentItem(position);
        if (mPagerProvider instanceof TabListener) {
            if (currentItem != position) {
                ((TabListener) mPagerProvider).onPageSelected(position);
            } else {
                ((TabListener) mPagerProvider).onPageReselected(position);
            }
        }
    }

    private boolean dispatchTabLongClick(int position) {
        if (mPagerProvider instanceof TabListener) {
            return ((TabListener) mPagerProvider).onTabLongClick(position);
        }
        return false;
    }

    private int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public void setCurrentItem(int item) {
        mViewPager.setCurrentItem(item);
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mPageChangeListener = listener;
    }

    @Override
    public void setViewPager(ViewPager view) {
        setViewPager(view, view.getCurrentItem());
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        final PagerAdapter adapter = view.getAdapter();
        if (!(adapter instanceof TabProvider)) {
            throw new IllegalArgumentException();
        }
        mViewPager = view;
        mPagerProvider = adapter;
        view.addOnPageChangeListener(this);
        mIndicatorAdapter.setTabProvider((TabProvider) adapter);
    }

    @Override
    public boolean isPostApplyTheme() {
        return false;
    }

    @Nullable
    @Override
    public Appearance createAppearance(@NonNull Context context, @NonNull AttributeSet attributeSet, @NonNull Chameleon.Theme theme) {
        final Appearance appearance = new Appearance();
        final int toolbarColor = theme.getColorToolbar();
        final int itemColor = ChameleonUtils.getColorDependent(toolbarColor);
        appearance.setLabelColor(itemColor);
        appearance.setIconColor(itemColor);
        if (theme.isToolbarColored()) {
            appearance.setStripColor(itemColor);
        } else {
            appearance.setStripColor(theme.getColorAccent());
        }
        return appearance;
    }

    @Override
    public void applyAppearance(@NonNull ChameleonView.Appearance appearance) {
        Appearance a = (Appearance) appearance;
        setIconColor(a.getIconColor());
        setLabelColor(a.getLabelColor());
        setStripColor(a.getStripColor());
        updateAppearance();
    }

    public void updateAppearance() {
        final int positionStart = mLayoutManager.findFirstVisibleItemPosition();
        final int itemCount = mLayoutManager.findLastVisibleItemPosition() - positionStart + 1;
        mIndicatorAdapter.notifyItemRangeChanged(positionStart, itemCount);
    }

    private int getTabHorizontalPadding() {
        return mHorizontalPadding;
    }

    private int getTabVerticalPadding() {
        return mVerticalPadding;
    }

    private boolean isIconDisplayed() {
        return (mOption & DisplayOption.ICON) != 0;
    }

    private boolean isLabelDisplayed() {
        return (mOption & DisplayOption.LABEL) != 0;
    }

    private boolean isTabExpandEnabled() {
        return mLayoutManager.isTabExpandEnabled();
    }

    public void setTabExpandEnabled(boolean expandEnabled) {
        mLayoutManager.setTabExpandEnabled(expandEnabled);
    }

    private void setHorizontalPadding(int padding) {
        mHorizontalPadding = padding;
    }

    private void setTabShowDivider(boolean showDivider) {
        if (showDivider) {
            addItemDecoration(mItemDecoration);
        } else {
            removeItemDecoration(mItemDecoration);
        }
    }

    private void setVerticalPadding(int padding) {
        mVerticalPadding = padding;
    }

    private boolean isTabSelected(int position) {
        final int current = getCurrentItem();
        final int columns = getColumns();
        final int count = getCount();
        if (current + columns > count) {
            return position >= count - columns;
        }
        return position >= current && position < current + columns;
    }

    public static class Appearance implements ChameleonView.Appearance {
        @ColorInt
        int iconColor, labelColor, stripColor;

        public int getIconColor() {
            return iconColor;
        }

        public void setIconColor(int iconColor) {
            this.iconColor = iconColor;
        }

        public int getLabelColor() {
            return labelColor;
        }

        public void setLabelColor(int labelColor) {
            this.labelColor = labelColor;
        }

        public int getStripColor() {
            return stripColor;
        }

        public void setStripColor(int stripColor) {
            this.stripColor = stripColor;
        }
    }

    @IntDef({DisplayOption.ICON, DisplayOption.LABEL, DisplayOption.BOTH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DisplayOption {
        int LABEL = 0x1;
        int ICON = 0x2;
        int BOTH = LABEL | ICON;
    }

    public static final class SampleView extends LinearLayout {

        private final LayoutInflater inflater;
        private final int stripHeight;
        private int horizontalPadding;
        private int verticalPadding;
        private int stripColor;
        private int iconColor;
        private int labelColor;
        private int tabDisplayOption;
        private boolean tabShowDivider;
        private boolean tabExpandEnabled;

        public SampleView(Context context) {
            this(context, null);
        }

        public SampleView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public SampleView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            inflater = LayoutInflater.from(context);
            setOrientation(HORIZONTAL);

            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabPagerIndicator);
            this.setHorizontalPadding(a.getDimensionPixelSize(R.styleable.TabPagerIndicator_tabHorizontalPadding, 0));
            this.setTabExpandEnabled(a.getBoolean(R.styleable.TabPagerIndicator_tabExpandEnabled, false));
            this.setVerticalPadding(a.getDimensionPixelSize(R.styleable.TabPagerIndicator_tabVerticalPadding, 0));
            this.setStripColor(a.getColor(R.styleable.TabPagerIndicator_tabStripColor, 0));
            this.setIconColor(a.getColor(R.styleable.TabPagerIndicator_tabIconColor, 0));
            this.setLabelColor(a.getColor(R.styleable.TabPagerIndicator_tabLabelColor, ThemeUtils.getTextColorPrimary(context)));
            this.setTabDisplayOption(a.getInt(R.styleable.TabPagerIndicator_tabDisplayOption, DisplayOption.ICON));
            this.setTabShowDivider(a.getBoolean(R.styleable.TabPagerIndicator_tabShowDivider, false));
            a.recycle();

            stripHeight = context.getResources().getDimensionPixelSize(R.dimen.element_spacing_small);
        }

        public void setHorizontalPadding(int horizontalPadding) {
            this.horizontalPadding = horizontalPadding;
        }

        public void setVerticalPadding(int verticalPadding) {
            this.verticalPadding = verticalPadding;
        }

        public void setStripColor(int stripColor) {
            this.stripColor = stripColor;
        }

        public void setIconColor(int iconColor) {
            this.iconColor = iconColor;
        }

        public void setLabelColor(int labelColor) {
            this.labelColor = labelColor;
        }

        public void setTabDisplayOption(int tabDisplayOption) {
            this.tabDisplayOption = tabDisplayOption;
        }

        public void setTabShowDivider(boolean tabShowDivider) {
            this.tabShowDivider = tabShowDivider;
        }

        public void addTab(int icon, CharSequence label, int unread, boolean isCurrent) {
            final ItemLayout layout = (ItemLayout) inflater.inflate(R.layout.layout_tab_item, this, false);
            final ImageView tabIcon = (ImageView) layout.findViewById(R.id.tab_icon);
            final BadgeView badgeView = (BadgeView) layout.findViewById(R.id.unread_indicator);
            final TextView tabLabel = (TextView) layout.findViewById(R.id.tab_label);

            layout.setStripColor(stripColor);
            layout.setStripHeight(stripHeight);
            layout.setIsCurrent(isCurrent);

            tabIcon.setImageResource(icon);
            tabIcon.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);
            tabIcon.setVisibility((tabDisplayOption & DisplayOption.ICON) != 0 ? VISIBLE : GONE);

            tabLabel.setText(label);
            tabLabel.setTextColor(labelColor);
            tabLabel.setVisibility((tabDisplayOption & DisplayOption.LABEL) != 0 ? VISIBLE : GONE);

            badgeView.setText(String.valueOf(unread));
            badgeView.setVisibility(unread != 0 ? VISIBLE : GONE);

            final LayoutParams params;
            if (tabExpandEnabled) {
                params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
            } else {
                params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 0);
            }
            addView(layout, params);
        }

        public void setTabExpandEnabled(boolean tabExpandEnabled) {
            this.tabExpandEnabled = tabExpandEnabled;
        }
    }

    public static final class ItemLayout extends RelativeLayout {

        private final Paint mStripPaint;

        private boolean mIsCurrent;
        private int mStripColor;
        private int mStripHeight;

        public ItemLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            mStripPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            setWillNotDraw(false);
        }

        public void setIsCurrent(boolean isCurrent) {
            if (mIsCurrent == isCurrent) return;
            mIsCurrent = isCurrent;
            invalidate();
        }

        public void setStripColor(int stripColor) {
            if (mStripColor == stripColor) return;
            mStripColor = stripColor;
            mStripPaint.setColor(stripColor);
            invalidate();
        }

        public void setStripHeight(int stripHeight) {
            if (mStripHeight == stripHeight) return;
            mStripHeight = stripHeight;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (mIsCurrent) {
                final int width = canvas.getWidth(), height = canvas.getHeight();
                canvas.drawRect(0, height - mStripHeight, width, height, mStripPaint);
            }
            super.onDraw(canvas);
        }
    }

    private static class TabItemHolder extends ViewHolder implements OnClickListener, OnLongClickListener {

        private final TabPagerIndicator indicator;
        private final ItemLayout itemView;
        private final ImageView iconView;
        private final TextView labelView;
        private final BadgeView badgeView;

        TabItemHolder(TabPagerIndicator indicator, View itemView) {
            super(itemView);
            this.indicator = indicator;
            this.itemView = (ItemLayout) itemView;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            iconView = (ImageView) itemView.findViewById(R.id.tab_icon);
            labelView = (TextView) itemView.findViewById(R.id.tab_label);
            badgeView = (BadgeView) itemView.findViewById(R.id.unread_indicator);
        }

        @Override
        public void onClick(View v) {
            indicator.dispatchTabClick(getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            final int position = getLayoutPosition();
            if (position == RecyclerView.NO_POSITION) return false;
            return indicator.dispatchTabLongClick(position);
        }

        void setBadge(int count, boolean display) {
            badgeView.setText(String.valueOf(count));
            badgeView.setVisibility(display && count > 0 ? VISIBLE : GONE);
        }

        void setDisplayOption(boolean iconDisplayed, boolean labelDisplayed) {
            iconView.setVisibility(iconDisplayed ? VISIBLE : GONE);
            labelView.setVisibility(labelDisplayed ? VISIBLE : GONE);
        }

        void setIconColor(int color) {
            if (color != 0) {
                iconView.setColorFilter(color);
            } else {
                iconView.clearColorFilter();
            }
        }

        void setLabelColor(int color) {
            labelView.setTextColor(color);
        }

        void setPadding(int horizontalPadding, int verticalPadding) {
            itemView.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        }

        void setStripColor(int color) {
            itemView.setStripColor(color);
        }

        void setStripHeight(int stripHeight) {
            itemView.setStripHeight(stripHeight);
        }

        void setTabData(Drawable icon, CharSequence title, boolean activated) {
            itemView.setContentDescription(title);
            iconView.setImageDrawable(icon);
            labelView.setText(title);
            itemView.setIsCurrent(activated);
        }
    }

    private static class TabLayoutManager extends FixedLinearLayoutManager {

        private boolean mTabExpandEnabled;
        private final RecyclerView mRecyclerView;

        TabLayoutManager(Context context, RecyclerView recyclerView) {
            super(context, HORIZONTAL, false);
            mRecyclerView = recyclerView;
            setAutoMeasureEnabled(true);
        }

        @Override
        public void measureChildWithMargins(View child, int widthUsed, int heightUsed) {
            // first get default measured size
            super.measureChildWithMargins(child, widthUsed, heightUsed);
            if (!isTabExpandEnabled()) return;
            final int count = getItemCount();
            if (count == 0) return;
            final int parentHeight = mRecyclerView.getHeight(), parentWidth = mRecyclerView.getWidth();
            final int decoratedWidth = getDecoratedMeasuredWidth(child);
            final int measuredWidth = child.getMeasuredWidth();
            final int decoratorWidth = decoratedWidth - measuredWidth;
            final int width = Math.max(measuredWidth, parentWidth / count - decoratorWidth);
            final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(parentHeight, MeasureSpec.EXACTLY);
            final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            child.measure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        protected boolean isLayoutRTL() {
            return false;
        }

        boolean isTabExpandEnabled() {
            return mTabExpandEnabled;
        }

        void setTabExpandEnabled(boolean tabExpandEnabled) {
            mTabExpandEnabled = tabExpandEnabled;
        }
    }

    private static class TabPagerIndicatorAdapter extends Adapter<TabItemHolder> {

        private final TabPagerIndicator mIndicator;
        private final SparseIntArray mUnreadCounts;
        private Context mItemContext;
        private LayoutInflater mInflater;

        private TabProvider mTabProvider;
        private int mStripColor, mIconColor, mLabelColor;
        private boolean mDisplayBadge;

        TabPagerIndicatorAdapter(TabPagerIndicator indicator) {
            mIndicator = indicator;
            mUnreadCounts = new SparseIntArray();
        }

        Context getItemContext() {
            return mItemContext;
        }

        void setItemContext(Context itemContext) {
            mItemContext = itemContext;
            mInflater = LayoutInflater.from(itemContext);
        }

        @Override
        public TabItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (mIndicator == null) throw new IllegalStateException("item context not set");
            final View view = mInflater.inflate(R.layout.layout_tab_item, parent, false);
            final TabItemHolder holder = new TabItemHolder(mIndicator, view);
            holder.setStripHeight(mIndicator.getStripHeight());
            holder.setPadding(mIndicator.getTabHorizontalPadding(), mIndicator.getTabVerticalPadding());
            holder.setDisplayOption(mIndicator.isIconDisplayed(), mIndicator.isLabelDisplayed());
            return holder;
        }

        @Override
        public void onBindViewHolder(TabItemHolder holder, int position) {
            final Drawable icon = mTabProvider.getPageIcon(position);
            final CharSequence title = mTabProvider.getPageTitle(position);
            holder.setTabData(icon, title, mIndicator.isTabSelected(position));
            holder.setBadge(mUnreadCounts.get(position, 0), mDisplayBadge);

            holder.setStripColor(mStripColor);
            holder.setIconColor(mIconColor);
            holder.setLabelColor(mLabelColor);
        }

        @Override
        public int getItemCount() {
            if (mTabProvider == null) return 0;
            return mTabProvider.getCount();
        }

        void setBadge(int position, int count) {
            mUnreadCounts.put(position, count);
            notifyItemChanged(position);
        }

        void clearBadge() {
            mUnreadCounts.clear();
            notifyDataSetChanged();
        }

        void setDisplayBadge(boolean display) {
            mDisplayBadge = display;
        }

        void setIconColor(int color) {
            mIconColor = color;
        }

        void setLabelColor(int color) {
            mLabelColor = color;
        }

        void setStripColor(int color) {
            mStripColor = color;
        }

        void setTabProvider(TabProvider tabProvider) {
            mTabProvider = tabProvider;
        }
    }
}