package com.dogegames.gama;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TitleDialogFragment extends DialogFragment {

    private static final String TAG = "TitleDialogFragment";
    private static final String ARG_DIALOG_MAIN_MSG = "dialog_main_msg";
    private String mMainMsg;

    static private Context context;
    static private UserTitleInfo userTitleInfo;


    ActivityResultLauncher<Intent> resultLauncher;

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

    public interface OnItemDeleteListener{
        void onDeleteItem();
    }

    public interface OnItemSaveListener{
        void onSaveItem(String gameTitleName, String imagePath);
    }

    public TitleDialog.OnItemDeleteListener dListener=null;
    public TitleDialog.OnItemSaveListener sListener=null;

    public void setOnItemDeleteListener(TitleDialog.OnItemDeleteListener listener){
        this.dListener=listener;
    }

    public void setOnItemSaveListener(TitleDialog.OnItemSaveListener listener){
        this.sListener=listener;
    }


    public static TitleDialogFragment newInstance(String mainMsg, Context context1, UserTitleInfo userTitleInfo1) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_DIALOG_MAIN_MSG, mainMsg);
        TitleDialogFragment fragment = new TitleDialogFragment();
        fragment.setArguments(bundle);
        context=context1;
        userTitleInfo=userTitleInfo1;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        View view=getActivity().getLayoutInflater().inflate(R.layout.title_dialog_fragment,null);

        builder.setView(view);

        titleDBHelper=TitleDBHelper.getInstance(getContext());

        consoleNameDropdown=view.findViewById(R.id.consoleNameForTitleDialogFragmentDropdown);
        gameTitleNameET=view.findViewById(R.id.gameTitleNameForTitleDialogFragmentEditText);
        gameMakerET=view.findViewById(R.id.gameMakerNameForTitleDialogFragmentEditText);
        memoET=view.findViewById(R.id.gameMemoForTitleDialogFragmentEditText);
        ratingET=view.findViewById(R.id.gameMemoForTitleDialogFragmentEditText);
        buyPriceET=view.findViewById(R.id.gamePriceForTitleDialogFragmentEditText);
        buyDateET=view.findViewById(R.id.gameBuyDateForTitleDialogFragmentEditText);
        genreDropdown=view.findViewById(R.id.gameGenreDropdownForTitleDialogFragment);
        gameTitleIV=view.findViewById(R.id.gameTitleImageViewInTitleDialogFragment);

        imagePickerBTN=view.findViewById(R.id.imagePickerButtonInTitleDialogFragment);
        closeBTN=view.findViewById(R.id.closeButtonInTitleDialogFragment);
        saveBTN=view.findViewById(R.id.saveButtonInTitleDialogFragment);
        modifyBTN=view.findViewById(R.id.modifyButtonInTitleDialogFragment);
        deleteBTN=view.findViewById(R.id.deleteButtonInTitleDialogFragment);
        cancelBTN=view.findViewById(R.id.cancelButtonInTitleDialogFragment);
        datePickerBTN=view.findViewById(R.id.showDatePickerForTitleDialogFragmentButton);

        setConsoleDropdown();
        setGenreDropdown();

        gameTitleNameET.setText(userTitleInfo.getName());
        Log.d(TAG,"userTitleInfo.getImagePath() : "+getImageFullPath(userTitleInfo.getImagePath()));
        //Glide.with(context).load(getImageFullPath(userTitleInfo.getImagePath())).into(gameTitleIV);
        Glide.with(this).load(getImageFullPath(userTitleInfo.getImagePath())).apply(RequestOptions.skipMemoryCacheOf(true)).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(gameTitleIV);
        gameTitleIV.setScaleType(ImageView.ScaleType.FIT_XY);

        setModifyMode(false);

        imagePickerBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                resultLauncher.launch(intent);
            }
        });

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

                    String imgFileName = userTitleInfo.getImagePath();
                    saveBitmapToPNG(imgFileName);//사진첩에서 고른 사진을 내부저장소에 저장

                    titleDBHelper.modifyRecord(TitleDBHelper.TABLE_NAME, titleName, platform, makerName, buyDate, userTitleInfo.getImagePath(), genre, memo, Integer.valueOf(price), Integer.valueOf(rating), userTitleInfo.getId());
                    //if(sListener!=null) sListener.onSaveItem(titleName,userTitleInfo.getImagePath());
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


        resultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Log.d(TAG,"resultCode : "+result.getResultCode());
                if(result.getResultCode()== Activity.RESULT_OK){
                    Uri fileUri=result.getData().getData();
                    try{
                        Glide.with(getContext()).load(fileUri).into(gameTitleIV);
                        gameTitleIV.setScaleType(ImageView.ScaleType.FIT_XY);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }else if(result.getResultCode()==Activity.RESULT_CANCELED){
                    Log.d(TAG,"imagePicker canceled");
                }
            }
        });

        return builder.create();//super.onCreateDialog(savedInstanceState);
    }

    private void saveBitmapToPNG(String fileName){//Bitmap bitmap, String fileName){
        BitmapDrawable drawable= (BitmapDrawable) gameTitleIV.getDrawable();
        Bitmap bitmap=drawable.getBitmap();

        Glide.with(this).load(bitmap).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                File tempFile=new File(getActivity().getCacheDir(),fileName);
                try{
                    OutputStream out=new FileOutputStream(tempFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG,25,out);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                String titleName=gameTitleNameET.getText().toString();
                if(titleName.equals("")){
                    titleName="null";
                }
                if(sListener!=null) sListener.onSaveItem(titleName,userTitleInfo.getImagePath());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setDialogSize();

    }

    void setDialogSize(){
        Display display=getActivity().getWindowManager().getDefaultDisplay();
        Point size=new Point();
        display.getSize(size);
        int x=(int)(size.x*0.9f);
        int y=(int)(size.y*0.8f);

        getDialog().getWindow().setLayout(x,y);
    }

    private void setGenreDropdown(){
        String[] consoleItems=getContext().getResources().getStringArray(R.array.genre);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(), R.layout.spinner_item, consoleItems);
        genreDropdown.setAdapter(adapter);
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

    public String getImageFullPath(String imagePath){
        return getContext().getCacheDir() + "/" + imagePath;
    }

}
