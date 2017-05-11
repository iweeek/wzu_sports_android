package com.application.library.widget.horizontal;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单的横向 listview
 * 
 * @author CCCMAX
 */
public class HorizontalSimpleListView extends HorizontalScrollLinearlayout implements View.OnClickListener
{
    /** 当前控件的宽度 */
    int parentwidth = 0;
    /** 当前所有显示的item的最小index */
    int current_items_index_min = 0;
    /** 当前所有显示的item的最大index */
    int current_items_index_max = 0;

    /** 保存View与位置的键值对 */
    private Map<View, ItemInfo> mViewPos = new HashMap<View, ItemInfo>();

    public HorizontalSimpleListView(Context context)
    {
        super(context);
        parentwidth = getParentWidth();
    }

    public HorizontalSimpleListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        parentwidth = getParentWidth();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
    }

    boolean needTouchEvent = false;

    protected boolean subclassNeedTouchEvent()
    {
        return needTouchEvent;
    }

    float _mLastMotionX = 0;
    float _mLastMotionY = 0;

    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        Log.d("cccmax", "-------------------");
        if (scrollState == OnHLScrollListener.SCROLL_STATE_FLING)
        {
            Log.d("cccmax", "立刻停止");
            stop_now = true;
        }

        final float x = ev.getX();
        final float y = ev.getY();
        switch (ev.getAction())
        {
        case MotionEvent.ACTION_DOWN :
            _mLastMotionX = x;
            _mLastMotionY = y;
            needTouchEvent = false;
            break;
        case MotionEvent.ACTION_MOVE :
            int dx = (int) (x - _mLastMotionX);
            int dy = (int) (y - _mLastMotionY);
            int dx_abs = Math.abs(dx);
            int dy_abs = Math.abs(dy);
            if (dx_abs > dy_abs)// 横滑
            {
                if (dx > 0)
                {
                    // 手指向右 判断左侧
                    needTouchEvent = !(current_items_index_min == 0);
                    Log.d("cccmax", "dispatchTouchEvent 左侧 needTouchEvent=" + needTouchEvent);
                } else
                {
                    // 手指向左 判断右侧
                    needTouchEvent = !(current_items_index_max == mAdapter.getCount() - 1);
                    Log.d("cccmax", "dispatchTouchEvent 右侧 needTouchEvent=" + needTouchEvent);
                }
            }
            Log.d("cccmax", "dispatchTouchEvent needTouchEvent=" + needTouchEvent);
            break;
        default:
            needTouchEvent = false;
            break;
        }
        return super.dispatchTouchEvent(ev);
    }

    boolean isTouch = false;

    float l_x = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        float ev_x = ev.getX();
        switch (ev.getAction())
        {
        case MotionEvent.ACTION_DOWN :
            isTouch = true;
            break;
        case MotionEvent.ACTION_MOVE :
            isTouch = true;
            onScrollStateChanged(OnHLScrollListener.SCROLL_STATE_TOUCH_SCROLL);// 触摸滑动
            Log.e("cccmax", getScrollX() + " down_x＝" + l_x + " move_x=" + ev_x);
            int scrollX = getScrollX();

            int firstItemWidth = getCurrentLayoutItemWidth(0);
            if (l_x < ev_x)
            {
                // 加载左侧
                // 如果当前scrollX = 0， 往前设置一张，移除最后一张
                if (scrollX <= 0)
                {
                    loadPreItem();
                }
            } else if (l_x > ev_x)
            {
                // 加载右侧
                // 如果当前scrollX为第一个item的宽度，加载下一个，移除第一个
                if (scrollX >= firstItemWidth && firstItemWidth > 0)
                {
                    loadNextItem();
                }
            }

            break;
        // case MotionEvent.ACTION_CANCEL :
        case MotionEvent.ACTION_UP :
        {
            isTouch = false;
            // 延迟10毫秒发布滑动停止的状态变更 如果有惯性滑动发生的话 会取消这条
            handler_touch.sendEmptyMessageDelayed(HANDLER_TYPE_UP, 10);
        }
            break;
        }
        l_x = ev_x;
        return super.onTouchEvent(ev);
    }

    final int HANDLER_TYPE_FLING = 0x1001;
    final int HANDLER_TYPE_UP = 0x1002;
    final int HANDLER_TYPE_FLING_SCROLL = 0x1003;
    int lastScrollX = 0;
    /**
     * 用于用户手指UP的时候获取MyScrollView滚动的Y距离，然后回调给onScroll方法中
     */
    private Handler handler_touch = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            // case HANDLER_TYPE_FLING :// 手指抬起后的惯性滑动 每5毫秒获取一次
            // {
            // if (isTouch)
            // {
            // // 惯性滑动的时候 再次触摸 产生touch事件 终止当前操作
            // handler_touch.removeMessages(HANDLER_TYPE_FLING);
            // onScrollStateChanged(OnHLScrollListener.SCROLL_STATE_TOUCH_SCROLL);// 触摸滑动
            // return;
            // } else
            // {
            // int scrollX = HorizontalSimpleListView.this.getScrollX();
            // // 此时的距离和记录下的距离不相等，在隔5毫秒给handler发送消息
            // if (lastScrollX != scrollX)// 滑动位置不变了的时候就是停止了 否则还在滚动持续定时获取
            // {
            // lastScrollX = scrollX;
            // onScrollStateChanged(OnHLScrollListener.SCROLL_STATE_FLING);// 惯性滑动
            // handler_touch.sendEmptyMessageDelayed(HANDLER_TYPE_FLING, 25);
            // onScroll();
            // } else
            // {
            // onScrollStateChanged(OnHLScrollListener.SCROLL_STATE_IDLE);// 停止滑动
            // }
            // }
            // }
            // break;
            case HANDLER_TYPE_UP :
            {
                onScrollStateChanged(OnHLScrollListener.SCROLL_STATE_IDLE);// 停止滑动
                break;
            }
            }

        };
    };

    boolean stop_now = false;

    /**
     * 模拟惯性滑动
     */
    Handler handler_scroll = new Handler()
    {
        int setp_t = 1000 / 50; // 一秒钟刷新40次

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            case HANDLER_TYPE_FLING_SCROLL :
            {
                if (isTouch || stop_now)
                {
                    stop_now = false;

                    // 惯性滑动的时候 再次触摸 产生touch事件 终止当前操作
                    handler_touch.removeMessages(HANDLER_TYPE_FLING);
                    onScrollStateChanged(OnHLScrollListener.SCROLL_STATE_TOUCH_SCROLL);// 触摸滑动
                    return;
                } else if (msg.arg1 == 0)
                {
                    // 速度 ＝ 0
                    onScrollStateChanged(OnHLScrollListener.SCROLL_STATE_IDLE);// 停止滑动
                    return;
                } else
                {
                    HorizontalSimpleListView.this.requestFocus();

                    onScrollStateChanged(OnHLScrollListener.SCROLL_STATE_FLING);// 惯性滑动状态

                    int scrollX = HorizontalSimpleListView.this.getScrollX();

                    onScroll();

                    int velocityX = msg.arg1;
                    int time = msg.arg2;

                    int direction = velocityX > 0 ? 1 : -1;// 方向

                    // 当先时间对应的衰退速度
                    double current_velocity = getDecayCurve_Y(Math.abs(velocityX), time);
                    // 当前需要滑动的距离 ＝ 时间 * 速度　
                    int scoll_x = (int) (setp_t / 1000.0F * current_velocity);

                    int st = getScrollX() + scoll_x * direction;
                    Log.d("cccmax", "handler_scroll getScrollX()=" + getScrollX() + "  scoll_x=" + scoll_x * direction);
                    scrollTo(st, 0);

                    if (scoll_x > 0 && current_velocity > 200 && lastScrollX != scrollX)
                    {
                        lastScrollX = scrollX;

                        Message m = new Message();
                        m.what = HANDLER_TYPE_FLING_SCROLL;
                        m.arg1 = velocityX;// 速度最大值
                        m.arg2 = time + setp_t;// 衰退时间
                        handler_scroll.sendMessageDelayed(m, setp_t);
                    } else
                    {
                        onScrollStateChanged(OnHLScrollListener.SCROLL_STATE_IDLE);// 停止滑动
                        lastScrollX = -1;
                    }
                }
                break;
            }
            }
        }
    };

    @Override
    public void fling(int velocityX)
    {
        Log.d("cccmax", "fling velocity=" + velocityX + "    contentwidth 3  " + "right=" + content_layout.getWidth()
                + "  scalex=" + getScrollX());

        if (velocityX != 0 && !isTouch)
        {
            Message scrollmsg = new Message();
            scrollmsg.what = HANDLER_TYPE_FLING_SCROLL;
            scrollmsg.arg1 = velocityX;
            handler_scroll.removeMessages(HANDLER_TYPE_FLING_SCROLL);
            handler_scroll.sendMessage(scrollmsg);

            // handler_touch.removeMessages(HANDLER_TYPE_UP);
            // handler_touch.removeMessages(HANDLER_TYPE_FLING);
            // handler_touch.sendEmptyMessageDelayed(HANDLER_TYPE_FLING, 0);
        }

        super.fling(0);
    }

    protected void onAddFirstItemSetMargins(View childview, LinearLayout.LayoutParams childlp)
    {
        ItemInfo ii = mViewPos.get(childview);
        if (ii != null)
        {
            if (ii.position > 0)
            {
                // 如果 整个list范围内 前面还有别的item 就添加间隔
                childlp.setMargins(childlp.leftMargin + divide_horizontal, childlp.topMargin, childlp.rightMargin,
                        childlp.bottomMargin);
                ii.width += divide_horizontal;
            }
        }
    }

    /**
     * 获取当前容器中某个Item的宽度
     *
     * @param layout_index
     *            当前容器中的索引位置
     * @return
     */
    private int getCurrentLayoutItemWidth(int layout_index)
    {
        int ret = 0;
        if (content_layout != null && content_layout.getChildCount() > layout_index)
        {
            View itemview = content_layout.getChildAt(layout_index);
            ItemInfo ii = mViewPos.get(itemview);
            if (ii != null)
            {
                ret = ii.width;
            }
        }
        return ret;
    }

    /** 与adapter之间的绑定通知 */
    DataSetObserver mDataSetObserver = new DataSetObserver()
    {
        // 数据改变 Adapter notifyDataSetChanged 时 调用
        public void onChanged()
        {
            // TODO
            content_layout.removeAllViews();
            initItems();
        }

        // 数据失效 Adapter notifyDataSetInvalidated 时 调用
        public void onInvalidated()
        {
            // TODO
        }
    };

    public interface OnItemClickListener
    {
        public void onItemClick(BaseAdapter adapter, View view, int position);
    }

    OnItemClickListener onitemclicklistener = null;

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        onitemclicklistener = listener;
    }

    BaseAdapter mAdapter;

    /**
     * 设置adapter
     *
     * @param adapter
     */
    public void setAdapter(BaseAdapter adapter)
    {
        if (mAdapter != null)
        {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        mAdapter = adapter;
        mAdapter.registerDataSetObserver(mDataSetObserver);

        if (content_layout.getChildCount() == 0)
        {
            initItems();
        }
    }

    /**
     * 针对当前数据集 首次生成屏幕上可容纳的items
     */
    private void initItems()
    {
        content_layout.removeAllViews();
        mViewPos.clear();

        current_items_index_min = 0;
        current_items_index_max = 0;

        if (mAdapter == null || mAdapter.getCount() <= 0)
            return;

        int itemsWidth = 0;

        for (int i = 0; i < mAdapter.getCount(); i++)
        {
            View childview = mAdapter.getView(i, null, content_layout);
            childview.setOnClickListener(this);
            measureChildview(childview);
            addItemView(childview);
            int[] cv_size = getChildviewSizeMargin(childview);
            ItemInfo iteminfo = new ItemInfo(i, cv_size[0], cv_size[1]);
            mViewPos.put(childview, iteminfo);

            itemsWidth += cv_size[0];
            if ((float) (parentwidth * 1.5f) < itemsWidth)// 初次
            {
                current_items_index_max = i;
                break;
            }
        }

    }

    /**
     * 加载下一个Item
     */
    protected void loadNextItem()
    {
        // 数组边界值计算
        if (current_items_index_max == mAdapter.getCount() - 1)
            return;

        int firstItemWidth = getCurrentLayoutItemWidth(0);

        Log.d("cccmax", "loadnextitem");

        // 移除第一张图片，且将水平滚动位置置0
        mViewPos.remove(content_layout.getChildAt(0));
        content_layout.removeViewAt(0);
        scrollTo(0, 0);

        // 获取下一张图片，并且设置onclick事件，且加入容器中
        View view = mAdapter.getView(++current_items_index_max, null, content_layout);
        view.setOnClickListener(this);
        measureChildview(view);
        addItemView(view);
        int[] cv_size = getChildviewSizeMargin(view);
        ItemInfo iteminfo = new ItemInfo(current_items_index_max, cv_size[0], cv_size[1]);
        mViewPos.put(view, iteminfo);

        // 当前item最小索引递增
        current_items_index_min++;
        Log.d("cccmax", "loadnextitem-----end");
    }

    /**
     * 加载前一个Item
     */
    protected void loadPreItem()
    {
        // 如果当前已经是第一个Item，则返回
        if (current_items_index_min == 0)
            return;
        Log.d("cccmax", "loadPreItem--- start");

        // 获得当前应该显示为第一个Item的下标
        int index = current_items_index_min - 1;
        if (index >= 0)
        {
            Log.d("cccmax", "loadPreItem--- contentwidth 0 =" + content_layout.getWidth());
            // 移除最后一个Item
            int oldViewPos = content_layout.getChildCount() - 1;
            mViewPos.remove(content_layout.getChildAt(oldViewPos));
            content_layout.removeViewAt(oldViewPos);

            // 将此View放入第一个位置
            View view = mAdapter.getView(index, null, content_layout);
            measureChildview(view);
            int[] cv_size = getChildviewSizeMargin(view);
            ItemInfo iteminfo = new ItemInfo(index, cv_size[0], cv_size[1]);
            mViewPos.put(view, iteminfo);
            Log.d("cccmax", "loadPreItem--- contentwidth 1 =" + content_layout.getWidth());
            addItemView(view, 0);
            Log.d("cccmax", "loadPreItem--- contentwidth 2 =" + content_layout.getWidth());
            view.setOnClickListener(this);
            // 水平滚动位置向左移动view的宽度个像素
            scrollTo(cv_size[0], 0);
            // 当前位置--，当前第一个显示的下标--
            current_items_index_min--;
            current_items_index_max--;
            Log.d("cccmax", "loadPreItem--- end");
        }
    }

    private int getParentWidth()
    {
        int ret = getViewWidth(this);
        if (ret <= 0)
        {
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            ret = wm.getDefaultDisplay().getWidth();
        }
        Log.d("cccmax", "getParentWidth = " + ret);
        return ret;
    }

    private int[] measureChildview(View childview)
    {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        childview.measure(w, h);
        int mChildWidth = childview.getMeasuredWidth();
        int mChildHeight = childview.getMeasuredHeight();
        int[] ret =
        { mChildWidth, mChildHeight };
        // Log.d("cccmax", "measureChildview w=" + mChildWidth + " h=" + mChildHeight);
        return ret;
    }

    private int[] getChildviewSizeMargin(View childview)
    {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) childview.getLayoutParams();
        int w = childview.getMeasuredWidth() + (lp == null ? 0 : lp.leftMargin + lp.rightMargin);
        int h = childview.getMeasuredHeight() + (lp == null ? 0 : lp.topMargin + lp.bottomMargin);
        int[] ret =
        { w, h };
        Log.d("cccmax", "getChildviewSize w=" + ret[0] + " h=" + ret[1]);
        return ret;
    }

    @Override
    public void onClick(View v)
    {
        if (onitemclicklistener != null)
        {
            // Iterator iter = mViewPos.entrySet().iterator();
            // while (iter.hasNext())
            // {
            // Map.Entry entry = (Map.Entry) iter.next();
            // Object key = entry.getKey();
            // Object val = entry.getValue();
            // }
            onitemclicklistener.onItemClick(mAdapter, v, mViewPos.get(v).position);
        }
    }

    /**
     * 计算一个View的宽度 不包括左右内边距，前提是这个view的parent已经放在屏幕上了
     * 
     * @param v
     * @return
     */
    public static int getViewWidth(View v)
    {
        if (v == null)
            return 0;
        int ret = 0;
        try
        {
            int pddingleft = v.getPaddingLeft();
            int paddingright = v.getPaddingRight();
            int leftMargin = 0;
            int rightMargin = 0;
            ViewGroup.LayoutParams vlp = v.getLayoutParams();
            if (vlp != null && vlp instanceof ViewGroup.MarginLayoutParams)
            {
                leftMargin = ((ViewGroup.MarginLayoutParams) vlp).leftMargin;
                rightMargin = ((ViewGroup.MarginLayoutParams) vlp).rightMargin;
            }

            if (v.getWidth() > 0)
            {
                ret = v.getWidth() - pddingleft - paddingright;
            } else
            {
                ViewParent vp = v.getParent();
                if (vp != null && vp instanceof View)
                {
                    View vp_view = (View) vp;
                    int vp_width = getViewWidth(vp_view);
                    if (vp_width > 0)
                    {
                        ret = vp_width - leftMargin - rightMargin - pddingleft - paddingright;
                    }
                } else
                {
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return ret;
    }

    public class ItemInfo
    {
        public int position = -1;
        public int width = 0;
        public int height = 0;

        public ItemInfo(int position, int width, int height)
        {
            this.position = position;
            this.width = width;
            this.height = height;
        }
    }

    public interface OnHLScrollListener
    {

        /** 没有滑动 */
        public static int SCROLL_STATE_IDLE = 0;
        /** 触摸滑动 */
        public static int SCROLL_STATE_TOUCH_SCROLL = 1;
        /** 惯性滑动 */
        public static int SCROLL_STATE_FLING = 2;

        /**
         * 滑动状态改变
         * 
         * @param view
         * @param scrollState
         *            滑动状态 {@link #SCROLL_STATE_IDLE}, {@link #SCROLL_STATE_TOUCH_SCROLL} or {@link #SCROLL_STATE_IDLE}.
         */
        public void onScrollStateChanged(HorizontalSimpleListView view, int scrollState);

        /**
         * 滑动中
         * 
         * @param view
         *            The view whose scroll state is being reported
         * @param firstVisibleItem
         *            the index of the first visible cell (ignore if visibleItemCount == 0)
         * @param visibleItemCount
         *            the number of visible cells
         * @param totalItemCount
         *            the number of items in the list adaptor
         */
        public void onScroll(HorizontalSimpleListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount);
    }

    /**
     * 滑动监听
     */
    OnHLScrollListener mOnscrollListener = null;

    public void setOnScrollStateChanged(OnHLScrollListener listener)
    {
        mOnscrollListener = listener;
    }

    private int scrollState = OnHLScrollListener.SCROLL_STATE_IDLE;

    // 滑动状态改变
    private void onScrollStateChanged(int scrollState)
    {
        if (this.scrollState == scrollState)
            return;

        this.scrollState = scrollState;

        Log.i("cccmax", "onScrollStateChanged state=" + scrollState);

        // TODO

        if (mOnscrollListener != null)
            mOnscrollListener.onScrollStateChanged(this, scrollState);
    }

    // 滑动中
    public void onScroll()
    {
        // TODO
        Log.i("cccmax", "onScroll " + getScrollX());

        if (this.scrollState == OnHLScrollListener.SCROLL_STATE_FLING)
        {
            Log.e("cccmax", "fling-----");
            int scrollX = getScrollX();
            // 如果当前scrollX为第一个item的宽度，加载下一个，移除第一个
            int firstItemWidth = getCurrentLayoutItemWidth(0);
            if (scrollX >= firstItemWidth && firstItemWidth > 0)
            {
                loadNextItem();
            }
            // 如果当前scrollX = 0， 往前设置一张，移除最后一张
            if (scrollX <= 0)
            {
                loadPreItem();
            }
        }

        int firstVisibleItem = 0;
        int visibleItemCount = 0;
        int totalItemCount = 0;
        if (mOnscrollListener != null)
            mOnscrollListener.onScroll(this, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    /**
     * 速度衰退曲线 根据【最大值】获取【当前时间】对应的【衰退值】 <br>
     * 例如 total是10000 ， 大约3000毫秒时已经非常接近0了 <br>
     * y = total * 0.999^(x*10000/total)
     * 
     * @param total
     *            最高总数
     * @param x
     *            X轴是时间轴，单位毫秒
     * @return
     */
    public static double getDecayCurve_Y(float total, float x)
    {
        double y = total * Math.pow(0.99F, (x * 2500 / total));
        Log.d("cccmax", "getDecayCurve_Y  total=" + total + " time=" + x + "  y=" + y);
        return y;
    }
}
