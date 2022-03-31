package com.dogegames.gama;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;

import androidx.annotation.NonNull;

public class TitleDialog extends Dialog {
    private final String TAG="TitleDialog";
    private Context context;
    private UserTitleInfo userTitleInfo;

    public interface OnItemDeleteListener{
        void onDeleteItem();
    }

    public interface OnItemSaveListener{
        void onSaveItem();
    }

    public OnItemDeleteListener dListener=null;
    public OnItemSaveListener sListener=null;

    public void setOnItemDeleteListener(OnItemDeleteListener listener){
        this.dListener=listener;
    }

    public void setOnItemSaveListener(OnItemSaveListener listener){
        this.sListener=listener;
    }

    public TitleDialog(@NonNull Context context, UserTitleInfo userTitleInfo) {
        super(context);
        this.context=context;
        this.userTitleInfo=userTitleInfo;
        setOwnerActivity((Activity) context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_dialog);

        setDialogSize();
    }

    void setDialogSize(){
        Display display=getOwnerActivity().getWindowManager().getDefaultDisplay();
        Point size=new Point();
        display.getSize(size);
        Window window=getWindow();
        int x=(int)(size.x*0.9f);
        int y=(int)(size.y*0.8f);
        window.setLayout(x,y);
    }
}
