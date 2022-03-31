package com.dogegames.gama;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ConsoleDataManager {
    static final String TAG="ConsoleDataManager";

    File file=null;

    private Context context;

    ConsoleDataManager(Context context){
        this.context=context;
        file=new File(context.getFilesDir(),"console.dat");//MainActivity.context.getFilesDir(),"console.dat");
    }

    public void SaveData(ArrayList<UserConsoleInfo> userConsoleInfoList){

        try {
            FileOutputStream fos=new FileOutputStream(file);
            BufferedOutputStream bos=new BufferedOutputStream(fos);

            ObjectOutputStream out=new ObjectOutputStream(bos);

            for(int i=0;i<userConsoleInfoList.size();i++){
                userConsoleInfoList.add(userConsoleInfoList.get(i));
            }

            out.writeObject(userConsoleInfoList);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<UserConsoleInfo> LoadData(){
        ArrayList<UserConsoleInfo> userConsoleInfoList=new ArrayList<>();

        try {
            FileInputStream fis= new FileInputStream(file);
            BufferedInputStream bis=new BufferedInputStream(fis);

            ObjectInputStream in=new ObjectInputStream(bis);

            userConsoleInfoList=(ArrayList<UserConsoleInfo>) in.readObject();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return userConsoleInfoList;
    }
}
