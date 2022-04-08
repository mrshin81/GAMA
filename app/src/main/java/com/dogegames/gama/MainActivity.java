package com.dogegames.gama;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final String TAG="MainActivity";
    static final int NormalFRAGMENT=0;
    static final int AddConsoleFRAGMENT=1;
    static final int AddTitleFRAGMENT=2;

    //각종 UI 객체 연결 변수
    EditText userNameET;
    ImageButton searchBTN;
    ImageButton userNameChangeBTN;
    TextView totalTitleCountTV;
    TextView totalBuyPriceTV;
    TextView totalConsoleCountTV;
    boolean isUserNameEditable=false;

    UserDataManager userDataManager=null;

    static Context context;

    ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this.getApplicationContext();

        setSupportActionBar(findViewById(R.id.toolbar));
        setToolbar("Game Manager",Color.BLUE, Color.WHITE);

        //userDataManager 인스턴스 생성
        userDataManager=new UserDataManager(getApplicationContext());

        //XML 객체 연결
        userNameET=findViewById(R.id.userNameEditText);
        searchBTN=findViewById(R.id.searchButton);
        userNameChangeBTN=findViewById(R.id.userNameChangeButton);

        totalBuyPriceTV=findViewById(R.id.totalBuyPriceTextView);
        totalTitleCountTV=findViewById(R.id.totalTitleCountTextView);
        totalConsoleCountTV=findViewById(R.id.totalConsoleCountTextView);

        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDataManager.LoadData();
            }
        });
        userNameChangeBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(isUserNameEditable){
                    UserInfo userInfo=new UserInfo(userNameET.getText().toString());
                    userDataManager.SaveData(userInfo);

                    userNameET.setEnabled(false);
                    isUserNameEditable=false;
                    //userNameChangeBTN.setText("변경");
                }else{
                    userNameET.setEnabled(true);
                    isUserNameEditable=true;
                    //userNameChangeBTN.setText("저장");
                }
            }
        });

        displayUserInfo();

        setFrament(NormalFRAGMENT);

    }

    public void setFrament(int fragmentType){
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        NormalFragment normalFragment=new NormalFragment();
        AddConsoleFragment addConsoleFragment=new AddConsoleFragment();
        AddTitleFragment addTitleFragment=new AddTitleFragment();

        switch(fragmentType){
            case NormalFRAGMENT:
                transaction.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom);
                transaction.replace(R.id.fragmentContainerView, normalFragment);
                break;
            case AddConsoleFRAGMENT:
                transaction.setCustomAnimations(R.anim.slide_in_bottom,R.anim.slide_out_top);
                transaction.replace(R.id.fragmentContainerView, addConsoleFragment);
                break;
            case AddTitleFRAGMENT:
                transaction.setCustomAnimations(R.anim.slide_in_bottom,R.anim.slide_out_top);
                transaction.replace(R.id.fragmentContainerView, addTitleFragment);
                break;
        }
        transaction.commit();
    }

    public void setToolbar(String name, int fontColor, int bgColor){
        ActionBar actionBar=getSupportActionBar();
        ColorDrawable colorDrawable=new ColorDrawable(bgColor);
        actionBar.setTitle(Html.fromHtml("<font color=\""+fontColor+"\">"+name+"</font>")); //Action Bar Font Color Set
        actionBar.setBackgroundDrawable(colorDrawable); //Action Bar Color Set
    }

    public void displayUserInfo(){
        UserInfo userInfo=userDataManager.LoadData();
        if(userInfo!=null){
            userNameET.setText(userInfo.getUserName());
            totalTitleCountTV.setText(String.valueOf(userInfo.getOwnTitleCount()));
            totalBuyPriceTV.setText(String.valueOf(userInfo.getTotalPrice()));
            totalConsoleCountTV.setText(String.valueOf(userInfo.getOwnConsoleCount()));
            Log.d(TAG,"userInfo : "+String.valueOf(userInfo.getOwnTitleCount()));
        }

    }

    static public int getImageId(String imageName){
        int id=context.getResources().getIdentifier("drawable/"+imageName,null,context.getPackageName());
        return id;
    }



    static public int getImageId(Context context, String imageName){
        int id=context.getResources().getIdentifier("drawable/"+imageName,null,context.getPackageName());
        Log.d(TAG,"getIdentifier : "+id);
        return id;
    }

    static public float pxToDp(Context context, float px){
        float density=context.getResources().getDisplayMetrics().density;
        Log.d(TAG,"density : "+density);
        if(density==1.0){
            density*=4.0;
        }else if(density==1.5){
            density*=(8/3);
        }else if(density==2.0){
            density*=2.0;
        }
        return px/density;
    }

    static public float dpToPx(Context context, float dp){
        int px=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.getResources().getDisplayMetrics());
        return px;
    }

    static public int getImage(String imageName) {
        int drawableResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        return drawableResourceId;
    }
}