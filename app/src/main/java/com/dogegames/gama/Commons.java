package com.dogegames.gama;

import android.app.Activity;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Commons extends Application {

    public void saveImageViewToPNG(ImageView imageView, String fileName){
        BitmapDrawable drawable=(BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap=drawable.getBitmap();
        Glide.with(this).load(bitmap).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                File tempFile=new File(getCacheDir(), fileName);
                try{
                    OutputStream out=new FileOutputStream(tempFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG,25,out);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                //String titleName=gameTitleNameET.getText().toString();
                /*if(titleName.equals("")){
                    titleName="null";
                }*/
                //if(sListener!=null) sListener.onSaveItem(titleName,userTitleInfo.getImagePath());
            }
        });
    }

    public void setDialogSize(Activity activity, Dialog dialog, float xScale, float yScale){
        Display display=activity.getWindowManager().getDefaultDisplay();
        Point size=new Point();
        display.getSize(size);
        int x=(int)(size.x*xScale);
        int y=(int)(size.y*yScale);

        dialog.getWindow().setLayout(x,y);
    }

    public void showDatePicker(EditText dateEditText){
        DatePickerDialog.OnDateSetListener mDateSetListener=
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String yyyyMMdd=year+"-"+(month+1)+"-"+dayOfMonth;
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date to=sdf.parse(yyyyMMdd);
                            dateEditText.setText(sdf.format(to));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                };
        int year, month, day;
        String strDate=dateEditText.getText().toString();
        String[] strDateSplit=strDate.split("-");
        try{
            year=Integer.valueOf(strDateSplit[0]);
            month=Integer.valueOf(strDateSplit[1])-1;
            day=Integer.valueOf(strDateSplit[2]);
        }catch (Exception e){
            Calendar calendar=Calendar.getInstance();
            year=calendar.get(Calendar.YEAR);
            month=calendar.get(Calendar.MONTH);
            day=calendar.get(Calendar.DATE);
        }
        new DatePickerDialog(dateEditText.getContext(),mDateSetListener,year,month,day).show();
    }

    public void setDropdown(String[] items, Spinner spinner){
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this, R.layout.spinner_item,items);
        spinner.setAdapter(adapter);
    }

    public Date convertStringToDate(String strDate){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();
        try {
            date=sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //Date 객체를 String으로 변환 메서드
    public String convertDateToString(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
