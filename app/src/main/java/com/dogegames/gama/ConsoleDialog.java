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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ConsoleDialog extends Dialog {

    final static String TAG="ConsoleDialog";

    public interface OnItemDeleteListener{
        void onDeleteItem();
    }

    public interface OnItemSaveListener{
        void onSaveItem(String consoleName, String imagePath);
    }

    public OnItemDeleteListener mListener=null;

    public OnItemSaveListener sListener=null;

    public void setOnItemDeleteListener(OnItemDeleteListener listener){
        this.mListener=listener;
    }

    public void setOnItemSaveListener(OnItemSaveListener listener){
        this.sListener=listener;
    }

    DatePickerDialog datePickerDialog=null;

    private Context context;
    private UserConsoleInfo userConsoleInfo;
    ImageButton modifyBTN;
    ImageButton cancelBTN;
    ImageButton closeBTN;
    ImageButton saveBTN;
    ImageButton deleteBTN;
    ImageButton datePickerBTN;
    EditText consoleBuyPriceET;
    EditText consoleBuyDateET;
    EditText consoleMemoET;

    TextView consoleDescMakerTV;
    TextView consoleDescDateTV;
    TextView consoleDescSpecTV;
    ImageView consoleDescIV;

    Spinner consoleNameDropdown;

    ConsoleDBHelper consoleDBHelper;

    boolean isModifyMode=false;

    public ConsoleDialog(@NonNull Context context, UserConsoleInfo userConsoleInfo) {
        super(context);
        this.context=context;
        setOwnerActivity((Activity)context);
        this.userConsoleInfo=userConsoleInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console_dialog);


        modifyBTN=findViewById(R.id.modifyButton);
        cancelBTN=findViewById(R.id.cancelButton);
        saveBTN=findViewById(R.id.saveButton);
        deleteBTN=findViewById(R.id.deleteButton);
        datePickerBTN=findViewById(R.id.showDatePickerButton);

        consoleNameDropdown=findViewById(R.id.consoleNameDropdown);
        consoleBuyPriceET=findViewById(R.id.consoleBuyPriceEditText);
        consoleBuyDateET=findViewById(R.id.consoleBuyDateEditText);
        consoleMemoET=findViewById(R.id.consoleMemoEditText);
        consoleDescMakerTV=findViewById(R.id.consoleDescMakerTextView);
        consoleDescDateTV=findViewById(R.id.consoleDescLaunchDateTextView);
        consoleDescSpecTV=findViewById(R.id.consoleDescSpecTextView);
        consoleDescIV=findViewById(R.id.consoleDescImageView);

        setConsoleDropdown();

        consoleNameDropdown.setSelection(userConsoleInfo.getConsoleNumber());
        consoleBuyPriceET.setText(String.valueOf(userConsoleInfo.getPrice()));
        consoleBuyDateET.setText(MainActivity.convertDateToString(userConsoleInfo.getDate()));
        consoleMemoET.setText(userConsoleInfo.getMemo());

        consoleNameDropdown.setEnabled(false);
        consoleBuyDateET.setEnabled(false);
        consoleBuyPriceET.setEnabled(false);
        consoleMemoET.setEnabled(false);
        datePickerBTN.setEnabled(false);

        consoleDBHelper=ConsoleDBHelper.getInstance(getContext());

        setDialogSize();

        closeBTN=findViewById(R.id.closeButton);
        closeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        modifyBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isModifyMode){
                    setModifyMode(true);
                    //Toast.makeText(getOwnerActivity(), .LENGTH_LONG);
                    //Log.d(TAG,"ID : "+userConsoleInfo.getId());
                }
            }
        });

        datePickerBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(isModifyMode){
                    String[] consoleImagePath=getContext().getResources().getStringArray(R.array.console_imagepath);

                    setModifyMode(false);
                    consoleDBHelper.modifyRecord(ConsoleDBHelper.TABLE_NAME, consoleNameDropdown.getSelectedItem().toString(),consoleImagePath[consoleNameDropdown.getSelectedItemPosition()], Integer.valueOf(consoleBuyPriceET.getText().toString()),convertStringToDate(consoleBuyDateET.getText().toString()),consoleMemoET.getText().toString(), userConsoleInfo.getId());
                    sListener.onSaveItem(consoleNameDropdown.getSelectedItem().toString(), consoleImagePath[consoleNameDropdown.getSelectedItemPosition()]);
                }
            }
        });

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isModifyMode){
                    setModifyMode(false);
                }
            }
        });

        deleteBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                consoleDBHelper.deleteRecord(ConsoleDBHelper.TABLE_NAME, userConsoleInfo.getId());
                dismiss();
                mListener.onDeleteItem();
            }
        });

        consoleNameDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setConsoleSpec(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setConsoleSpec(consoleNameDropdown.getSelectedItemPosition());
    }

    void setConsoleDropdown(){
        String[] consoleItems=getContext().getResources().getStringArray(R.array.console_list);//new String[]{"Playstation 1","Playstation 2","Playstation 3","XBOX","XBOX 360"};
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(), R.layout.spinner_item,consoleItems);
        consoleNameDropdown.setAdapter(adapter);
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

    void setModifyMode(boolean isModifyMode){
        if(!isModifyMode){
            consoleNameDropdown.setEnabled(false);
            consoleBuyDateET.setEnabled(false);
            consoleBuyPriceET.setEnabled(false);
            consoleMemoET.setEnabled(false);

            datePickerBTN.setEnabled(false);
            datePickerBTN.setImageResource(R.drawable.date_picker_3_gray);

            saveBTN.setVisibility(View.GONE);
            cancelBTN.setVisibility(View.GONE);
            modifyBTN.setVisibility(View.VISIBLE);
            deleteBTN.setVisibility(View.VISIBLE);
        }else{
            consoleNameDropdown.setEnabled(true);
            consoleBuyDateET.setEnabled(true);
            consoleBuyPriceET.setEnabled(true);
            consoleMemoET.setEnabled(true);

            datePickerBTN.setEnabled(true);
            datePickerBTN.setImageResource(R.drawable.date_picker_3);

            saveBTN.setVisibility(View.VISIBLE);
            cancelBTN.setVisibility(View.VISIBLE);
            modifyBTN.setVisibility(View.GONE);
            deleteBTN.setVisibility(View.GONE);
        }
        this.isModifyMode=isModifyMode;
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

    void setConsoleSpec(int selectedConsole){
        String[] consoleMaker=getContext().getResources().getStringArray(R.array.console_maker);
        String[] consoleDate=getContext().getResources().getStringArray(R.array.console_date);
        String[] consoleSpec=getContext().getResources().getStringArray(R.array.console_spec);
        String[] consoleImagePath=getContext().getResources().getStringArray(R.array.console_imagepath);

        consoleDescMakerTV.setText(consoleMaker[selectedConsole]);
        consoleDescDateTV.setText(consoleDate[selectedConsole]);
        consoleDescSpecTV.setText(consoleSpec[selectedConsole]);
        consoleDescIV.setImageResource(MainActivity.getImageId(getContext(),consoleImagePath[selectedConsole]));
    }

    void showDatePicker(){
        DatePickerDialog.OnDateSetListener mDateSetListener=
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String yyyyMMdd=year+"-"+(month+1)+"-"+dayOfMonth;
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                        Log.d(TAG,"onDateSet");
                        try {
                            Date to=sdf.parse(yyyyMMdd);
                            consoleBuyDateET.setText(sdf.format(to));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                };
        int year, month, day;
        String strDate=consoleBuyDateET.getText().toString();
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
}
