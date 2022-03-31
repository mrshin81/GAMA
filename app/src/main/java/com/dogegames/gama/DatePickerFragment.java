package com.dogegames.gama;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public interface OnDateSelectedListener{
        void onDateSetCallback(DatePicker datePicker, int year, int month, int day);
    }

    public OnDateSelectedListener mListener=null;

    public void setOnDateSelectedListener(OnDateSelectedListener listener){
        this.mListener=listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c=Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH);
        int day=c.get(Calendar.DATE);

        return new DatePickerDialog(getActivity(),this,year,month,day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        //날짜사 선택되어지면 실행되는 메서드
        mListener.onDateSetCallback(datePicker,i,i1,i2);
    }
}