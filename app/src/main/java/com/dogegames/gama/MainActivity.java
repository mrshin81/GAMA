package com.dogegames.gama;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    static final String TAG="MainActivity";
    static final int NormalFRAGMENT=0;
    static final int AddConsoleFRAGMENT=1;
    static final int AddTitleFRAGMENT=2;
    static final int StatisticFRAGMENT=3;

    //각종 UI 객체 연결 변수
    EditText userNameET;
    Button userNameChangeBTN;
    TextView totalTitleCountTV;
    TextView totalBuyPriceTV;
    TextView totalConsoleCountTV;
    BottomNavigationView bottomNavigationView;
    ImageView userIV;

    boolean isUserNameEditable=false;

    UserDataManager userDataManager=null;

    static Context context;

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
        userNameChangeBTN=findViewById(R.id.userNameChangeButton);

        totalBuyPriceTV=findViewById(R.id.totalBuyPriceTextView);
        totalTitleCountTV=findViewById(R.id.totalTitleCountTextView);
        totalConsoleCountTV=findViewById(R.id.totalConsoleCountTextView);
        userIV=findViewById(R.id.userImageView);

        bottomNavigationView=findViewById(R.id.bottomNavView);

        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setBackgroundColor(Color.WHITE);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.item_home:
                        setFrament(NormalFRAGMENT);
                        break;
                    case R.id.item_statistic:
                        setFrament(StatisticFRAGMENT);
                        break;
                }

                return true;
            }
        });

        userIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"User Image View Clicked");
                //클릭시 image picker 뜨게 하고 이미지 선택시 이미지 변경하도록 한다.
            }
        });

        userNameChangeBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(isUserNameEditable){
                    String totalConsoleCountText=totalConsoleCountTV.getText().toString();
                    String totalTitleCountText=totalTitleCountTV.getText().toString();
                    String totalPriceText=totalBuyPriceTV.getText().toString();

                    int totalConsoleCount=Integer.valueOf(totalConsoleCountText.substring(0,totalConsoleCountText.length()-2).replace(",",""));
                    int totalTitleCount=Integer.valueOf(totalTitleCountText.substring(0,totalTitleCountText.length()-2).replace(",",""));
                    int totalPrice=Integer.valueOf(totalPriceText.substring(0,totalPriceText.length()-2).replace(",",""));

                    Log.d(TAG,"totalConsoleCount : "+totalConsoleCount);
                    UserInfo userInfo=new UserInfo(userNameET.getText().toString(), totalConsoleCount,totalTitleCount,totalPrice);//Integer.valueOf(totalTitleCountTV.getText().toString()),Integer.valueOf(totalBuyPriceTV.getText().toString()));
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
        StatisticFragment statisticFragment=new StatisticFragment();

        Fragment fragment=getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);


        switch(fragmentType){
            case NormalFRAGMENT:
                if(fragment instanceof StatisticFragment){
                    transaction.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right);
                    transaction.replace(R.id.fragmentContainerView, normalFragment);
                }else if(!(fragment instanceof NormalFragment)){
                    transaction.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom);
                    transaction.replace(R.id.fragmentContainerView, normalFragment);
                }
                break;
            case AddConsoleFRAGMENT:
                if(!(fragment instanceof AddConsoleFragment)) {
                    transaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top);
                    transaction.replace(R.id.fragmentContainerView, addConsoleFragment);
                }
                break;
            case AddTitleFRAGMENT:
                if(!(fragment instanceof AddTitleFragment)) {
                    transaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top);
                    transaction.replace(R.id.fragmentContainerView, addTitleFragment);
                }
                break;
            case StatisticFRAGMENT:
                if(!(fragment instanceof StatisticFragment)){
                    transaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left);
                    transaction.replace(R.id.fragmentContainerView, statisticFragment);
                }
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
        String won=getResources().getString(R.string.unit_won);
        String ea=getResources().getString(R.string.unit_ea);

        if(userInfo!=null){
            String titleCount= NumberFormat.getInstance().format(userInfo.getOwnTitleCount());
            String totalPrice= NumberFormat.getInstance().format(userInfo.getTotalPrice());
            String consoleCount= NumberFormat.getInstance().format(userInfo.getOwnConsoleCount());

            userNameET.setText(userInfo.getUserName());
            totalTitleCountTV.setText(titleCount+ea);
            totalBuyPriceTV.setText(totalPrice+won);
            totalConsoleCountTV.setText(consoleCount+ea);

            Commons.totalConsoleCount=userInfo.getOwnConsoleCount();
            Commons.totalTitleCount=userInfo.getOwnTitleCount();
            Commons.totalConsumePrice=userInfo.getTotalPrice();

            Log.d(TAG,"userInfo : "+String.valueOf(userInfo.getOwnTitleCount()));
        }else{
            userNameET.setText("Unknown");
            totalTitleCountTV.setText(String.valueOf(0)+ea);
            totalBuyPriceTV.setText(String.valueOf(0)+won);
            totalConsoleCountTV.setText(String.valueOf(0)+ea);

            Commons.totalConsoleCount=0;
            Commons.totalTitleCount=0;
            Commons.totalConsumePrice=0;
        }

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