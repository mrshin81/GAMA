package com.dogegames.gama;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTitleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTitleFragment extends Fragment {
    final static String TAG="AddTitleFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageButton closeBTN;
    ImageButton addTitleBTN;
    ImageButton showDatePickerBTN;
    ImageButton imagePickerBTN;

    Spinner platformDropdown;
    Spinner genreDropdown;
    EditText titleNameET;
    EditText makerNameET;
    EditText buyDateET;
    EditText memoET;
    EditText buyPriceET;
    EditText ratingET;
    ImageView titleIV;

    TitleDBHelper titleDBHelper;

    ActivityResultLauncher<Intent> resultLauncher;

    Bitmap imgBitmap;
    Bitmap rotatedBitmap;

    public AddTitleFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AddTitleFragment newInstance(String param1, String param2) {
        AddTitleFragment fragment = new AddTitleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add_title, container, false);
        closeBTN=view.findViewById(R.id.closeAddTitleFragmentButton);
        addTitleBTN=view.findViewById(R.id.addTitleButton);
        showDatePickerBTN=view.findViewById(R.id.showDatePickerForAddTitleButton);
        platformDropdown=view.findViewById(R.id.consoleNameForAddTitleDropdown);
        titleNameET=view.findViewById(R.id.gameTitleNameForAddTitleEditText);
        makerNameET=view.findViewById(R.id.gameMakerNameForAddTitleEditText);
        buyDateET=view.findViewById(R.id.gameBuyDateForAddTitleEditText);
        buyPriceET=view.findViewById(R.id.gamePriceForAddTitleEditText);
        memoET=view.findViewById(R.id.gameMemoForAddTitleEditText);
        ratingET=view.findViewById(R.id.gameRatingForAddTitleEditText);
        genreDropdown=view.findViewById(R.id.gameGenreDropdown);
        titleIV=view.findViewById(R.id.gameTitleImageView);
        imagePickerBTN=view.findViewById(R.id.imagePickerButton);
        titleDBHelper=TitleDBHelper.getInstance(getContext());

        loadImageFromFile("test.png");
        setPlatformDropdown();
        setGenreDropdown();
        //getImageOrientation("test.png");

        closeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setFrament(MainActivity.NormalFRAGMENT);
            }
        });

        imagePickerBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                resultLauncher.launch(intent);
            }
        });

        addTitleBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!titleNameET.getText().toString().equals("")){
                    addTitle();
                    ((MainActivity)getActivity()).setFrament(MainActivity.NormalFRAGMENT);
                    Log.d(TAG,"addTitle Completed.."+titleNameET.getText());
                }else{
                    Toast.makeText(getContext(), "게임이름은 반드시 입력되어야 합니다.\n게임타이틀 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"addTitle Button Clicked, but titleName Empty." );
                }
            }
        });

        resultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Log.d(TAG,"resultCode : "+result.getResultCode());
                if(result.getResultCode()== Activity.RESULT_OK){
                    Uri fileUri=result.getData().getData();
                    try{
                        Glide.with(getContext()).load(fileUri).into(titleIV);
                        titleIV.setScaleType(ImageView.ScaleType.FIT_XY);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }else if(result.getResultCode()==Activity.RESULT_CANCELED){
                    Log.d(TAG,"imagePicker canceled");
                }
            }
        });

        showDatePickerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void loadImageFromFile(String fileName){
        File imgFile=new File(getActivity().getCacheDir()+"/"+fileName);
        if(imgFile.exists()){
            Glide.with(this).load(imgFile).apply(RequestOptions.skipMemoryCacheOf(true)).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(titleIV);
        }else {
            titleIV.setImageResource(R.drawable.image_icon);
        }
        titleIV.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    private int getImageOrientation(String fileName){
        File imgFile=new File(getActivity().getCacheDir()+"/"+fileName);
        try{
            ExifInterface exif= null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                exif = new ExifInterface(imgFile.getAbsoluteFile());
            }
            int exifOrientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
            int exifDegree=exifOrientationToDegrees(exifOrientation);

            Log.d(TAG,"exifDegree : "+exifDegree+", exifOrientation : "+exifOrientation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int exifOrientationToDegrees(int exifOrientation){
        if(exifOrientation== ExifInterface.ORIENTATION_ROTATE_90){
            return 90;
        }else if(exifOrientation==ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        }else if(exifOrientation==ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
    }

    private Bitmap rotateBitmap(Bitmap src, float degree){
        Matrix matrix=new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(src,0,0,src.getWidth(),src.getHeight(),matrix,true);
    }

    private void saveBitmapToPNG(String fileName){//Bitmap bitmap, String fileName){
        BitmapDrawable drawable= (BitmapDrawable) titleIV.getDrawable();
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

            }
        });
    }

    private void setPlatformDropdown(){
        String[] consoleItems=getResources().getStringArray(R.array.console_list);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(), R.layout.spinner_item, consoleItems);
        platformDropdown.setAdapter(adapter);
    }

    private void setGenreDropdown(){
        String[] consoleItems=getResources().getStringArray(R.array.genre);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(), R.layout.spinner_item, consoleItems);
        genreDropdown.setAdapter(adapter);
    }

    private void addTitle() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long timeStamp = System.currentTimeMillis();

        String titleFirstLetter;
        if (!titleNameET.getText().toString().equals("")) {
            titleFirstLetter = titleNameET.getText().toString().substring(0, 1);
        } else {
            titleFirstLetter = "x";
        }

        String platform = platformDropdown.getSelectedItem().toString();
        Log.d(TAG, "platform : " + platform);
        if(platform.equals("")){
            platform="null";
        }

        String genre=genreDropdown.getSelectedItem().toString();
        if(genre.equals("")){
            genre="null";
        }

        String id = titleFirstLetter + timeStamp;

        String price = buyPriceET.getText().toString();
        if (price.equals("")) {
            price = "0";
        }

        String rating = ratingET.getText().toString();
        if (rating.equals("")) {
            rating = "0";
        }

        String makerName = makerNameET.getText().toString();
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

        String imgFileName = id + ".png";
        saveBitmapToPNG(imgFileName);//사진첩에서 고른 사진을 내부저장소에 저장

        String imagePath = imgFileName;//getActivity().getCacheDir() + "/" + imgFileName;//사진 찍던지 사진첩에서 선택해서 저장된 리소스를 활용해야한다.

        titleDBHelper.insertRecord(TitleDBHelper.TABLE_NAME, titleNameET.getText().toString(), platform, makerName, buyDate, imagePath, genre, memoET.getText().toString(), Integer.valueOf(price),Integer.valueOf(rating), titleDBHelper.getNo(), id);
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
        new DatePickerDialog(getContext(),mDateSetListener,year,month,day).show();
    }
}