package com.dogegames.gama;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyGameTitleItemDecoration extends RecyclerView.ItemDecoration{
    final static String TAG="MyGameTitleItemDecoration";

    public static final int padding=8;

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int paddingDP=(int)MainActivity.dpToPx(parent.getContext(), padding);
        outRect.top=paddingDP;//*2;
        outRect.left=paddingDP;
        outRect.right=paddingDP;

        int row=(int)Math.ceil(parent.getAdapter().getItemCount()/3f);

        if(parent.getChildAdapterPosition(view)>=(3*(row-1)) && parent.getChildAdapterPosition(view)<(3*row)){
            outRect.top=paddingDP*2;
            outRect.bottom=paddingDP*2;
        }
    }
}
