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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Date;

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

    private View normalFragmentView;

    ConsoleDBHelper consoleDBHelper;
    TitleDBHelper titleDBHelper;
    ConsoleRecyclerViewAdapter consoleRecyclerViewAdapter;
    GameTitleRecyclerViewAdapter gameTitleRecyclerViewAdapter;

    Handler mHandler=new Handler();

    ActivityResultLauncher<Intent> resultLauncherForTitleDialog;

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
        //saveConsoleData();
        Log.d(TAG,"onCreateView, NormalFragment");
        //setConsoleRecyclerView();
        setConsoleRecyclerViewFromDB(consoleDBHelper);//DB로부터 데이터 불러오기를 Background Thread로 동작시킨다.
        setGameTitleRecyclerViewFromDB(titleDBHelper);
        setImagePickerCallback();
        // Inflate the layout for this fragment
        return normalFragmentView;
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

    private void showTitleDialog(ArrayList<UserTitleInfo> list, int pos){
        TitleDialogFragment titleDialogFragment=TitleDialogFragment.newInstance("TitleDialogFragment",getContext(),list.get(pos));
        titleDialogFragment.show(getActivity().getSupportFragmentManager(), "dialog");
        Log.d(TAG,"showTitleDialog");
        //titleDialog=new TitleDialog(getContext(), list.get(pos));
        //titleDialog.show();

        //추가로 삭제 리스너, 아이템 리스너 동작을 코딩해야한다.
        titleDialogFragment.setOnItemSaveListener(new TitleDialogFragment.OnItemSaveListener() {
            @Override
            public void onSaveItem(String gameTitleName, String imagePath) {
                gameTitleName="gameTitleName";
                gameTitleRecyclerViewAdapter.list.get(pos).setName(gameTitleName);
                gameTitleRecyclerViewAdapter.list.get(pos).setImagePath(imagePath);
                gameTitleRecyclerViewAdapter.notifyItemChanged(pos);
            }
        });

        titleDialogFragment.setOnItemDeleteListener(new TitleDialogFragment.OnItemDeleteListener() {
            @Override
            public void onDeleteItem() {
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
    }

    void setGameTitleRecyclerViewFromDB(TitleDBHelper titleDBHelper){
        ArrayList<UserTitleInfo> list=new ArrayList<>();

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                Cursor cursor=titleDBHelper.selectRecord();
                UserTitleInfo[] userTitleInfo;
                if(cursor!=null){
                    userTitleInfo=new UserTitleInfo[cursor.getCount()];
                    for(int i=0;i<userTitleInfo.length;i++){
                        userTitleInfo[i]=new UserTitleInfo(getContext());
                    }

                    int i=0;
                    Date date;
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

                    while(cursor.moveToNext()){
                        userTitleInfo[i].setName(cursor.getString(1));
                        userTitleInfo[i].setMaker(cursor.getString(2));
                        userTitleInfo[i].setPlatform(cursor.getString(3));
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

                        list.add(userTitleInfo[i]);
                        i++;
                    }
                }

                //무조건 ADD Title 아이템은 RV에 추가되어야 한다.
                UserTitleInfo userTitleAddItem=new UserTitleInfo(getContext(), "ADD Title...");
                list.add(userTitleAddItem);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setTitleRecyclerView(list);
                    }
                });
            }
        };

        Thread thread=new Thread(runnable);
        thread.start();

    }

    private void setTitleRecyclerView(ArrayList<UserTitleInfo> list){
        gameTitleRecyclerViewAdapter=new GameTitleRecyclerViewAdapter(getContext(), list);
        RecyclerView recyclerView=normalFragmentView.findViewById(R.id.gameTitleRecyclerView);

        recyclerView.addItemDecoration(new MyGameTitleItemDecoration());
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),3);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(gameTitleRecyclerViewAdapter);

        gameTitleRecyclerViewAdapter.setOnItemClickListener(new GameTitleRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                //Log.d(TAG,"onItemClick in setTitleRecyclerView, pos : "+pos);
                if(pos==(recyclerView.getAdapter().getItemCount()-1)) {//클릭한 아이템이 마지막 아이템, 즉, 추가 아이템이면 콘솔 추가 프래그먼트 띄우기
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.setFrament(MainActivity.AddTitleFRAGMENT);
                    Log.d(TAG,"onItemClick when clicked last item in setTitleRecyclerView, pos : "+pos);
                }else{
                    showAndSetTitleDialog(titleDBHelper, pos);
                    Log.d(TAG,"onItemClick when clicked normal items in setTitleRecyclerView, pos : "+pos);
                }
            }
        });
    }

    private void showAndSetTitleDialog(TitleDBHelper titleDBHelper, int pos){
        ArrayList<UserTitleInfo> list=new ArrayList<>();

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                Cursor cursor=titleDBHelper.selectRecord();
                UserTitleInfo[] userTitleInfo;
                if(cursor!=null){
                    userTitleInfo=new UserTitleInfo[cursor.getCount()];
                    for(int i=0;i<userTitleInfo.length;i++){
                        userTitleInfo[i]=new UserTitleInfo(getContext());
                    }

                    int i=0;
                    Date date;
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

                    while(cursor.moveToNext()){
                        userTitleInfo[i].setName(cursor.getString(1));
                        userTitleInfo[i].setMaker(cursor.getString(2));
                        userTitleInfo[i].setPlatform(cursor.getString(3));
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

                        list.add(userTitleInfo[i]);
                        i++;
                    }

                    UserTitleInfo userTitleAddItem=new UserTitleInfo(getContext(), "ADD Title...");
                    list.add(userTitleAddItem);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showTitleDialog(list, pos);
                        }
                    });
                }
            }
        };

        Thread thread=new Thread(runnable);
        thread.start();
    }


}