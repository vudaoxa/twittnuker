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

package de.vanita5.twittnuker.fragment

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ItemDecoration
import android.view.*
import kotlinx.android.synthetic.main.fragment_content_recyclerview.*
import kotlinx.android.synthetic.main.layout_content_fragment_common.*
import de.vanita5.twittnuker.R
import de.vanita5.twittnuker.activity.iface.IControlBarActivity
import de.vanita5.twittnuker.activity.iface.IControlBarActivity.ControlBarShowHideHelper
import de.vanita5.twittnuker.adapter.LoadMoreSupportAdapter
import de.vanita5.twittnuker.adapter.iface.ILoadMoreSupportAdapter
import de.vanita5.twittnuker.adapter.iface.ILoadMoreSupportAdapter.IndicatorPosition
import de.vanita5.twittnuker.fragment.iface.RefreshScrollTopInterface
import de.vanita5.twittnuker.util.*
import de.vanita5.twittnuker.view.ExtendedSwipeRefreshLayout
import de.vanita5.twittnuker.view.HeaderDrawerLayout
import de.vanita5.twittnuker.view.iface.IExtendedView

abstract class AbsContentRecyclerViewFragment<A : LoadMoreSupportAdapter<RecyclerView.ViewHolder>,
        L : RecyclerView.LayoutManager> : BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        HeaderDrawerLayout.DrawerCallback, RefreshScrollTopInterface, IControlBarActivity.ControlBarOffsetListener,
        ContentScrollHandler.ContentListSupport, ControlBarShowHideHelper.ControlBarAnimationListener {

    lateinit var layoutManager: L
        protected set
    override lateinit var adapter: A
        protected set
    var itemDecoration: ItemDecoration? = null
        private set

    // Callbacks and listeners
    private lateinit var drawerCallback: SimpleDrawerCallback
    lateinit var scrollListener: RecyclerViewScrollHandler
    // Data fields
    private val systemWindowsInsets = Rect()

    override fun canScroll(dy: Float): Boolean {
        return drawerCallback.canScroll(dy)
    }

    override fun cancelTouch() {
        drawerCallback.cancelTouch()
    }

    override fun fling(velocity: Float) {
        drawerCallback.fling(velocity)
    }

    override fun isScrollContent(x: Float, y: Float): Boolean {
        return drawerCallback.isScrollContent(x, y)
    }

    override fun onControlBarOffsetChanged(activity: IControlBarActivity, offset: Float) {
        updateRefreshProgressOffset()
    }

    override final fun onRefresh() {
        if (!triggerRefresh()) {
            refreshing = false
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        updateRefreshProgressOffset()
    }

    override fun scrollBy(dy: Float) {
        drawerCallback.scrollBy(dy)
    }

    override fun scrollToStart(): Boolean {
        scrollToPositionWithOffset(0, 0)
        recyclerView.stopScroll()
        setControlVisible(true)
        return true
    }

    protected abstract fun scrollToPositionWithOffset(position: Int, offset: Int)

    override fun onControlBarVisibleAnimationFinish(visible: Boolean) {
        updateRefreshProgressOffset()
    }

    override fun setControlVisible(visible: Boolean) {
        val activity = activity
        if (activity is IControlBarActivity) {
            //TODO hide only if top > actionBar.height
            val manager = layoutManager
            if (manager.childCount == 0) return
            val firstView = manager.getChildAt(0)
            if (manager.getPosition(firstView) != 0) {
                activity.setControlBarVisibleAnimate(visible, this)
                return
            }
            val top = firstView.top
            activity.setControlBarVisibleAnimate(visible || top > 0, this)
        }
    }

    override fun shouldLayoutHeaderBottom(): Boolean {
        return drawerCallback.shouldLayoutHeaderBottom()
    }

    override fun topChanged(offset: Int) {
        drawerCallback.topChanged(offset)
    }

    override var refreshing: Boolean
        get () = swipeLayout.isRefreshing
        set(value) {
            val currentRefreshing = swipeLayout.isRefreshing
            if (!currentRefreshing) {
                updateRefreshProgressOffset()
            }
            if (value == currentRefreshing) return
            val layoutRefreshing = value && adapter.loadMoreIndicatorPosition != ILoadMoreSupportAdapter.NONE
            swipeLayout.isRefreshing = layoutRefreshing
        }

    var refreshEnabled: Boolean
        get() = swipeLayout.isEnabled
        set(value) {
            swipeLayout.isEnabled = value
        }

    override fun onLoadMoreContents(@IndicatorPosition position: Long) {
        setLoadMoreIndicatorPosition(position)
        refreshEnabled = position == ILoadMoreSupportAdapter.NONE
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is IControlBarActivity) {
            context.registerControlBarOffsetListener(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_content_recyclerview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        drawerCallback = SimpleDrawerCallback(recyclerView)

        val backgroundColor = ThemeUtils.getThemeBackgroundColor(context)
        val colorRes = TwidereColorUtils.getContrastYIQ(backgroundColor,
                R.color.bg_refresh_progress_color_light, R.color.bg_refresh_progress_color_dark)
        swipeLayout.setOnRefreshListener(this)
        swipeLayout.setProgressBackgroundColorSchemeResource(colorRes)
        adapter = onCreateAdapter(context)
        layoutManager = onCreateLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        if (swipeLayout is ExtendedSwipeRefreshLayout) {
            (swipeLayout as ExtendedSwipeRefreshLayout).setTouchInterceptor(object : IExtendedView.TouchInterceptor {
                override fun dispatchTouchEvent(view: View, event: MotionEvent): Boolean {
                    return false
                }

                override fun onInterceptTouchEvent(view: View, event: MotionEvent): Boolean {
                    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                        updateRefreshProgressOffset()
                    }
                    return false
                }

                override fun onTouchEvent(view: View, event: MotionEvent): Boolean {
                    return false
                }

            })
        }
        setupRecyclerView(context, recyclerView)
        recyclerView.adapter = adapter

        scrollListener = RecyclerViewScrollHandler(this, RecyclerViewScrollHandler.RecyclerViewCallback(recyclerView))
        scrollListener.touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        recyclerView.setOnTouchListener(scrollListener.touchListener)
    }

    protected open fun setupRecyclerView(context: Context, recyclerView: RecyclerView) {
        itemDecoration = createItemDecoration(context, recyclerView, layoutManager)
        if (itemDecoration != null) {
            recyclerView.addItemDecoration(itemDecoration)
        }
    }

    protected abstract fun onCreateLayoutManager(context: Context): L

    override fun onStart() {
        super.onStart()
        recyclerView.addOnScrollListener(scrollListener)
    }

    override fun onStop() {
        recyclerView.removeOnScrollListener(scrollListener)
        super.onStop()
    }


    override fun onDetach() {
        val activity = activity
        if (activity is IControlBarActivity) {
            activity.unregisterControlBarOffsetListener(this)
        }
        super.onDetach()
    }

    protected open val extraContentPadding: Rect
        get() = Rect()

    override fun fitSystemWindows(insets: Rect) {
        val extraPadding = extraContentPadding
        recyclerView.setPadding(insets.left + extraPadding.left, insets.top + extraPadding.top,
                insets.right + extraPadding.right, insets.bottom + extraPadding.bottom)
        errorContainer.setPadding(insets.left, insets.top, insets.right, insets.bottom)
        progressContainer.setPadding(insets.left, insets.top, insets.right, insets.bottom)
        systemWindowsInsets.set(insets)
        updateRefreshProgressOffset()
    }

    open fun setLoadMoreIndicatorPosition(@IndicatorPosition position: Long) {
        adapter.loadMoreIndicatorPosition = position
    }

    override fun triggerRefresh(): Boolean {
        return false
    }

    protected abstract fun onCreateAdapter(context: Context): A


    protected open fun createItemDecoration(context: Context,
                                            recyclerView: RecyclerView,
                                            layoutManager: L): ItemDecoration? {
        return null
    }

    protected fun showContent() {
        errorContainer.visibility = View.GONE
        progressContainer.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    protected fun showProgress() {
        errorContainer.visibility = View.GONE
        progressContainer.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    protected fun showError(icon: Int, text: CharSequence) {
        errorContainer.visibility = View.VISIBLE
        progressContainer.visibility = View.GONE
        recyclerView.visibility = View.GONE
        errorIcon.setImageResource(icon)
        errorText.text = text
    }

    protected fun showEmpty(icon: Int, text: CharSequence) {
        errorContainer.visibility = View.VISIBLE
        progressContainer.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        errorIcon.setImageResource(icon)
        errorText.text = text
    }

    protected fun updateRefreshProgressOffset() {
        val activity = activity
        val insets = this.systemWindowsInsets
        if (activity !is IControlBarActivity || insets.top == 0 || swipeLayout == null || refreshing) {
            return
        }
        val progressCircleDiameter = swipeLayout.progressCircleDiameter
        if (progressCircleDiameter == 0) return
        val density = resources.displayMetrics.density
        val controlBarOffsetPixels = Math.round(activity.controlBarHeight * (1 - activity.controlBarOffset))
        val swipeStart = insets.top - controlBarOffsetPixels - progressCircleDiameter
        // 64: SwipeRefreshLayout.DEFAULT_CIRCLE_TARGET
        val swipeDistance = Math.round(64 * density)
        swipeLayout.setProgressViewOffset(false, swipeStart, swipeStart + swipeDistance)
    }
}