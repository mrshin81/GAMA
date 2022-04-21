package com.dogegames.gama;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    int titlePriceAfter=0;
    int titlePriceBefore=0;

    public interface OnItemDeleteListener{
        void onDeleteItem();
    }

    public interface OnItemSaveListener{
        void onSaveItem(String gameTitleName, String imagePath);
    }

    public TitleDialogFragment.OnItemDeleteListener dListener=null;
    public TitleDialogFragment.OnItemSaveListener sListener=null;

    public void setOnItemDeleteListener(TitleDialogFragment.OnItemDeleteListener listener){
        this.dListener=listener;
    }

    public void setOnItemSaveListener(TitleDialogFragment.OnItemSaveListener listener){
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
        ratingET=view.findViewById(R.id.gameRatingForTitleDialogFragmentEditText);
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

        //Open시 불러온 데이터 각 UI에 표시하기
        String[] consoleItems=getContext().getResources().getStringArray(R.array.console_list);
        ((Commons)getActivity().getApplication()).setDropdown(consoleItems, consoleNameDropdown);
        consoleNameDropdown.setSelection(userTitleInfo.getConsoleNumber());
        //Log.d(TAG,"userTitleInfo.getConsoleNumber() : "+userTitleInfo.getConsoleNumber());

        String[] genreItems=getContext().getResources().getStringArray(R.array.genre);
        ((Commons)getActivity().getApplication()).setDropdown(genreItems,genreDropdown);
        genreDropdown.setSelection(userTitleInfo.getGenreNumber());
        Log.d(TAG,"getGenreNumber() : "+userTitleInfo.getGenreNumber());

        gameTitleNameET.setText(userTitleInfo.getName());
        gameMakerET.setText(userTitleInfo.getMaker());
        memoET.setText(userTitleInfo.getMemo());
        ratingET.setText(String.valueOf(userTitleInfo.getRating()));
        buyPriceET.setText(String.valueOf(userTitleInfo.getPrice()));
        buyDateET.setText(((Commons)getActivity().getApplication()).convertDateToString(userTitleInfo.getBuyDate()));

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
                titlePriceBefore=Integer.valueOf(buyPriceET.getText().toString());
            }
        });

        datePickerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Commons)getActivity().getApplication()).showDatePicker(buyDateET);
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
                    saveImageViewToPNG_AutoRefresh(imgFileName);//사진첩에서 고른 사진을 내부저장소에 저장
                    //((Commons)getActivity().getApplication()).saveImageViewToPNG(gameTitleIV,imgFileName);

                    if(userTitleInfo.getPlatform().equals(platform)){//타이틀 수정시에 플랫폼이 수정 되지 않을경우(즉, 기존 저장된 플랫폼하고 새로 저장할 플랫폼이 같은 경우)
                        //Log.d(TAG,"equals, getPlatform() : "+userTitleInfo.getPlatform()+", platform : "+platform);
                        String tableName=platform.replace(" ","_");
                        titleDBHelper.modifyRecord(tableName, titleName, platform, makerName, buyDate, userTitleInfo.getImagePath(), genre, memo, Integer.valueOf(price), Integer.valueOf(rating), userTitleInfo.getId());
                        //Log.d(TAG,"Selected Genre : "+genre);
                    }else{//타이틀 수정시에 플랫폼이 수정된 경우(다른 플랫폼의 테이블에 데이터 추가하고, 기존 테이블에 데이터 삭제한다.)
                        Log.d(TAG,"Not equals, getPlatform() : "+userTitleInfo.getPlatform()+", platform : "+platform);
                        String preTableName=userTitleInfo.getPlatform().replace(" ","_");
                        String tableName=platform.replace(" ","_");
                        titleDBHelper.createTable(tableName);
                        titleDBHelper.insertRecord(tableName, titleName, platform, makerName, buyDate, userTitleInfo.getImagePath(),genre,memo,Integer.valueOf(price),Integer.valueOf(rating),titleDBHelper.getNo(tableName),userTitleInfo.getId());
                        titleDBHelper.deleteRecord(preTableName, userTitleInfo.getId());
                        //Log.d(TAG,"Selected Genre 2 : "+genre);
                    }

                    titlePriceAfter=Integer.valueOf(buyPriceET.getText().toString());
                    int diffPrice=titlePriceAfter-titlePriceBefore;

                    ((Commons)getActivity().getApplication()).modifyTotalTitleCountAndPrice(0,+1*diffPrice);
                    ((MainActivity)getActivity()).displayUserInfo(); //수정 후에 User Status Bar를 갱신해준다.

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
                String tableName= userTitleInfo.getPlatform().replace(" ","_");
                titleDBHelper.deleteRecord(tableName, userTitleInfo.getId());

                //user status bar 정보 수정
                ((Commons)getActivity().getApplication()).modifyTotalTitleCountAndPrice(-1,-1*Integer.valueOf(buyPriceET.getText().toString()));
                ((MainActivity)getActivity()).displayUserInfo(); //수정 후에 User Status Bar를 갱신해준다.

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

    private void saveImageViewToPNG_AutoRefresh(String fileName){//Bitmap bitmap, String fileName){
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
            public void onLoadCleared(@Nullable Drawable placeholder) {//titledialogfragment가 닫히면 자동으로 recyclerview 아이템들을 리프레시 한다.
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
        ((Commons)getActivity().getApplication()).setDialogSize(getActivity(), getDialog(), 0.9f, 0.8f);
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

            imagePickerBTN.setEnabled(false);
            imagePickerBTN.setVisibility(View.INVISIBLE);
            datePickerBTN.setEnabled(false);
            //datePickerBTN.setClickable(false);
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

            imagePickerBTN.setEnabled(true);
            imagePickerBTN.setVisibility(View.VISIBLE);
            datePickerBTN.setEnabled(true);
            datePickerBTN.setImageResource(R.drawable.date_picker_3);

            saveBTN.setVisibility(View.VISIBLE);
            cancelBTN.setVisibility(View.VISIBLE);
            modifyBTN.setVisibility(View.GONE);
            deleteBTN.setVisibility(View.GONE);
        }
        this.isModifyMode=isModifyMode;
    }

    public String getImageFullPath(String imagePath){
        return getContext().getCacheDir() + "/" + imagePath;
    }

}
