package com.k.neleme.Views;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.k.neleme.R;
import com.k.neleme.adapters.FoodAdapter;
import com.k.neleme.adapters.TypeAdapter;
import com.k.neleme.bean.FoodBean;
import com.k.neleme.utils.BaseUtils;

import java.util.List;

public class ListContainer extends LinearLayout {

	private TypeAdapter typeAdapter;
	private RecyclerView recyclerView2;
	private LinearLayoutManager linearLayoutManager;
	private List<FoodBean> foodBeanList;
	private boolean move;
	private int index;

	public ListContainer(Context context) {
		super(context);
	}

	public ListContainer(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		inflate(context, R.layout.view_listcontainer, this);
		RecyclerView recyclerView1 = (RecyclerView) findViewById(R.id.recycler1);
		recyclerView1.setLayoutManager(new LinearLayoutManager(context));
		typeAdapter = new TypeAdapter(BaseUtils.getTypes());
		recyclerView1.setAdapter(typeAdapter);
		recyclerView1.addItemDecoration(new SimpleDividerDecoration(context));
		recyclerView1.addOnItemTouchListener(new OnItemClickListener() {
			@Override
			public void onSimpleItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
				if (recyclerView2.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
					typeAdapter.setChecked(i);
					String type = view.getTag().toString();
					for (int ii = 0; ii < foodBeanList.size(); ii++) {
						FoodBean typeBean = foodBeanList.get(ii);
						if (typeBean.getType().equals(type)) {
							index = ii;
							moveToPosition(index);
							break;
						}
					}
				}
			}
		});
		recyclerView2 = (RecyclerView) findViewById(R.id.recycler2);
		linearLayoutManager = new LinearLayoutManager(context);
		recyclerView2.setLayoutManager(linearLayoutManager);
		foodBeanList = BaseUtils.getDatas(context);

	}

	private void moveToPosition(int n) {
		//先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
		int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
		int lastItem = linearLayoutManager.findLastVisibleItemPosition();
		//然后区分情况
		if (n <= firstItem) {
			//当要置顶的项在当前显示的第一个项的前面时
			recyclerView2.scrollToPosition(n);
		} else if (n <= lastItem) {
			//当要置顶的项已经在屏幕上显示时
			int top = recyclerView2.getChildAt(n - firstItem).getTop();
			recyclerView2.scrollBy(0, top);
		} else {
			//当要置顶的项在当前显示的最后一项的后面时
			recyclerView2.scrollToPosition(n);
			//这里这个变量是用在RecyclerView滚动监听里面的
			move = true;
		}
	}

	public void setAddClick(AddWidget.OnAddClick onAddClick) {
		FoodAdapter foodAdapter = new FoodAdapter(foodBeanList, onAddClick);
		recyclerView2.setAdapter(foodAdapter);
		final View stickView = findViewById(R.id.stick_header);
		final TextView tvStickyHeaderView = (TextView) stickView.findViewById(R.id.tv_header);
		recyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				if (move) {
					move = false;
					//获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
					int n = index - linearLayoutManager.findFirstVisibleItemPosition();
					if (0 <= n && n < recyclerView.getChildCount()) {
						//获取要置顶的项顶部离RecyclerView顶部的距离
						int top = recyclerView.getChildAt(n).getTop();
						//最后的移动
						recyclerView.smoothScrollBy(0, top);
					}
				} else {
					View stickyInfoView = recyclerView.findChildViewUnder(stickView.getMeasuredWidth() / 2, 5);
					if (stickyInfoView != null && stickyInfoView.getContentDescription() != null) {
						tvStickyHeaderView.setText(String.valueOf(stickyInfoView.getContentDescription()));
					}

					View transInfoView = recyclerView.findChildViewUnder(stickView.getMeasuredWidth() / 2, stickView.getMeasuredHeight
							() + 1);
					if (transInfoView != null && transInfoView.getTag() != null) {
						int transViewStatus = (int) transInfoView.getTag();
						int dealtY = transInfoView.getTop() - stickView.getMeasuredHeight();

						if (transViewStatus == FoodAdapter.HAS_STICKY_VIEW) {
							if (transInfoView.getTop() > 0) {
								stickView.setTranslationY(dealtY);
							} else {
								stickView.setTranslationY(0);
							}
						} else if (transViewStatus == FoodAdapter.NONE_STICKY_VIEW) {
							stickView.setTranslationY(0);
						}
					}
				}
			}
		});
	}
}