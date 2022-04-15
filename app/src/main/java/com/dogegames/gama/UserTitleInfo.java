package com.dogegames.gama;

import android.content.Context;

import java.util.Date;

public class UserTitleInfo {
    private Context context;
    String name;
    String maker;
    String platform;
    Date launchDate;
    String imagePath;
    String genre;
    String memo;
    int price;
    int rating;
    String id;

    UserTitleInfo(Context context){
        this.context=context;
        this.name="Unknown Title";
        this.platform=null;
        this.imagePath=null;
        this.maker=null;
        this.launchDate=null;
        this.genre=null;
        this.memo=null;
        this.price=0;
        this.rating=0;
    }

    //타이틀 rv에 plus icon을 추가하기 위해서 정의한 생성자
    UserTitleInfo(Context context, String name){
        this.context=context;
        this.name=name;
        this.platform=null;
        this.imagePath="plus_icon_square_dotted_gray";
        this.maker=null;
        this.launchDate=null;
        this.genre=null;
        this.memo=null;
        this.price=0;
        this.rating=0;
    }

    UserTitleInfo(Context context, String name, String platform, String maker, Date launchDate, String imagePath, String genre, String memo, int price, int rating){
        this.context=context;
        this.name=name;
        this.platform=platform;
        this.maker=maker;
        this.launchDate=launchDate;
        this.imagePath=imagePath;
        this.genre=genre;
        this.memo=memo;
        this.price=price;
        this.rating=rating;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getName(){
        return name;
    }

    public void setPlatform(String platform){
        this.platform=platform;
    }

    public String getPlatform(){
        return platform;
    }

    public void setMaker(String maker){
        this.maker=maker;
    }

    public String getMaker(){
        return maker;
    }

    public void setLaunchDate(Date launchDate){
        this.launchDate=launchDate;
    }

    public Date getLaunchDate(){
        return launchDate;
    }

    public void setImagePath(String imagePath){
        this.imagePath=imagePath;
    }

    public String getImagePath(){
        return imagePath;
    }

    public void setGenre(String genre){
        this.genre=genre;
    }

    public String getGenre(){
        return genre;
    }

    public void setMemo(String memo){
        this.memo=memo;
    }

    public String getMemo(){
        return memo;
    }

    public void setPrice(int price){
        this.price=price;
    }

    public int getPrice(){
        return price;
    }

    public void setRating(int rating){
        this.rating=rating;
    }

    public int getRating(){
        return rating;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id=id;
    }

    public int getConsoleNumber(){
        int consoleNumber=0;
        String[] consoleList=context.getResources().getStringArray(R.array.console_list);

        for(int i=0;i<consoleList.length;i++){
            if(platform.equals(consoleList[i])){
                consoleNumber=i;
            }
        }
        return consoleNumber;
    }
}
