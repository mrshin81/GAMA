package com.dogegames.gama;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    static final String TAG="ItemTouchHelperCallback";

    public interface ItemTouchHelperListener{
        boolean onItemMove(RecyclerView.ViewHolder viewHolder, int from_position, int to_position);
        void onItemSwipe(int position);
        void activeAnimation(RecyclerView.ViewHolder viewHolder, boolean isActive);
    }

    private ItemTouchHelperListener listener;

    public ItemTouchHelperCallback(ItemTouchHelperListener listener){
        this.listener=listener;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int drag_flags=ItemTouchHelper.START | ItemTouchHelper.END;//| ItemTouchHelper.UP | ItemTouchHelper.DOWN ;//드래그는 상하좌우 전부 허용
        int swipe_flags=0;//ItemTouchHelper.UP; // 스와이프는 삭제시에 사용할 용도로, 위만 허용
        return makeMovementFlags(drag_flags,swipe_flags);//makeMoverFlags(drag, swipe);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        Log.d(TAG,"onMoved, viewHolder.getAdapterPosition : "+viewHolder.getAdapterPosition() +", target.getAdapterPosition : "+target.getAdapterPosition());
        return listener.onItemMove(viewHolder, viewHolder.getAdapterPosition(), target.getAdapterPosition());
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if(viewHolder!=null) viewHolder.itemView.clearAnimation();
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        Log.d(TAG,"onSelectedChanged");
        if(viewHolder!=null) listener.activeAnimation(viewHolder,true);
        //super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
        Log.d(TAG,"onMoved"+fromPos+","+toPos);
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onItemSwipe(viewHolder.getAdapterPosition());
        Log.d(TAG,"onSwiped");
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
}
