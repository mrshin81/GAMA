package com.dogegames.gama;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

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

    boolean isModifyMode=false;

    TitleDBHelper titleDBHelper;

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


        consoleNameDropdown=findViewById(R.id.consoleNameForTitleDialogDropdown);
        gameTitleNameET=findViewById(R.id.gameTitleNameForTitleDialogEditText);
        gameMakerET=findViewById(R.id.gameMakerNameForTitleDialogEditText);
        memoET=findViewById(R.id.gameMemoForTitleDialogEditText);
        ratingET=findViewById(R.id.gameMemoForTitleDialogEditText);
        buyPriceET=findViewById(R.id.gamePriceForTitleDialogEditText);
        buyDateET=findViewById(R.id.gameBuyDateForAddTitleEditText);
        genreDropdown=findViewById(R.id.gameGenreDropdown);

        closeBTN=findViewById(R.id.closeButtonInTitleDialog);
        saveBTN=findViewById(R.id.saveButtonInTitleDialog);
        modifyBTN=findViewById(R.id.modifyButtonInTitleDialog);
        deleteBTN=findViewById(R.id.deleteButtonInTitleDialog);
        cancelBTN=findViewById(R.id.cancelButtonInTitleDialog);
        datePickerBTN=findViewById(R.id.showDatePickerForTitleDialogButton);

        titleDBHelper=TitleDBHelper.getInstance(getContext());
        setConsoleDropdown();
        setDialogSize();

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
                    titleDBHelper.modifyRecord(TitleDBHelper.TABLE_NAME, gameTitleNameET.getText().toString(), consoleNameDropdown.getSelectedItem().toString(), gameMakerET.getText().toString(), convertStringToDate(buyDateET.getText().toString()), userTitleInfo.getImagePath(), genreDropdown.getSelectedItem().toString(), memoET.getText().toString(), Integer.valueOf(buyPriceET.getText().toString()), Integer.valueOf(ratingET.getText().toString()), userTitleInfo.getId());
                    sListener.onSaveItem();
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
}
