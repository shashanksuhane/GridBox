package com.suhane.gridbox.ui.items;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.suhane.gridbox.R;
import com.suhane.gridbox.repository.model.item.Item;
import com.suhane.gridbox.repository.model.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by shashanksuhane on 03/04/18.
 */
public class ItemGridView {

    private static final int GRID_COLUMN_COUNT = 3;
    private static final int ANIMATION_DURATION = 500;

    private Context context;
    private GridLayout gridLayout;
    private ScrollView scrollView;

    private ValueAnimator mAnimator;
    private AtomicBoolean mIsScrolling = new AtomicBoolean(false);

    private List<Item> itemList = new ArrayList<>();

    private int screenWidth;
    private int screenHeight;

    private ItemUpdateListener listener;

    enum Action {
        NONE,
        DELETE,
        REPLACE
    }

    public ItemGridView(Context ctx, GridLayout layout, ScrollView sView) {
        context = ctx;
        scrollView = sView;

        gridLayout = layout;
        gridLayout.setColumnCount(GRID_COLUMN_COUNT);
        gridLayout.setOnDragListener(new DragListener());

        WindowManager wm = (WindowManager)    context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    public void setItemUpdateListener(ItemUpdateListener listener) {
        this.listener = listener;
    }

    public void updateItemList(List<Item> itemList) {
        if (listener != null) {
            listener.onItemUpdate(itemList);
        }

        Items items = new Items();
        items.setItems(itemList);
        addAllItems(items);
    }

    public void addAllItems(Items items) {

        itemList = items.getItems();

        gridLayout.removeAllViews();
        for (int i=0; i<itemList.size(); i++) {
            addView(i);
        }

        //add the add button
        addView(itemList.size());

    }

    public void addView(final int num) {

        LayoutInflater inflater = ( LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.grid_item, null);
        ImageView imageView = view.findViewById(R.id.imageView);

        ImageView closeView = view.findViewById(R.id.closeView);

        if (num == itemList.size()) {
            Picasso.with(context).load(R.mipmap.add_item).fit().centerInside().into(imageView);
            closeView.setVisibility(View.GONE);
        } else {
            Picasso.with(context)
                    .load(itemList.get(num).getImageUrlString())
                    .fit()
                    .centerInside()
                    .into(imageView);
            closeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteView(num);
                }
            });
            view.setOnLongClickListener(new LongPressListener());
        }

        view.setTag(num);
        view.setOnClickListener(new ClickListener());

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();

        //first item has bigger dimensions
        if (num == 0) {
            layoutParams.columnSpec = GridLayout.spec(0, 2,2f);
            layoutParams.rowSpec = GridLayout.spec(0, 2,2f);
            layoutParams.height = (screenWidth/3) * 2;
            layoutParams.width = 0;
            gridLayout.addView(view, layoutParams);
        }
        // second item takes third position in first row
        else if (num == 1) {
            layoutParams.columnSpec = GridLayout.spec(2,1f);
            layoutParams.rowSpec = GridLayout.spec(0,1f);
            layoutParams.height = screenWidth/3;
            layoutParams.width = 0;
            gridLayout.addView(view, layoutParams);
        }
        // third item takes third position in second row
        else if (num == 2) {
            layoutParams.columnSpec = GridLayout.spec(2,1f);
            layoutParams.rowSpec = GridLayout.spec(1,1f);
            layoutParams.height = screenWidth/3;
            layoutParams.width = 0;
            gridLayout.addView(view, layoutParams);
        }
        // all other items can be drawn generically
        else {
            int colStart = num%GRID_COLUMN_COUNT;

            int rowStart = num/GRID_COLUMN_COUNT+1;
            if (rowStart % 2 == 0) {
                colStart = GRID_COLUMN_COUNT-colStart-1;
            }
            layoutParams.columnSpec = GridLayout.spec(colStart, 1f);
            layoutParams.rowSpec = GridLayout.spec(rowStart, 1f);
            layoutParams.height = screenWidth/3;
            layoutParams.width = 0;
            gridLayout.addView(view, layoutParams);
        }

    }

    private void deleteView(int num) {
        if (num == itemList.size()-1) {
            itemList.remove(num);
            updateItemList(itemList);
        } else {
            shiftViewsReverse(num, itemList.size()-1, Action.DELETE);
        }
    }

    private void shiftViewsReverse(final int start, final int end, final Action action) {
        List<Animator> animators = new ArrayList<>();

        for (int i=start; i<end; i++) {
            View child1 = gridLayout.getChildAt(i);
            View child2 = gridLayout.getChildAt(i+1);

            if (child1 == null || child2 == null) continue;

            float startX1 = child1.getLeft();
            float startY1 = child1.getTop();

            int w1 = child1.getMeasuredWidth();
            int h1 = child1.getMeasuredHeight();

            float startX2 = child2.getLeft();
            float startY2 = child2.getTop();

            int w2 = child2.getMeasuredWidth();
            int h2 = child2.getMeasuredHeight();

            float scaleFactorX = w2 * (1 - (w1 / w2)) / 2;
            float scaleFactorY = h2 * (1 - (h1 / h2)) / 2;
            ObjectAnimator translateXAnimation= ObjectAnimator.ofFloat(child2, "translationX", 0f, startX1-startX2-scaleFactorX);
            ObjectAnimator translateYAnimation= ObjectAnimator.ofFloat(child2, "translationY", 0f, startY1-startY2-scaleFactorY);

            if (i == end-1) {
                translateYAnimation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {}

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (action == Action.REPLACE) {
                            Item item = itemList.remove(start);
                            itemList.add(end, item);
                        } else if (action == Action.DELETE) {
                            itemList.remove(start);
                        }

                        updateItemList(itemList);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {}

                    @Override
                    public void onAnimationRepeat(Animator animator) {}
                });
            }

            animators.add(translateXAnimation);
            animators.add(translateYAnimation);

            if (i == 0) {
                ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(child2, "scaleX", 2f);
                ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(child2, "scaleY", 2f);
                animators.add(scaleUpX);
                animators.add(scaleUpY);
            }
        }

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIMATION_DURATION);
        //set.playSequentially(animators);
        set.playTogether(animators);
        set.start();
    }

    private void shiftViewsForward(final int start, final int end, final Action action) {
        List<Animator> animators = new ArrayList<>();

        for (int i=end; i>start; i--) {
            View child1 = gridLayout.getChildAt(i);
            View child2 = gridLayout.getChildAt(i-1);

            if (child1 == null || child2 == null) continue;

            float startX1 = child1.getLeft();
            float startY1 = child1.getTop();

            float startX2 = child2.getLeft();
            float startY2 = child2.getTop();

            ObjectAnimator translateXAnimation= ObjectAnimator.ofFloat(child2, "translationX", 0f, startX1-startX2);
            ObjectAnimator translateYAnimation= ObjectAnimator.ofFloat(child2, "translationY", 0f, startY1-startY2);

            if (i == start+1) {
                translateYAnimation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {}

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (action == Action.REPLACE) {
                            Item item = itemList.remove(end);
                            itemList.add(start, item);
                        }

                        updateItemList(itemList);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {}

                    @Override
                    public void onAnimationRepeat(Animator animator) {}
                });
            }

            animators.add(translateXAnimation);
            animators.add(translateYAnimation);
        }

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIMATION_DURATION);
        //set.playSequentially(animators);
        set.playTogether(animators);
        set.start();
    }

    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int index = (int)view.getTag();
            if (index < itemList.size()) {
                Toast.makeText(context, String.valueOf(itemList.get(index).getUuid()), Toast.LENGTH_SHORT).show();
            } else {
                new ItemAddDialog(context).create(new ItemAddListener() {
                    @Override
                    public void onItemAdd(Item item) {
                        itemList.add(item);
                        updateItemList(itemList);
                    }
                });
            }
        }
    }

    class LongPressListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            final ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.INVISIBLE);
            return true;
        }
    }

    class DragListener implements View.OnDragListener {
        int startIndex = -1;

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final View view = (View) event.getLocalState();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    startIndex = calculateIndex(event.getX(), event.getY());
                    Log.d("Start Index", String.valueOf(startIndex));
                    break;

                case DragEvent.ACTION_DRAG_LOCATION:
                    // do nothing if hovering above own position
                    if (view == v) return true;

                    final Rect rect = new Rect();
                    scrollView.getHitRect(rect);
                    final int scrollY = scrollView.getScrollY();

                    if (event.getY() -  scrollY > scrollView.getBottom() - 250) {
                        startScrolling(scrollY, gridLayout.getHeight());
                    } else if (event.getY() - scrollY < scrollView.getTop() + 250) {
                        startScrolling(scrollY, 0);
                    } else {
                        stopScrolling();
                    }

                    // get the new list index
                    int newIndex = calculateIndex(event.getX(), event.getY());

                    break;
                case DragEvent.ACTION_DROP:
                    view.setVisibility(View.VISIBLE);

                    int endIndex = calculateIndex(event.getX(), event.getY());
                    Log.d("End Index", String.valueOf(endIndex));

                    if (startIndex > -1 && endIndex < itemList.size()) {
                        if (startIndex > endIndex) {
                            shiftViewsForward(endIndex, startIndex, Action.REPLACE);
                        } else {
                            shiftViewsReverse(startIndex, endIndex, Action.REPLACE);
                        }
                    }

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (!event.getResult()) {
                        view.setVisibility(View.VISIBLE);
                    }
                    break;
            }
            return true;
        }
    }

    private void startScrolling(int from, int to) {
        if (from != to && mAnimator == null) {
            mIsScrolling.set(true);
            mAnimator = new ValueAnimator();
            mAnimator.setInterpolator(new OvershootInterpolator());
            mAnimator.setDuration(Math.abs(to - from));
            mAnimator.setIntValues(from, to);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    scrollView.smoothScrollTo(0, (int) valueAnimator.getAnimatedValue());
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsScrolling.set(false);
                    mAnimator = null;
                }
            });
            mAnimator.start();
        }
    }

    private void stopScrolling() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    private int calculateIndex(float x, float y) {
        // calculate which column to move to
        final float cellWidth = gridLayout.getWidth() / gridLayout.getColumnCount();
        final int column = (int)(x / cellWidth);

        // calculate which row to move to
        final float cellHeight = gridLayout.getHeight() / gridLayout.getRowCount();
        final int row = (int)Math.floor(y / cellHeight);

        // the items in the GridLayout is organized as a wrapping list
        // and not as an actual grid, so this is how to get the new index
        int index = row * gridLayout.getColumnCount() + column;

        // first row, one item is less
        if ((row == 0) && (index > 0)) {
            index = index -1;
        }
        // second row, first & second item space is reserved
        else if (row == 1) {
            if (index == 3 || index == 4)
                index = 0;
            else
                index = 2;
        }
        // from row three onwards, get the index generically
        else {
            //if row is odd, change the order
            if (row % 2 == 0) {
                index = row * gridLayout.getColumnCount() + (GRID_COLUMN_COUNT - 1) - column;
            }

            // since first item occupies space other three items
            index = index - 3;
        }

        if (index >= gridLayout.getChildCount()) {
            index = gridLayout.getChildCount() - 1;
        }

        return index;
    }

}
