package com.quansoon.facecamera.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author: Caoy
 * @created on: 2018/9/6 16:24
 * @description:
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration{
    private final int leftRight;
    private final int topBottom;

    public SpaceItemDecoration(float leftRight, float topBottom) {
        this.leftRight = (int) leftRight;
        this.topBottom = (int) topBottom;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        //竖直方向的
        if (layoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
            //最后一项需要 bottom
            if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {
                outRect.bottom = topBottom;
            }
            outRect.top = topBottom;
            outRect.left = leftRight;
            outRect.right = leftRight;
        } else {
            //最后一项需要right
            if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {
                outRect.right = leftRight;
            }
            outRect.top = topBottom;
            outRect.left = leftRight;
            outRect.bottom = topBottom;
        }
    }

}
