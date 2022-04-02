package com.dogegames.gama;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ConsoleRecyclerViewAdapter extends RecyclerView.Adapter<ConsoleRecyclerViewAdapter.ConsoleHolder> implements ItemTouchHelperCallback.ItemTouchHelperListener {
    final static String TAG="ConsoleRVAdapter";
    public final static String SELECTED_CONSOLE_ITEM_NUMBER="SelectedConsoleItemNumber";

    public int selectedItemPosition=-1;

    static public boolean isAnimationRunning=false;

    private Context context;
    View view;
    ConsoleDBHelper consoleDBHelper;

    public interface OnItemClickListener{
        void onItemClick(View v,int pos);
    }

    public OnItemClickListener mListener=null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener=listener;
    }

    ArrayList<UserConsoleInfo> list;

    //item이 drag & drop으로 이동되었을때 동작하는 함수
    @Override
    public boolean onItemMove(RecyclerView.ViewHolder viewHolder, int from_position, int to_position) {
        //Log.d(TAG,"onItemMove, to_position : "+to_position+", from_position : "+from_position+", getItemCount : "+getItemCount());
        if(to_position<(getItemCount()-1) && from_position<(getItemCount()-1)){
            UserConsoleInfo userConsoleInfo=list.get(from_position);
            list.remove(from_position);
            list.add(to_position,userConsoleInfo);
            notifyItemMoved(from_position,to_position);
            //consoleDBHelper.modifyOrdering(ConsoleDBHelper.TABLE_NAME, from_position, to_position);
            consoleDBHelper.modifyOrdering(ConsoleDBHelper.TABLE_NAME, getItemCount()-2-from_position, getItemCount()-2-to_position);

            if(selectedItemPosition==to_position){
                selectedItemPosition=from_position;
            }else if(selectedItemPosition==from_position){
                selectedItemPosition=to_position;
            }
            PreferenceManager.setInt(context,SELECTED_CONSOLE_ITEM_NUMBER,selectedItemPosition);

            activeAnimation(viewHolder,true);

            notifyItemRangeChanged(0,getItemCount());
        }

        return true;
    }

    //item이 swipe되었을때 동작하는 함수
    @Override
    public void onItemSwipe(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void activeAnimation(RecyclerView.ViewHolder viewHolder, boolean isActive) {
        Animation animation= AnimationUtils.loadAnimation(MainActivity.context,R.anim.wobble);
        viewHolder.itemView.startAnimation(animation);
        isAnimationRunning=true;
    }

    void activateAnimation(View itemView, boolean isActive){
        Animation animation= AnimationUtils.loadAnimation(MainActivity.context,R.anim.wobble);
        if(isActive){
            itemView.startAnimation(animation);
        }else{
            itemView.clearAnimation();
        }
    }



    ConsoleRecyclerViewAdapter(Context context, ArrayList<UserConsoleInfo> list){
        this.list=list;
        this.context=context;
        this.selectedItemPosition=PreferenceManager.getInt(context,SELECTED_CONSOLE_ITEM_NUMBER);
        //this.selectedItemPosition=selectedItemPosition;
        Log.d(TAG,"selectedItemPosition at Constructing: "+selectedItemPosition);
        //this.lastItemSelectedPosition=selectedItemPosition;

        this.consoleDBHelper=ConsoleDBHelper.getInstance(context);
    }

    public void deleteItem(int pos){
        list.remove(pos);
    }

    @NonNull
    @Override
    public ConsoleRecyclerViewAdapter.ConsoleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.console_recycler,parent,false);
        ViewGroup.LayoutParams params=view.getLayoutParams();
        params.width=parent.getMeasuredWidth()/5;
        view.setLayoutParams(params);

        return new ConsoleRecyclerViewAdapter.ConsoleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsoleRecyclerViewAdapter.ConsoleHolder holder, int position) {

        UserConsoleInfo userConsoleInfo = list.get(position);

        holder.consoleNameTv.setText(userConsoleInfo.getName());
        Glide.with(context).load(MainActivity.getImageId(userConsoleInfo.getImagePath())).into(holder.consoleImageIV);
        //holder.consoleImageIV.setImageResource(MainActivity.getImageId(userConsoleInfo.getImagePath()));//R.drawable.playstation5);

        if(holder.getAdapterPosition()!=(getItemCount()-1))
        {
            if (position == selectedItemPosition) {
                holder.itemView.setBackgroundResource(R.drawable.border_blue);
                Log.d(TAG,"=====, position : "+position+", selectedItemPosition : "+selectedItemPosition);
            } else {
                holder.itemView.setBackgroundResource(R.drawable.rounded_corner_none_border);
                Log.d(TAG,"==========, position : "+position+", selectedItemPosition : "+selectedItemPosition);
            }
        }

        Log.d(TAG,"onBindViewHolder executed."+", userConsoleInfo.getName() : "+userConsoleInfo.getName()+", userConsoleInfo.getImagePath() : "+userConsoleInfo.getImagePath());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ConsoleHolder extends RecyclerView.ViewHolder{
        TextView consoleNameTv;
        ImageView consoleImageIV;

        public ConsoleHolder(@NonNull View itemView) {
            super(itemView);
            consoleNameTv=itemView.findViewById(R.id.consoleNameTextView);
            consoleImageIV=itemView.findViewById(R.id.consoleImageView);
            consoleNameTv.setHorizontallyScrolling(true);
            consoleNameTv.setSelected(true);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Log.d(TAG,"selectedItemPosition at onClick before : "+selectedItemPosition+", getItemCount : "+getItemCount());

                    if(getAdapterPosition()==(getItemCount()-1))
                    {
                        mListener.onItemClick(view, getAdapterPosition());
                    }
                    else
                    {
                        Log.d(TAG,"SELECTED ONCE");
                        if(selectedItemPosition!=(getItemCount()-1)) PreferenceManager.setInt(context,SELECTED_CONSOLE_ITEM_NUMBER,getAdapterPosition());
                        if(selectedItemPosition==getAdapterPosition() || selectedItemPosition==-1){
                            Log.d(TAG,"SELECTED AGAIN");
                            if(selectedItemPosition==-1) selectedItemPosition=0;
                            mListener.onItemClick(view, selectedItemPosition);
                        }
                        selectedItemPosition=getAdapterPosition();
                        notifyItemRangeChanged(0,getItemCount());
                    }

                    Log.d(TAG,"selectedItemPosition at onClick : "+selectedItemPosition);
                }
            });
        }
    }

}

