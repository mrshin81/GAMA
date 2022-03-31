package com.dogegames.gama;

import android.content.Context;

import java.util.Date;

public class UserConsoleInfo implements java.io.Serializable{
    private Context context;
    String name;
    String imagePath;
    int price;
    Date date;
    String memo;
    String id;

    UserConsoleInfo(Context context){
        this.name="Unknown Console";
        this.imagePath=null;
        this.price=0;
        this.date=null;
        this.memo=null;
        this.context=context;
    }

    //콘솔 rv에 plus icon을 추가하기 위해서 정의한 생성자
    UserConsoleInfo(Context context, String name){
        this.name=name;
        this.imagePath="plus_icon_square_dotted_gray";
        this.price=0;
        this.date=null;
        this.memo=null;
        this.context=context;
    }

    UserConsoleInfo(Context context, String name, String imagePath, int price, Date date, String memo){
        this.name=name;
        this.imagePath=imagePath;
        this.price=price;
        this.date=date;
        this.memo=memo;
        this.context=context;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getName(){
        return name;
    }

    public void setImagePath(String imagePath){
        this.imagePath=imagePath;
    }

    public String getImagePath(){
        return imagePath;
    }

    public void setPrice(int price){
        this.price=price;
    }

    public int getPrice(){
        return price;
    }

    public void setDate(Date date){
        this.date=date;
    }

    public Date getDate(){
        return date;
    }

    public void setMemo(String memo){
        this.memo=memo;
    }

    public String getMemo(){
        return memo;
    }

    public String toString(){
        return "("+name+","+imagePath+","+price+","+date+","+memo+")";
    }

    public String getId(){
        return this.id;
    }

    public void setId(String id){
        this.id=id;
    }

    public int getConsoleNumber(){
        int consoleNumber=0;
        String[] consoleList=context.getResources().getStringArray(R.array.console_list);

        for(int i=0;i<consoleList.length;i++){
            if(name.equals(consoleList[i])){
                consoleNumber=i;
            }
        }
        return consoleNumber;
    }
}
