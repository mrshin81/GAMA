package com.dogegames.gama;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

    BarChart consumeBarChart;

    TitleDBHelper titleDBHelper;
    ConsoleDBHelper consoleDBHelper;

    TextView consumePriceTV;

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
        consumeBarChart=view.findViewById(R.id.consumeBarChart);
        consumePriceTV=view.findViewById(R.id.consumePriceTextView);

        titleDBHelper=TitleDBHelper.getInstance(getContext());
        consoleDBHelper=ConsoleDBHelper.getInstance(getContext());

        setPlatformPieChart(titleDBHelper, "Platforms");
        setGenrePieChart(titleDBHelper, "Genres");

        //ConsumePricePerMonth consumePricePerMonth=new ConsumePricePerMonth(null,null);
        //setConsumeBarChart(consumeBarChart,"소비내역",consumePricePerMonth);
        //setConsumeBarChartFromConsoleDB(consoleDBHelper,"Console Consume");
        setConsumeBarChartFromTitleDB(titleDBHelper,"Consume");
        //getDBTableName(titleDBHelper);

        // Inflate the layout for this fragment
        return view;
    }

    public class ConsumePricePerMonth{
        String[] month;
        float[] consumePrice;

        ConsumePricePerMonth(String[] month, float[] consumePrice){
            this.month=month;
            this.consumePrice=consumePrice;
        }

        public void setMonth(String[] monthList){
            this.month=monthList;
        }

        public String[] getMonth(){
            return month;
        }

        public void setConsumePrice(float[] consumePrice){
            this.consumePrice=consumePrice;
        }

        public float[] getConsumePrice(){
            return consumePrice;
        }
    }

    private void setConsumeBarChart(BarChart barChart, String chartName, ConsumePricePerMonth consumePricePerMonth){

        float[] consumePricePerMonthValues=new float[12];
        String[] monthList=new String[12];

        consumePricePerMonthValues=consumePricePerMonth.getConsumePrice();
        Log.d(TAG,"consumePricePerMonthValues.length : "+consumePricePerMonthValues.length);
        monthList= consumePricePerMonth.getMonth();

        ArrayList yValues2=new ArrayList();

        for(int i=0;i<consumePricePerMonthValues.length;i++){
            yValues2.add(new BarEntry((i+1),consumePricePerMonthValues[i]));
            Log.d(TAG,"consumePricePerMonthValues : "+i+": "+String.valueOf(consumePricePerMonthValues[i]));
        }

        /*yValues2.add(new BarEntry(1f,10.0f)); //1
        yValues2.add(new BarEntry(2f,13.0f)); //2
        yValues2.add(new BarEntry(3f,16.0f)); //3
        yValues2.add(new BarEntry(4f,19.0f)); //4
        yValues2.add(new BarEntry(5f,21.0f)); //5
        yValues2.add(new BarEntry(6f,23.0f)); //6
        yValues2.add(new BarEntry(7f,25.0f)); //7
        yValues2.add(new BarEntry(8f,30.0f)); //8
        yValues2.add(new BarEntry(9f,21.0f)); //9
        yValues2.add(new BarEntry(10f,24.0f)); //10
        yValues2.add(new BarEntry(11f,42.0f)); //11
        yValues2.add(new BarEntry(12f,100.0f)); //12
*/
        BarDataSet dataSet=new BarDataSet(yValues2,chartName);
        //dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        Description description=new Description();
        description.setText("");
        barChart.setDescription(description);

        ArrayList<String> labels = new ArrayList<String>();
        for(String temp:consumePricePerMonth.getMonth()){
            temp=temp.substring(5,7)+"월";
            labels.add(temp);
        }

        /*for (int i = 0; i < 12; i++) {
            labels.add((i+1)+"월");
        }*/
        Legend legend=barChart.getLegend();
        legend.setEnabled(false);

        YAxis yAxis_Right=barChart.getAxisRight();
        yAxis_Right.setEnabled(false);
        barChart.setTouchEnabled(false);

        XAxis xAxis=barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(labels.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                Log.d(TAG,"getFormattedValue value : "+Math.round(value)+" float value : "+value);
                String label=labels.get(((int)value-1));
                return label;
            }
        });

        xAxis.setLabelRotationAngle(35);
        //xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        BarData data=new BarData(dataSet);
        barChart.setData(data);
        barChart.invalidate();
        Log.d(TAG,"setConsumeBarChart Completed...");

    }

    private void setConsumeBarChartFromTitleDB(TitleDBHelper titleDBHelper, String chartName){
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
                        Log.d(TAG,"not null");

                        userTitleInfo=new UserTitleInfo[cursor.getCount()];

                        for(int j=0;j<userTitleInfo.length;j++){
                            userTitleInfo[j]=new UserTitleInfo(getContext());
                        }

                        int k=0;
                        Date date;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        while(cursor.moveToNext()){
                            //userTitleInfo[k].setGenre(cursor.getString(6));
                            //userTitleInfo[k].setBuyDate();
                            userTitleInfo[k].setName(cursor.getString(1));
                            userTitleInfo[k].setPlatform(cursor.getString(2));
                            userTitleInfo[k].setMaker(cursor.getString(3));
                            try {
                                date = sdf.parse(cursor.getString(4));
                            } catch (ParseException e) {
                                e.printStackTrace();
                                date = new Date();
                            }
                            userTitleInfo[k].setBuyDate(date);
                            userTitleInfo[k].setImagePath(cursor.getString(5));
                            userTitleInfo[k].setGenre(cursor.getString(6));
                            userTitleInfo[k].setMemo(cursor.getString(7));
                            userTitleInfo[k].setPrice(cursor.getInt(8));
                            userTitleInfo[k].setRating(cursor.getInt(9));
                            userTitleInfo[k].setId(cursor.getString(11));

                            list.add(userTitleInfo[k]);
                            k++;
                        }

                    }
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setConsumeBarChartFromConsoleDB(consoleDBHelper,chartName, list);
                        //if(consumeBarChart!=null)
                        //setConsumeBarChart(consumeBarChart,chartName,getConsumePrice(list));

                    }
                });
            }
        };

        Thread thread=new Thread(runnable);
        thread.start();
    }

    private void setConsumeBarChartFromConsoleDB(ConsoleDBHelper consoleDBHelper, String chartName, ArrayList<UserTitleInfo> userTitleInfo){
        ArrayList<UserConsoleInfo> list=new ArrayList<>();

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                Cursor cursor;
                cursor=consoleDBHelper.selectRecord();
                UserConsoleInfo[] userConsoleInfo;
                if(cursor!=null){
                    userConsoleInfo=new UserConsoleInfo[cursor.getCount()];
                    for(int i=0;i<userConsoleInfo.length;i++){
                        userConsoleInfo[i]=new UserConsoleInfo(getContext());
                    }

                    int i=0;
                    Date date;
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

                    while(cursor.moveToNext()){
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

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ConsumePricePerMonth consumePricePerMonthForTitle= getConsumePrice(userTitleInfo);
                            ConsumePricePerMonth consumePricePerMonthForConsole=getConsumePriceForConsole(list);
                            String[] monthList=consumePricePerMonthForTitle.getMonth();
                            float[] monthConsumePriceForTitle=consumePricePerMonthForTitle.getConsumePrice();
                            float[] monthConsumePriceForConsole=consumePricePerMonthForConsole.getConsumePrice();
                            float[] monthConsumePriceForAll=new float[12];
                            for(int i=0;i<monthConsumePriceForTitle.length;i++){
                                monthConsumePriceForAll[i]=monthConsumePriceForConsole[i]+monthConsumePriceForTitle[i];
                            }
                            ConsumePricePerMonth consumePricePerMonthForAll=new ConsumePricePerMonth(monthList,monthConsumePriceForAll);
                            //setConsumeBarChart(consumeBarChart,chartName,getConsumePriceForConsole(list));
                            setConsumeBarChart(consumeBarChart,chartName,consumePricePerMonthForAll);
                        }
                    });
                }
            }
        };
        Thread thread=new Thread(runnable);
        thread.start();
    }

    private ConsumePricePerMonth getConsumePriceForConsole(ArrayList<UserConsoleInfo> list){
        Date currentDate;
        Calendar cal=Calendar.getInstance();

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");

        //현재부터 1년전까지의 yyyy-MM을 구해서 monthList 배열에 넣어둔다.
        //monthList[11]이 현재 년,월 이고 monthList[0]이 1년전 년, 월
        String[] monthList=new String[12];
        for(int i=(monthList.length-1);i>=0;i--){
            if(i!=(monthList.length-1))
                cal.add(Calendar.MONTH,-1);
            currentDate=cal.getTime();
            monthList[i]=sdf.format(currentDate);
            Log.d(TAG,"monthList["+i+"] : "+monthList[i]);
        }

        float[] totalPrice=new float[12];

        for(int j=0;j<totalPrice.length;j++){
            for(int i=0;i<list.size();i++){
                String buyDate=((Commons)getActivity().getApplication()).convertDateToString(list.get(i).getDate()).substring(0,7);
                Log.d(TAG,"list.get : "+buyDate);
                if(buyDate.equals(monthList[j])){
                    totalPrice[j]+=list.get(i).getPrice();
                }
            }
            Log.d(TAG,"totalPrice["+j+"] : "+totalPrice[j]);
        }

        ConsumePricePerMonth consumePricePerMonth=new ConsumePricePerMonth(monthList, totalPrice);

        return consumePricePerMonth;
    }

    private ConsumePricePerMonth getConsumePrice(ArrayList<UserTitleInfo> list){

        Date currentDate;
        Calendar cal=Calendar.getInstance();
        
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");

        //현재부터 1년전까지의 yyyy-MM을 구해서 monthList 배열에 넣어둔다.
        //monthList[11]이 현재 년,월 이고 monthList[0]이 1년전 년, 월
        String[] monthList=new String[12];
        for(int i=(monthList.length-1);i>=0;i--){
            if(i!=(monthList.length-1))
            cal.add(Calendar.MONTH,-1);
            currentDate=cal.getTime();
            monthList[i]=sdf.format(currentDate);
            Log.d(TAG,"monthList["+i+"] : "+monthList[i]);
        }

        float[] totalPrice=new float[12];

        for(int j=0;j<totalPrice.length;j++){
            for(int i=0;i<list.size();i++){
                String buyDate=((Commons)getActivity().getApplication()).convertDateToString(list.get(i).getBuyDate()).substring(0,7);
                Log.d(TAG,"list.get : "+buyDate);
                if(buyDate.equals(monthList[j])){
                    totalPrice[j]+=list.get(i).getPrice();
                }
            }
            Log.d(TAG,"totalPrice["+j+"] : "+totalPrice[j]);
        }

        ConsumePricePerMonth consumePricePerMonth=new ConsumePricePerMonth(monthList, totalPrice);

        return consumePricePerMonth;
    }

    private void setGenrePieChart(TitleDBHelper titleDBHelper, String chartName){
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
                        getGenreCount(list, chartName);
                    }
                });
            }
        };

        Thread thread=new Thread(runnable);
        thread.start();
    }

    private void getGenreCount(ArrayList<UserTitleInfo> list, String chartName){
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

        setPieChart(genrePieChart, listOfGenreCount, chartName, ColorTemplate.JOYFUL_COLORS);

    }


    private void setPlatformPieChart(TitleDBHelper titleDBHelper, String chartName){
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
                        setPieChart(platformPieChart, returnValues,chartName,ColorTemplate.PASTEL_COLORS);
                    }
                });
            }
        };
        Thread thread=new Thread(runnable);
        thread.start();
    }

    public void setPieChart(PieChart pieChart, ArrayList yValues, String chartName, int[] color){
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


        PieDataSet dataSet=new PieDataSet(yValues, chartName);
        /*for(int i=0;i<10;i++){
            Log.d(TAG,"dataSet.getValues() " +dataSet.getValues().get(i));

        }*/
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(color);//ColorTemplate.JOYFUL_COLORS);

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