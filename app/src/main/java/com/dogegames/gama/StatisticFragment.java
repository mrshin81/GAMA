package com.dogegames.gama;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment {

    private static final String TAG="StatisticFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    PieChart platformPieChart;
    PieChart genrePieChart;

    TitleDBHelper titleDBHelper;

    Handler mHandler=new Handler();

    public StatisticFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticFragment newInstance(String param1, String param2) {
        StatisticFragment fragment = new StatisticFragment();
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
        View view=inflater.inflate(R.layout.fragment_statistic, container, false);
        platformPieChart=view.findViewById(R.id.platformPieChart);
        genrePieChart=view.findViewById(R.id.genrePieChart);
        titleDBHelper=TitleDBHelper.getInstance(getContext());

        ArrayList yValues=new ArrayList();

        yValues.add(new PieEntry(34f,"Japan"));
        yValues.add(new PieEntry(23f,"USA"));
        yValues.add(new PieEntry(14f,"UK"));
        yValues.add(new PieEntry(35f,"India"));
        yValues.add(new PieEntry(40f,"Russia"));
        yValues.add(new PieEntry(40f,"Korea"));

        ArrayList yValues2=new ArrayList();

        yValues2.add(new PieEntry(34f,"Japan"));
        yValues2.add(new PieEntry(23f,"USA"));
        yValues2.add(new PieEntry(14f,"UK"));
        yValues2.add(new PieEntry(35f,"India"));

        //setPieChart(platformPieChart, yValues);
        //setPieChart(genrePieChart, yValues2);
        setPlatformPieChart(titleDBHelper);
        setGenrePieChart(titleDBHelper);
        //getDBTableName(titleDBHelper);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setGenrePieChart(TitleDBHelper titleDBHelper){
        ArrayList<UserTitleInfo> list=new ArrayList<>();

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                Cursor cursor;
                cursor=titleDBHelper.getTableList();
                String[] tableName;
                String[] tableNameAll;

                if(cursor!=null){
                    tableNameAll=new String[cursor.getCount()];
                    int i=0;
                    int nullCount=0;

                    while(cursor.moveToNext()){
                        String tableNameTemp= cursor.getString(1);
                        if (!tableNameTemp.equals("android_metadata") && !tableNameTemp.equals("sqlite_sequence")) {
                            tableNameAll[i] = tableNameTemp;
                            i++;
                        } else {
                            nullCount++;
                        }
                    }
                    tableName=Arrays.copyOf(tableNameAll, tableNameAll.length-nullCount);

                    if(tableName.length==0){ //최초 실행시 tableName 배열의 길이가 0이라서 lentgh=0, index=0으로 프로그램 뻗는 것 방지하기 위함
                        String[] temp=new String[1];
                        temp[0]="";
                        cursor = titleDBHelper.selectRecordMultipleTables(temp);
                    }else{
                        cursor = titleDBHelper.selectRecordMultipleTables(tableName);
                    }

                    UserTitleInfo[] userTitleInfo;


                    if(cursor !=null){
                        userTitleInfo=new UserTitleInfo[cursor.getCount()];

                        for(int j=0;j<userTitleInfo.length;j++){
                            userTitleInfo[j]=new UserTitleInfo(getContext());
                        }

                        int k=0;

                        while(cursor.moveToNext()){
                            userTitleInfo[k].setGenre(cursor.getString(6));
                            list.add(userTitleInfo[k]);
                            k++;
                        }
                    }
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        getGenreCount(list);
                    }
                });
            }
        };

        Thread thread=new Thread(runnable);
        thread.start();
    }

    private void getGenreCount(ArrayList<UserTitleInfo> list){
        int[] genreCount=new int[9];
        for(int g=0;g<genreCount.length;g++) genreCount[g]=0;

        String[] genreArray=getResources().getStringArray(R.array.genre);


        for(int n=0;n<list.size();n++){
            for(int m=0;m<genreArray.length;m++){
                if(list.get(n).getGenre().equals(genreArray[m])){
                    genreCount[m]++;
                }
            }
        }

        ArrayList listOfGenreCount=new ArrayList();

        for(int n=0;n<genreCount.length;n++){
            listOfGenreCount.add(new PieEntry(genreCount[n],genreArray[n]));
        }

        setPieChart(genrePieChart, listOfGenreCount);

    }


    private void setPlatformPieChart(TitleDBHelper titleDBHelper){
        ArrayList returnValues=new ArrayList();

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                Cursor cursor;
                cursor=titleDBHelper.getTableList();

                String[] tableName;
                String[] tableNameAll;
                int[] rowCount;

                if(cursor!=null){
                    tableNameAll=new String[cursor.getCount()];
                    int i=0;
                    int nullCount=0;
                    while(cursor.moveToNext()){
                        String tableNameTemp=cursor.getString(1);
                        if(!tableNameTemp.equals("android_metadata") && !tableNameTemp.equals("sqlite_sequence")){
                            tableNameAll[i]=tableNameTemp;
                            i++;
                        }else{
                            nullCount++;
                        }
                    }
                    rowCount=new int[cursor.getCount()-nullCount];
                    tableName= Arrays.copyOf(tableNameAll, tableNameAll.length-nullCount);

                    for (int j = 0; j < tableName.length; j++) {
                        rowCount[j]=titleDBHelper.getNo(tableName[j]);
                        Log.d(TAG, "tableName[" + j + "] : " + tableName[j]+" rowCount : "+rowCount[j]);
                        //if(rowCount[j]!=0)
                        returnValues.add(new PieEntry(rowCount[j],tableName[j]));
                    }
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setPieChart(platformPieChart, returnValues);
                    }
                });
            }
        };
        Thread thread=new Thread(runnable);
        thread.start();
    }

    public void setPieChart(PieChart pieChart, ArrayList yValues){
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,5,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setEntryLabelTextSize(10f);
        //pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(10f);

        /*Description description=new Description();
        description.setText("기종별 타이틀수");
        description.setTextSize(15);
        description.setPosition(pieChart.getX(),pieChart.getY());
        pieChart.setDescription(description);*/

        pieChart.animateY(1000, Easing.EaseInOutCubic);

        Log.d(TAG,"yValues.size : "+yValues.size());

        //타이틀 수가 0인 플랫폼은 pieEntry에서 뺀다.
        Iterator<PieEntry> it=yValues.iterator();
        while(it.hasNext()){
            PieEntry pieEntry=it.next();
            if(pieEntry.getValue()==0){
                it.remove();
                Log.d(TAG,"pieEntry.getValue() : "+pieEntry.getValue());
            }
        }
        Log.d(TAG,"yValues.size : "+yValues.size());


        PieDataSet dataSet=new PieDataSet(yValues, "Platforms");
        /*for(int i=0;i<10;i++){
            Log.d(TAG,"dataSet.getValues() " +dataSet.getValues().get(i));

        }*/
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data=new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);


        //pieChart.getLegend().setEnabled(false);
        //pieChart.getLegend().setYOffset(-1f);
        Legend legend=pieChart.getLegend();
        legend.setWordWrapEnabled(true);
        //pieChart.setCenterText("기종별\n타이틀수");

        pieChart.setData(data);
    }
}