package com.dogegames.gama;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
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
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TitleDialog extends Dialog {
    private final String TAG="TitleDialog";
    private Context context;
    private UserTitleInfo userTitleInfo;

    Spinner consoleNameDropdown;
    Spinner genreDropdown;
    EditText gameTitleNameET;
    EditText gameMakerET;
    EditText memoET;
    EditText ratingET;
    EditText buyPriceET;
    EditText buyDateET;

    ImageButton modifyBTN;
    ImageButton cancelBTN;
    ImageButton closeBTN;
    ImageButton saveBTN;
    ImageButton deleteBTN;
    ImageButton datePickerBTN;
    ImageButton imagePickerBTN;

    ImageView gameTitleIV;

    boolean isModifyMode=false;

    TitleDBHelper titleDBHelper;

    ActivityResultLauncher<Intent> resultLauncher;

    public interface OnItemDeleteListener{
        void onDeleteItem();
    }

    public interface OnItemSaveListener{
        void onSaveItem(String gameTitleName, String imagePath);
    }

    public interface OnShowDatePickerListener{
        void onShowDatePicker();
    }

    public interface OnShowImagePickerListener{
        void onShowImagePicker(String imagePath);
    }

    public OnItemDeleteListener dListener=null;
    public OnItemSaveListener sListener=null;
    public OnShowDatePickerListener datePickerListener=null;
    public OnShowImagePickerListener imagePickerListener=null;

    public void setOnShowDatePickerListener(OnShowDatePickerListener listener){
        this.datePickerListener=listener;
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener){
        this.dListener=listener;
    }

    public void setOnItemSaveListener(OnItemSaveListener listener){
        this.sListener=listener;
    }

    public void setOnImagePickerListener(OnShowImagePickerListener listener){
        this.imagePickerListener=listener;
    }

    public TitleDialog(@NonNull Context context, UserTitleInfo userTitleInfo) {
        super(context);
        this.context=context;
        this.userTitleInfo=userTitleInfo;
        setOwnerActivity((Activity) context);
    }

    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            Glide.with(context).load(getImageFullPath(userTitleInfo.getImagePath())).into(gameTitleIV);
            gameTitleIV.setScaleType(ImageView.ScaleType.FIT_XY);
            Log.d(TAG,"Title dialog has Focused");
        }else{
            Log.d(TAG,"Title dialog focus has been lost");
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_dialog);

        consoleNameDropdown=findViewById(R.id.consoleNameForTitleDialogDropdown);
        gameTitleNameET=findViewById(R.id.gameTitleNameForTitleDialogEditText);
        gameMakerET=findViewById(R.id.gameMakerNameForTitleDialogEditText);
        memoET=findViewById(R.id.gameMemoForTitleDialogEditText);
        ratingET=findViewById(R.id.gameMemoForTitleDialogEditText);
        buyPriceET=findViewById(R.id.gamePriceForTitleDialogEditText);
        buyDateET=findViewById(R.id.gameBuyDateForAddTitleEditText);
        genreDropdown=findViewById(R.id.gameGenreDropdown);
        gameTitleIV=findViewById(R.id.gameTitleImageViewInTitleDialog);


        closeBTN=findViewById(R.id.closeButtonInTitleDialog);
        saveBTN=findViewById(R.id.saveButtonInTitleDialog);
        modifyBTN=findViewById(R.id.modifyButtonInTitleDialog);
        deleteBTN=findViewById(R.id.deleteButtonInTitleDialog);
        cancelBTN=findViewById(R.id.cancelButtonInTitleDialog);
        datePickerBTN=findViewById(R.id.showDatePickerForTitleDialogButton);
        imagePickerBTN=findViewById(R.id.imagePickerButtonInTitleDialog);

        titleDBHelper=TitleDBHelper.getInstance(getContext());

        setConsoleDropdown();
        setDialogSize();
        setGenreDropdown();

        gameTitleNameET.setText(userTitleInfo.getName());
        Log.d(TAG,"userTitleInfo.getImagePath() : "+getImageFullPath(userTitleInfo.getImagePath()));
        Glide.with(context).load(getImageFullPath(userTitleInfo.getImagePath())).into(gameTitleIV);
        gameTitleIV.setScaleType(ImageView.ScaleType.FIT_XY);

        setModifyMode(false);

        // if(NormalFragment.imagePickerReturnValue!=null) Log.d(TAG,"ImagePickerReturnValue in NormalFragment !: "+NormalFragment.imagePickerReturnValue);
        //Log.d(TAG,"ImagePickerReturnValue in NormalFragment : "+NormalFragment.imagePickerReturnValue);
        closeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        modifyBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                setModifyMode(true);
            }
        });

        datePickerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isModifyMode){
                    setModifyMode(false);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    String titleName=gameTitleNameET.getText().toString();
                    if(titleName.equals("")){
                        titleName="null";
                    }

                    String platform = consoleNameDropdown.getSelectedItem().toString();
                    Log.d(TAG, "platform : " + platform);
                    if(platform.equals("")){
                        platform="null";
                    }

                    String genre=genreDropdown.getSelectedItem().toString();
                    if(genre.equals("")){
                        genre="null";
                    }

                    String price = buyPriceET.getText().toString();
                    if (price.equals("")) {
                        price = "0";
                    }

                    String rating = ratingET.getText().toString();
                    if (rating.equals("")) {
                        rating = "0";
                    }

                    String makerName = gameMakerET.getText().toString();
                    if (makerName.equals("")) {
                        makerName = "NONE";
                    }

                    String memo = memoET.getText().toString();
                    if (memo.equals("")) {
                        memo = "NONE";
                    }

                    Date buyDate;

                    try {
                        buyDate = sdf.parse(buyDateET.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        buyDate = new Date();
                    }

                    titleDBHelper.modifyRecord(TitleDBHelper.TABLE_NAME, titleName, platform, makerName, buyDate, userTitleInfo.getImagePath(), genre, memo, Integer.valueOf(price), Integer.valueOf(rating), userTitleInfo.getId());
                    if(sListener!=null) sListener.onSaveItem(titleName,userTitleInfo.getImagePath());
                }
            }
        });

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isModifyMode){
                    setModifyMode(false);
                }
            }
        });

        deleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleDBHelper.deleteRecord(TitleDBHelper.TABLE_NAME, userTitleInfo.getId());
                dismiss();
                dListener.onDeleteItem();
            }
        });

        imagePickerBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                imagePickerListener.onShowImagePicker(userTitleInfo.getImagePath());
                /*Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                resultLauncher.launch(intent);*/
                //datePickerListener.onShowDatePicker();
            }
        });
    }


    private void setGenreDropdown(){
        String[] consoleItems=getContext().getResources().getStringArray(R.array.genre);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(), R.layout.spinner_item, consoleItems);
        genreDropdown.setAdapter(adapter);
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

    void setConsoleDropdown(){
        String[] consoleItems=getContext().getResources().getStringArray(R.array.console_list);//new String[]{"Playstation 1","Playstation 2","Playstation 3","XBOX","XBOX 360"};
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(), R.layout.spinner_item,consoleItems);
        consoleNameDropdown.setAdapter(adapter);
    }

    void setModifyMode(boolean isModifyMode){
        if(!isModifyMode) {//수정 모드가 아닐때
            consoleNameDropdown.setEnabled(false);
            genreDropdown.setEnabled(false);
            gameTitleNameET.setEnabled(false);
            gameMakerET.setEnabled(false);
            memoET.setEnabled(false);
            ratingET.setEnabled(false);
            buyPriceET.setEnabled(false);
            buyDateET.setEnabled(false);
            
            datePickerBTN.setEnabled(false);
            datePickerBTN.setImageResource(R.drawable.date_picker_3_gray);

            saveBTN.setVisibility(View.GONE);
            cancelBTN.setVisibility(View.GONE);
            modifyBTN.setVisibility(View.VISIBLE);
            deleteBTN.setVisibility(View.VISIBLE);
        }else{//수정 모드 일때
            consoleNameDropdown.setEnabled(true);
            genreDropdown.setEnabled(true);
            gameTitleNameET.setEnabled(true);
            gameMakerET.setEnabled(true);
            memoET.setEnabled(true);
            ratingET.setEnabled(true);
            buyPriceET.setEnabled(true);
            buyDateET.setEnabled(true);

            datePickerBTN.setEnabled(true);
            datePickerBTN.setImageResource(R.drawable.date_picker_3);

            saveBTN.setVisibility(View.VISIBLE);
            cancelBTN.setVisibility(View.VISIBLE);
            modifyBTN.setVisibility(View.GONE);
            deleteBTN.setVisibility(View.GONE);
        }
        this.isModifyMode=isModifyMode;
    }

    void showDatePicker(){
        DatePickerDialog.OnDateSetListener mDateSetListener=new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                String yyyyMMdd=year+"-"+(month+1)+"-"+dayOfMonth;
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                Log.d(TAG,"onDateSet");
                try {
                    Date to=sdf.parse(yyyyMMdd);
                    buyDateET.setText(sdf.format(to));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        int year, month, day;
        String strDate=buyDateET.getText().toString();
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
        new DatePickerDialog(context,mDateSetListener,year,month,day).show();
    }

    Date convertStringToDate(String strDate){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();
        try {
            date=sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public String getImageFullPath(String imagePath){
        return getContext().getCacheDir() + "/" + imagePath;
    }
}
