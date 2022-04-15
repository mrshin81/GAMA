package com.dogegames.gama;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NormalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NormalFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "NormalFragment";
    private static final int FOR_SET_CONSOLE_RV=0;
    private static final int FOR_SET_CONSOLE_DIALOG=1;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String imagePathFromTitleDialog;

    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper mGameItemTouchHelper;


    private View normalFragmentView;
    Button showAllTitleBTN;
    EditText searchET;

    ConsoleDBHelper consoleDBHelper;
    TitleDBHelper titleDBHelper;
    ConsoleRecyclerViewAdapter consoleRecyclerViewAdapter;
    GameTitleRecyclerViewAdapter gameTitleRecyclerViewAdapter;

    Handler mHandler=new Handler();

    ActivityResultLauncher<Intent> resultLauncherForTitleDialog;

    ArrayList<UserTitleInfo> userTitleInfoArrayList;

    static public String imagePickerReturnValue;

    public NormalFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NormalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NormalFragment newInstance(String param1, String param2) {
        NormalFragment fragment = new NormalFragment();
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
        consoleDBHelper=ConsoleDBHelper.getInstance(getContext());
        titleDBHelper=TitleDBHelper.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        normalFragmentView=inflater.inflate(R.layout.fragment_normal, container, false);
        showAllTitleBTN=normalFragmentView.findViewById(R.id.showAllTitleButton);
        searchET=normalFragmentView.findViewById(R.id.searchEditText);

        setConsoleRecyclerViewFromDB(consoleDBHelper);//DB로부터 데이터 불러오기를 Background Thread로 동작시킨다.

        String selectedTableName=PreferenceManager.getString(getContext(), ConsoleRecyclerViewAdapter.SELECTED_CONSOLE_ITEM_STRING).replace(" ","_");

        Log.d(TAG,"selectedTableName : "+selectedTableName);
        setGameTitleRecyclerViewFromDB(titleDBHelper,selectedTableName);

        setImagePickerCallback();

        showAllTitleBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consoleRecyclerViewAdapter.unselectAllItems();
                consoleRecyclerViewAdapter.notifyDataSetChanged();
                setGameTitleRecyclerViewFromDB(titleDBHelper,"");
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText=searchET.getText().toString();
                if(userTitleInfoArrayList!=null){
                    searchFilter(userTitleInfoArrayList,searchText);
                    Log.d(TAG,"searchFilter");
                }
            }
        });
        // Inflate the layout for this fragment
        return normalFragmentView;
    }

    public void searchFilter(ArrayList<UserTitleInfo> list, String searchText){
        ArrayList<UserTitleInfo> filteredList=new ArrayList<>();
        filteredList.clear();
        for(int i=0;i<list.size();i++){
            if(list.get(i).getName().toLowerCase().contains(searchText.toLowerCase())){
                filteredList.add(list.get(i));
            }
        }
        gameTitleRecyclerViewAdapter.filterList(filteredList);
    }

    private void setImagePickerCallback(){
        //registerForAcitityResult 콜백함수 등록
        resultLauncherForTitleDialog=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                Log.d(TAG,"resultCode : "+result.getResultCode());

                if(result.getResultCode()== Activity.RESULT_OK){
                    Uri fileUri=result.getData().getData();

                    //Intent intent=result.getData();

                    //String imagePath=intent.getStringExtra("imagePath");

                    Log.d(TAG,"imagePath : "+imagePathFromTitleDialog);

                    /*Bitmap bitmap=null;
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            bitmap=ImageDecoder.decodeBitmap(ImageDecoder.createSource(getActivity().getContentResolver(), fileUri));
                        }else{
                            bitmap= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fileUri);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    /*File tempFile=new File(getActivity().getCacheDir(),imagePathFromTitleDialog);

                    try{
                        tempFile.createNewFile();
                        FileOutputStream out=new FileOutputStream(tempFile);
                        bitmap.compress(Bitmap.CompressFormat.PNG,25,out);
                        out.close();
                    } catch (Exception e){
                        e.printStackTrace();
                    }*/
                     /*try{
                                Glide.with(getContext()).load(fileUri).into(titleIV);
                                titleIV.setScaleType(ImageView.ScaleType.FIT_XY);
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }*/
                }else if(result.getResultCode()==Activity.RESULT_CANCELED){
                    Log.d(TAG,"imagePicker canceled");
                }
            }
        });
    }

    private String getSelectedConsoleName(int selectedConsole){
        String[] consoleName=getResources().getStringArray(R.array.console_list);
        String selectedConsoleName=consoleName[selectedConsole];
        selectedConsoleName=selectedConsoleName.replace(" ","_");
        return selectedConsoleName;
    }
    
    void saveConsoleData(){
        ArrayList<UserConsoleInfo> list=new ArrayList<>();
        UserConsoleInfo console1=new UserConsoleInfo(getContext(), "PlayStation 5");
        UserConsoleInfo console2=new UserConsoleInfo(getContext(),"Sega Saturn");
        list.add(console1);
        list.add(console2);
        ConsoleDataManager consoleDataManager=new ConsoleDataManager(getContext());
        consoleDataManager.SaveData(list);
    }

    
    //데이터 베이스의 Console 테이블에서 데이터를 읽어와서 ArrayList에 저장한 후 Arraylist를 리턴하는 메서드, background thread에서 동작한다.
    private void setConsoleRecyclerViewFromDB(ConsoleDBHelper consoleDBHelper){//ArrayList<UserConsoleInfo> setDBtoArrayList(ConsoleDBHelper consoleDBHelper){

        ArrayList<UserConsoleInfo> list=new ArrayList<>();

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                Cursor cursor=consoleDBHelper.selectRecord();
                UserConsoleInfo[] userConsoleInfo;
                if(cursor!=null){
                    userConsoleInfo= new UserConsoleInfo[cursor.getCount()];
                    for(int i=0;i<userConsoleInfo.length;i++){
                        userConsoleInfo[i]=new UserConsoleInfo(getContext());
                    }

                    int i=0;
                    Date date;
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

                    while(cursor.moveToNext()) {
                            userConsoleInfo[i].setName(cursor.getString(1));
                            userConsoleInfo[i].setImagePath(cursor.getString(2));
                            userConsoleInfo[i].setPrice(cursor.getInt(3));
                        try {
                            date = sdf.parse(cursor.getString(4));
                            userConsoleInfo[i].setDate(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            date=new Date();
                        }
                        userConsoleInfo[i].setMemo(cursor.getString(5));
                        userConsoleInfo[i].setId(cursor.getString(6));

                        list.add(userConsoleInfo[i]);
                        i++;
                    }
                }
                //무조건 ADD console 아이템은 RV에 추가되어야 하므로 무조건 ADD Console 아이템은 넣는다.
                {
                    UserConsoleInfo userConsoleAddItem=new UserConsoleInfo(getContext(),"ADD Console...");
                    list.add(userConsoleAddItem);
                }

                /*Message message=new Message();
                message.obj=list;
                //message.what=FOR_SET_CONSOLE_RV;
                mConsoleRVHandler.sendMessage(message);*/

                //mConsoleRVHandler.post(ConsoleRVRunnable);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setConsoleRecyclerView(list);
                    }
                });
            }
        };

        Thread thread=new Thread(runnable);
        thread.start();
    }

    private void showConsoleDialog(ArrayList<UserConsoleInfo> list, int pos){

        ConsoleDialogFragment consoleDialogFragment=ConsoleDialogFragment.newInstance(getContext(),list.get(pos));
        consoleDialogFragment.show(getActivity().getSupportFragmentManager(),"dialog");
        Log.d(TAG,"showConsoleDialog");


        consoleDialogFragment.setOnItemDeleteListener(new ConsoleDialogFragment.OnItemDeleteListener() {
            @Override
            public void onDeleteItem() {
                consoleRecyclerViewAdapter.deleteItem(pos);
                consoleRecyclerViewAdapter.notifyItemRemoved(pos);
            }
        });

        consoleDialogFragment.setOnItemSaveListener(new ConsoleDialogFragment.OnItemSaveListener() {
            @Override
            public void onSaveItem(String consoleName, String imagePath) {
                consoleRecyclerViewAdapter.list.get(pos).setName(consoleName);
                consoleRecyclerViewAdapter.list.get(pos).setImagePath(imagePath);
                //consoleRecyclerViewAdapter.notifyDataSetChanged();
                consoleRecyclerViewAdapter.notifyItemChanged(pos);
                //consoleRecyclerViewAdapter.notifyItemRangeChanged(0, gameTitleRecyclerViewAdapter.getItemCount());
                Log.d(TAG,"onSaveItem Completed..., ConsoleDialog, "+list.get(pos).getName());
            }
        });
    }



    private void showAndSetConsoleDialog(ConsoleDBHelper consoleDBHelper, int pos){//ArrayList<UserConsoleInfo> setDBtoArrayList(ConsoleDBHelper consoleDBHelper){

        ArrayList<UserConsoleInfo> list=new ArrayList<>();

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                Cursor cursor=consoleDBHelper.selectRecord();
                UserConsoleInfo[] userConsoleInfo;
                if(cursor!=null){
                    userConsoleInfo= new UserConsoleInfo[cursor.getCount()];
                    for(int i=0;i<userConsoleInfo.length;i++){
                        userConsoleInfo[i]=new UserConsoleInfo(getContext());
                    }

                    int i=0;
                    Date date;
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

                    while(cursor.moveToNext()) {
                        userConsoleInfo[i].setName(cursor.getString(1));
                        userConsoleInfo[i].setImagePath(cursor.getString(2));
                        userConsoleInfo[i].setPrice(cursor.getInt(3));

                        try {
                            date = sdf.parse(cursor.getString(4));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            date=new Date();
                        }

                        userConsoleInfo[i].setDate(date);

                        userConsoleInfo[i].setMemo(cursor.getString(5));
                        userConsoleInfo[i].setId(cursor.getString(7));

                        list.add(userConsoleInfo[i]);
                        i++;
                    }
                }
                //무조건 ADD console 아이템은 RV에 추가되어야 하므로 무조건 ADD Console 아이템은 넣는다.
                {
                    UserConsoleInfo userConsoleAddItem=new UserConsoleInfo(getContext(),"ADD Console...");
                    list.add(userConsoleAddItem);
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showConsoleDialog(list,pos);
                    }
                });

            }
        };

        Thread thread=new Thread(runnable);
        thread.start();
    }


    private void setConsoleRecyclerView(ArrayList<UserConsoleInfo> list){
        //ArrayList<UserConsoleInfo> list=new ArrayList<>();

        consoleRecyclerViewAdapter=new ConsoleRecyclerViewAdapter(MainActivity.context, list);//setDBtoArrayList(consoleDBHelper));

        RecyclerView recyclerView=normalFragmentView.findViewById(R.id.consoleRecyclerView);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(consoleRecyclerViewAdapter);
        recyclerView.addItemDecoration(new MyConsoleItemDecoration());
        recyclerView.setItemAnimator(null);

        mItemTouchHelper=new ItemTouchHelper(new ItemTouchHelperCallback(consoleRecyclerViewAdapter));
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        consoleRecyclerViewAdapter.setOnItemClickListener(new ConsoleRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                if(pos==(recyclerView.getAdapter().getItemCount()-1)) {//클릭한 아이템이 마지막 아이템, 즉, 추가 아이템이면 콘솔 추가 프래그먼트 띄우기
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.setFrament(MainActivity.AddConsoleFRAGMENT);
                }else{ //일반 콘솔을 클릭했을때 수정 다이얼로그 띄우기
                    Log.d(TAG,"position : "+pos);
                    showAndSetConsoleDialog(consoleDBHelper, pos);
                }
            }
        });

        consoleRecyclerViewAdapter.setOnSelectedConsoleChangedListener(new ConsoleRecyclerViewAdapter.OnSelectedConsoleChangedListener() {
            @Override
            public void onSelectedConsoleChanged(String selectedConsoleName) {
                String tableName=selectedConsoleName.replace(" ","_");
                Log.d(TAG,"onSelectedConsoleChanged, tableName : "+tableName);
                setGameTitleRecyclerViewFromDB(titleDBHelper,tableName);
            }
        });
    }

    void setGameTitleRecyclerViewFromDB(TitleDBHelper titleDBHelper, String platformName){
        //ArrayList<UserTitleInfo> list=new ArrayList<>();
        userTitleInfoArrayList=new ArrayList<>();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {

                Cursor cursor;
                if(platformName.equals("")){ //platform Name에 ""이 입력될 경우, 모든 콘솔의 게임 타이틀을 DB로부터 불러온다.
                    cursor=titleDBHelper.getTableList();
                    String[] tableName;
                    String[] tableNameAll;

                    if(cursor!=null) {
                        tableNameAll = new String[cursor.getCount()];
                        int i = 0;
                        int nullCount = 0;
                        while (cursor.moveToNext()) {
                            String tableNameTemp = cursor.getString(1);
                            /*if (tableNameTemp.equals("android_metadata")) {
                                Log.d(TAG, "android_metadata");
                            }*/
                            if (!tableNameTemp.equals("android_metadata") && !tableNameTemp.equals("sqlite_sequence")) {
                                tableNameAll[i] = tableNameTemp;
                                i++;
                            } else {
                                nullCount++;
                            }
                        }
                        tableName = Arrays.copyOf(tableNameAll, tableNameAll.length - nullCount);

                        for (int j = 0; j < tableName.length; j++) {
                            Log.d(TAG, "tableName[" + j + "] : " + tableName[j]);
                        }
                        if(tableName.length==0){ //최초 실행시 tableName 배열의 길이가 0이라서 lentgh=0, index=0으로 프로그램 뻗는 것 방지하기 위함
                            String[] temp=new String[1];
                            temp[0]="";
                            cursor = titleDBHelper.selectRecordMultipleTables(temp);
                        }else{
                            cursor = titleDBHelper.selectRecordMultipleTables(tableName);
                        }
                        //Log.d(TAG,"tableName : "+tableName[0].toString());
                        //setGameTitleRecyclerViewFromDB(titleDBHelper,tableName[j]);

                        //getGameTitleRecyclerViewFromMultipleTables(titleDBHelper,tableName[0],tableName[1]);
                        UserTitleInfo[] userTitleInfo;
                        if (cursor != null) {
                            userTitleInfo = new UserTitleInfo[cursor.getCount()];
                            Log.d(TAG, "cursor getCount() : " + cursor.getCount());
                            for (int j = 0; j < userTitleInfo.length; j++) {
                                userTitleInfo[j] = new UserTitleInfo(getContext());
                            }

                            int k = 0;
                            Date date;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                            while (cursor.moveToNext()) {
                                userTitleInfo[k].setName(cursor.getString(1));
                                userTitleInfo[k].setPlatform(cursor.getString(2));
                                userTitleInfo[k].setMaker(cursor.getString(3));
                                try {
                                    date = sdf.parse(cursor.getString(4));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    date = new Date();
                                }
                                userTitleInfo[k].setLaunchDate(date);
                                userTitleInfo[k].setImagePath(cursor.getString(5));
                                userTitleInfo[k].setGenre(cursor.getString(6));
                                userTitleInfo[k].setMemo(cursor.getString(7));
                                userTitleInfo[k].setPrice(cursor.getInt(8));
                                userTitleInfo[k].setRating(cursor.getInt(9));
                                userTitleInfo[k].setId(cursor.getString(11));

                                //Log.d(TAG, "userTitleInfo[" + i + "] : " + userTitleInfo[i].getId());

                                userTitleInfoArrayList.add(userTitleInfo[k]);
                                k++;
                            }

                        }
                    }
                }else{ //platform name에 특정 콘솔이 선택되어질 경우
                    Log.d(TAG,"setGameTitleRecyclerViewFromDB, platformName not empty");
                    cursor=titleDBHelper.selectRecord(platformName);

                    UserTitleInfo[] userTitleInfo;
                    if(cursor!=null){
                        userTitleInfo=new UserTitleInfo[cursor.getCount()];
                        Log.d(TAG,"cursor getCount() : "+cursor.getCount());
                        for(int i=0;i<userTitleInfo.length;i++){
                            userTitleInfo[i]=new UserTitleInfo(getContext());
                        }

                        int i=0;
                        Date date;
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

                        while(cursor.moveToNext()){
                            userTitleInfo[i].setName(cursor.getString(1));
                            userTitleInfo[i].setPlatform(cursor.getString(2));
                            userTitleInfo[i].setMaker(cursor.getString(3));
                            try{
                                date=sdf.parse(cursor.getString(4));
                            } catch (ParseException e) {
                                e.printStackTrace();
                                date=new Date();
                            }
                            userTitleInfo[i].setLaunchDate(date);
                            userTitleInfo[i].setImagePath(cursor.getString(5));
                            userTitleInfo[i].setGenre(cursor.getString(6));
                            userTitleInfo[i].setMemo(cursor.getString(7));
                            userTitleInfo[i].setPrice(cursor.getInt(8));
                            userTitleInfo[i].setRating(cursor.getInt(9));
                            userTitleInfo[i].setId(cursor.getString(11));

                            Log.d(TAG,"userTitleInfo["+i+"] : "+userTitleInfo[i].getId());

                            userTitleInfoArrayList.add(userTitleInfo[i]);
                            i++;
                        }
                    }


                }
                //cursor=titleDBHelper.selectRecord(platformName);

                //무조건 ADD Title 아이템은 RV에 추가되어야 한다.
                UserTitleInfo userTitleAddItem=new UserTitleInfo(getContext(), "ADD Title...");
                userTitleInfoArrayList.add(userTitleAddItem);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setTitleRecyclerView(userTitleInfoArrayList);
                    }
                });
            }
        };

        Thread thread=new Thread(runnable);
        thread.start();

    }

    ArrayList<UserTitleInfo> getGameTitleRecyclerViewFromMultipleTables(TitleDBHelper titleDBHelper, String platformName, String platformNameNEXT){
        //ArrayList<UserTitleInfo> list=new ArrayList<>();
        userTitleInfoArrayList=new ArrayList<>();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                Cursor cursor;
                cursor=titleDBHelper.selectRecord(platformName);

                UserTitleInfo[] userTitleInfo;
                if(cursor!=null) {
                    userTitleInfo = new UserTitleInfo[cursor.getCount()];
                    Log.d(TAG, "cursor getCount() : " + cursor.getCount());
                    for (int i = 0; i < userTitleInfo.length; i++) {
                        userTitleInfo[i] = new UserTitleInfo(getContext());
                    }

                    int i = 0;
                    Date date;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    while (cursor.moveToNext()) {
                        userTitleInfo[i].setName(cursor.getString(1));
                        userTitleInfo[i].setPlatform(cursor.getString(2));
                        userTitleInfo[i].setMaker(cursor.getString(3));
                        try {
                            date = sdf.parse(cursor.getString(4));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            date = new Date();
                        }
                        userTitleInfo[i].setLaunchDate(date);
                        userTitleInfo[i].setImagePath(cursor.getString(5));
                        userTitleInfo[i].setGenre(cursor.getString(6));
                        userTitleInfo[i].setMemo(cursor.getString(7));
                        userTitleInfo[i].setPrice(cursor.getInt(8));
                        userTitleInfo[i].setRating(cursor.getInt(9));
                        userTitleInfo[i].setId(cursor.getString(11));

                        Log.d(TAG, "userTitleInfo[" + i + "] : " + userTitleInfo[i].getId());

                        userTitleInfoArrayList.add(userTitleInfo[i]);
                        i++;
                    }
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!platformNameNEXT.equals("")){
                            getGameTitleRecyclerViewFromMultipleTables(titleDBHelper, platformName, platformNameNEXT);
                        }
                    }
                });
            }
        };

        Thread thread=new Thread(runnable);
        thread.start();

        return userTitleInfoArrayList;

    }

    private void setTitleRecyclerView(ArrayList<UserTitleInfo> list){
        RecyclerView recyclerView=normalFragmentView.findViewById(R.id.gameTitleRecyclerView);
        if(gameTitleRecyclerViewAdapter==null){ //최초 프래그먼트 생성시 Recycler View 세팅
            gameTitleRecyclerViewAdapter=new GameTitleRecyclerViewAdapter(getContext(), list);

            recyclerView.addItemDecoration(new MyGameTitleItemDecoration());
            GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),3);

            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(gameTitleRecyclerViewAdapter);

            mGameItemTouchHelper=new ItemTouchHelper(new GameItemTouchHelperCallback(gameTitleRecyclerViewAdapter));
            mGameItemTouchHelper.attachToRecyclerView(recyclerView);
        }else{ //선택된 콘솔 아이템이 바뀔경우, 게임타이틀 아이템만 변경
            gameTitleRecyclerViewAdapter=new GameTitleRecyclerViewAdapter(getContext(), list);
            recyclerView.setAdapter(gameTitleRecyclerViewAdapter);
        }

        gameTitleRecyclerViewAdapter.setOnItemClickListener(new GameTitleRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos, String tableName, String id) {
                //Log.d(TAG,"onItemClick in setTitleRecyclerView, pos : "+pos);
                /*if(pos==(recyclerView.getAdapter().getItemCount()-1)) {//클릭한 아이템이 마지막 아이템, 즉, 추가 아이템이면 콘솔 추가 프래그먼트 띄우기
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.setFrament(MainActivity.AddTitleFRAGMENT);
                    Log.d(TAG,"onItemClick when clicked last item in setTitleRecyclerView, pos : "+pos);
                }
                else
                {
                    //클릭한 아이템의 기종을 얻어와야지
                    showAndSetTitleDialog(titleDBHelper, pos, tableName, id);
                    Log.d(TAG,"onItemClick when clicked normal items in setTitleRecyclerView, pos : "+pos);
                }*/
                if(tableName==null){
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.setFrament(MainActivity.AddTitleFRAGMENT);
                }else{
                    showAndSetTitleDialog(titleDBHelper, pos, tableName, id);
                }
            }
        });
    }

    private void showTitleDialog(UserTitleInfo userTitleInfo, int pos){
        TitleDialogFragment titleDialogFragment=TitleDialogFragment.newInstance("TitleDialogFragment",getContext(),userTitleInfo);
        titleDialogFragment.show(getActivity().getSupportFragmentManager(), "dialog");
        Log.d(TAG,"showTitleDialog");
        //titleDialog=new TitleDialog(getContext(), list.get(pos));
        //titleDialog.show();

        //추가로 삭제 리스너, 아이템 리스너 동작을 코딩해야한다.
        titleDialogFragment.setOnItemSaveListener(new TitleDialogFragment.OnItemSaveListener() {
            @Override
            public void onSaveItem(String gameTitleName, String imagePath) {
                gameTitleRecyclerViewAdapter.list.get(pos).setName(gameTitleName);
                gameTitleRecyclerViewAdapter.list.get(pos).setImagePath(imagePath);
                gameTitleRecyclerViewAdapter.notifyItemChanged(pos);
            }
        });

        titleDialogFragment.setOnItemDeleteListener(new TitleDialogFragment.OnItemDeleteListener() {
            @Override
            public void onDeleteItem() {
                gameTitleRecyclerViewAdapter.deleteItem(pos);
                gameTitleRecyclerViewAdapter.notifyItemRemoved(pos);
            }
        });
    }


    private void showAndSetTitleDialog(TitleDBHelper titleDBHelper, int pos, String tableName, String id){
        //ArrayList<UserTitleInfo> list=new ArrayList<>();
        UserTitleInfo userTitleInfo=new UserTitleInfo(getContext());
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                Cursor cursor=titleDBHelper.selectOneRecord(tableName,id);
                Log.d(TAG,"tableName : "+tableName+", id : "+id);
                //Cursor cursor=titleDBHelper.selectRecord("TEST추후수정필요하다");
                if(cursor!=null){
                    Date date;
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                    cursor.moveToFirst();
                    userTitleInfo.setName(cursor.getString(1));
                    userTitleInfo.setMaker(cursor.getString(3));
                    userTitleInfo.setPlatform(cursor.getString(2));
                    try{
                        date=sdf.parse(cursor.getString(4));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        date=new Date();
                    }
                    userTitleInfo.setLaunchDate(date);
                    userTitleInfo.setImagePath(cursor.getString(5));
                    userTitleInfo.setGenre(cursor.getString(6));
                    userTitleInfo.setMemo(cursor.getString(7));
                    userTitleInfo.setPrice(cursor.getInt(8));
                    userTitleInfo.setRating(cursor.getInt(9));
                    userTitleInfo.setId(cursor.getString(11));

                        //list.add(userTitleInfo);

                    /*UserTitleInfo userTitleAddItem=new UserTitleInfo(getContext(), "ADD Title...");
                    list.add(userTitleAddItem);
                    */
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showTitleDialog(userTitleInfo, pos);
                        }
                    });
                }
            }
        };

        Thread thread=new Thread(runnable);
        thread.start();
    }


}