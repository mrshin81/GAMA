package com.dogegames.gama;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;

public class GameTitleRecyclerViewAdapter extends RecyclerView.Adapter<GameTitleRecyclerViewAdapter.GameTitleHolder>{
    final static String TAG="GameTitleRecyclerViewAdapter";

    private Context context;

    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
    }

    public OnItemClickListener mListener=null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener=listener;
    }

    ArrayList<UserTitleInfo> list;


    GameTitleRecyclerViewAdapter(Context context, ArrayList<UserTitleInfo> list){
        this.context=context;
        this.list=list;
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

        if(position==(getItemCount()-1)){
            Glide.with(context).load(MainActivity.getImage(userTitleInfo.getImagePath())).apply(RequestOptions.skipMemoryCacheOf(true)).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(holder.gameTitleImageIV);

            //Glide.with(context).load(MainActivity.getImage(userTitleInfo.getImagePath())).into(holder.gameTitleImageIV);
        }else{
            File imgFile=new File(context.getCacheDir()+"/"+userTitleInfo.getImagePath());
            if(!imgFile.exists()){
                Glide.with(context).load(R.drawable.image_icon).apply(RequestOptions.skipMemoryCacheOf(true)).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(holder.gameTitleImageIV);
            }else{
                Glide.with(context).load(imgFile).apply(RequestOptions.skipMemoryCacheOf(true)).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(holder.gameTitleImageIV);
            }
        }
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
                    if(mListener!=null) mListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }
}


