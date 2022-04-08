package com.dogegames.gama;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleDialogFragment extends DialogFragment {
    final static String TAG="ConsoleDialogFragment";

    public interface OnItemDeleteListener{
        void onDeleteItem();
    }

    public interface OnItemSaveListener{
        void onSaveItem(String consoleName, String imagePath);
    }

    public ConsoleDialogFragment.OnItemDeleteListener mListener=null;

    public ConsoleDialogFragment.OnItemSaveListener sListener=null;

    public void setOnItemDeleteListener(ConsoleDialogFragment.OnItemDeleteListener listener){
        this.mListener=listener;
    }

    public void setOnItemSaveListener(ConsoleDialogFragment.OnItemSaveListener listener){
        this.sListener=listener;
    }

    static private Context context;
    static private UserConsoleInfo userConsoleInfo;
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


    public static ConsoleDialogFragment newInstance(Context context1, UserConsoleInfo userConsoleInfo1){
        ConsoleDialogFragment fragment=new ConsoleDialogFragment();
        context=context1;
        userConsoleInfo=userConsoleInfo1;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        View view=getActivity().getLayoutInflater().inflate(R.layout.console_dialog_fragment,null);
        builder.setView(view);

        modifyBTN=view.findViewById(R.id.modifyButtonInConsoleDialogFragment);
        cancelBTN=view.findViewById(R.id.cancelButtonInConsoleDialogFragment);
        saveBTN=view.findViewById(R.id.saveButtonInConsoleDialogFragment);
        deleteBTN=view.findViewById(R.id.deleteButtonInConsoleDialogFragment);
        datePickerBTN=view.findViewById(R.id.showDatePickerButtonInConsoleDialogFragment);
        closeBTN=view.findViewById(R.id.closeButtonInConsoleDialogFragment);

        consoleNameDropdown=view.findViewById(R.id.consoleNameDropdownInConsoleDialogFragment);
        consoleBuyPriceET=view.findViewById(R.id.consoleBuyPriceEditTextInConsoleDialogFragment);
        consoleBuyDateET=view.findViewById(R.id.consoleBuyDateEditTextInConsoleDialogFragment);
        consoleMemoET=view.findViewById(R.id.consoleMemoEditTextInConsoleDialogFragment);
        consoleDescMakerTV=view.findViewById(R.id.consoleDescMakerTextViewInConsoleDialogFragment);
        consoleDescDateTV=view.findViewById(R.id.consoleDescLaunchDateTextViewInConsoleDialogFragment);
        consoleDescSpecTV=view.findViewById(R.id.consoleDescSpecTextViewInConsoleDialogFragment);
        consoleDescIV=view.findViewById(R.id.consoleDescImageViewInConsoleDialogFragment);

        String[] consoleItems=getContext().getResources().getStringArray(R.array.console_list);
        ((Commons)context.getApplicationContext()).setDropdown(consoleItems,consoleNameDropdown);

        consoleNameDropdown.setSelection(userConsoleInfo.getConsoleNumber());
        consoleBuyPriceET.setText(String.valueOf(userConsoleInfo.getPrice()));
        consoleBuyDateET.setText(((Commons)getActivity().getApplication()).convertDateToString(userConsoleInfo.getDate()));
        consoleMemoET.setText(userConsoleInfo.getMemo());

        consoleNameDropdown.setEnabled(false);
        consoleBuyDateET.setEnabled(false);
        consoleBuyPriceET.setEnabled(false);
        consoleMemoET.setEnabled(false);
        datePickerBTN.setEnabled(false);

        consoleDBHelper=ConsoleDBHelper.getInstance(getContext());

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
                ((Commons)context.getApplicationContext()).showDatePicker(consoleBuyDateET);
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(isModifyMode){
                    String[] consoleImagePath=getContext().getResources().getStringArray(R.array.console_imagepath);

                    setModifyMode(false);
                    consoleDBHelper.modifyRecord(ConsoleDBHelper.TABLE_NAME, consoleNameDropdown.getSelectedItem().toString(),consoleImagePath[consoleNameDropdown.getSelectedItemPosition()], Integer.valueOf(consoleBuyPriceET.getText().toString()),((Commons)getActivity().getApplication()).convertStringToDate(consoleBuyDateET.getText().toString()),consoleMemoET.getText().toString(), userConsoleInfo.getId());
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


        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        //setDialogSize();
        ((Commons)getActivity().getApplication()).setDialogSize(getActivity(), getDialog(), 0.9f, 0.8f);
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

    /*Date convertStringToDate(String strDate){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();
        try {
            date=sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }*/
}
