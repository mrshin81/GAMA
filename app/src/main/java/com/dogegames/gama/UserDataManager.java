package com.dogegames.gama;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class UserDataManager {
    final static String TAG="UserDataManager";
    File file=null;
    private Context context;

    UserDataManager(Context context){
        this.context=context;
        file=new File(context.getFilesDir(),"user.dat");
    }

    public void SaveData(UserInfo userInfo){
        try
        {
            FileOutputStream fos=new FileOutputStream(file);
            BufferedOutputStream bos=new BufferedOutputStream(fos);

            ObjectOutputStream out=new ObjectOutputStream(bos);

            out.writeObject(userInfo);
            out.close();
            Commons.userName=userInfo.getUserName();
            Log.d(TAG,"SaveData Completed..");
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserInfo LoadData(){
        UserInfo userInfo=null;
        try {
            FileInputStream fis=new FileInputStream(file);
            BufferedInputStream bis=new BufferedInputStream(fis);

            ObjectInputStream in=new ObjectInputStream(bis);

            userInfo= (UserInfo) in.readObject();
            in.close();
            Commons.userName=userInfo.getUserName();
            Log.d(TAG,"LoadData Completed.. : "+userInfo);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return userInfo;
    }


}
