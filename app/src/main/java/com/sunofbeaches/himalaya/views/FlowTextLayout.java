package com.sunofbeaches.himalaya.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunofbeaches.himalaya.R;

import java.util.ArrayList;
import java.util.List;

public class FlowTextLayout extends ViewGroup {

    private static final String TAG = "FlowTextLayout";
    //每一行内容
    private List<Line> mLines = new ArrayList<>();
    private Line mCurrentLineCursor;
    private int mHorizontalSpace = 20;
    private int mVerticalSpace = 20;
    private ItemClickListener mItemClickListener = null;

    public FlowTextLayout(Context context) {
        this(context, null);
    }

    public FlowTextLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowTextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setTextContents(List<String> texts) {
        this.removeAllViews();
        for (final String text : texts) {
            TextView item = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_text, this, false);
            item.setText(text);
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(text);
                    }
                }
            });
            addView(item);
        }

    }

    public void setClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(String text);
    }

    public void setSpace(int horizontalSpace, int verticalSpace) {
        this.mHorizontalSpace = horizontalSpace;
        this.mVerticalSpace = verticalSpace;
    }

    /**
     * 测量分两步，先测量孩子，再测量自己，关于自定义控件
     * 我们会在自定义控件的课程里详细给大家讲解onMeasure，onLayout方法。自定义ViewGroup的步骤。
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mLines.clear();
        mCurrentLineCursor = null;
        //获取宽度
        int layoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        //计算最大宽度
        int maxLineWidth = layoutWidth - getPaddingLeft() - getPaddingRight();
        //先测量孩子
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            // 如果孩子不可见
            if (view.getVisibility() == View.GONE) {
                continue;
            }

            // 测量孩子
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
            // 往lines添加孩子
            if (mCurrentLineCursor == null) {
                // 说明还没有开始添加孩子
                mCurrentLineCursor = new Line(maxLineWidth, mHorizontalSpace);
                // 添加到 Lines中
                mLines.add(mCurrentLineCursor);
                // 行中一个孩子都没有
                mCurrentLineCursor.addView(view);
            } else {
                // 行不为空,行中有孩子了
                boolean canAdd = mCurrentLineCursor.canAdd(view);
                if (canAdd) {
                    // 可以添加
                    mCurrentLineCursor.addView(view);
                } else {
                    // 新建行
                    mCurrentLineCursor = new Line(maxLineWidth, mHorizontalSpace);
                    // 添加到lines中
                    mLines.add(mCurrentLineCursor);
                    // 将view添加到line
                    mCurrentLineCursor.addView(view);
                }
            }
        }

        // 设置自己的宽度和高度
        int measuredWidth = layoutWidth;
        float allHeight = 0;
        for (int i = 0; i < mLines.size(); i++) {
            float mHeigth = mLines.get(i).mHeigth;
            // 加行高
            allHeight += mHeigth;
            // 加间距
            if (i != 0) {
                allHeight += mVerticalSpace;
            }
        }

        int measuredHeight = (int) (allHeight + getPaddingTop() + getPaddingBottom() + 0.5f);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 给Child 布局---> 给Line布局
        int paddingLeft = getPaddingLeft();
        int offsetTop = getPaddingTop();
        for (int i = 0; i < mLines.size(); i++) {
            Line line = mLines.get(i);
            // 给行布局
            line.layout(paddingLeft, offsetTop);
            offsetTop += line.mHeigth + mVerticalSpace;
        }
    }

    private class Line {
        // 属性
        private List<View> mViews = new ArrayList<View>();
        private float mMaxWidth;
        private float mUsedWidth;
        private float mHeigth;
        private float mMarginLeft;
        private float mMarginRight;
        private float mMarginTop;
        private float mMarginBottom;
        private float mHorizontalSpace;

        public Line(int maxWidth, int horizontalSpace) {
            this.mMaxWidth = maxWidth;
            this.mHorizontalSpace = horizontalSpace;
        }

        /**
         * 添加view，记录属性的变化
         *
         * @param view
         */
        public void addView(View view) {
            // 加载View的方法
            int size = mViews.size();
            int viewWidth = view.getMeasuredWidth();
            int viewHeight = view.getMeasuredHeight();
            // 计算宽和高
            if (size == 0) {
                // 说还没有添加View
                if (viewWidth > mMaxWidth) {
                    mUsedWidth = mMaxWidth;
                } else {
                    mUsedWidth = viewWidth;
                }
                mHeigth = viewHeight;
            } else {
                // 多个view的情况
                mUsedWidth += viewWidth + mHorizontalSpace;
                mHeigth = mHeigth < viewHeight ? viewHeight : mHeigth;
            }
            // 将View记录到集合中
            mViews.add(view);
        }

        /**
         * 用来判断是否可以将View添加到line中
         *
         * @param view
         * @return
         */
        public boolean canAdd(View view) {
            // 判断是否能添加View
            int size = mViews.size();
            if (size == 0) {
                return true;
            }
            int viewWidth = view.getMeasuredWidth();
            // 预计使用的宽度
            float planWidth = mUsedWidth + mHorizontalSpace + viewWidth;
            if (planWidth > mMaxWidth) {
                // 加不进去
                return false;
            }
            return true;
        }

        /**
         * 给孩子布局
         *
         * @param offsetLeft
         * @param offsetTop
         */
        public void layout(int offsetLeft, int offsetTop) {
            // 给孩子布局
            int currentLeft = offsetLeft;
            int size = mViews.size();
            // 判断已经使用的宽度是否小于最大的宽度
            float extra = 0;
            float widthAvg = 0;
            if (mMaxWidth > mUsedWidth) {
                extra = mMaxWidth - mUsedWidth;
                widthAvg = extra / size;
            }

            for (int i = 0; i < size; i++) {
                View view = mViews.get(i);
                int viewWidth = view.getMeasuredWidth();
                int viewHeight = view.getMeasuredHeight();
                // 判断是否有富余
                if (widthAvg != 0) {
                    // 改变宽度
                    int newWidth = (int) (viewWidth + widthAvg + 0.5f);
                    int widthMeasureSpec = MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.AT_MOST);
                    int heightMeasureSpec = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY);
                    view.measure(widthMeasureSpec, heightMeasureSpec);

                    viewWidth = view.getMeasuredWidth();
                    viewHeight = view.getMeasuredHeight();
                }

                // 布局
                int left = currentLeft;
                int top = (int) (offsetTop + (mHeigth - viewHeight) / 2 + 0.5f);
                // int top = offsetTop;
                int right = left + viewWidth;
                Log.d(TAG, "viewWidth -- > " + viewWidth);
                int bottom = top + viewHeight;
                view.layout(left, top, right, bottom);
                currentLeft += viewWidth + mHorizontalSpace;
            }
        }
    }
}
