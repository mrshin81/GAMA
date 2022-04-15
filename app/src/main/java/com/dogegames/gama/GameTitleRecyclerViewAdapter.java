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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;

public class GameTitleRecyclerViewAdapter extends RecyclerView.Adapter<GameTitleRecyclerViewAdapter.GameTitleHolder> implements GameItemTouchHelperCallback.GameItemTouchHelperListener{
    final static String TAG="GameTitleRecyclerViewAdapter";

    private Context context;

    static public boolean isAnimationRunning=false;

    TitleDBHelper titleDBHelper;



    @Override
    public boolean onItemMove(RecyclerView.ViewHolder viewHolder, int from_position, int to_position) {
        if(to_position<(getItemCount()-1) && from_position<(getItemCount()-1)){
            UserTitleInfo userTitleInfo=list.get(from_position);
            list.remove(from_position);
            list.add(to_position, userTitleInfo);
            notifyItemMoved(from_position,to_position);

            String tableName=userTitleInfo.getPlatform().replace(" ","_");

            titleDBHelper.modifyOrdering(tableName, getItemCount()-2-from_position, getItemCount()-2-to_position);

            activeAnimation(viewHolder,true);

            notifyItemRangeChanged(0,getItemCount());
        }
        return true;
    }

    @Override
    public void onItemSwipe(int position) {
        //현재 여기에서는 필요 없어서 구현하지 않음.
        //추후에 스와이프로 아이템 삭제하려면 구현해야 함.
    }

    @Override
    public void activeAnimation(RecyclerView.ViewHolder viewHolder, boolean isActive) {
        Animation animation= AnimationUtils.loadAnimation(MainActivity.context, R.anim.wobble);
        viewHolder.itemView.startAnimation(animation);
        isAnimationRunning=true;
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int pos, String tableName, String id);
    }

    public OnItemClickListener mListener=null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener=listener;
    }

    ArrayList<UserTitleInfo> list;


    GameTitleRecyclerViewAdapter(Context context, ArrayList<UserTitleInfo> list){
        this.context=context;
        this.list=list;
        this.titleDBHelper=TitleDBHelper.getInstance(context);
    }

    @NonNull
    @Override
    public GameTitleRecyclerViewAdapter.GameTitleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.gametitle_recycler,parent,false);
        ViewGroup.LayoutParams params=view.getLayoutParams();
        params.height=parent.getMeasuredWidth()/3;
        view.setLayoutParams(params);
        return new GameTitleRecyclerViewAdapter.GameTitleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameTitleHolder holder, int position) {

        UserTitleInfo userTitleInfo=list.get(position);
        holder.gameTitleNameTV.setText(userTitleInfo.getName());

        if(userTitleInfo.getName().equals("ADD Title...")){
            Glide.with(context).load(MainActivity.getImage(userTitleInfo.getImagePath())).apply(RequestOptions.skipMemoryCacheOf(true)).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(holder.gameTitleImageIV);
        }else{
            File imgFile=new File(context.getCacheDir()+"/"+userTitleInfo.getImagePath());
            if(!imgFile.exists()){
                Glide.with(context).load(R.drawable.image_icon).apply(RequestOptions.skipMemoryCacheOf(true)).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(holder.gameTitleImageIV);
            }else{
                Glide.with(context).load(imgFile).apply(RequestOptions.skipMemoryCacheOf(true)).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(holder.gameTitleImageIV);
            }
        }
        /*if(position==(getItemCount()-1)){ //Bind된  아이템이 ADD Game Title 아이템일 경우
            Glide.with(context).load(MainActivity.getImage(userTitleInfo.getImagePath())).apply(RequestOptions.skipMemoryCacheOf(true)).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(holder.gameTitleImageIV);
        }else{
            File imgFile=new File(context.getCacheDir()+"/"+userTitleInfo.getImagePath());
            if(!imgFile.exists()){
                Glide.with(context).load(R.drawable.image_icon).apply(RequestOptions.skipMemoryCacheOf(true)).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(holder.gameTitleImageIV);
            }else{
                Glide.with(context).load(imgFile).apply(RequestOptions.skipMemoryCacheOf(true)).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(holder.gameTitleImageIV);
            }
        }*/
        Log.d(TAG,"onBindViewHolder");
    }

    public void filterList(ArrayList<UserTitleInfo> filteredList){
        this.list=filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class GameTitleHolder extends RecyclerView.ViewHolder{
        TextView gameTitleNameTV;
        ImageView gameTitleImageIV;

        public GameTitleHolder(@NonNull View itemView) {
            super(itemView);
            gameTitleNameTV=itemView.findViewById(R.id.gameTitleNameTextView);
            gameTitleImageIV=itemView.findViewById(R.id.titleImageView);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    /*if(getAdapterPosition()!=(getItemCount()-1))
                    {
                        String platformName=list.get(getAdapterPosition()).getPlatform();
                        String tableName=platformName.replace(" ","_");
                        String id=list.get(getAdapterPosition()).getId();
                        Log.d(TAG,"tableName " +tableName+", id : "+id);
                        if(mListener!=null) mListener.onItemClick(v, getAdapterPosition(), tableName, id);
                    }
                    else{

                        if(mListener!=null) mListener.onItemClick(v, getAdapterPosition(), null, null);
                    }*/
                    String platformName=list.get(getAdapterPosition()).getPlatform();
                    if(platformName!=null){
                        String tableName=platformName.replace(" ","_");
                        String id=list.get(getAdapterPosition()).getId();
                        Log.d(TAG,"tableName " +tableName+", id : "+id);
                        if(mListener!=null) mListener.onItemClick(v, getAdapterPosition(), tableName, id);
                    }else{
                        if(mListener!=null) mListener.onItemClick(v, getAdapterPosition(), null, null);
                    }

                }
            });
        }
    }

    public void deleteItem(int pos){
        list.remove(pos);
    }
}


